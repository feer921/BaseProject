package common.base.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.LevelListDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.collection.SparseArrayCompat;
import android.util.Pair;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.RemoteViews;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/12/25<br>
 * Time: 10:23<br>
 * <P>DESC:
 * 通知先生
 * </p>
 * ******************(^_^)***********************
 */
public class MrNotification {
    /**
     * 最近的一条通知的id
     * def: 0x10
     */
    private int theLastNotificationId = 0x10;
    private NotificationManagerCompat notifyManager;
    private static MrNotification me;
    /**
     *
     */
//    private HashMap<Integer,String> notifiesIdMapTags;

    private SparseArrayCompat<String> notifiesIdMapTags;
    private MrNotification() {

    }
    public static MrNotification getMe() {
        if (me == null) {
            synchronized (MrNotification.class) {
                if (me == null) {
                    me = new MrNotification();
                }
            }
        }
        return me;
    }
    /**
     * 判断是否通知权限被拒绝了
     * @param context Context
     * @return true:允许发送通知(一些系统会涉及到Toast"权限"); false:通知栏权限可能被禁止了
     */
    public static boolean isNotifyPermissionAllowed(Context context) {
       return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 兼容性的跳转到系统通知栏权限界面
     * @param context
     */
    public static void jumpToApplyNotifyPermiss(Context context) {
        // vivo 点击设置图标>加速白名单>我的app
        //      点击软件管理>软件管理权限>软件>我的app>信任该软件
        Intent appIntent = context.getPackageManager().getLaunchIntentForPackage("com.iqoo.secure");
        if(appIntent != null){
            context.startActivity(appIntent);
            return;
        }

        // oppo 点击设置图标>应用权限管理>按应用程序管理>我的app>我信任该应用
        //      点击权限隐私>自启动管理>我的app
        appIntent = context.getPackageManager().getLaunchIntentForPackage("com.oppo.safe");
        if(appIntent != null){
            context.startActivity(appIntent);
            return;
        }

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        }
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD){
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName("com.android.settings","com.android.settings.InstalledAppDetails");
            intent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
        }
        context.startActivity(intent);
    }
    public NotificationBuilder aBuilder(Context context, String channelId) {
        NotificationBuilder builder = new NotificationBuilder(context, channelId);
        if (notifyManager == null) {
            notifyManager = NotificationManagerCompat.from(context);
//            notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return builder;
    }
    /**
     * 是否授予音量操作的权限
     * <a>https://blog.csdn.net/manjianchao/article/details/77576638</a>
     * @param context
     * @return
     */
    public static boolean isNotificationPolicyAccessGranted(Context context) {
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null && Build.VERSION.SDK_INT >= 23) {
            return nm.isNotificationPolicyAccessGranted();//need api >= 23
        }
        return true;
    }

    /**
     * 在Android 8.0后，需要创建通知 channel后，再使用发送对应 channel的通知
     * @param context Context
     * @param channelId 可惟一的通知channel id
     * @param channelName 渠道名称，供用户查看，需要看名知意
     * @param importanceLevel 重要等级；见{@linkplain NotificationManager#IMPORTANCE_HIGH}
     */
    public static void createNotificationChannel(Context context,String channelId,CharSequence channelName,int importanceLevel) {
        if (android.os.Build.VERSION.SDK_INT >= 26) {//Android 8.0+ 才有通知渠道的功能
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importanceLevel);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
    }

    /**
     * 创建 通知渠道
     * @param context Context
     * @param channelId 创建的Channel ID Android 8.0后需要创建了Channel ID，才能发送通知
     * @param channelName  Channel Name
     * @param importanceLevel 重要等级
     * @param autoCreateChannel 是否本方法自动直接创建；如果是false:则 调用处拿到返回值后，还能设置 NotificationChannel的属性
     * @return Pair<NotificationChannel,NotificationManager>
     */
    public static Pair<NotificationChannel,NotificationManager> createNotificationChannel(Context context,String channelId,CharSequence channelName,int importanceLevel,boolean autoCreateChannel){
        if (android.os.Build.VERSION.SDK_INT >= 26) {//Android 8.0+ 才有通知渠道的功能
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importanceLevel);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (autoCreateChannel) {
                if (notificationManager != null) {
                    notificationManager.createNotificationChannel(notificationChannel);
                }
            }
            return new Pair<>(notificationChannel, notificationManager);
        }
        return null;
    }
    /**
     * 调用系统通知管理者向系统发送 一条通知
     * 注：如果同时指定tag,和notifyId,则如果要取消该通知，需要也同时指定tag和notifyId
     * @param tag 通知的tag
     * @param notifyId 通知的id
     * @param notification 通知对象
     * @return notifyId
     */
    public int send(@Nullable String tag, int notifyId,@NonNull Notification notification) {
        if (notifyManager != null) {
            notifyManager.notify(tag,notifyId,notification);
        }
        if (notifiesIdMapTags == null) {
            notifiesIdMapTags = new SparseArrayCompat<>();
        }
        notifiesIdMapTags.put(notifyId,tag);
        theLastNotificationId = notifyId;
        return notifyId;
    }

    public int send(int notifyId, @NonNull Notification notification) {
        return send(null,notifyId,notification);
    }

    public int send(@NonNull Notification notification) {
        return send(theLastNotificationId + 1, notification);
    }

    public int send(@Nullable String tag, @NonNull Notification notification) {
        return send(tag, theLastNotificationId + 1, notification);
    }

    public void cancelAll() {
        if (notifyManager != null) {
            notifyManager.cancelAll();
            if (notifiesIdMapTags != null) {
                notifiesIdMapTags.clear();
            }
        }
    }

    /**
     * @param toCancelNotificationId 不用考虑该id可能是外部自己定的id
     */
    public void cancel(int toCancelNotificationId) {
        if (notifyManager != null) {
            String tag = queryMapNotificationIdTag(toCancelNotificationId);
            if (tag != null) {//
                notifyManager.cancel(tag, toCancelNotificationId);
            }
            else {
                notifyManager.cancel(toCancelNotificationId);
            }
        }
    }
    public void cancel(String notificationTag,int theId) {
        if (notifyManager != null) {
            notifyManager.cancel(notificationTag, theId);
        }
    }

    public String queryMapNotificationIdTag(int theId) {
        if (notifiesIdMapTags != null) {
            return notifiesIdMapTags.get(theId);
        }
        return null;
    }
    public class NotificationBuilder extends NotificationCompat.Builder {
//        private Context mContext;
        /**
         * Constructor.
         * <p>
         * Automatically sets the when field to {@link System#currentTimeMillis()
         * System.currentTimeMillis()} and the audio stream to the
         * {@link Notification#STREAM_DEFAULT}.
         *
         * @param context   A {@link Context} that will be used to construct the
         *                  RemoteViews. The Context will not be held past the lifetime of this
         *                  Builder object.
         * @param channelId The constructed Notification will be posted on this
         */
        public NotificationBuilder(@NonNull Context context, @NonNull String channelId) {
            super(context, channelId);
//            mContext = context.getApplicationContext();
        }

        /**
         * Set the time that the event occurred.  Notifications in the panel are
         * sorted by this time.
         *
         * @param when
         */
        @Override
        public NotificationBuilder setWhen(long when) {
            super.setWhen(when);
            return this;
        }

        /**
         * Control whether the timestamp set with {@link #setWhen(long) setWhen} is shown
         * in the content view.
         *
         * @param show
         */
        @Override
        public NotificationBuilder setShowWhen(boolean show) {
             super.setShowWhen(show);
             return this;
        }

        /**
         * Show the {@link Notification#when} field as a stopwatch.
         * <p>
         * Instead of presenting <code>when</code> as a timestamp, the notification will show an
         * automatically updating display of the minutes and seconds since <code>when</code>.
         * <p>
         * Useful when showing an elapsed time (like an ongoing phone call).
         *
         * @param b
         * @see Chronometer
         * @see Notification#when
         */
        @Override
        public NotificationBuilder setUsesChronometer(boolean b) {
            super.setUsesChronometer(b);
            return this;
        }

        /**
         * Set the small icon to use in the notification layouts.  Different classes of devices
         * may return different sizes.  See the UX guidelines for more information on how to
         * design these icons.
         *
         * @param icon A resource ID in the application's package of the drawable to use.
         */
        @Override
        public NotificationBuilder setSmallIcon(int icon) {
            super.setSmallIcon(icon);
            return this;
        }

        /**
         * A variant of {@link #setSmallIcon(int) setSmallIcon(int)} that takes an additional
         * level parameter for when the icon is a {@link LevelListDrawable
         * LevelListDrawable}.
         *
         * @param icon  A resource ID in the application's package of the drawable to use.
         * @param level The level to use for the icon.
         * @see LevelListDrawable
         */
        @Override
        public NotificationBuilder setSmallIcon(int icon, int level) {
            super.setSmallIcon(icon, level);
            return this;
        }

        /**
         * Set the title (first row) of the notification, in a standard notification.
         *
         * @param title
         */
        @Override
        public NotificationBuilder setContentTitle(CharSequence title) {
            super.setContentTitle(title);
            return this;
        }

        /**
         * Set the text (second row) of the notification, in a standard notification.
         *
         * @param text
         */
        @Override
        public NotificationBuilder setContentText(CharSequence text) {
            super.setContentText(text);
            return this;
        }

        /**
         * Set the third line of text in the platform notification template.
         * Don't use if you're also using {@link #setProgress(int, int, boolean)};
         * they occupy the same location in the standard template.
         * <br>
         * If the platform does not provide large-format notifications, this method has no effect.
         * The third line of text only appears in expanded view.
         * <br>
         *
         * @param text
         */
        @Override
        public NotificationBuilder setSubText(CharSequence text) {
            super.setSubText(text);
            return this;
        }

        /**
         * Set the remote input history.
         * <p>
         * This should be set to the most recent inputs that have been sent
         * through a {@link RemoteInput} of this Notification and cleared once the it is no
         * longer relevant (e.g. for chat notifications once the other party has responded).
         * <p>
         * The most recent input must be stored at the 0 index, the second most recent at the
         * 1 index, etc. Note that the system will limit both how far back the inputs will be shown
         * and how much of each individual input is shown.
         *
         * <p>Note: The reply text will only be shown on notifications that have least one action
         * with a {@code RemoteInput}.</p>
         *
         * @param text
         */
        @Override
        public NotificationBuilder setRemoteInputHistory(CharSequence[] text) {
            super.setRemoteInputHistory(text);
            return this;
        }

        /**
         * Set the large number at the right-hand side of the notification.  This is
         * equivalent to setContentInfo, although it might show the number in a different
         * font size for readability.
         *
         * @param number
         */
        @Override
        public NotificationBuilder setNumber(int number) {
            super.setNumber(number);
            return this;
        }

        /**
         * Set the large text at the right-hand side of the notification.
         *
         * @param info
         */
        @Override
        public NotificationBuilder setContentInfo(CharSequence info) {
            super.setContentInfo(info);
            return this;
        }

        /**
         * Set the progress this notification represents, which may be
         * represented as a {@link ProgressBar}.
         *
         * @param max
         * @param progress
         * @param indeterminate
         */
        @Override
        public NotificationBuilder setProgress(int max, int progress, boolean indeterminate) {
            super.setProgress(max, progress, indeterminate);
            return this;
        }

        /**
         * Supply a custom RemoteViews to use instead of the standard one.
         *
         * @param views
         */
        @Override
        public NotificationBuilder setContent(RemoteViews views) {
            super.setContent(views);
            return this;
        }

        /**
         * Supply a {@link PendingIntent} to send when the notification is clicked.
         * If you do not supply an intent, you can now add PendingIntents to individual
         * views to be launched when clicked by calling {@link RemoteViews#setOnClickPendingIntent
         * RemoteViews.setOnClickPendingIntent(int,PendingIntent)}.  Be sure to
         * read {@link Notification#contentIntent Notification.contentIntent} for
         * how to correctly use this.
         *
         * @param intent
         */
        @Override
        public NotificationBuilder setContentIntent(PendingIntent intent) {
            super.setContentIntent(intent);
            return this;
        }

        /**
         * Supply a {@link PendingIntent} to send when the notification is cleared by the user
         * directly from the notification panel.  For example, this intent is sent when the user
         * clicks the "Clear all" button, or the individual "X" buttons on notifications.  This
         * intent is not sent when the application calls
         * {@link NotificationManager#cancel NotificationManager.cancel(int)}.
         *
         * @param intent
         */
        @Override
        public NotificationBuilder setDeleteIntent(PendingIntent intent) {
            super.setDeleteIntent(intent);
            return this;
        }

        /**
         * An intent to launch instead of posting the notification to the status bar.
         * Only for use with extremely high-priority notifications demanding the user's
         * <strong>immediate</strong> attention, such as an incoming phone call or
         * alarm clock that the user has explicitly set to a particular time.
         * If this facility is used for something else, please give the user an option
         * to turn it off and use a normal notification, as this can be extremely
         * disruptive.
         *
         * <p>
         * On some platforms, the system UI may choose to display a heads-up notification,
         * instead of launching this intent, while the user is using the device.
         * </p>
         *
         * @param intent       The pending intent to launch.
         * @param highPriority Passing true will cause this notification to be sent
         */
        @Override
        public NotificationBuilder setFullScreenIntent(PendingIntent intent, boolean highPriority) {
            super.setFullScreenIntent(intent, highPriority);
            return this;
        }

        /**
         * Sets the "ticker" text which is sent to accessibility services. Prior to
         * {@link Build.VERSION_CODES#LOLLIPOP}, sets the text that is displayed in the status bar
         * when the notification first arrives.
         *
         * @param tickerText
         */
        @Override
        public NotificationBuilder setTicker(CharSequence tickerText) {
            super.setTicker(tickerText);
            return this;
        }

        /**
         * Sets the "ticker" text which is sent to accessibility services. Prior to
         * {@link Build.VERSION_CODES#LOLLIPOP}, sets the text that is displayed in the status bar
         * when the notification first arrives, and also a RemoteViews object that may be displayed
         * instead on some devices.
         *
         * @param tickerText
         * @param views
         */
        @Override
        public NotificationBuilder setTicker(CharSequence tickerText, RemoteViews views) {
            super.setTicker(tickerText, views);
            return this;
        }

        /**
         * Set the large icon that is shown in the ticker and notification.
         *
         * @param icon
         */
        @Override
        public NotificationBuilder setLargeIcon(Bitmap icon) {
            super.setLargeIcon(icon);
            return this;
        }

        /**
         * Set the sound to play.  It will play on the default stream.
         *
         * <p>
         * On some platforms, a notification that is noisy is more likely to be presented
         * as a heads-up notification.
         * </p>
         *
         * @param sound
         */
        @Override
        public NotificationBuilder setSound(Uri sound) {
            super.setSound(sound);
            return this;
        }

        /**
         * Set the sound to play.  It will play on the stream you supply.
         *
         * <p>
         * On some platforms, a notification that is noisy is more likely to be presented
         * as a heads-up notification.
         * </p>
         *
         * @param sound
         * @param streamType
         * @see Notification#STREAM_DEFAULT
         * @see AudioManager for the <code>STREAM_</code> constants.
         */
        @Override
        public NotificationBuilder setSound(Uri sound, int streamType) {
            super.setSound(sound, streamType);
            return this;
        }

        /**
         * Set the vibration pattern to use.
         *
         * <p>
         * On some platforms, a notification that vibrates is more likely to be presented
         * as a heads-up notification.
         * </p>
         *
         * @param pattern
         * @see Vibrator for a discussion of the <code>pattern</code>
         * parameter.
         */
        @Override
        public NotificationBuilder setVibrate(long[] pattern) {
            super.setVibrate(pattern);
            return this;
        }

        /**
         * Set the argb value that you would like the LED on the device to blink, as well as the
         * rate.  The rate is specified in terms of the number of milliseconds to be on
         * and then the number of milliseconds to be off.
         *
         * @param argb
         * @param onMs
         * @param offMs
         */
        @Override
        public NotificationBuilder setLights(int argb, int onMs, int offMs) {
            super.setLights(argb, onMs, offMs);
            return this;
        }

        /**
         * Set whether this is an ongoing notification.
         *
         * <p>Ongoing notifications differ from regular notifications in the following ways:
         * <ul>
         * <li>Ongoing notifications are sorted above the regular notifications in the
         * notification panel.</li>
         * <li>Ongoing notifications do not have an 'X' close button, and are not affected
         * by the "Clear all" button.
         * </ul>
         *
         * @param ongoing
         */
        @Override
        public NotificationBuilder setOngoing(boolean ongoing) {
            super.setOngoing(ongoing);
            return this;
        }

        /**
         * Set whether this notification should be colorized. When set, the color set with
         * {@link #setColor(int)} will be used as the background color of this notification.
         * <p>
         * This should only be used for high priority ongoing tasks like navigation, an ongoing
         * call, or other similarly high-priority events for the user.
         * <p>
         * For most styles, the coloring will only be applied if the notification is for a
         * foreground service notification.
         * <p>
         * However, for MediaStyle and DecoratedMediaCustomViewStyle notifications
         * that have a media session attached there is no such requirement.
         * <p>
         * Calling this method on any version prior to {@link Build.VERSION_CODES#O} will
         * not have an effect on the notification and it won't be colorized.
         *
         * @param colorize
         * @see #setColor(int)
         */
        @Override
        public NotificationBuilder setColorized(boolean colorize) {
            super.setColorized(colorize);
            return this;
        }

        /**
         * Set this flag if you would only like the sound, vibrate
         * and ticker to be played if the notification is not already showing.
         *
         * @param onlyAlertOnce
         */
        @Override
        public NotificationBuilder setOnlyAlertOnce(boolean onlyAlertOnce) {
            super.setOnlyAlertOnce(onlyAlertOnce);
            return this;
        }

        /**
         * Setting this flag will make it so the notification is automatically
         * canceled when the user clicks it in the panel.  The PendingIntent
         * set with {@link #setDeleteIntent} will be broadcast when the notification
         * is canceled.
         *
         * @param autoCancel
         */
        @Override
        public NotificationBuilder setAutoCancel(boolean autoCancel) {
            super.setAutoCancel(autoCancel);
            return this;
        }

        /**
         * Set whether or not this notification is only relevant to the current device.
         *
         * <p>Some notifications can be bridged to other devices for remote display.
         * This hint can be set to recommend this notification not be bridged.
         *
         * @param b
         */
        @Override
        public NotificationBuilder setLocalOnly(boolean b) {
            super.setLocalOnly(b);
            return this;
        }

        /**
         * Set the notification category.
         *
         * <p>Must be one of the predefined notification categories (see the <code>CATEGORY_*</code>
         * constants in {@link Notification}) that best describes this notification.
         * May be used by the system for ranking and filtering.
         *
         * @param category
         */
        @Override
        public NotificationBuilder setCategory(String category) {
            super.setCategory(category);
            return this;
        }

        /**
         * Set the default notification options that will be used.
         * <p>
         * The value should be one or more of the following fields combined with
         * bitwise-or:
         * {@link Notification#DEFAULT_SOUND}, {@link Notification#DEFAULT_VIBRATE},
         * {@link Notification#DEFAULT_LIGHTS}.
         * <p>
         * For all default values, use {@link Notification#DEFAULT_ALL}.
         *
         * @param defaults
         */
        @Override
        public NotificationBuilder setDefaults(int defaults) {
            super.setDefaults(defaults);
            return this;
        }

        /**
         * Set the relative priority for this notification.
         * <p>
         * Priority is an indication of how much of the user's
         * valuable attention should be consumed by this
         * notification. Low-priority notifications may be hidden from
         * the user in certain situations, while the user might be
         * interrupted for a higher-priority notification.
         * The system sets a notification's priority based on various factors including the
         * setPriority value. The effect may differ slightly on different platforms.
         *
         * @param pri Relative priority for this notification. Must be one of
         *            the priority constants defined by {@link NotificationCompat}.
         *            Acceptable values range from {@link
         *            NotificationCompat#PRIORITY_MIN} (-2) to {@link
         *            NotificationCompat#PRIORITY_MAX} (2).
         */
        @Override
        public NotificationBuilder setPriority(int pri) {
            super.setPriority(pri);
            return this;
        }

        /**
         * Add a person that is relevant to this notification.
         *
         * <p>
         * Depending on user preferences, this annotation may allow the notification to pass
         * through interruption filters, and to appear more prominently in the user interface.
         * </P>
         *
         * <p>
         * The person should be specified by the {@code String} representation of a
         * {@link ContactsContract.Contacts#CONTENT_LOOKUP_URI}.
         * </P>
         *
         * <P>The system will also attempt to resolve {@code mailto:} and {@code tel:} schema
         * URIs.  The path part of these URIs must exist in the contacts database, in the
         * appropriate column, or the reference will be discarded as invalid. Telephone schema
         * URIs will be resolved by {@link ContactsContract.PhoneLookup}.
         * </P>
         *
         * @param uri A URI for the person.
         * @see Notification#EXTRA_PEOPLE
         */
        @Override
        public NotificationBuilder addPerson(String uri) {
            super.addPerson(uri);
            return this;
        }

        /**
         * Set this notification to be part of a group of notifications sharing the same key.
         * Grouped notifications may display in a cluster or stack on devices which
         * support such rendering.
         *
         * <p>To make this notification the summary for its group, also call
         * {@link #setGroupSummary}. A sort order can be specified for group members by using
         * {@link #setSortKey}.
         *
         * @param groupKey The group key of the group.
         * @return this object for method chaining
         */
        @Override
        public NotificationBuilder setGroup(String groupKey) {
            super.setGroup(groupKey);
            return this;
        }

        /**
         * Set this notification to be the group summary for a group of notifications.
         * Grouped notifications may display in a cluster or stack on devices which
         * support such rendering. Requires a group key also be set using {@link #setGroup}.
         *
         * @param isGroupSummary Whether this notification should be a group summary.
         * @return this object for method chaining
         */
        @Override
        public NotificationBuilder setGroupSummary(boolean isGroupSummary) {
            super.setGroupSummary(isGroupSummary);
            return this;
        }


        @Override
        public NotificationBuilder setSortKey(String sortKey) {
            super.setSortKey(sortKey);
            return this;
        }

        /**
         * Merge additional metadata into this notification.
         *
         * <p>Values within the Bundle will replace existing extras values in this Builder.
         *
         * @param extras
         * @see Notification#extras
         */
        @Override
        public NotificationBuilder addExtras(Bundle extras) {
            super.addExtras(extras);
            return this;
        }

        /**
         * Set metadata for this notification.
         *
         * <p>A reference to the Bundle is held for the lifetime of this Builder, and the Bundle's
         * current contents are copied into the Notification each time {@link #build()} is
         * called.
         *
         * <p>Replaces any existing extras values with those from the provided Bundle.
         * Use {@link #addExtras} to merge in metadata instead.
         *
         * @param extras
         * @see Notification#extras
         */
        @Override
        public NotificationBuilder setExtras(Bundle extras) {
            super.setExtras(extras);
            return this;
        }

        /**
         * Add an action to this notification. Actions are typically displayed by
         * the system as a button adjacent to the notification content.
         * <br>
         * Action buttons won't appear on platforms prior to Android 4.1. Action
         * buttons depend on expanded notifications, which are only available in Android 4.1
         * and later. To ensure that an action button's functionality is always available, first
         * implement the functionality in the {@link Activity} that starts when a user
         * clicks the  notification (see {@link #setContentIntent setContentIntent()}), and then
         * enhance the notification by implementing the same functionality with
         * {@link #addAction addAction()}.
         *
         * @param icon   Resource ID of a drawable that represents the action.
         * @param title  Text describing the action.
         * @param intent {@link PendingIntent} to be fired when the action is invoked.
         */
        @Override
        public NotificationBuilder addAction(int icon, CharSequence title, PendingIntent intent) {
            super.addAction(icon, title, intent);
            return this;
        }

        /**
         * Add an action to this notification. Actions are typically displayed by
         * the system as a button adjacent to the notification content.
         * <br>
         * Action buttons won't appear on platforms prior to Android 4.1. Action
         * buttons depend on expanded notifications, which are only available in Android 4.1
         * and later. To ensure that an action button's functionality is always available, first
         * implement the functionality in the {@link Activity} that starts when a user
         * clicks the  notification (see {@link #setContentIntent setContentIntent()}), and then
         * enhance the notification by implementing the same functionality with
         * {@link #addAction addAction()}.
         *
         * @param action The action to add.
         */
        @Override
        public NotificationBuilder addAction(NotificationCompat.Action action) {
            super.addAction(action);
            return this;
        }

        /**
         * Add a rich notification style to be applied at build time.
         * <br>
         * If the platform does not provide rich notification styles, this method has no effect. The
         * user will always see the normal notification style.
         *
         * @param style Object responsible for modifying the notification style.
         */
        @Override
        public NotificationBuilder setStyle(NotificationCompat.Style style) {
            super.setStyle(style);
            return this;
        }

        /**
         * Sets {@link Notification#color}.
         *
         * @param argb The accent color to use
         * @return The same Builder.
         */
        @Override
        public NotificationBuilder setColor(int argb) {
            super.setColor(argb);
            return this;
        }

        /**
         * Sets {@link Notification#visibility}.
         *
         * @param visibility One of {@link Notification#VISIBILITY_PRIVATE} (the default),
         *                   {@link Notification#VISIBILITY_PUBLIC}, or
         *                   {@link Notification#VISIBILITY_SECRET}.
         */
        @Override
        public NotificationBuilder setVisibility(int visibility) {
            super.setVisibility(visibility);
            return this;
        }


        @Override
        public NotificationBuilder setPublicVersion(Notification n) {
            super.setPublicVersion(n);
            return this;
        }

        /**
         * Supply custom RemoteViews to use instead of the platform template.
         * <p>
         * This will override the layout that would otherwise be constructed by this Builder
         * object.
         *
         * @param contentView
         */
        @Override
        public NotificationBuilder setCustomContentView(RemoteViews contentView) {
            super.setCustomContentView(contentView);
            return this;
        }

        /**
         * Supply custom RemoteViews to use instead of the platform template in the expanded form.
         * <p>
         * This will override the expanded layout that would otherwise be constructed by this
         * Builder object.
         * <p>
         * No-op on versions prior to {@link Build.VERSION_CODES#JELLY_BEAN}.
         *
         * @param contentView
         */
        @Override
        public NotificationBuilder setCustomBigContentView(RemoteViews contentView) {
            super.setCustomBigContentView(contentView);
            return this;
        }

        /**
         * Supply custom RemoteViews to use instead of the platform template in the heads up dialog.
         * <p>
         * This will override the heads-up layout that would otherwise be constructed by this
         * Builder object.
         * <p>
         * No-op on versions prior to {@link Build.VERSION_CODES#LOLLIPOP}.
         *
         * @param contentView
         */
        @Override
        public NotificationBuilder setCustomHeadsUpContentView(RemoteViews contentView) {
            super.setCustomHeadsUpContentView(contentView);
            return this;
        }

        /**
         * Specifies the channel the notification should be delivered on.
         * <p>
         * No-op on versions prior to {@link Build.VERSION_CODES#O} .
         *
         * @param channelId
         */
        @Override
        public NotificationBuilder setChannelId(@NonNull String channelId) {
            super.setChannelId(channelId);
            return this;
        }

        /**
         * Specifies the time at which this notification should be canceled, if it is not already
         * canceled.
         *
         * @param durationMs
         */
        @Override
        public NotificationBuilder setTimeoutAfter(long durationMs) {
            super.setTimeoutAfter(durationMs);
            return this;
        }

        /**
         * If this notification is duplicative of a Launcher shortcut, sets the
         * {@link ShortcutInfoCompat#getId() id} of the shortcut, in
         * case the Launcher wants to hide the shortcut.
         *
         * <p><strong>Note:</strong>This field will be ignored by Launchers that don't support
         * badging or {@link ShortcutManagerCompat shortcuts}.
         *
         * @param shortcutId the {@link ShortcutInfoCompat#getId() id}
         *                   of the shortcut this notification supersedes
         */
        @Override
        public NotificationBuilder setShortcutId(String shortcutId) {
            super.setShortcutId(shortcutId);
            return this;
        }


        @Override
        public NotificationBuilder setBadgeIconType(int icon) {
            super.setBadgeIconType(icon);
            return this;
        }


        @Override
        public NotificationBuilder setGroupAlertBehavior(int groupAlertBehavior) {
            super.setGroupAlertBehavior(groupAlertBehavior);
            return this;
        }

        /**
         * Apply an extender to this notification builder. Extenders may be used to add
         * metadata or change options on this builder.
         *
         * @param extender
         */
        @Override
        public NotificationBuilder extend(NotificationCompat.Extender extender) {
            super.extend(extender);
            return this;
        }

        public int send(int notifyId) {
            return send(null,notifyId);
        }
        public int send(@Nullable String tag,int notifyId) {
//            NotificationManagerCompat.from(mContext).notify(tag, notifyId, build());
            return MrNotification.this.send(tag, notifyId, build());
        }

        public int send() {
            return MrNotification.this.send(build());
        }
    }
}

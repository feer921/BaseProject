package common.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;

import common.base.utils.Util;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/6/24<br>
 * Time: 18:04<br>
 * <P>DESC:
 * extends Intent,自已本身来调用启动Activity或者Service
 * 场景为：再使用Intent设置各种数据后，并不想立刻调用 Context # startActivity()等API，而是需要再次去设置
 * 可能需要的数据，则使用本类非常合适
 * </p>
 * ******************(^_^)***********************
 */
public class StarterIntent extends Intent {

    /**
     * Create an empty intent.
     */
    public StarterIntent() {
    }

    /**
     * Copy constructor.
     *
     * @param o
     */
    public StarterIntent(Intent o) {
        super(o);
    }


    public StarterIntent(String action) {
        super(action);
    }


    public StarterIntent(String action, Uri uri) {
        super(action, uri);
    }


    public StarterIntent(Context packageContext, Class<?> cls) {
        super(packageContext, cls);
    }


    public StarterIntent(String action, Uri uri, Context packageContext, Class<?> cls) {
        super(action, uri, packageContext, cls);
    }

    public void startActivity(Context context) {
        startActivity(context,false,-1,null);
    }

    public void startActivityForResult(Context activity, int requestCode) {
        startActivity(activity,true,requestCode,null);
//        if (activity != null) {
//            activity.startActivityFromFragment();
//        }
    }

    public void startActivity(Context context, Class targetActivityClass) {
        setClass(context, targetActivityClass);
        startActivity(context,false,-1,null);
    }
//    public void startActivityFromFragment() {
//
//    }

    /**
     * 注：在调用startActivity() API 之后，就不能 put数据了
     * @param context Context
     * @param needReturnResult true:需要被启动的Activity返回result数据
     * @param requestCode 如果needReturnResult 为true, 则需要启动Activity的请求码
     * @param extraOptionsBundle 附加的启动操作
     * 可能 抛出 android.content.ActivityNotFoundException
     */
    @SuppressLint("NewApi")
    public void startActivity(Context context, boolean needReturnResult, int requestCode, Bundle extraOptionsBundle) {
        if (context != null) {
            boolean isActivityContext = context instanceof Activity;
            if (needReturnResult && isActivityContext) {
                Activity theActivity = (Activity) context;
                if (extraOptionsBundle == null) {
                    theActivity.startActivityForResult(this,requestCode);
                }
                else {
                    if (Util.isCompateApi(16)) {
                        theActivity.startActivityForResult(this, requestCode, extraOptionsBundle);
                    }
                }
            }
            else {
                if (!isActivityContext) {
                    setFlags(FLAG_ACTIVITY_NEW_TASK);
                }
                if (extraOptionsBundle == null) {
                    context.startActivity(this);
                }
                else {
                    if (Util.isCompateApi(16)) {
                        context.startActivity(this, extraOptionsBundle);
                    }
                }
            }
        }
    }

    public ComponentName startService(Context context) {
        if (context != null) {
            return context.startService(this);
        }
        return null;
    }

    @RequiresApi(api = 26)
    public ComponentName startForegroundService(Context context) {
        if (context != null) {
            return context.startForegroundService(this);
        }
        return null;
    }

    @NonNull
    @Override
    public StarterIntent putExtra(String name, int value) {
        super.putExtra(name, value);
        return this;
    }



    /**
     * Set the data this intent is operating on.  This method automatically
     * clears any type that was previously set by {@link #setType} or
     * {@link #setTypeAndNormalize}.
     *
     * <p><em>Note: scheme matching in the Android framework is
     * case-sensitive, unlike the formal RFC. As a result,
     * you should always write your Uri with a lower case scheme,
     * or use {@link Uri#normalizeScheme} or
     * {@link #setDataAndNormalize}
     * to ensure that the scheme is converted to lower case.</em>
     *
     * @param data The Uri of the data this intent is now targeting.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #getData
     * @see #setDataAndNormalize
     * @see Uri#normalizeScheme()
     */
    @NonNull
    @Override
    public StarterIntent setData(@Nullable Uri data) {
         super.setData(data);
         return this;
    }

    /**
     * Normalize and set the data this intent is operating on.
     *
     * <p>This method automatically clears any type that was
     * previously set (for example, by {@link #setType}).
     *
     * <p>The data Uri is normalized using
     * {@link Uri#normalizeScheme} before it is set,
     * so really this is just a convenience method for
     * <pre>
     * setData(data.normalize())
     * </pre>
     *
     * @param data The Uri of the data this intent is now targeting.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #getData
     * @see #setType
     * @see Uri#normalizeScheme
     */
    @NonNull
    @Override
    public StarterIntent setDataAndNormalize(@NonNull Uri data) {
         super.setDataAndNormalize(data);
         return this;
    }

    /**
     * Set an explicit MIME data type.
     *
     * <p>This is used to create intents that only specify a type and not data,
     * for example to indicate the type of data to return.
     *
     * <p>This method automatically clears any data that was
     * previously set (for example by {@link #setData}).
     *
     * <p><em>Note: MIME type matching in the Android framework is
     * case-sensitive, unlike formal RFC MIME types.  As a result,
     * you should always write your MIME types with lower case letters,
     * or use {@link #normalizeMimeType} or {@link #setTypeAndNormalize}
     * to ensure that it is converted to lower case.</em>
     *
     * @param type The MIME type of the data being handled by this intent.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #getType
     * @see #setTypeAndNormalize
     * @see #setDataAndType
     * @see #normalizeMimeType
     */
    @NonNull
    @Override
    public StarterIntent setType(@Nullable String type) {
        super.setType(type);
        return this;
    }

    /**
     * Normalize and set an explicit MIME data type.
     *
     * <p>This is used to create intents that only specify a type and not data,
     * for example to indicate the type of data to return.
     *
     * <p>This method automatically clears any data that was
     * previously set (for example by {@link #setData}).
     *
     * <p>The MIME type is normalized using
     * {@link #normalizeMimeType} before it is set,
     * so really this is just a convenience method for
     * <pre>
     * setType(Intent.normalizeMimeType(type))
     * </pre>
     *
     * @param type The MIME type of the data being handled by this intent.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #getType
     * @see #setData
     * @see #normalizeMimeType
     */
    @NonNull
    @Override
    public StarterIntent setTypeAndNormalize(@Nullable String type) {
         super.setTypeAndNormalize(type);
         return this;
    }

    /**
     * (Usually optional) Set the data for the intent along with an explicit
     * MIME data type.  This method should very rarely be used -- it allows you
     * to override the MIME type that would ordinarily be inferred from the
     * data with your own type given here.
     *
     * <p><em>Note: MIME type and Uri scheme matching in the
     * Android framework is case-sensitive, unlike the formal RFC definitions.
     * As a result, you should always write these elements with lower case letters,
     * or use {@link #normalizeMimeType} or {@link Uri#normalizeScheme} or
     * {@link #setDataAndTypeAndNormalize}
     * to ensure that they are converted to lower case.</em>
     *
     * @param data The Uri of the data this intent is now targeting.
     * @param type The MIME type of the data being handled by this intent.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #setType
     * @see #setData
     * @see #normalizeMimeType
     * @see Uri#normalizeScheme
     * @see #setDataAndTypeAndNormalize
     */
    @NonNull
    @Override
    public StarterIntent setDataAndType(@Nullable Uri data, @Nullable String type) {
        super.setDataAndType(data, type);
        return this;
    }

    /**
     * (Usually optional) Normalize and set both the data Uri and an explicit
     * MIME data type.  This method should very rarely be used -- it allows you
     * to override the MIME type that would ordinarily be inferred from the
     * data with your own type given here.
     *
     * <p>The data Uri and the MIME type are normalize using
     * {@link Uri#normalizeScheme} and {@link #normalizeMimeType}
     * before they are set, so really this is just a convenience method for
     * <pre>
     * setDataAndType(data.normalize(), Intent.normalizeMimeType(type))
     * </pre>
     *
     * @param data The Uri of the data this intent is now targeting.
     * @param type The MIME type of the data being handled by this intent.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #setType
     * @see #setData
     * @see #setDataAndType
     * @see #normalizeMimeType
     * @see Uri#normalizeScheme
     */
    @NonNull
    @Override
    public StarterIntent setDataAndTypeAndNormalize(@NonNull Uri data, @Nullable String type) {
        super.setDataAndTypeAndNormalize(data, type);
        return this;
    }

    /**
     * Add a new category to the intent.  Categories provide additional detail
     * about the action the intent performs.  When resolving an intent, only
     * activities that provide <em>all</em> of the requested categories will be
     * used.
     *
     * @param category The desired category.  This can be either one of the
     *                 predefined Intent categories, or a custom category in your own
     *                 namespace.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #hasCategory
     * @see #removeCategory
     */
    @NonNull
    @Override
    public StarterIntent addCategory(String category) {
        super.addCategory(category);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The boolean data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanExtra(String, boolean)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, boolean value) {
         super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteExtra(String, byte)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, byte value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The char data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharExtra(String, char)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, char value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The short data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortExtra(String, short)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, short value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The long data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongExtra(String, long)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, long value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The float data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatExtra(String, float)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, float value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The double data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleExtra(String, double)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, double value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The String data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, String value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The CharSequence data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, CharSequence value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Parcelable data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, Parcelable value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Parcelable[] data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, Parcelable[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<Parcelable> data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayListExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        super.putParcelableArrayListExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<Integer> data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntegerArrayListExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        super.putIntegerArrayListExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<String> data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayListExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putStringArrayListExtra(String name, ArrayList<String> value) {
        super.putStringArrayListExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<CharSequence> data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayListExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        super.putCharSequenceArrayListExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Serializable data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getSerializableExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, Serializable value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The boolean array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, boolean[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, byte[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The short array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, short[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The char array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, char[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The int array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, int[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, long[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The float array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, float[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The double array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, double[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The String array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, String[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The CharSequence array data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, CharSequence[] value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.  The name must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Bundle data value.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getBundleExtra(String)
     */
    @NonNull
    @Override
    public StarterIntent putExtra(String name, Bundle value) {
        super.putExtra(name, value);
        return this;
    }

    /**
     * Copy all extras in 'src' in to this intent.
     *
     * @param src Contains the extras to copy.
     * @see #putExtra
     */
    @NonNull
    @Override
    public StarterIntent putExtras(@NonNull Intent src) {
        super.putExtras(src);
        return this;
    }

    /**
     * Add a set of extended data to the intent.  The keys must include a package
     * prefix, for example the app com.android.contacts would use names
     * like "com.android.contacts.ShowAll".
     *
     * @param extras The Bundle of extras to add to this intent.
     * @see #putExtra
     * @see #removeExtra
     */
    @NonNull
    @Override
    public StarterIntent putExtras(@NonNull Bundle extras) {
        super.putExtras(extras);
        return this;
    }

    /**
     * Completely replace the extras in the Intent with the given Bundle of
     * extras.
     *
     * @param extras The new set of extras in the Intent, or null to erase
     *               all extras.
     */
    @NonNull
    @Override
    public StarterIntent replaceExtras(@NonNull Bundle extras) {
        super.replaceExtras(extras);
        return this;
    }

    /**
     * Set special flags controlling how this intent is handled.  Most values
     * here depend on the type of component being executed by the Intent,
     * specifically the FLAG_ACTIVITY_* flags are all for use with
     * {@link Context#startActivity Context.startActivity()} and the
     * FLAG_RECEIVER_* flags are all for use with
     * {@link Context#sendBroadcast(Intent) Context.sendBroadcast()}.
     *
     * <p>See the
     * <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks and Back
     * Stack</a> documentation for important information on how some of these options impact
     * the behavior of your application.
     *
     * @param flags The desired flags.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #getFlags
     * @see #addFlags
     * @see #removeFlags
     */
    @NonNull
    @Override
    public StarterIntent setFlags(int flags) {
        super.setFlags(flags);
        return this;
    }

    /**
     * Add additional flags to the intent (or with existing flags value).
     *
     * @param flags The new flags to set.
     * @return Returns the same Intent object, for chaining multiple calls into
     * a single statement.
     * @see #setFlags
     * @see #getFlags
     * @see #removeFlags
     */
    @NonNull
    @Override
    public StarterIntent addFlags(int flags) {
        super.addFlags(flags);
        return this;
    }

    /**
     * (Usually optional) Set an explicit application package name that limits
     * the components this Intent will resolve to.  If left to the default
     * value of null, all components in all applications will considered.
     * If non-null, the Intent can only match the components in the given
     * application package.
     *
     * @param packageName The name of the application package to handle the
     *                    intent, or null to allow any application package.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #getPackage
     * @see #resolveActivity
     */
    @NonNull
    @Override
    public StarterIntent setPackage(@Nullable String packageName) {
        super.setPackage(packageName);
        return this;
    }

    /**
     * (Usually optional) Explicitly set the component to handle the intent.
     * If left with the default value of null, the system will determine the
     * appropriate class to use based on the other fields (action, data,
     * type, categories) in the Intent.  If this class is defined, the
     * specified class will always be used regardless of the other fields.  You
     * should only set this value when you know you absolutely want a specific
     * class to be used; otherwise it is better to let the system find the
     * appropriate class so that you will respect the installed applications
     * and user preferences.
     *
     * @param component The name of the application component to handle the
     *                  intent, or null to let the system find one for you.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #setClass
     * @see #setClassName(Context, String)
     * @see #setClassName(String, String)
     * @see #getComponent
     * @see #resolveActivity
     */
    @NonNull
    @Override
    public StarterIntent setComponent(@Nullable ComponentName component) {
        super.setComponent(component);
        return this;
    }

    /**
     * Convenience for calling {@link #setComponent} with an
     * explicit class name.
     *
     * @param packageContext A Context of the application package implementing
     *                       this class.
     * @param className      The name of a class inside of the application package
     *                       that will be used as the component for this Intent.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #setComponent
     * @see #setClass
     */
    @NonNull
    @Override
    public StarterIntent setClassName(@NonNull Context packageContext, @NonNull String className) {
        super.setClassName(packageContext, className);
        return this;
    }

    /**
     * Convenience for calling {@link #setComponent} with an
     * explicit application package name and class name.
     *
     * @param packageName The name of the package implementing the desired
     *                    component.
     * @param className   The name of a class inside of the application package
     *                    that will be used as the component for this Intent.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #setComponent
     * @see #setClass
     */
    @NonNull
    @Override
    public StarterIntent setClassName(@NonNull String packageName, @NonNull String className) {
        super.setClassName(packageName, className);
        return this;
    }

    /**
     * Convenience for calling {@link #setComponent(ComponentName)} with the
     * name returned by a {@link Class} object.
     *
     * @param packageContext A Context of the application package implementing
     *                       this class.
     * @param cls            The class name to set, equivalent to
     *                       <code>setClassName(context, cls.getName())</code>.
     * @return Returns the same Intent object, for chaining multiple calls
     * into a single statement.
     * @see #setComponent
     */
    @NonNull
    @Override
    public StarterIntent setClass(@NonNull Context packageContext, @NonNull Class<?> cls) {
        super.setClass(packageContext, cls);
        return this;
    }

    public void sendBroadcast(Context context) {
        if (context != null) {
            context.sendBroadcast(this);
        }
    }
}

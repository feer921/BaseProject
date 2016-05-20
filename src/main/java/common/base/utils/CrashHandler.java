package common.base.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Process;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * 程序崩溃处理者，记录崩溃日志
 * <br/>
 * 2015年12月23日-下午2:59:52
 * @author lifei
 */
public class CrashHandler implements UncaughtExceptionHandler {

    /** 记录标志. */
    private static final String TAG = "logex" + CommonConfigs.APP_BASE_PATH;

    /** 错误日志文件夹名称 . */
    private static final String DEBUE_DIRNAME = ".logex";

    /** CrashHandler实例. */
    private static CrashHandler instanec;

    /**
     * 初始化.
     * @param context
     */
    public static synchronized void initConfig(Context context) {
        if (null == instanec) {
            instanec = new CrashHandler(context);
        }
    }

    /** 程序的Context对象. */
    private Context mContext;
    /** 用于格式化日期,作为日志文件名的一部分. */
    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault());

    /** 系统默认的UncaughtException处理类. */
    @SuppressWarnings("unused")
    private UncaughtExceptionHandler mDefaultHandler;

    /** 进程名字. */
    private String mProcessName;

    /** 保证只有一个CrashHandler实例. */
    private CrashHandler(Context context) {
        mContext = context.getApplicationContext();
        // 获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);

        mProcessName = PackageManagerUtil.getProcessNameByPid(mContext, Process.myPid());
        CommonLog.d(TAG, "CrashHandler is init! ProcessName:", mProcessName);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理.
     */
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        android.util.Log.d(TAG, "---------------uncaughtException start---------------");
        android.util.Log.d(TAG, "process [" + mProcessName + "],is abnormal! CxConfig.DEBUG:" + CommonLog.ISDEBUG);
        try {
            handleException(thread, throwable);
        } catch (Exception ex) {
            android.util.Log.d(TAG, "uncaughtException,ex:", ex);
        }
        if (CommonLog.ISDEBUG) {
            android.util.Log.e(TAG, "uncaughtException,throwable:", throwable);
        }
        if (PackageManagerUtil.isMainProcess(mContext, mProcessName)) {
            // 提示
            Toast.makeText(mContext, "~~你弄坏了我~~", Toast.LENGTH_SHORT).show();

            ComponentName componentName = PackageManagerUtil.getTheProcessBaseActivity(mContext);
            if (componentName != null) {
                Intent intent = new Intent();
                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                // HjLog.d(TAG,
                intent.setComponent(componentName);

                // mContext.startActivity(intent);
                AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
                PendingIntent restartIntent = PendingIntent.getActivity(mContext, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, restartIntent); // 500毫秒钟后重启应用
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                CommonLog.e(TAG, "error : ", e);
            }
        }
        android.util.Log.d(TAG, "---------------uncaughtException end---------------");
        //
        Process.killProcess(Process.myPid());
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param rhrowable
     * @return true:如果处理了该异常信息;否则返回false.
     * @throws IOException
     */
    private boolean handleException(Thread thread, Throwable rhrowable) throws IOException {
        String logdirPath = null;
        // 记录文件文件夹
        if (Environment.getExternalStorageDirectory() != null) {
            logdirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DEBUE_DIRNAME + File.separator;
        } else {
            logdirPath = mContext.getCacheDir().getAbsolutePath() + File.separator + DEBUE_DIRNAME + File.separator;
        }
        File logdir = new File(logdirPath);
        if (logdir.exists()) {
            if (!logdir.isDirectory()) {
                boolean b = logdir.delete();
                android.util.Log.d(TAG, "handleException,delete:" + b);
                b = logdir.mkdirs();
                android.util.Log.d(TAG, "handleException,mkdirs:" + b);
            } else {
                if (!CommonLog.ISDEBUG) {
                    try {
                        clearLogexMax(logdir, 10);
                    } catch (Exception ex) {
                        android.util.Log.e(TAG, "handleException,ex:" + ex);
                    }
                }
            }
        } else {
            logdir.mkdirs();
        }
        // 本次记录文件名
        Date date = new Date(); // 当前时间
        String logFileName = formatter.format(date) + String.format("[%s-%d]", thread.getName(), thread.getId());
        File logex = new File(logdir, logFileName);
        // 写入异常到文件中
        FileWriter fw = new FileWriter(logex, true);
        fw.write("\r\nProcess[" + mProcessName + "," + Process.myPid() + "],CxConfig.DEBUG:" + CommonLog.ISDEBUG); // 进程信息，线程信息
        fw.write("\r\n" + thread + "(" + thread.getId() + ")"); // 进程信息，线程信息
        fw.write("\r\nTime stamp：" + date); // 日期
        // 打印调用栈
        PrintWriter printWriter = new PrintWriter(fw);
        rhrowable.printStackTrace(printWriter);
        Throwable cause = rhrowable.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        //
        fw.write("\r\n");
        fw.flush();
        printWriter.close();
        fw.close();
        return true;
    }

    /**
     * 清理日志,限制日志数量.
     * @param logdir
     * @param max
     *            自多保存的日志数量
     */
    private void clearLogexMax(File logdir, int max) {
        File[] logList = logdir.listFiles();
        final int length = logList.length;
        android.util.Log.d(TAG, "clearLogexMax,length:" + length + ",max:" + max);
        if (length > max) {
            // 按照时间排序
            Comparator<? super File> comparator = new Comparator<File>() {
                @Override
                public int compare(File lhs, File rhs) {
                    // long result = lhs.lastModified() - rhs.lastModified();
                    // return (int) (result);
                    return lhs.getName().compareToIgnoreCase(rhs.getName());
                }
            };
            Arrays.sort(logList, comparator);
            // 保留
            for (int i = max; i < length; i++) {
                try {
                    File logF = logList[i];
                    boolean b = logF.delete();
                    android.util.Log.d(TAG, "clearLogexMax,log:" + logF.getName() + ",b:" + b);
                } catch (Exception ex) {
                    android.util.Log.e(TAG, "clearLogexMax,ex:" + ex);
                }
            }
        }
    }

    /**
     * 获取错误信息.
     * @param ex
     * @return 返回错误信息
     */
    public static String getCrashInfo(Throwable ex) {
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        return result;
    }
}

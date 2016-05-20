package common.base.utils;


import common.base.BuildConfig;

/**
 * 日志输出
 * <br/>
 * 2015年12月23日-上午11:41:07
 * @author lifei
 */
public final class CommonLog {
//    public final static boolean ISDEBUG = BuildConfig.DEBUG;
    public static boolean ISDEBUG = BuildConfig.DEBUG;
    public static void w(String tag, String content) {
        if (ISDEBUG) {
            android.util.Log.w(tag, content);
        }
    }

    public static void w(final String tag, Object... objs) {
        if (ISDEBUG) {
            android.util.Log.w(tag, getInfo(objs));
        }
    }

    public static void i(String tag, String content) {
        if (ISDEBUG) {
            android.util.Log.i(tag, content);
        }
    }

    public static void i(final String tag, Object... objs) {
        if (ISDEBUG) {
            android.util.Log.i(tag, getInfo(objs));
        }
    }

    public static void d(String tag, String content) {
        if (ISDEBUG) {
            android.util.Log.d(tag, content);
        }
    }

    public static void d(final String tag, Object... objs) {
        if (ISDEBUG) {
            android.util.Log.d(tag, getInfo(objs));
        }
    }

    public static void e(String tag, String content) {
        if (ISDEBUG) {
            android.util.Log.e(tag, content);
        }
    }

    public static void e(String tag, String content, Throwable e) {
        if (ISDEBUG) {
            android.util.Log.e(tag, content, e);
        }
    }

    public static void e(final String tag, Object... objs) {
        if (ISDEBUG) {
            android.util.Log.e(tag, getInfo(objs));
        }
    }

    private static String getInfo(Object... objs) {
        if (objs == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Object object : objs) {
            sb.append(object);
        }
        return sb.toString();
    }

    public static void sysOut(Object msg) {
        if (ISDEBUG) {
            System.out.println(msg);
        }
    }

    public static void sysErr(Object msg) {
        if (ISDEBUG) {
            System.err.println(msg);
        }
    }
}

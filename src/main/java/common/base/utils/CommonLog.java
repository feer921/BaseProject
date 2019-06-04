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

    public static void logEnable(boolean toEnable) {
        ISDEBUG = toEnable;
    }
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

    public static void e(String logTag, String logContent, boolean appendStackInfo) {
        if (!appendStackInfo) {
            e(logTag,logContent);
        }
        else {
            StringBuilder sb = new StringBuilder();
            appendStack(sb);
            sb.append(logContent);
            e(logTag, sb.toString());
        }
    }
    public static void i(String logTag, String logContent, boolean appendStackInfo) {
        if (!appendStackInfo) {
            i(logTag,logContent);
        }
        else {
            StringBuilder sb = new StringBuilder();
            appendStack(sb);
            sb.append(logContent);
            i(logTag, sb.toString());
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
    private static final int START_STACK_INDEX = 4;
    private static final int PRINT_STACK_COUNT = 2;

    private static void appendStack(StringBuilder sb) {
        StackTraceElement[] stacks = Thread.currentThread().getStackTrace();
        if (stacks != null && stacks.length > START_STACK_INDEX) {
            int lastIndex = Math.min(stacks.length - 1, START_STACK_INDEX + PRINT_STACK_COUNT);
            for (int i=lastIndex; i >= START_STACK_INDEX; i--) {
                if (stacks[i] == null) {
                    continue;
                }

                String fileName = stacks[i].getFileName();
                if (fileName != null) {
                    int dotIndx = fileName.indexOf('.');
                    if (dotIndx > 0) {
                        fileName = fileName.substring(0, dotIndx);
                    }
                }

                sb.append(fileName);
                sb.append('(');
                sb.append(stacks[i].getLineNumber());
                sb.append(")");
                sb.append("->");
            }
            StackTraceElement stackTraceElement = stacks[START_STACK_INDEX];
            if (stackTraceElement != null) {
                sb.append(stackTraceElement.getMethodName());
            }
        }
        sb.append('\n');
    }

}

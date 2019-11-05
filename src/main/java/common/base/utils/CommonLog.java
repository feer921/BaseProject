package common.base.utils;


import android.util.Log;
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
        StringBuilder sb = new StringBuilder();
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

    /**
     * 由于Android系统对日志长度有限制的，最大长度为4K（注意是字符串的长度），超过这个范围的自动截断，会出现打印不全的情况
     * @param logLevelFlagChar Log的输出级别字符标志；取值: 'd'; 'i'; 'e'; 'w'; 'v'
     * @param logTag Log的tag
     * @param logContent Log的内容
     */
    public static void fullLog(char logLevelFlagChar, String logTag, String logContent) {
        if (ISDEBUG && !CheckUtil.isEmpty(logContent)) {
            int segmentSize = 3 * 1024;
            int len = logContent.length();
            if (len <= segmentSize) {
                log(logLevelFlagChar,logTag,logContent);
            }
            else {
                while (logContent.length() > segmentSize) {
                    String suitableLogContent = logContent.substring(0, segmentSize);
                    log(logLevelFlagChar, logTag, suitableLogContent);
                    logContent = logContent.substring(segmentSize);
//                    StringBuilder sb = new StringBuilder(logContent);
//                    sb.delete(0, segmentSize);
//                    logContent = sb.toString();
//                    sb.append(logContent, segmentSize, logContent.length());
//                    logContent = logContent.replace(logContent, "");//这个效率低
                }
                log(logLevelFlagChar, logTag, logContent);
            }
        }
    }

    public static void iFullLog(String logTag, String logContent) {
        fullLog('i', logTag, logContent);
    }

    public static void iFullLog(String logTag, Object... logObj) {
        iFullLog(logTag, getInfo(logObj));
    }
    public static void log(char logLevelFlagChar, String logTag, String logContent) {
        if (ISDEBUG) {
            switch (logLevelFlagChar) {
                case 'i':
                    i(logTag, logContent);
                    break;
                case 'e':
                    e(logTag,logContent);
                    break;
                case 'd':
                    d(logTag,logContent);
                    break;
                case 'w':
                    w(logTag,logContent);
                    break;
                case 'v':
                    Log.v(logTag, logContent);
                    break;
            }
        }
    }
}

package common.base.utils;

/**
 * ******************(^_^)***********************<br>
 * User: 11776610771@qq.com<br>
 * Date: 2017/10/12<br>
 * Time: 9:52<br>
 * <P>DESC:
 * 统计开始到结束的所用的时间
 * 注意，这个只适合单线程
 * </p>
 * ******************(^_^)***********************
 */

public class CountTimer {
    private static final String TAG = "CountTimer";
    private static long startTime;

    public static void start() {
        startTime = System.currentTimeMillis();
    }

    public static void end() {
        end(null);
    }
    public static void end(String logTag) {
        end(logTag, null);
    }

    public static void end(String logTag, String logExtraInfo) {
        end(logTag, logExtraInfo, 1);
    }
    /**
     * 结束计时，并且输出打印信息
     * @param logTag Log 的tag,如果为null 则用本类的TAG="CountTimer"
     * @param logExtraInfo Log的内容 ： 如果为null,则直接是"-->end() experience time: "，否则，为 "xxxx() -->end() experience time: "
     * @param logLevel Log输出级别，1：i; 2:w；3：e.
     */
    public static void end(String logTag, String logExtraInfo, int logLevel) {
        long endTiem = System.currentTimeMillis();
        long wasteTiem = endTiem - startTime;
        String finalLogTag = logTag == null ? TAG : logTag;
        String logInfoContent = " --> waste time: "+ wasteTiem;
        if (logExtraInfo != null) {
            logInfoContent = logExtraInfo + logInfoContent;
        }
        switch (logLevel) {
            case 1:
                CommonLog.i(finalLogTag, logInfoContent);
                break;
            case 2:
                CommonLog.w(finalLogTag, logInfoContent);
                break;
            case 3:
                CommonLog.e(finalLogTag, logInfoContent);
                break;
        }
    }
}

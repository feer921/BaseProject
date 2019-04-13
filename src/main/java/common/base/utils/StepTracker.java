package common.base.utils;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/4/11<br>
 * Time: 16:51<br>
 * <P>DESC:
 * 运行步骤的步次tracker
 * </p>
 * ******************(^_^)***********************
 */
public class StepTracker {
    private static int curStep;
    private static final String TAG = "StepTracker";

    private static void trackStep(String stepTag, String logMsg,int logLevel) {
        curStep++;
        if (CheckUtil.isEmpty(stepTag)) {
            stepTag = TAG;
        }
        if (logMsg == null) {
            logMsg = "";
        }
        logMsg += " curStep = " + curStep;
        switch (logLevel) {
            case 1:
                CommonLog.i(stepTag, logMsg);
                break;
            case 2:
                CommonLog.w(stepTag,logMsg);
                break;
            case 3:
                CommonLog.e(stepTag,logMsg);
                break;
            case 4:
                CommonLog.d(stepTag, logMsg);
                break;
            default:
                CommonLog.i(stepTag, logMsg);
                break;
        }
    }
    public static void trackStepLogW(String stepTag, String logMsg) {
        trackStep(stepTag,logMsg,2);
    }
    public static void trackStepLogD(String stepTag, String logMsg) {
        trackStep(stepTag,logMsg,4);
    }
    public static void trackStepLogI(String stepTag, String logMsg) {
        trackStep(stepTag,logMsg,1);
    }
    public static void trackStepLogE(String stepTag, String logMsg) {
        trackStep(stepTag,logMsg,3);
    }
}

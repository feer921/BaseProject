package common.base.utils;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/11/9<br>
 * Time: 12:11<br>
 * <P>DESC:
 * 费时统计标记者
 * </p>
 * ******************(^_^)***********************
 */
public class WasteTimeMarker {
    private final String TAG = "WasteTimeMarker";
    private String logTag;
    private long curStartTime;
    public void start(String startLogTag) {
        curStartTime = System.currentTimeMillis();
        logTag = startLogTag;
    }

    public long end() {
        long endTime = System.currentTimeMillis();
        long wasteTime = endTime - curStartTime;
        CommonLog.i(logTag == null ? TAG : logTag, " waste time : " + wasteTime);
        return wasteTime;
    }

    public long end(String extraLogInfo) {
        long endTime = System.currentTimeMillis();
        long wasteTime = endTime - curStartTime;
        CommonLog.i(logTag, extraLogInfo + " waste time: " + wasteTime);
        return wasteTime;
    }
    public long end(String extraLogInfo, int logLevel) {
        long endTime = System.currentTimeMillis();
        long wasteTime = endTime - curStartTime;
        String logTag = this.logTag;
        if (Util.isEmpty(logTag)) {
            logTag = TAG;
        }
        String finalExtraInfo = " waste time : " + wasteTime;
        if (!Util.isEmpty(extraLogInfo)) {
            finalExtraInfo = extraLogInfo + finalExtraInfo;
        }
        switch (logLevel) {
            case 1:
                CommonLog.i(logTag, finalExtraInfo);
                break;
            case 2:
                CommonLog.w(logTag, finalExtraInfo);
                break;
            case 3:
                CommonLog.e(logTag, finalExtraInfo);
                break;
        }
        return wasteTime;
    }
}

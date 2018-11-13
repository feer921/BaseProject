package common.base.utils;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/11/9<br>
 * Time: 12:11<br>
 * <P>DESC:
 * 费时统计标记者
 * 适合在同一个线程中使用
 * </p>
 * ******************(^_^)***********************
 */
public class WasteTimeMarker {
    private final String TAG = "WasteTimeMarker";
    private String markTag;
    private long curStartTime;
    public void start(String startLogTag) {
        curStartTime = System.currentTimeMillis();
        markTag = startLogTag;
    }

    public long end() {
        long endTime = System.currentTimeMillis();
        long wasteTime = endTime - curStartTime;
        CommonLog.w(TAG, markTag + " --> waste time : " + wasteTime);
        return wasteTime;
    }

    public long end(String logTag,String extraLogInfo) {
        long endTime = System.currentTimeMillis();
        long wasteTime = endTime - curStartTime;
        String finalLogTag = logTag == null || "".equals(logTag) ? TAG : logTag;
        CommonLog.w(finalLogTag, extraLogInfo + "-->" + markTag + "--> waste time:" + wasteTime);
        return wasteTime;
    }
    public long end(String extraLogInfo) {
        long endTime = System.currentTimeMillis();
        long wasteTime = endTime - curStartTime;
        CommonLog.i(markTag, extraLogInfo + " waste time: " + wasteTime);
        return wasteTime;
    }
    public long end(String logTag, String extraLogInfo, int logLevel) {
        long endTime = System.currentTimeMillis();
        long wasteTime = endTime - curStartTime;
        String finalLogTag = logTag == null || "".equals(logTag) ? TAG : logTag;

        String finalExtraInfo = markTag + " --> waste time : " + wasteTime;
        if (!Util.isEmpty(extraLogInfo)) {
            finalExtraInfo = extraLogInfo + finalExtraInfo;
        }
        switch (logLevel) {
            case 1:
                CommonLog.i(finalLogTag, finalExtraInfo);
                break;
            case 2:
                CommonLog.w(finalLogTag, finalExtraInfo);
                break;
            case 3:
                CommonLog.e(finalLogTag, finalExtraInfo);
                break;
        }
        return wasteTime;
    }
}

package common.base.utils;

import java.util.HashMap;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/11/9<br>
 * Time: 16:22<br>
 * <P>DESC:
 * 多TAG的标记开始和耗时时间
 * 后续考虑多线程问题
 * </p>
 * ******************(^_^)***********************
 */
public class MultyWasteTimeMarker {
    private static volatile HashMap<String, Long> tagTimeInfos = new HashMap<>();
    private static final String TAG = "MultyWasteTimeMarker";

    public static void start(String markTag) {
        long startTime = System.currentTimeMillis();
        if (markTag == null || "".equals(markTag)) {
            return;
        }
        tagTimeInfos.put(markTag, startTime);
    }

    public static long markEnd(String markTag) {
        long theEndTime = System.currentTimeMillis();
        if (markTag == null) {
            return 0;
        }
        Long startTimeOfTheTag = tagTimeInfos.get(markTag);
        if (startTimeOfTheTag != null) {
            long wasteTime = theEndTime - startTimeOfTheTag;
            CommonLog.i(TAG, markTag + "--> waste time：" + wasteTime);
            return wasteTime;
        }
        return 0;
    }
    public static long markEnd(String markTag,String logTag) {
        long theEndTime = System.currentTimeMillis();
        if (markTag == null) {
            return 0;
        }
        Long startTimeOfTheTag = tagTimeInfos.get(markTag);
        if (startTimeOfTheTag != null) {
            long wasteTime = theEndTime - startTimeOfTheTag;
            String finalLogTag = logTag == null || "".equals(logTag) ? TAG : logTag;
            CommonLog.i(finalLogTag, markTag + "--> waste time: " + wasteTime);
            return wasteTime;
        }
        return 0;
    }
    public static long markEnd(String markTag,String logTag, String logExtraInfo) {
        long theEndTime = System.currentTimeMillis();
        if (markTag == null) {
            return 0;
        }
        Long startTimeOfTheTag = tagTimeInfos.get(markTag);
        if (startTimeOfTheTag != null) {
            long wasteTime = theEndTime - startTimeOfTheTag;
            String finalLogTag = logTag == null || "".equals(logTag) ? TAG : logTag;
            if (logExtraInfo == null) {
                logExtraInfo = "";
            }
            CommonLog.i(finalLogTag, markTag + logExtraInfo + " waste time: " + wasteTime);
            return wasteTime;
        }
        return 0;
    }

    public static void clear() {
        if (tagTimeInfos != null) {
            tagTimeInfos.clear();
        }
    }
}

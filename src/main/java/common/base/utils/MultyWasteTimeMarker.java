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


    public static void start(String tag) {
        if (tag == null || "".equals(tag)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        tagTimeInfos.put(tag, startTime);
    }

    public static long markEnd(String tag) {
        long theEndTime = System.currentTimeMillis();
        if (tag == null) {
            return 0;
        }
        Long startTimeOfTheTag = tagTimeInfos.get(tag);
        if (startTimeOfTheTag != null) {
            long wasteTime = theEndTime - startTimeOfTheTag;
            CommonLog.i(tag, " waste time: " + wasteTime);
            return wasteTime;
        }
        return 0;
    }
    public static long markEnd(String tag, String logExtraInfo) {
        long theEndTime = System.currentTimeMillis();
        if (tag == null) {
            return 0;
        }
        Long startTimeOfTheTag = tagTimeInfos.get(tag);
        if (startTimeOfTheTag != null) {
            long wasteTime = theEndTime - startTimeOfTheTag;
            CommonLog.i(tag, logExtraInfo + " waste time: " + wasteTime);
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

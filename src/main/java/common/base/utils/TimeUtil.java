package common.base.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 关于时间信息的工具类
 * <br/>
 * 2015年12月25日-下午2:53:50
 * @author lifei
 */
public class TimeUtil {
    /**
     * 一般的格式化时间样式：年月日 时分秒 eg.: 2016-09-09 13:28:20
     */
    public static final String NORMAL_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 只需要年月日的时间格式 eg.: 2016-09-09
     */
    public static final String YMD_TIME_FORMAT = "yyyy-MM-dd";
    /**
     * 
     * @param timePattern 时间正则格式 eg. yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getFormatTimeForNow(String timePattern){
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern,Locale.getDefault());
        return sdf.format(new Date());
    }

    /**
     * 获取当前时间
     *以yyyy-MM-dd HH:mm:ss eg.: 1988-11-08 18:08:08的方式显示
     * @return
     */
    public static String getCurTimeStr() {
        return getFormatTimeForNow(NORMAL_TIME_FORMAT);
    }

    public static String formatMillsTimes(String millsTime, String timeFormat) {
        if (millsTime == null) {
            return "";
        }
        if (timeFormat == null) {
            timeFormat = NORMAL_TIME_FORMAT;
        }
        int serverTimeLen = millsTime.length();
        long localSystemTime = System.currentTimeMillis();
        int localSystemTimeLen = (localSystemTime + "").length();
        int timeGap = localSystemTimeLen - serverTimeLen;
        String appendedZero = "";
        while (timeGap-- > 0) {
            appendedZero += "0";
        }
        millsTime+= appendedZero;
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat,Locale.getDefault());
        return sdf.format(convetStr2Date(millsTime));
    }

    /**
     * 以年月日 时分秒 的样式{@linkplain #NORMAL_TIME_FORMAT}格式化
     * @param millsTime
     * @return
     */
    public static String formatMillsTimes(String millsTime) {
        return formatMillsTimes(millsTime, NORMAL_TIME_FORMAT);
    }
    public static Date convetStr2Date(String dateStr) {
        Date theDate = new Date(Long.parseLong(dateStr));
        return theDate;
    }
}

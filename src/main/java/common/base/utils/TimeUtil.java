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
        return getFormatTimeForNow("yyyy-MM-dd HH:mm:ss");
    }

    public static String convertServerTime(String serverTime, String timeFormat) {
        if (serverTime == null) {
            return "";
        }
        if (timeFormat == null) {
            return serverTime;
        }
        int serverTimeLen = serverTime.length();
        long localSystemTime = System.currentTimeMillis();
        int localSystemTimeLen = (localSystemTime + "").length();
        int timeGap = localSystemTimeLen - serverTimeLen;
        String appendedZero = "";
        while (timeGap-- > 0) {
            appendedZero += "0";
        }
        serverTime+= appendedZero;
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat,Locale.getDefault());
        return sdf.format(convetStr2Date(serverTime));
    }

    public static Date convetStr2Date(String dateStr) {
        Date theDate = new Date(Long.parseLong(dateStr));
        return theDate;
    }
}

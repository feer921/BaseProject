package common.base.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
    public static String geNowTimeStr() {
        return getFormatTimeForNow(NORMAL_TIME_FORMAT);
    }

    public static String formatMillsTimes(String millsTime, String timeFormat) {
        if (millsTime == null) {
            return "";
        }
        if (timeFormat == null) {
            timeFormat = NORMAL_TIME_FORMAT;
        }
//        int serverTimeLen = millsTime.length();
//        long localSystemTime = System.currentTimeMillis();
//        int localSystemTimeLen = (localSystemTime + "").length();
//        int timeGap = localSystemTimeLen - serverTimeLen;
//        String appendedZero = "";
//        while (timeGap-- > 0) {
//            appendedZero += "0";
//        }
//        millsTime+= appendedZero;
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat,Locale.getDefault());
        return sdf.format(convetStr2Date(diffLocalMillisTimeAndAppend0(millsTime)));
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

    /***
     * 将一个毫秒表示的时间字符串与系统本地的毫秒时间字符串比较，如果参数的毫秒时间比本地的毫秒时间字符数短，则补全相差数量的“0”字符
     * 以供本地系统能正确解析完整时间
     * @param maybeShortMillisTime 字符长度可能短于本地的毫秒时间字符串的毫秒时间字符串
     * @return 原字符串后补全"0"的新毫秒时间字符串
     */
    private static String diffLocalMillisTimeAndAppend0(String maybeShortMillisTime) {
        String resultMillisTime = "";
        if (maybeShortMillisTime != null && maybeShortMillisTime.trim().length() > 0) {
            String localMillisTime = System.currentTimeMillis() + "";
            resultMillisTime = maybeShortMillisTime;
            int maybeShortMillisTimeStrLen = maybeShortMillisTime.length();
            int timeStrLenGap = localMillisTime.length() - maybeShortMillisTimeStrLen;
            String appendedZero = "";
            while (timeStrLenGap-- > 0) {
                appendedZero += "0";
            }
            resultMillisTime += appendedZero;
        }
        return resultMillisTime;
    }

    /***
     * 根据一个毫秒时间字符串，获取这个时间的二维信息描述
     * 这里只获取1、该时间距离本地系统当前时间是今天、昨天、然后星期；2、月-日信息
     * 注：如果是服务器给的时间，则本地去获取是否为今天、昨天，有不准的风险，因为当前系统时间用户可以随便调整，导致比较时间基线变化
     * @param theGiveMillisTime 所给的毫秒时间串
     * @return new String[]{"今天","11-25"}
     */
    public static String[] getTime2DDescInfos(String theGiveMillisTime) {
//        String[] twoDimensionDesc = {
//          "",""
//        };
//        String standardMillisTime = diffLocalMillisTimeAndAppend0(theGiveMillisTime);
//        long standardMillis = 0;
//        try {
//            standardMillis = Long.parseLong(standardMillisTime);
//            Calendar calendar = Calendar.getInstance();//这里拿到的也就是系统当前日历时间
//            long nowMillisTime = System.currentTimeMillis();//系统当前毫秒时间：距离(UTC时间)1970-1-1 00:00:00的毫秒数
//            //本地系统的时间所在时区与UTC对比偏移的毫秒数，比如，当前系统时区为在中国，则所在时区为8，系统初始时间则为1970-1-1 08:00:00开始计算
//            int curTimeZoneRawOffsetMillis = calendar.getTimeZone().getRawOffset();
//
//            //就是系统当前时间 距离所在时区的初始时间 偏移了多少天，说白了就是现在已经过了多少天
//            long daysOffsetToday = (nowMillisTime + curTimeZoneRawOffsetMillis) / 86400000;
//            //所给出的时间距离 所在时区的初始时间 偏移了多少天，说白了就是所给出的时间已经过了多少天
//            long theTimeOffsetDays = (standardMillis + curTimeZoneRawOffsetMillis)/86400000;
//            long gapDays = theTimeOffsetDays - daysOffsetToday;
//            calendar.setTimeInMillis(standardMillis);//把日历切换到参数所给的时间
//            String desc1 = "";
//            if (gapDays == 0) {
//                desc1 = "今天";
//            } else if (gapDays == -1) {
//                desc1 = "昨天";
//            } else if (gapDays == -2) {
//                desc1 = "前天";
//            }
//            else{
//                //拿到周几的信息
//                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//                desc1 = dayOfWeekDesc(dayOfWeek);
//            }
//            int minute = calendar.get(Calendar.MINUTE);
//            String desc2 = calendar.get(Calendar.HOUR_OF_DAY) + ":" + (minute < 10 ? "0" + minute : minute);
//            twoDimensionDesc[0] = desc1;
//            twoDimensionDesc[1] = desc2;
//        } catch (Exception ignored) {
//
//        }
        return getTime2DDescInfos(theGiveMillisTime,3,true);
    }

    /**
     * 得到所给的参数时间与当前系统今天的间隔天数
     * @param theMillisTime 所给的毫秒时间 注该毫秒需要与Java系统毫秒时间位数一致
     * @return 0:所给的时间即为【今天】；1:所给的比今天还多一天，即为【明天】；2：后天；-1：所给的时间比今天少一天即为【昨天】；-2：前天;
     */
    public static long getDiffNowDayNum(String theMillisTime){
        Calendar nowCalendar = Calendar.getInstance();//这里拿到的也就是系统当前日历时间
        //本地系统的时间所在时区与UTC对比偏移的毫秒数，比如，当前系统时区为在中国，则所在时区为8，系统初始时间则为1970-1-1 08:00:00开始计算
        //则偏移的毫秒数应该为 8(时) * 60(分) * 60(秒) * 1000(毫秒)
        int curTimeZoneRawOffsetMillis = nowCalendar.getTimeZone().getRawOffset();
        //就是系统当前时间 距离所在时区的初始时间 偏移了多少天，说白了就是现在已经过了多少天
        long daysOffsetToday = (System.currentTimeMillis() + curTimeZoneRawOffsetMillis) / 86400000;

        long theTimeMilliSecends = Long.parseLong(theMillisTime);
        //计算参数中所给的时间距离所在时区的初始时间 偏移了多少天，说白了就是theMillisTime这个时间已经过了多少天
        long theTimeOffsetDays = (theTimeMilliSecends + curTimeZoneRawOffsetMillis)/86400000;
        return theTimeOffsetDays - daysOffsetToday;//参数时间 的已过天数 - 现在已过的天数;
    }

    private static String dayOfWeekDesc(int dayOfWeek) {
        String desc = "周一";
        switch (dayOfWeek) {
            case Calendar.MONDAY:
                break;
            case Calendar.TUESDAY:
                desc = "周二";
                break;
            case Calendar.WEDNESDAY:
                desc = "周三";
                break;
            case Calendar.THURSDAY:
                desc = "周四";
                break;
            case Calendar.FRIDAY:
                desc = "周五";
                break;
            case Calendar.SATURDAY:
                desc = "周六";
                break;
            case Calendar.SUNDAY:
                desc = "周日";
                break;
        }
        return desc;
    }

    /***
     * 根据一个毫秒时间字符串，获取这个时间的二维信息描述
     * 这里只获取1、该时间距离本地系统当前时间是今天、昨天、然后星期；2、月-日信息
     * 注：如果是服务器给的时间，则本地去获取是否为今天、昨天，有不准的风险，因为当前系统时间用户可以随便调整，导致比较时间基线变化
     * @param theGiveMillisTime 所给的毫秒时间串
     * @param descTodayAndAfterNums 最好为2-3 要求描述含“今天在内的这种描述法，要描述几个，即：如果要描述三个，则为[今天、昨天、前天]，依所传参数类推
     * @param descDateAfterAWeek 除去前面描述了 ”今天“、”昨天”、“前天”后，距离一星期外的时间是否描述为年月日.为true时：以
     * @return new String[]{"今天","11-25"}
     */
    public static String[] getTime2DDescInfos(String theGiveMillisTime,int descTodayAndAfterNums,boolean descDateAfterAWeek) {
        String[] twoDimensionDesc = {
                "",""
        };
        String standardMillisTime = diffLocalMillisTimeAndAppend0(theGiveMillisTime);
        long standardMillis = 0;
        try {
            standardMillis = Long.parseLong(standardMillisTime);
            Calendar calendar = Calendar.getInstance();//这里拿到的也就是系统当前日历时间
            long nowMillisTime = System.currentTimeMillis();//系统当前毫秒时间：距离(UTC时间)1970-1-1 00:00:00的毫秒数
            //本地系统的时间所在时区与UTC对比偏移的毫秒数，比如，当前系统时区为在中国，则所在时区为8，系统初始时间则为1970-1-1 08:00:00开始计算
            int curTimeZoneRawOffsetMillis = calendar.getTimeZone().getRawOffset();

            //就是系统当前时间 距离所在时区的初始时间 偏移了多少天，说白了就是现在已经过了多少天
            long daysOffsetToday = (nowMillisTime + curTimeZoneRawOffsetMillis) / 86400000;
            //所给出的时间距离 所在时区的初始时间 偏移了多少天，说白了就是所给出的时间已经过了多少天
            long theTimeOffsetDays = (standardMillis + curTimeZoneRawOffsetMillis)/86400000;
            long gapDays = theTimeOffsetDays - daysOffsetToday;
            calendar.setTimeInMillis(standardMillis);//把日历切换到参数所给的时间
            String desc1 = "";

            if (gapDays > -descTodayAndAfterNums) {//用来描述 距离今天(含)的总共几天
                int dayGapPositiveIndex = (int) (gapDays * -1);
                desc1 = dayDesc[dayGapPositiveIndex];
            }
            else{//eg.:前天之前了
                if (descDateAfterAWeek) {//如果需要在时间过一周后用年月日描述
                    if (gapDays > -7) {
                        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                        desc1 = dayOfWeekDesc(dayOfWeek);
                        descDateAfterAWeek = false;
                    }
                    else{
                        descDateAfterAWeek = true;
                    }
                }
//                else{//再描述了“今天”、“昨天”、“前天”等后，都一律用“年、月、日”描述
//
//                }
                if (descDateAfterAWeek) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(calendar.get(Calendar.YEAR)).append("年")
                            .append(calendar.get(Calendar.MONTH)).append("月")
                            .append(calendar.get(Calendar.DATE)).append("日");
                    desc1 = sb.toString();
                }
            }
//            else if(gapDays > -7){//除去，"今天"，”昨天“，”前天“ 外剩下的在一周之前内的用 ["周X”][10：30]描述
//                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
//                desc1 = dayOfWeekDesc(dayOfWeek);
//            }
//            else{//用[年月日] [10:30] 描述了
//                StringBuilder sb = new StringBuilder();
//                sb.append(calendar.get(Calendar.YEAR)).append("年")
//                        .append(calendar.get(Calendar.MONTH)).append("月")
//                        .append(calendar.get(Calendar.DATE)).append("日");
//                desc1 = sb.toString();
//            }
            int minute = calendar.get(Calendar.MINUTE);
            String desc2 = calendar.get(Calendar.HOUR_OF_DAY) + ":" + (minute < 10 ? "0" + minute : minute);
            twoDimensionDesc[0] = desc1;
            twoDimensionDesc[1] = desc2;
        } catch (Exception ignored) {

        }
        return twoDimensionDesc;
    }
    private static final String[] dayDesc = {"今天","昨天","前天","大前天"};
}

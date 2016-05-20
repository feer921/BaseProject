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
     * @param timePattern 时间正则格式 eg. YYYY-MM-dd HH:mm:ss
     * @return
     */
    public static String getTime(String timePattern){
        SimpleDateFormat sdf = new SimpleDateFormat(timePattern,Locale.getDefault());
        return sdf.format(new Date());
    }
}

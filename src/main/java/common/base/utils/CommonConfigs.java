package common.base.utils;

/**
 * 一些能公共配置信息的类
 * eg.任何使用该通用库的APK程序所要存储数据的根路径配置
 * <br/>
 * 2015年12月23日-下午2:13:52
 * @author lifei
 */
public class CommonConfigs {
    /**
     * 如果使用方需要使用{@linkplain StorageUtil}
     * 需要先配置一下当前开始的APP的存储的根目录地址
     * 本程序的存储信息根目录名eg.: "/myapp/"
     */
    public static String APP_BASE_PATH = "";

    /**
     * 调试追踪日志标志
     */
    public static boolean DEBUG_TRACE_FLAG = true;
    public static final String DEBUG_TRACE_DIR_NAME = "CommonModule_Debug/";
    public static final String DEBUG_TRACE_FILE_NAME = "/debug_trace.txt";
}

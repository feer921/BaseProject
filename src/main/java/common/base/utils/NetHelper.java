package common.base.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 网络检测工具类
 * <br/>
 * 2015年12月23日-下午2:26:53
 * @author lifei
 */
public class NetHelper {
    public static final int NETWORK_TYPE_NONE = 0;
    public static final int NETWORK_TYPE_WIFI = NETWORK_TYPE_NONE + 1;
    public static final int NETWORK_TYPE_2G = NETWORK_TYPE_WIFI + 1;
    public static final int NETWORK_TYPE_3G = NETWORK_TYPE_2G + 1;
    public static final int NETWORK_TYPE_4G = NETWORK_TYPE_3G + 1;// LTE

    public static boolean isWifiNet(Context c) {
        boolean bRet = false;
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (null != wifiInfo && wifiInfo.isConnectedOrConnecting()) {
            bRet = true;
        }
        return bRet;
    }

    /**
     * public static int getNetType(Context context) { int type = -1;
     * 
     * ConnectivityManager
     * cm=(ConnectivityManager)context.getSystemService(Context
     * .CONNECTIVITY_SERVICE);
     * 
     * NetworkInfo mobileInfo =
     * cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); NetworkInfo wifiInfo
     * = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
     * 
     * if(null != mobileInfo && mobileInfo.isConnected()) { type =
     * ConnectivityManager.TYPE_MOBILE; } if(null != wifiInfo &&
     * wifiInfo.isConnected()) { type = ConnectivityManager.TYPE_WIFI; }
     * 
     * return type; }
     */

    /*
     * @return NETWORK_TYPE_2G NETWORK_TYPE_3G NETWORK_TYPE_4G NETWORK_TYPE_WIFI
     * NETWORK_TYPE_NONE
     */
    public static int netType(Context context) {
        int type = NETWORK_TYPE_NONE;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if (null != mobileInfo && mobileInfo.isConnected()) {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            int netType = tm.getNetworkType();
            switch (netType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                case TelephonyManager.NETWORK_TYPE_EDGE:
                case TelephonyManager.NETWORK_TYPE_CDMA:
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                case TelephonyManager.NETWORK_TYPE_IDEN:
                    type = NETWORK_TYPE_2G;
                    break;
                case TelephonyManager.NETWORK_TYPE_UMTS:
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    // API14
                    /*
                     * case TelephonyManager.NETWORK_TYPE_EVDO_B: case
                     * TelephonyManager.NETWORK_TYPE_EHRPD: case
                     * TelephonyManager.NETWORK_TYPE_HSPAP: type =
                     * NETWORK_TYPE_3G; break; case
                     * TelephonyManager.NETWORK_TYPE_LTE: type =
                     * NETWORK_TYPE_4G; break;
                     */
                default:
                    type = NETWORK_TYPE_2G;
                    break;
            }

        }
        if (null != wifiInfo && wifiInfo.isConnected()) {
            type = NETWORK_TYPE_WIFI;
        }
        return type;
    }

    /**
     * 是否已经联网
     */
    public static boolean isNetConnected(Context context) {
        return getNetType(context) > 0;
    }

    /**
     * 获取网络类型: -1 无网络,
     * ConnectivityManager.TYPE_ETHERNET/ConnectivityManager.TYPE_WIFI
     * /ConnectivityManager.TYPE_MOBILE
     */

    public static int getNetType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = null, wifiInfo = null;
        try {
            mobileInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            // ethInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        } catch (NullPointerException e) {
            // 酷派的二货手机系统bug,可能出现NPE
        }

        if (wifiInfo != null && wifiInfo.isConnected()) {
            return ConnectivityManager.TYPE_WIFI;
        }

        if (mobileInfo != null && mobileInfo.isConnected()) {
            return ConnectivityManager.TYPE_MOBILE;
        }

        return -1;
    }

    /**
     * 
     * 当wifi不能访问网络时，mobile才会起作用
     * 
     * @return GPRS是否连接可用
     */

    public static boolean isMobileConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (mMobile != null) {
            return mMobile.isConnected();
        }

        return false;

    }

    /**
     * 判断Wifi是否已连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isConnected();
            }
        }
        return false;
    }

    /**
     * 网络是否有效
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        appContext = context;
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }
    /**
     * 获取网络类型[未经验证]
     * 
     * @param context
     * @return
     */
    public static int getNetWorkType(Context context) {
        /** wifi网络 */
        final int NETWORKTYPE_WIFI = 1;
        /** 2G网络 */
        final int NETWORKTYPE_2G = 2;
        /** 3G和3G以上网络，或统称为快速网络 */
        final int NETWORKTYPE_3G = 3;
        int mNetWorkType = 0;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                String proxyHost = android.net.Proxy.getDefaultHost();
                mNetWorkType = TextUtils.isEmpty(proxyHost) ? (isFastMobileNetwork(context) ? NETWORKTYPE_3G : NETWORKTYPE_2G)
                        : NETWORKTYPE_2G;
            }
        }
        return mNetWorkType;
    }

    private static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_CDMA:
                return false; // ~ 14-64 kbps
            case TelephonyManager.NETWORK_TYPE_EDGE:
                return false; // ~ 50-100 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                return true; // ~ 400-1000 kbps
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                return true; // ~ 600-1400 kbps
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return false; // ~ 100 kbps
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                return true; // ~ 2-14 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPA:
                return true; // ~ 700-1700 kbps
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                return true; // ~ 1-23 Mbps
            case TelephonyManager.NETWORK_TYPE_UMTS:
                return true; // ~ 400-7000 kbps
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                return true; // ~ 1-2 Mbps
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                return true; // ~ 5 Mbps
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return true; // ~ 10-20 Mbps
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return false; // ~25 kbps
            case TelephonyManager.NETWORK_TYPE_LTE:
                return true; // ~ 10+ Mbps
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                return false;
            default:
                return false;
        }
    }
    /**
     * 根据以太网抽象路径，获取MAC地址
     * @return
     */
    public static String getEthMac() {
        File file = new File("/sys/class/net/eth0/address");
        if (!file.exists()) {
            return null;
        }
        String ethAdress = null;
        try {
            BufferedReader in = new BufferedReader(new FileReader(file), 32);
            try {
                ethAdress = in.readLine();
                CommonLog.i("info", "ethAddress>>>>>>>>>>" + ethAdress);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            return null;
        }
        return ethAdress;
    }

    /**
     * 
     * 获取mac
     */
    public static String getMacString(Context context) {
        String mac = null;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        boolean bOpenWifi = false;
        int state = wifiManager.getWifiState();
        if (state != WifiManager.WIFI_STATE_ENABLED && state != WifiManager.WIFI_STATE_ENABLING) {
            bOpenWifi = wifiManager.setWifiEnabled(true);

        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            mac = wifiInfo.getMacAddress();
        }
        if (bOpenWifi) {
            wifiManager.setWifiEnabled(false);
        }
        return mac == null ? "" : mac.replace(":", "");
    }
    //temp 
    private static Context appContext;
    public static Context getAppContext(){
        return appContext;
    }


    public static void enableMobileData(Context context) {
        boolean isMobileDataEnabled = isMobileDataEnabled(context);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (!isMobileDataEnabled) {
            //开启
            Boolean boolArg = true;
            ReflectUtil.invokeMethod(connectivityManager,"setMobileDataEnabled", boolArg);
        }
    }

    /***
     * 移动网络是否开启了
     * @param context
     * @return
     */
    public static boolean isMobileDataEnabled(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Object[] methodArgs = null;
        Object result = ReflectUtil.invokeMethod(connectivityManager,"getMobileDataEnabled",methodArgs);
        if (result != null) {
            return (boolean) result;
        }
        return false;
    }


}

package common.base.netAbout;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;
import common.base.utils.CommonLog;
import common.base.utils.JsonUtil;
import common.base.utils.Util;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-17
 * Time: 17:31
 * DESC: 服务端响应结果转化成本地Java实体示例
 * 对应服务端的响应数据类型为Json,并且格式如下
 {
 "statusCode": "1",
 "msg": "请求成功",
 "data": {
     }
 }
 */
public class BaseServerResult {
    public static final int ERROR_CODE_NO_NET = 0;
    protected final String TAG = getClass().getSimpleName();
    public String statusCode;
    public String msg;
    /**
     * 这里是为了通用data字段对应的数据类型，有可能data对应就一个字符串 eg.:data:"",也有可能data对应的是
     * 一个Json对象 eg.: data:{}
     */
    public JsonNode data;
    /**
     * 这里是为了再把JsonNode的data再转化成字符串，方便使用Json序列化工具再转化成对应的Java实体类
     */
    @JsonIgnore
    protected String dataStr = "";

    public boolean isResponseOk() {
        getDataStr();
        return isStatusCodeOk();
    }

    protected boolean isStatusCodeOk() {
        return "0".equals(statusCode);
    }
    public String getDataStr() {
        if (!Util.isEmpty(dataStr)) {
            return dataStr;
        }
        dataStr = jsonNodeDataToString();
        return dataStr;
    }

    protected String jsonNodeDataToString() {
        if (data != null) {
            return data.toString();
        }
        return "";
    }

    public <T> T convertData2Bean(Class<T> beanClass) {
        String dataStr = getDataStr();
        try {
            if (!Util.isEmpty(dataStr)) {
                return JsonUtil.jsonStr2Object(dataStr, beanClass);
            }
        } catch (IOException e) {
            CommonLog.e("ServerResult","--> convertData2Bean() occur : " + e);
        }
        return null;
    }

    /**
     * 如果服务端响应的data中为数组类型数据需要转换成集合
     * @param elementClass
     * @param <T>
     * @return
     */
    public <T> List<T> convertData2List(Class<T> elementClass) {
        String dataStr = getDataStr();
        try {
            if (!Util.isEmpty(dataStr)) {
                return JsonUtil.jsonArrayStr2ListObject(dataStr, elementClass);
            }
        } catch (Exception e) {
            CommonLog.e(TAG, "--> convertData2List() e: " + e);
        }
        return null;
    }

    public static <T extends BaseServerResult> T jsonStr2Me(String jsonStr, Class<T> beanClass) {
        if (!Util.isEmpty(jsonStr)) {
            return JsonUtil.jsonStr2Obj(jsonStr, beanClass);
        }
        return null;
    }

    public static <T> T anyJsonStrToAnyBean(String anyJsonStr, Class<T> anyBeanClass) {
        if (!Util.isEmpty(anyJsonStr)) {
            return JsonUtil.jsonStr2Obj(anyJsonStr, anyBeanClass);
        }
        return null;
    }

    public static <T> List<T> anyJsonStrToAnyListBean(String anyJsonStr, Class<T> anyListItemBeanClass) {
        if (!Util.isEmpty(anyJsonStr)) {
            return JsonUtil.jsonArrayStr2ListObject(anyJsonStr, anyListItemBeanClass);
        }
        return null;
    }
    @Override
    public String toString() {
        return TAG + "{" +
                "statusCode='" + statusCode + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                ", dataStr='" + dataStr + '\'' +
                '}';
    }

    @JsonIgnore
    public JSONObject dataJson;

    public Object peekValueInData(String key) {
        if (dataJson == null || dataJson.length() < 1) {
            try {
                dataJson = new JSONObject(getDataStr());
            } catch (Exception e) {
                e.printStackTrace();
                dataJson = new JSONObject();
            }
        }
//        if (dataJson != null) {
//            return dataJson.opt(key);
//        }
        return dataJson.opt(key);
    }
//    public Object peekValueInData(String key) {
//        if (dataJson == null) {
//            try {
//                dataJson = new JSONObject(getDataStr());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if (dataJson != null) {
//            return dataJson.opt(key);
//        }
//        return null;
//    }

//    public <V> V peekValueInDataWithDefValue(String keyInData, V defValue) {
//        if (dataJson == null) {
//            try {
//                dataJson = new JSONObject(getDataStr());
//            } catch (Exception ignore) {
//                ignore.printStackTrace();
//            }
//        }
//        if (dataJson != null && dataJson.has(keyInData)) {
//            if (defValue != null) {
//                try {
//                    V result = defValue;
//                    if (defValue instanceof String) {
//                        result = (V) dataJson.optString(keyInData);
//                    } else if (defValue instanceof Integer) {
//                        Integer value = dataJson.optInt(keyInData);
//                        result = (V) value;
//                    } else if (defValue instanceof Double) {
//
//                    }
//                    return result;
//                } catch (Exception ignore) {
//                    //类型转换异常
//                    ignore.printStackTrace();
//                }
//            }
//        }
//        return defValue;
//    }
}

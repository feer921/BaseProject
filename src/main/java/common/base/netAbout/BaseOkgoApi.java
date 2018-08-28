package common.base.netAbout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.request.BaseRequest;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.TreeMap;

import common.base.utils.CommonConfigs;
import common.base.utils.CommonLog;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2018/8/28<br>
 * Time: 9:59<br>
 * <P>DESC:
 * 基于Okgo网络请求框架的通用请求接口
 * </p>
 * ******************(^_^)***********************
 */
public class BaseOkgoApi extends BaseApi {

    static final byte GET = 1;
    static final byte POST = GET + 1;
    static final byte PUT = POST + 1;

    /**
     * 不带请求参数的get请求
     * @see #getWithParams(String, AbsCallback, String[], String...)
     * @param apiUrl api url地址
     * @param callback 网络响应回调
     */
    public static void get(String apiUrl,AbsCallback callback) {
//        excute(createRequest(wholeUrl,GET),callback);
        getWithParams(apiUrl, callback, null, "");
    }

    public static void getWithParams(String wholeApiUrl, AbsCallback callback, String[] appendKeys, String... keyValues) {
        GetRequest getRequest = OkGo.get(wholeApiUrl);
        buildCommonRequestParams(getRequest, appendKeys, keyValues);
        excute(getRequest, callback);
    }
    /**
     * @param request
     * @param appendKeys keys
     * @param values values 要一一对应到key的值
     */
    public static BaseRequest buildCommonRequestParams(BaseRequest request, String[] appendKeys, String... values) {
        if (appendKeys != null && values != null) {//如果有外部传入的参数
            int keysLen = appendKeys.length;
            int valuesLen = values.length;
            if (keysLen > 0 && valuesLen > 0 && valuesLen >= keysLen) {
                for (int keyIndex = 0; keyIndex < keysLen; keyIndex++) {
                    String curKey = appendKeys[keyIndex];
                    String curValue = values[keyIndex];
                    request.params(curKey, curValue);
                }
            }
        }
        return request;
    }
    public static BaseRequest getRequest(String wholeUrl) {
        return createRequest(wholeUrl, GET);
    }

    /**
     * 不带请求参数指定请求类型值的get请求
     * @param appPartialUrl 网络请求地址后缀部分
     * @param callback 网络响应回调
     * @param requestType 请求类型值
     */
    public static void getWithType(String appPartialUrl, AbsCallback callback, int requestType) {
        String thisWholeUrl = URL_HOST + appPartialUrl;
        callback.requestType = requestType;
        get(thisWholeUrl,callback);
    }

    /**
     * 带有请求参数/值，键值对的post请求
     * @param wholeApiUrl 完整的api url地址
     * @param callback 回调
     * @param keysInParams 参数键
     * @param keysValues 参数值
     */
    public static void postWithParams(String wholeApiUrl, AbsCallback callback, String[] keysInParams, String... keysValues) {
        PostRequest postRequest = postRequest(wholeApiUrl);
        buildCommonRequestParams(postRequest, keysInParams, keysValues);
        excute(postRequest, callback);
    }
    public static void post(String wholeUrl, AbsCallback callback) {
        excute(createRequest(wholeUrl,POST),callback);
    }
    public static PostRequest postRequest(String wholeUrl) {
        return (PostRequest) createRequest(wholeUrl, POST);
    }
    public static void postWithType(String appPartialUrl, AbsCallback callback, int requestType) {
        String thisWholUrl = URL_HOST + appPartialUrl;
        callback.requestType = requestType;
        post(thisWholUrl, callback);
    }
    protected static BaseRequest createRequest(String wholeUrl, byte requestMethod) {
        switch (requestMethod) {
            case GET:
                return OkGo.get(wholeUrl);
            case POST:
                return OkGo.post(wholeUrl);
            case PUT:
                return OkGo.put(wholeUrl);
        }
        return null;
    }
    public static void excute(BaseRequest request, AbsCallback callback) {
        request.tag(request.getUrl()).execute(callback);
    }

    /**
     * 根据String类型的keys和String类型的values创建JSONObject
     * @param keys
     * @param values
     * @return JSONObject
     */
    public static JSONObject createJsonWithParams(String[] keys, String... values) {
        JSONObject jsonObj = new JSONObject();
        if (keys != null && values != null && values.length >= keys.length) {
            int keyLen = keys.length;
            for(int i = 0; i < keyLen;i++) {
                String curKey = keys[i];
                String curV = values[i];
                try {
                    jsonObj.put(curKey,curV);
                } catch (JSONException e) {
                    CommonLog.e("info", "AppBaseAPi-->createJsonWithParams() put the key :" + curKey + " the value: " + curV + " occur :" + e);
                }
            }
        }
        return jsonObj;
    }

    /**
     * 根据键、值对，生成JSONObject
     * @param keys
     * @param values 值可为任意数据类型
     * @return 如果没有keys也会有JSONObject生成
     */
    public static JSONObject createJsonWithParams(String[] keys, Object... values) {
        JSONObject jsonObj = new JSONObject();
        if (keys != null && values != null && values.length >= keys.length) {
            int keyLen = keys.length;
            for(int i = 0; i < keyLen;i++) {
                String curKey = keys[i];
                Object curV = values[i];
                try {
                    jsonObj.put(curKey,curV);
                } catch (JSONException e) {
                    CommonLog.e("info", "AppBaseAPi-->createJsonWithParams() put the key :" + curKey + " the value: " + curV + " occur :" + e);
                }
            }
        }
        return jsonObj;
    }

//    /**
//     * 构建通用的Json参数body(不MD5参数体)
//     * 键所对应的值皆为String类型
//     * @param keysInParmJsonObject 仅为参数体的keys
//     * @param valuesInParamJsonObject 仅为参数体keys对应的values
//     * @return post请求中的Json body数据
//     */
//    public static JSONObject buildCommonJSonBody(String[] keysInParmJsonObject, String... valuesInParamJsonObject) {
//        Object[] valuesInParams = null;
//        if (valuesInParamJsonObject != null) {
//            int valuesLen = valuesInParamJsonObject.length;
//            if (valuesLen > 0) {
////                System.arraycopy();
//                valuesInParams = new Object[valuesInParamJsonObject.length];
//                for(int index = 0; index < valuesLen; index++) {
//                    valuesInParams[index] = valuesInParamJsonObject[index];
//                }
//            }
//        }
//        return buildCommonJSonBody(keysInParmJsonObject,false,valuesInParams);
//    }

//    /**
//     *
//     * @param keysInParmJsonObject
//     * @param signParam 是否要加密参数数据体
//     * @param valuesInParamJsonObject
//     * @return 根节点的JSONObject
//     */
//    public static JSONObject buildCommonJSonBody(String[] keysInParmJsonObject,boolean signParam,Object... valuesInParamJsonObject) {
////        JSONObject rootJsonRequestBody = new JSONObject();
////        JSONObject paramJsonObject = createJsonWithParams(keysInParmJsonObject, valuesInParamJsonObject);
////        return rootJsonRequestBody;
//        return null;
//    }
    /**
     * 仅可指定Param JSon数据体的键、值 的post请求
     * @param partialUrl
     * @param callback
     * @param keysInParmJsonObject
     * @param valuesInParamJsonObject
     */
    protected static void jsonPost(String partialUrl, AbsCallback callback, String[] keysInParmJsonObject, Object... valuesInParamJsonObject) {
        JSONObject jsonBody = createJsonWithParams(keysInParmJsonObject, valuesInParamJsonObject);
        postRequest(URL_HOST + partialUrl)
                .upJson(jsonBody)
                .execute(callback);
    }

}

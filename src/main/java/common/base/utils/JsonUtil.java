package common.base.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.module.kotlin.KotlinModule;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-06-29
 * Time: 18:21
 * DESC:
 */
public final class JsonUtil {
    private static ObjectMapper mapper = null;
    static {
        mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);//属性为 空（“”） 或者为 NULL 都不序列化

        //解决kotlin中数据类非空类型但是json反序列化时为空抛异常的问题
        mapper.registerModule(new KotlinModule.Builder().nullIsSameAsDefault(true).nullToEmptyMap(true).nullToEmptyCollection(true).build());
    }

    private JsonUtil() {

    }

    /**
     * 将一个Java对象转换成Json字符串表示
     * @param nodeName 表示该Java对象的节点名称，eg.: user:{"name":"jack","sex":1}; 其中user:即为nodeName
     * @param curObject 支持Map，List
     * @return 如果nodeName为null
     * @throws IOException
     */
    public static String convertObject2JsonStr(String nodeName, Object curObject) throws IOException {
        if (curObject != null) {
            if (curObject instanceof JSONObject) {//因为如果是JSONObject对象，会序列化失败 No serializer found for class org.json.JSONObject and no pro
                return curObject.toString();
            }
        }
        if (Util.isEmpty(nodeName)) {
            return mapper.writeValueAsString(curObject);
        }
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.putPOJO(nodeName, curObject);
        return mapper.writeValueAsString(rootNode);
    }

    /**
     * 将Object对象转为JSONObject 字符串
     * @param toJsonObj
     * @return
     */
    public static String convertObj2JSonStr(Object toJsonObj) {
        String jsonResultStr = "";
        try {
            jsonResultStr = converObject2JsonStr(toJsonObj);
        } catch (IOException e) {
            jsonResultStr = "";
            e.printStackTrace();
        }
        return jsonResultStr;
    }
    /**
     * 将一个Java对象转换成Json字符串
     * @param curObject
     * @return eg.: User ==>  {"name":"jack","sex":1,"city":"sz"}
     * @throws IOException
     */
    public static String converObject2JsonStr(Object curObject) throws IOException {
        return convertObject2JsonStr(null, curObject);
    }

    /***
     * 将一个Java对象转换成JsonObject对象(这是无聊的节奏)
     * @param curObject
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static JSONObject object2JsonObject(Object curObject) throws IOException, JSONException {
        String object2JsonStr = converObject2JsonStr(curObject);
        return new JSONObject(object2JsonStr);
    }

    /**
     * 将Json字符串转换成Java对象(反序列化)
     * @param jsonStr
     * @param javaObjClass 目标Java对象的类型
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> T jsonStr2Object(String jsonStr, Class<T> javaObjClass) throws IOException {
        return mapper.readValue(jsonStr, javaObjClass);
    }
    public static <T> T jsonStr2Obj(String jsonStr, Class<T> javaObjClass) {
        T javaObj = null;
        try {
            javaObj = mapper.readValue(jsonStr, javaObjClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return javaObj;
    }
    /**
     * json 数组字符串 转换成List集合
     * @param jsonArrayStr
     * @param listTypeReference
     * @param <T> 集合中元素的对象类型
     * @return
     */
    public static <T> List<T> jsonArrayStr2ListObject(String jsonArrayStr, TypeReference<List<T>> listTypeReference) {
        try {
            return mapper.readValue(jsonArrayStr, listTypeReference);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 依List中元素的Java类型将 json 数组字符串，转换成List集合
     * @param jsonArrayStr
     * @param elementClass List中元素的Java类型
     * @param <T>
     * @return
     */
    public static <T> List<T> jsonArrayStr2ListObject(String jsonArrayStr, Class<T> elementClass) {
        JavaType resultJavaBeanType = mapper.getTypeFactory().constructParametricType(List.class, elementClass);
        try {
            return mapper.readValue(jsonArrayStr, resultJavaBeanType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据提供的 键值对信息，组装成一个JSONObject对象
     * @param keys 键
     * @param values 值
     * @return JSONObject 对象 eg.: {"name":"feer","sex":"男", ...}
     */
    public static JSONObject assembleJSONObj(String[] keys,Object... values) {
        JSONObject paramJson = new JSONObject();
        if (keys != null && values != null) {
            int keyLen = Math.min(keys.length, values.length);
            for (int i = 0; i < keyLen; i++) {
                String curKey = keys[i];
                Object curV = values[i];
                try {
                    paramJson.put(curKey, curV);
                } catch (JSONException e) {
                    CommonLog.e("info", "JsonUtil --> assembleJsonObj() put the key :" + curKey + " the value: " + curV + " occur :" + e);
                }
            }
        }
        return paramJson;
    }

    public static HashMap<String, Object> jsonStr2HashMap(String jsonStr) {
        if (jsonStr != null && "".equals(jsonStr.trim())) {
            try {
                JavaType jvt = mapper.getTypeFactory().constructParametricType(HashMap.class,String.class,Object.class);
                return mapper.readValue(jsonStr, jvt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void jsonPutKVIgnoreException(JSONObject jsonObj, String theKey, Object theValue) {
        if (jsonObj != null) {
            try {
//                jsonObj.putOpt(theKey, theValue);//要求 key value 都不为空
                jsonObj.put(theKey, theValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

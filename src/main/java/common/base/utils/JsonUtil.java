package common.base.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
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
        if (Util.isEmpty(nodeName)) {
            return mapper.writeValueAsString(curObject);
        }
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.putPOJO(nodeName, curObject);
        return mapper.writeValueAsString(rootNode);
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
        } catch (IOException e) {
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
            mapper.readValue(jsonArrayStr, resultJavaBeanType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}

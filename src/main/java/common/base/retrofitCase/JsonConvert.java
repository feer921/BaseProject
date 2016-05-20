package common.base.retrofitCase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-05-16
 * Time: 18:09
 * DESC: JsonObject 转换器，即将Retrofit的响应结果转换成JsonObject数据
 */
public class JsonConvert implements Converter<ResponseBody,JSONObject>{
    private Type mType;
    public JsonConvert(Type type) {
        this.mType = type;
    }
    @Override
    public JSONObject convert(ResponseBody value) throws IOException {
        if (mType == JSONObject.class) {
            JSONObject result = null;
            try {
                result = new JSONObject(value.string());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
        return null;
    }
}

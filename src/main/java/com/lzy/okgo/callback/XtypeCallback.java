package com.lzy.okgo.callback;

import com.lzy.okgo.utils.OkLogger;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import common.base.utils.JsonUtil;
import okhttp3.Response;

/**
 * ================================================
 * User: 11776610771@qq.com
 * Date: 2017/3/30
 * Time: 15:10
 * DESC: 通过Josn解析框架将服务端的响应信息解析成泛型的数据对象
 * 数据类型一(最外层数据类型是JsonObject，data 数据也是JsonObject)：
 * {
    "code":0,
    "msg":"请求成功",
    "data":{
            "id":123456,
            "name":"张三",
            "age":18
           }
 }
 * 数据类型二(最外层数据类型是JsonObject，data 数据是JsonArray)：
 * {
    "code":0,
    "msg":"请求成功",
    "data":[
    {
        "id":123456,
        "name":"张三",
        "age":18
    },
    {
        "id":123456,
        "name":"张三",
        "age":18
    },
    {
        "id":123456,
        "name":"张三",
        "age":18
    }
    ]
    }
  数据类型三(没有固定的 msg、code 字段格式包装，服务器任意返回对象):
    "data":{
    "id":123456,
    "name":"张三",
    "age":18
    }

 数据类型D-最外层数据类型是JsonArray，内部数据是JsonObject
 [
 {
 "id":123456,
 "name":"张三",
 "age":18
 },
 {
 "id":123456,
 "name":"张三",
 "age":18
 },
 {
 "id":123456,
 "name":"张三",
 "age":18
 }
 ]
 * ================================================
 * 其中的T为，希望返回什么类型对象，就通过Josn解析框架来返回对应类型的对象，eg.: XtypeCallback<BaseServerResult>
 * 而其中BaseServerResult中的定义为上面的数据类型一、二
 * BaseServerResult｛
 *   public int code;
 *   public String msg;
 *   public JsonNode data;//这里用来通用data返回不同的数据类型可以是像数据类型一，也可以是像数据类型二，甚至可以直接是字符串[data:"kdfdkjfkdjf"]
 * ｝
 * 对应服务器的响应为:
  {
   "code":0,
   "msg":"ok",
   "data":xxxxxx//无具体类型
  }
 * T 是任何对象类型都能返回，只要与服务器响应格式对得上，就能正确返回.
 */
public abstract class XtypeCallback<T> extends AbsCallback<T> {

    @Override
    public T convertSuccess(Response response) throws Exception {
        //获取本类的实现类
        /**
         * genType = com.lzy.okgo.callback.XtypeCallback<com.fee.theoneproject.ServerModel>
         *     对应外部使用为XtypeCallback<ServerModel>
         *genType = com.lzy.okgo.callback.XtypeCallback
         * <com.fee.theoneproject.LzyResponse<java.util.List<com.fee.theoneproject.ServerModel>>>
         * 对应外部使用为XtypeCallback<LzyResponse<List<ServerModel>>>
         *
         */
        Type genType = getGenType(getClass());
        boolean isGenTypeParameterized = genType instanceof ParameterizedType;
        OkLogger.i("info", "--> isGenTypeParameterized = " + isGenTypeParameterized);

        Type[] tParams = null;//泛型T的参数对象们，其实本类就一个<T>,这个是考虑如果本类有多个泛型eg.:XtypeCallback<T,E>
        Class<T> paramTypeClass = null;//泛型参数的Class对象，方便转化成T对象

        if (isGenTypeParameterized) {
            tParams = ((ParameterizedType) genType).getActualTypeArguments();
        }


//        for (Type one : tParams) {
//            /**
//             *       eg.: one = com.fee.theoneproject.LzyResponse<java.util.List<com.fee.theoneproject.ServerModel>>
//             */
//            OkLogger.w("info", "--->Type[] params one = " + one);
//        }
        /**
         * paramsType = com.fee.theoneproject.LzyResponse<java.util.List<com.fee.theoneproject.ServerModel>>
         */
        Type paramsType = tParams[0];

        if (paramsType instanceof Class) {
            paramTypeClass = (Class<T>) paramsType;
        }
        /**
         *
        这里我的理解为，子类所指定T的泛型参数仍然是可参数化的Type，eg.: 有一个对象public class MyBean<T>{
                public int code;
                  public String msg;
                  public T data;
         },
         * 这时如果XtypeCallback<MyBean<Data>>
         * 则我的理解为XtypeCallback中的泛型参数MyBean<?> 仍然是一个可参数化的Type
         */
        else if (paramsType instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) paramsType).getRawType();
            OkLogger.i("info",TAG + "--> rawType = " + rawType);
            paramTypeClass = (Class<T>) rawType;
        }
        else {
            throw new IllegalArgumentException("泛型参数异常...");
        }
        OkLogger.e("info", "--> 泛型参数T的类名为：" + paramTypeClass.getSimpleName());
        String bodyStr = response.body().string();
//        OkLogger.i("info","--> 服务器响应数据：" + bodyStr);
        return JsonUtil.jsonStr2Object(bodyStr,paramTypeClass);
    }

    /**
     * 获取当前类的直接父类Type
     * 1、如果在外部使用时为匿名内部类时，getClass为匿名类自身，则getGenericSuperclass()即为XtypeCallback<T>了
     * 2、如果有XtypeCallback的实现类(子类、孙类...),则需要一层层往上查找到最终的父类即本类
     * @return 最终能代表本类XtypeCallback的Type
     */
    private Type getGenType(Class curClass) {
        OkLogger.e("info", "getGenType()--> curClass：" + curClass);
        Type thisGenType = curClass.getGenericSuperclass();//返回直接超类
        OkLogger.e("info", "getGenType()--> thisGenType：" + thisGenType);
        if (thisGenType instanceof Class) {
            //这种情况，表明当前类是XtypeCallback的子类
            Class superClass = curClass.getSuperclass();
            OkLogger.e("info", "getGenType()--> superClass：" + superClass);
            //递归调用
            thisGenType = getGenType(superClass);
//            thisGenType = superClass.getGenericSuperclass();
            OkLogger.e("info", "getGenType()--> thisGenType：" + thisGenType);
        }
        return thisGenType;
    }
}

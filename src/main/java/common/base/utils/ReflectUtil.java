package common.base.utils;

import java.lang.reflect.Method;

/**
 * User: fee(1176610771@qq.com)
 * Date: 2016-07-06
 * Time: 19:59
 * DESC: 反射工具
 */
public class ReflectUtil {
    /**
     * 从一个对象中反射出目标方法出来
     * @param ownerClass
     * @param methodName
     * @param methodArgs
     * @return
     */
    public static Method getMethodInAClass(Class ownerClass, String methodName, Object... methodArgs) {
        if (ownerClass == null || Util.isEmpty(methodName)) {
            return null;
        }
        Class[] methodArgsClassType = null;
        if (methodArgs != null) {
            int argsCount = methodArgs.length;
            methodArgsClassType = new Class[argsCount];
            for(int i = 0; i < argsCount ; i++) {
                methodArgsClassType[i] = methodArgs[i].getClass();
            }
        }
        try {
            Method methodResult = ownerClass.getMethod(methodName, methodArgsClassType);
            return methodResult;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 调用一个反射出来的方法并返回相应的结果
     * @param methodOwerObj 所要调用的方法所在的对象
     * @param methodName 方法名
     * @param methodArgs 方法参数
     * @return
     */
    public static Object invokeMethod(Object methodOwerObj, String methodName, Object... methodArgs) {
        if (methodOwerObj == null) {
            return null;
        }
        Method targetMethod = getMethodInAClass(methodOwerObj.getClass(), methodName, methodArgs);
        if (targetMethod != null) {
            try {
                return targetMethod.invoke(methodOwerObj, methodArgs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

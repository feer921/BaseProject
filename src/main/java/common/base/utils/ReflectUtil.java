package common.base.utils;

import java.lang.reflect.Field;
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

    /**
     * 通过反射获取一个类中所声明的外部不可直接访问的属性对象
     * @param fieldOfObj 该属性所归属的对象
     * @param declaredFieldName 该属性被所属的对象声明的名称
     * @return
     */
    public static Field getFieldOfObject(Object fieldOfObj, String declaredFieldName) {
        if (fieldOfObj != null) {
            return getFidelOfClass(fieldOfObj.getClass(), declaredFieldName);
        }
        return null;
    }

    /**
     * 通过反射获取一个类Class中声明的某个不可被外部直接引用的属性/字段
     * @param fieldOfClass 属性所属的类Class
     * @param declaredFieldName 声明的属性/字段名称
     * @return
     */
    public static Field getFidelOfClass(Class fieldOfClass, String declaredFieldName) {
        Field theField = null;
        if (fieldOfClass != null) {
            try {
                theField = fieldOfClass.getDeclaredField(declaredFieldName);
                theField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return theField;
    }
    /**
     * 通过反射 获取一个类中所声明的外部不可直接访问的属性对象
     * @param fieldOfObj 持有目标属性的类的对象
     * @param declaredFieldName 类中所声明的属性名称 eg.: PrivateObj myObjName;
     * @param <T>               返回通过反射出的该属性的对象
     * @return
     */
    public static <T> T getFieldInstanceObj(Object fieldOfObj, String declaredFieldName) {
        Field theField = getFieldOfObject(fieldOfObj, declaredFieldName);
        if (theField != null) {
            theField.setAccessible(true);
            try {
                return (T) theField.get(fieldOfObj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

package common.base.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/12/4<br>
 * Time: 14:37<br>
 * <P>DESC:
 * 自定义注解
 * 该注解用来标记某个类、属性、方法不使用
 * </p>
 * ******************(^_^)***********************
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR,ElementType.FIELD,ElementType.METHOD,ElementType.LOCAL_VARIABLE,ElementType.TYPE})
public @interface NotUse {
    String value();
}

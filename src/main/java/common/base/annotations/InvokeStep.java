package common.base.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fee
 * <P> DESC:
 * 提示 调用步骤/顺序的 注解类
 * </P>
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.CONSTRUCTOR})
public @interface InvokeStep {
    String desc();
    int value();
}

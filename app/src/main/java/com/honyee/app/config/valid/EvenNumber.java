package com.honyee.app.config.valid;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EvenValidator.class})
public @interface EvenNumber {
    
    String message() default "输入的等级必须为偶数";
    
    Class<?>[] groups() default {};
    
    Class<? extends Payload>[] payload() default {};
}
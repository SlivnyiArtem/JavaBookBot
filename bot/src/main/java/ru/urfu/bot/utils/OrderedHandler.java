package ru.urfu.bot.utils;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Order
public @interface OrderedHandler {
    @AliasFor(annotation = Order.class, attribute = "value")
    int order() default 50;
}

package ru.urfu.bot.utils;

import org.springframework.core.annotation.AliasFor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Указывает, что бин является обработчиком обновлений с задданным приоритетом. Чем меньше занчение,
 * тем раньше будет вызван обработчик.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Order
public @interface OrderedHandler {

    /**
     * @return приоритет обработчика
     */
    @AliasFor(annotation = Order.class, attribute = "value")
    int order() default 50;
}

package eu.cyzetlc.commentarii.service.modal.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface ModalSpecification {
    String id();

    String title() default "";

    ModalTitleType type() default ModalTitleType.TEXT;

    enum ModalTitleType {
        MESSAGE_KEY,
        TEXT;
    }
}
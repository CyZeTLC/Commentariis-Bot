package eu.cyzetlc.commentariis.service.button.annotation;

import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface ButtonSpecification {
    String id();

    String label() default "";

    ButtonStyle style() default ButtonStyle.PRIMARY;

    ButtonLabelType type() default ButtonLabelType.TEXT;

    enum ButtonLabelType {
        MESSAGE_KEY,
        TEXT;
    }
}
package eu.cyzetlc.commentariis.service.command.annotation;

import lombok.Getter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE_USE})
public @interface CommandSpecification {
    String command();

    String permission() default "";

    String description() default "A normal Command";

    String[] aliases() default {};

    TimeUnit cooldownType() default TimeUnit.SECONDS;

    int cooldownValue() default 0;

    enum TimeUnit {
        MILLI_SECONDS(1),
        SECONDS(1000),
        MINUTES(1000*60),
        HOURS(1000*60*60),
        DAYS(1000*60*60*24);

        @Getter
        public final long value;

        TimeUnit(long time) {
            this.value = time;
        }
    }
}

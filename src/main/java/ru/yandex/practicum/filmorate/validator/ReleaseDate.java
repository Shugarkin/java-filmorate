package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReleaseDateConstraintValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDate {
    String message() default "позже условленной даты";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

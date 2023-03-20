package ru.yandex.practicum.filmorate.validator;

import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateConstraintValidator implements ConstraintValidator<ReleaseDate, LocalDate> {

    private static final LocalDate DAYX = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(ReleaseDate releaseDate) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext cxt) {
        if(releaseDate == null) {
            return false;
        }
        return releaseDate.isAfter(DAYX);
    }

}

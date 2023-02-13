package ar.edu.itba.paw.webapp.validators.interfaces;

import ar.edu.itba.paw.webapp.validators.UniqueNftConstraintValidator;
import org.springframework.stereotype.Component;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueNftConstraintValidator.class)
@Target( { ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface UniqueNftConstraint {
    String message() default "Nft already exists";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String nftId();
    String contractAddr();
    String chain();

    String errorMessageTemplate();
}

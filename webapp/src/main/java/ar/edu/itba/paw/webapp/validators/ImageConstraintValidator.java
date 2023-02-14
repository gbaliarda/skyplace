package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.webapp.validators.interfaces.ImageConstraint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageConstraintValidator implements ConstraintValidator<ImageConstraint, MultipartFile> {

    private double maxSizeMB;
    private String errorMessageTemplate;

    @Autowired
    private MessageSource messageSource;

    @Override
    public void initialize(ImageConstraint imageConstraint) {
        maxSizeMB = imageConstraint.maxSizeMB();
        errorMessageTemplate = imageConstraint.errorMessageTemplate();
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = multipartFile != null && !multipartFile.isEmpty() &&
                multipartFile.getContentType().startsWith("image/") && multipartFile.getSize() <= maxSizeMB * 1048576;
                
        if (!result) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(messageSource.getMessage(errorMessageTemplate, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
        }
        return result;
    }
}

package ar.edu.itba.paw.webapp.validators;

import ar.edu.itba.paw.service.NftService;
import ar.edu.itba.paw.webapp.validators.interfaces.UniqueNftConstraint;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueNftConstraintValidator implements ConstraintValidator<UniqueNftConstraint, Object> {

    @Autowired
    private NftService nftService;

    @Autowired
    private MessageSource messageSource;

    private String nftId;
    private String contractAddr;
    private String chain;

    private String errorMessageTemplate;

    @Override
    public void initialize(UniqueNftConstraint arg0) {
        nftId = arg0.nftId();
        contractAddr = arg0.contractAddr();
        chain = arg0.chain();
        errorMessageTemplate = arg0.errorMessageTemplate();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapperImpl wrapper = new BeanWrapperImpl(obj);
        int nftIdObj = (int)wrapper.getPropertyValue(nftId);
        String contractAddrObj = (String)wrapper.getPropertyValue(contractAddr);
        String chainObj = (String)wrapper.getPropertyValue(chain);
        boolean notExists = !nftService.isNftCreated(nftIdObj, contractAddrObj, chainObj);
        if (notExists) {
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext
                    .buildConstraintViolationWithTemplate(messageSource.getMessage(errorMessageTemplate, null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();
        }
        return notExists;
    }
}

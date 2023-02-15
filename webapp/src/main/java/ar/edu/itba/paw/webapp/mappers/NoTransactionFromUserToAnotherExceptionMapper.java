package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.NoTransactionFromUserToAnotherException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.helpers.ApiReturnCodes;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NoTransactionFromUserToAnotherExceptionMapper implements ExceptionMapper<NoTransactionFromUserToAnotherException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(NoTransactionFromUserToAnotherException e) {
        final String errorMessage = messageSource.getMessage(e.getMessage(), new Object[]{e.getFrom(), e.getTo(), e.getAmount()}, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromGenericException(e, 400, errorMessage, ApiReturnCodes.NO_TRANSACTION_FROM_USER_TO_ANOTHER.getCode());

        return ResponseHelpers.fromErrorDtoAndStatusCode(error, Response.Status.BAD_REQUEST);
    }

}

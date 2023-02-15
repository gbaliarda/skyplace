package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserNotLoggedInException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UserNotLoggedInExceptionMapper implements ExceptionMapper<UserNotLoggedInException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UserNotLoggedInException e) {
        final String errorMessage = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromGenericException(e, 401, errorMessage);

        return ResponseHelpers.fromErrorDtoAndStatusCode(error, Response.Status.UNAUTHORIZED);
    }
}

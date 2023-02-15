package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserNoPermissionException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
@Provider
public class UserNoPermissionExceptionMapper implements ExceptionMapper<UserNoPermissionException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(UserNoPermissionException e) {
        final String errorMessage = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromGenericException(e, 403, errorMessage);

        return ResponseHelpers.fromErrorDtoAndStatusCode(error, Response.Status.FORBIDDEN);
    }
}

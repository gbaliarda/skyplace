package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.helpers.ResponseHelpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.NotAllowedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotAllowedExceptionMapper implements ExceptionMapper<NotAllowedException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(NotAllowedException e) {
        final String errorMessage = messageSource.getMessage("NotAllowedException.message", null, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromClientErrorException(e, errorMessage);

        return ResponseHelpers.fromErrorDtoAndStatusCode(error, Response.Status.METHOD_NOT_ALLOWED);
    }

}

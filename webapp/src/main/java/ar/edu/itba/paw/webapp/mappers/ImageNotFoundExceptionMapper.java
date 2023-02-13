package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.ImageNotFoundException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public class ImageNotFoundExceptionMapper implements ExceptionMapper<ImageNotFoundException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(ImageNotFoundException e) {
        final String errorMessage = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromGenericException(e, 404, errorMessage);
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));

        return Response.status(Response.Status.NOT_FOUND)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

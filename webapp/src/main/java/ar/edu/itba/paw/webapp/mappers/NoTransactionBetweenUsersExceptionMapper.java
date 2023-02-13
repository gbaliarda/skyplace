package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.NoTransactionBetweenUsersException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import ar.edu.itba.paw.webapp.helpers.ApiReturnCodes;
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
public class NoTransactionBetweenUsersExceptionMapper implements ExceptionMapper<NoTransactionBetweenUsersException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(NoTransactionBetweenUsersException e) {
        final String errorMessage = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromGenericException(e, 400, errorMessage, ApiReturnCodes.NO_TRANSACTION_BETWEEN_USERS.getCode());
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}

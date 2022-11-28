package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.NoTransactionBetweenUsersException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Collections;

@Provider
public class NoTransactionBetweenUsersExceptionMapper implements ExceptionMapper<NoTransactionBetweenUsersException> {

    @Override
    public Response toResponse(NoTransactionBetweenUsersException e) {
        final ErrorDto error = ErrorDto.fromGenericException(e, 400, "51");
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

}

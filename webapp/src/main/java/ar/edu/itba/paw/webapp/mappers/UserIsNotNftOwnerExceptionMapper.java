package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserIsNotNftOwnerException;
import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UserIsNotNftOwnerExceptionMapper implements ExceptionMapper<UserIsNotNftOwnerException> {

    @Override
    public Response toResponse(UserIsNotNftOwnerException e) {
        final ResponseErrorDto error = ResponseErrorDto.fromGenericException(e, 403);

        return Response.status(Response.Status.FORBIDDEN)
                .entity(new GenericEntity<ResponseErrorDto>(error){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

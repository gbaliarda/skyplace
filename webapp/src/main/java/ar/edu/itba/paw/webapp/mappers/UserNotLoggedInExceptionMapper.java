package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.UserNotLoggedInException;
import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class UserNotLoggedInExceptionMapper implements ExceptionMapper<UserNotLoggedInException> {

    @Override
    public Response toResponse(UserNotLoggedInException e) {
        final ResponseErrorDto error = ResponseErrorDto.fromGenericException(e, 401);

        return Response.status(Response.Status.UNAUTHORIZED)
                .entity(new GenericEntity<ResponseErrorDto>(error){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

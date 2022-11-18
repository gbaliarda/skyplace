package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.InvalidReviewException;
import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class InvalidReviewExceptionMapper implements ExceptionMapper<InvalidReviewException> {

    @Override
    public Response toResponse(InvalidReviewException e) {
        final ResponseErrorDto error = ResponseErrorDto.fromGenericException(e, 400);

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(new GenericEntity<ResponseErrorDto>(error){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

package ar.edu.itba.paw.webapp.mappers;

import ar.edu.itba.paw.exceptions.SellOrderHasPendingBuyOrderException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.Collections;

public class SellOrderHasPendingBuyOrderExceptionMapper implements ExceptionMapper<SellOrderHasPendingBuyOrderException> {

    @Override
    public Response toResponse(SellOrderHasPendingBuyOrderException e) {
        final ErrorDto error = ErrorDto.fromGenericException(e, 403, "41");
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));


        return Response.status(Response.Status.FORBIDDEN)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}

package ar.edu.itba.paw.webapp.mappers;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import ar.edu.itba.paw.exceptions.NftAlreadyHasSellOrderException;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import ar.edu.itba.paw.webapp.helpers.ApiReturnCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Collections;

@Provider
public class NftAlreadyHasSellOrderExceptionMapper implements ExceptionMapper<NftAlreadyHasSellOrderException> {

    @Autowired
    private MessageSource messageSource;

    @Override
    public Response toResponse(NftAlreadyHasSellOrderException e) {
        final String errorMessage = messageSource.getMessage(e.getMessage(), null, LocaleContextHolder.getLocale());
        final ErrorDto error = ErrorDto.fromGenericException(e, 403, errorMessage, ApiReturnCodes.NFT_ALREADY_HAS_SELLORDER.getCode());
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(error));

        return Response.status(Response.Status.FORBIDDEN)
                .entity(new GenericEntity<ResponseErrorsDto>(errorList){})
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
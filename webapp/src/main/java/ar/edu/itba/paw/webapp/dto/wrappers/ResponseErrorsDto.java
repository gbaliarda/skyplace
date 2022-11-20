package ar.edu.itba.paw.webapp.dto.wrappers;

import ar.edu.itba.paw.webapp.dto.ErrorDto;

import java.util.List;

public class ResponseErrorsDto {

    private List<ErrorDto> errors;

    public static ResponseErrorsDto fromResponseErrorDtoList(List<ErrorDto> errorList){
        final ResponseErrorsDto dto = new ResponseErrorsDto();
        dto.errors = errorList;
        return dto;
    }

    public List<ErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDto> errors) {
        this.errors = errors;
    }
}

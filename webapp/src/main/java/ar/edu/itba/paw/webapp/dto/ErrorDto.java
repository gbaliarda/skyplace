package ar.edu.itba.paw.webapp.dto;

import ar.edu.itba.paw.webapp.exceptions.InvalidParameterException;
import ar.edu.itba.paw.webapp.helpers.ApiReturnCodes;

import javax.validation.ConstraintViolation;
import javax.ws.rs.ClientErrorException;

public class ErrorDto {

    private Integer id;
    private Integer status;
    private String code;
    private String title;
    private String detail;
    private SourceDto source;

    public static ErrorDto fromGenericException(final RuntimeException e, final int statusCode, final String message){
        final ErrorDto dto = new ErrorDto();

        dto.status = statusCode;
        dto.title = message;
        // dto.title = e.getMessage();

        return dto;
    }

    public static ErrorDto fromGenericException(final RuntimeException e, final int statusCode, final String message, final String internalCode){
        final ErrorDto dto = new ErrorDto();

        dto.status = statusCode;
        dto.title = message;
        // dto.title = e.getMessage();
        dto.code = internalCode;

        return dto;
    }

    public static ErrorDto fromClientErrorException(final ClientErrorException e, final String message){
        final ErrorDto dto = new ErrorDto();

        dto.status = e.getResponse().getStatus();
        dto.title = message;
        // dto.title = e.getResponse().getStatusInfo().getReasonPhrase();

        return dto;
    }

    public static ErrorDto fromInvalidParameterException(final InvalidParameterException e, final String message) {
        final ErrorDto dto = new ErrorDto();

        dto.status = 400;
        dto.code = ApiReturnCodes.EMPTY_PARAMETER.getCode();
        dto.title = message;
        // dto.title = e.getMessage();
        dto.source = new SourceDto();
        dto.source.setParameter(e.getParameter());

        return dto;
    }

    public static ErrorDto fromValidationException(final ConstraintViolation<?> vex) {
        final ErrorDto dto = new ErrorDto();

        dto.status = 400;
        dto.code = ApiReturnCodes.INVALID_PARAMETER.getCode();                        // TODO: Check if this is not needed
        if(vex.getInvalidValue() == null)
            dto.code = ApiReturnCodes.EMPTY_PARAMETER.getCode();
        dto.source = new SourceDto();
        dto.source.setPointer(getPointerString(vex.getPropertyPath().toString()));
        dto.title = vex.getMessage();
        return dto;
    }

    private static String getPointerString(String propertyPath) {
        if(propertyPath == null)
            return "";
        String[] pointerStrings = propertyPath.split("\\.");
        return pointerStrings[pointerStrings.length-1];
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public SourceDto getSource() {
        return source;
    }

    public void setSource(SourceDto source) {
        this.source = source;
    }

}

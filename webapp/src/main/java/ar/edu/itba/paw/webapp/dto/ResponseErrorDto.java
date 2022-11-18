package ar.edu.itba.paw.webapp.dto;

import javax.validation.ConstraintViolation;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;

public class ResponseErrorDto {

    private Integer id;
    private Integer status;
    private Integer code;
    private String title;
    private String detail;
    private SourceDto source;

    public static ResponseErrorDto fromGenericException(final RuntimeException e, final int statusCode){
        final ResponseErrorDto dto = new ResponseErrorDto();

        dto.status = statusCode;
        dto.title = e.getMessage();

        return dto;
    }

    public static ResponseErrorDto fromValidationException(final ConstraintViolation<?> vex) {
        final ResponseErrorDto dto = new ResponseErrorDto();
        Map<String, String> sourceMap = new HashMap<>();

        dto.status = 400;
        sourceMap.put("pointer", getPointerString(vex.getPropertyPath().toString()));
        dto.source = SourceDto.toSourceList(sourceMap);
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

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
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

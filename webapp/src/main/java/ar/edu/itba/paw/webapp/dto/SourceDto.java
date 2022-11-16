package ar.edu.itba.paw.webapp.dto;

import java.util.Map;

public class SourceDto {

    private String pointer;
    private String parameter;
    private String header;

    public static SourceDto toSourceList(final Map<String, String> sourceMap){
        SourceDto dto = new SourceDto();
        dto.pointer = sourceMap.getOrDefault("pointer", "");
        return dto;
    }

    public String getPointer() {
        return pointer;
    }

    public void setPointer(String pointer) {
        this.pointer = pointer;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}

package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;
import com.google.gson.Gson;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class SkyplaceAccessDeniedHandler implements AccessDeniedHandler {

    private final Gson gson = new Gson();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        final ResponseErrorDto dto = ResponseErrorDto.fromGenericException(accessDeniedException, HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().print(gson.toJson(dto));
        response.getWriter().flush();
    }
}

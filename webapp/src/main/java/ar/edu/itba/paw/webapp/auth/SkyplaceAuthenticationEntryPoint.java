package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.dto.ResponseErrorDto;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

public class SkyplaceAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Gson gson = new Gson();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final ResponseErrorDto dto = ResponseErrorDto.fromGenericException(authException, HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().print(gson.toJson(dto));
        response.getWriter().flush();
    }

}

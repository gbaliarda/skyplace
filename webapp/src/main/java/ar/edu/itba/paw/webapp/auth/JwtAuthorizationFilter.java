package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import javafx.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final SkyplaceUserDetailsService userDetailsService;
    private final static Dotenv env = Dotenv.load();

    // TODO: Move this to JwtConfig POJO Object and secret to somewhere else
    private final static String USERAUTH_PREFIX = "basic";
    private final static String JWTAUTH_PREFIX = "bearer";
    private final static String JWT_KEY_PARAMETER = "JWT_KEY";

    private final static int accessTokenValidMinutes = 10;
    private final static int refreshTokenValidMinutes = 15;

    private final Key jwtKey;

    public JwtAuthorizationFilter(SkyplaceUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
        this.jwtKey = new SecretKeySpec(Objects.requireNonNull(env.get(JWT_KEY_PARAMETER)).getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        UsernamePasswordAuthenticationToken token;
        Instant now = Instant.now();

        String accessToken;
        String refreshToken;
        String prefix = "";
        String credentials = "";

        Map<String, Object> claimsMap;

        String headerContent = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(headerContent != null){
            String[] contentInfo = headerContent.split(" ", 2);
            if(contentInfo.length == 2){
                prefix = contentInfo[0].toLowerCase(Locale.ROOT);
                credentials = contentInfo[1];
            }
        }
        try {
            switch (prefix) {
                case USERAUTH_PREFIX:
                    // validate user credentails and, if valid, return new jwt auth and refresh tokens
                    String decodedCredentials = new String(Base64.getDecoder().decode(credentials));
                    String[] userPass = decodedCredentials.split(":", 2);

                    token = userDetailsService.restLogin(userPass[0], userPass[1]);

                    if (token.isAuthenticated())
                        SecurityContextHolder.getContext().setAuthentication(token);

                    claimsMap = new HashMap<>();
                    claimsMap.put("user", userPass[0]);

                    accessToken = JwtUtils.generateAccessToken(claimsMap, "skyplace", userPass[0],
                            Date.from(now.plus(accessTokenValidMinutes, ChronoUnit.MINUTES)), Date.from(now), Date.from(now), jwtKey);
                    refreshToken = JwtUtils.generateRefreshToken(claimsMap, "skyplace", userPass[0],
                            Date.from(now.plus(refreshTokenValidMinutes, ChronoUnit.MINUTES)), Date.from(now), Date.from(now), jwtKey);

                    response.addHeader("Access Token", accessToken);
                    response.addHeader("Renewal Token", refreshToken);
                    break;
                case JWTAUTH_PREFIX:
                    String email;
                    Pair<String, String> tokenSubjectPair = JwtUtils.validateAccessToken(credentials, jwtKey, Date.from(now));
                    switch (tokenSubjectPair.getKey()) {
                        case "access":
                            email = tokenSubjectPair.getValue();
                            token = userDetailsService.jwtLogin(email);
                            if (token.isAuthenticated())
                                SecurityContextHolder.getContext().setAuthentication(token);
                            break;
                        case "refresh":
                            email = tokenSubjectPair.getValue();
                            claimsMap = new HashMap<>();
                            claimsMap.put("user", email);

                            accessToken = JwtUtils.generateAccessToken(claimsMap, "skyplace", email,
                                    Date.from(now.plus(accessTokenValidMinutes, ChronoUnit.MINUTES)), Date.from(now), Date.from(now), jwtKey);
                            refreshToken = JwtUtils.generateRefreshToken(claimsMap, "skyplace", email,
                                    Date.from(now.plus(refreshTokenValidMinutes, ChronoUnit.MINUTES)), Date.from(now), Date.from(now), jwtKey);

                            response.addHeader("Access Token", accessToken);
                            response.addHeader("Renewal Token", refreshToken);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }

            // go to the next filter in the filter chain
            chain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            final Gson gson = new Gson();
            final ErrorDto dto = ErrorDto.fromGenericException(e, HttpServletResponse.SC_UNAUTHORIZED, "14");
            final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(dto));
            response.setContentType(MediaType.APPLICATION_JSON);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print(gson.toJson(errorList));
            response.getWriter().flush();
        }
    }

}

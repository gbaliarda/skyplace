package ar.edu.itba.paw.webapp.auth;

import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.dto.ErrorDto;
import ar.edu.itba.paw.webapp.dto.wrappers.ResponseErrorsDto;
import ar.edu.itba.paw.webapp.helpers.Pair;
import com.google.gson.Gson;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    private final UserService userService;
    private final SkyplaceUserDetailsService userDetailsService;
    private final static Dotenv env = Dotenv.load();

    // TODO: Move this to JwtConfig POJO Object and secret to somewhere else
    private final static String USERAUTH_PREFIX = "basic";
    private final static String JWTAUTH_PREFIX = "bearer";
    private final static String JWT_KEY_PARAMETER = "JWT_KEY";

    private final static String ACCESS_TOKEN_HEADER = "X-Access-Token";
    private final static String REFRESH_TOKEN_HEADER = "X-Renewal-Token";

    private final static String JWT_ISSUER = "skyplace";

    private final static int accessTokenValidMinutes = 60 * 2;              // 2 hs
    private final static int refreshTokenValidMinutes = 60 * 24 * 7;        // 7 days

    private final Key jwtKey;

    public JwtAuthorizationFilter(SkyplaceUserDetailsService userDetailsService, UserService userService) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.jwtKey = new SecretKeySpec(Objects.requireNonNull(env.get(JWT_KEY_PARAMETER)).getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        UsernamePasswordAuthenticationToken token;
        Instant now = Instant.now();

        String prefix = "";
        String credentials = "";

        String headerContent = request.getHeader(HttpHeaders.AUTHORIZATION);

        if(headerContent != null){
            String[] contentInfo = headerContent.split(" ", 2);
            if(contentInfo.length == 2){
                prefix = contentInfo[0].toLowerCase(Locale.ROOT);
                credentials = contentInfo[1];
            }
        }

        switch (prefix) {
            case USERAUTH_PREFIX:
                // validate user credentails and, if valid, return new jwt auth and refresh tokens
                String decodedCredentials = new String(Base64.getDecoder().decode(credentials));
                String[] userPass = decodedCredentials.split(":", 2);
                try {
                    token = userDetailsService.restLogin(userPass[0], userPass[1]);
                } catch (UsernameNotFoundException e) {
                    setErrorResponse(response, e, HttpServletResponse.SC_BAD_REQUEST, "21");
                    return;
                }

                if (token.isAuthenticated())
                    SecurityContextHolder.getContext().setAuthentication(token);

                setResponseTokens(response, userPass[0]);
                break;
            case JWTAUTH_PREFIX:
                String email;
                try {
                    Pair<String, String> tokenSubjectPair = JwtUtils.validateAccessToken(credentials, jwtKey, Date.from(now));
                    email = tokenSubjectPair.getRightValue();
                    token = userDetailsService.jwtLogin(email);

                    if (token.isAuthenticated())
                        SecurityContextHolder.getContext().setAuthentication(token);

                    if (tokenSubjectPair.getLeftValue().equals("refresh"))           // Can also be "access", but currently not used for anything
                        setResponseTokens(response, email);

                } catch (ExpiredJwtException e) {
                    setErrorResponse(response, e, HttpServletResponse.SC_UNAUTHORIZED, "14");
                    return;
                } catch (UsernameNotFoundException e) {
                    setErrorResponse(response, e, HttpServletResponse.SC_BAD_REQUEST, "21");
                    return;
                }
                break;
            default:
                break;
        }
        // go to the next filter in the filter chain
        chain.doFilter(request, response);
    }

    private void setResponseTokens(HttpServletResponse r, String email) {
        Map<String, Object> claimsMap = new HashMap<>();
        Optional<User> maybeUser = userService.getUserByEmail(email);
        maybeUser.ifPresent(user -> {
            claimsMap.put("user", user.getId());
            claimsMap.put("roles", Collections.singletonList(user.getRole()));
        });

        Instant now = Instant.now();
        String accessToken = JwtUtils.generateAccessToken(claimsMap, JWT_ISSUER, email,
                Date.from(now.plus(accessTokenValidMinutes, ChronoUnit.MINUTES)), Date.from(now), Date.from(now), jwtKey);
        String refreshToken = JwtUtils.generateRefreshToken(claimsMap, JWT_ISSUER, email,
                Date.from(now.plus(refreshTokenValidMinutes, ChronoUnit.MINUTES)), Date.from(now), Date.from(now), jwtKey);

        r.addHeader(ACCESS_TOKEN_HEADER, accessToken);
        r.addHeader(REFRESH_TOKEN_HEADER, refreshToken);
    }

    private void setErrorResponse(HttpServletResponse r, RuntimeException e, int statusCode, String internalCode) throws IOException {
        final Gson gson = new Gson();
        final ErrorDto dto = ErrorDto.fromGenericException(e, statusCode, internalCode);
        final ResponseErrorsDto errorList = ResponseErrorsDto.fromResponseErrorDtoList(Collections.singletonList(dto));
        r.setContentType(MediaType.APPLICATION_JSON);
        r.setStatus(statusCode);
        r.getWriter().print(gson.toJson(errorList));
        r.getWriter().flush();
    }

}
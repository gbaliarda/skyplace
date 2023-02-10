package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.service.UserService;
import ar.edu.itba.paw.webapp.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private SkyplaceUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(userDetailsService, userService);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource());
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().headers().cacheControl().disable()
             .and().authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/users/*/buyorders", "/api/users/*/purchases", "/api/users/*/purchases/*", "/api/users/*/favorites").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.GET, "/api/images/*", "/api/nfts", "/api/nfts/*", "/api/users/*", "/api/users/*/reviews", "/api/users/*/reviews/*", "/api/sellorders", "/api/sellorders/*", "/api/sellorders/*/buyorders").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/users/*/favorites/*", "/api/sellorders/*", "/api/sellorders/*/buyorders/*").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.POST, "/api/nfts", "/api/users/*/reviews", "/api/sellorders", "/api/sellorders/*/buyorders", "/api/sellorders/*/buyorders/*").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/nfts/*", "/api/users/*/favorites/*", "/api/sellorders/*", "/api/sellorders/*/buyorders/*").hasAnyRole("USER","ADMIN")
            .and().exceptionHandling()
                .authenticationEntryPoint(new SkyplaceAuthenticationEntryPoint())
                .accessDeniedHandler(new SkyplaceAccessDeniedHandler())
            .and()
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                //.addFilterBefore(this.corsFilter, ChannelProcessingFilter.class)
            .csrf().disable();

    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/favicon.ico", "/css/**", "/index.html", "/js/**", "/resources/**", "/403");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "OPTIONS", "DELETE", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Location", "Link", "X-Access-Token", "X-Renewal-Token", "X-Total-Count", "X-Total-Pages"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

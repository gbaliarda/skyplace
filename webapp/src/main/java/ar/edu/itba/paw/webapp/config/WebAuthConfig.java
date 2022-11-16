package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.JwtAuthorizationFilter;
import ar.edu.itba.paw.webapp.auth.SkyplaceAccessDeniedHandler;
import ar.edu.itba.paw.webapp.auth.SkyplaceAuthenticationEntryPoint;
import ar.edu.itba.paw.webapp.auth.SkyplaceUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

@Configuration
@EnableWebSecurity
@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@EnableGlobalMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true)
public class WebAuthConfig extends WebSecurityConfigurerAdapter {

    public static final String REMEMBERME_KEY_PARAMETER = "REMEMBERME_KEY";

    @Autowired
    private SkyplaceUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(userDetailsService);
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
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().headers().cacheControl().disable()
//        http.sessionManagement()
//                .invalidSessionUrl("/")
             .and().authorizeRequests()
                //.antMatchers("/login", "/register").anonymous()
                // TODO: refactor with new login with API
                .antMatchers("/create", "/buyorder/accept", "/buyorder/validate", "/buyorder/delete","/product/*/delete", "/sell/*", "/sellOrder/*/update", "/sellOrder/*/delete", "/favorite/*/add", "/favorite/*/remove", "/review/*").hasAnyRole("USER","ADMIN")
                .antMatchers(HttpMethod.POST, "/product/*", "/nfts").hasAnyRole("USER","ADMIN")
                .antMatchers("/","/explore","/product/*", "/profile/*", "/images/*").permitAll()
                .antMatchers("/**").authenticated()
//            .and().formLogin()
//                .usernameParameter("email")
//                .passwordParameter("password")
//                .defaultSuccessUrl("/", false)
//                .loginPage("/login")
//            .and().rememberMe()
//                .rememberMeParameter("rememberme")
//                .userDetailsService(userDetailsService)
//                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(7))
//                .key(Dotenv.load().get(REMEMBERME_KEY_PARAMETER))
//            .and().logout()
//                .logoutUrl("/logout")
//                .logoutSuccessUrl("/")
//                .and.csrf.disable()
            .and().exceptionHandling()
                .authenticationEntryPoint(new SkyplaceAuthenticationEntryPoint())
                .accessDeniedHandler(new SkyplaceAccessDeniedHandler())
            .and()
                .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
            .csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/favicon.ico", "/css/**", "/js/**", "/resources/**", "/403", "/images/**");
    }

    @Bean
    public CorsConfiguration corsConfiguration() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedHeader("http://localhost:9000/");
        return corsConfiguration;
    }

}

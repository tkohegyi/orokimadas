package website.magyar.adoration.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Java-based replacement for the former WEB-INF/spring-security.xml.
 * The single custom {@code AdorationCustomAuthenticationProvider} bean is picked up automatically
 * by Spring Security's provider auto-registration, matching the old &lt;authentication-manager&gt; wiring.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /**
     * Defines the application's security filter chain.
     * Uses explicit {@link AntPathRequestMatcher}s rather than the MVC-aware
     * requestMatchers(String...) overload, since this app's security config lives in the root
     * ApplicationContext, separate from the DispatcherServlet's MVC context that would otherwise
     * be needed for Spring Security's MvcRequestMatcher/HandlerMappingIntrospector auto-detection.
     * The original XML config never actually gated any URL via Spring Security's own authorization
     * layer (no active intercept-url beyond the two permitAll patterns below) - every
     * /adorationSecure/** endpoint enforces its own access check in application code
     * (see e.g. AdoratorsController#isPrivilegedAdorator). anyRequest() is therefore permitAll
     * here too, to preserve that exact behavior rather than introducing new Spring-Security-level
     * blocking that didn't exist before.
     *
     * @param http is the security configuration builder
     * @return with the configured filter chain
     * @throws Exception as HttpSecurity#build() may throw one
     */
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/"), new AntPathRequestMatcher("/resources/**")).permitAll()
                        .anyRequest().permitAll())
                .csrf(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .logout(Customizer.withDefaults());
        return http.build();
    }
}

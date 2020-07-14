package com.mskim.search.place.configuration.security;

import com.mskim.search.place.app.auth.domain.Member;
import com.mskim.search.place.app.auth.repository.AuthRepository;
import com.mskim.search.place.configuration.security.support.Role;
import com.mskim.search.place.configuration.security.support.SignInFailureHandler;
import com.mskim.search.place.configuration.security.support.SignInSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_URL_PATH = "/view/auth/sign_in";
    private static final String LOGIN_PROCESS_URL_PATH = "/auth/validation";
    private static final String LOGOUT_URL_PATH = "/auth/sign_out";
    private static final String SUCCESS_REDIRECT_URL = "/";
    private static final String USERNAME_KEY = "id";
    private static final String PASSWORD_KEY = "password";

    private final AuthRepository authRepository;

    @Autowired
    public SecurityConfigurer(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**", "/**/favicon.ico");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers( "/static/**",
                             "/**/favicon.ico",
                             "/error",
                             LOGIN_URL_PATH).permitAll()
                .anyRequest().authenticated()
                .and()
                    .formLogin().usernameParameter(USERNAME_KEY) // Parameter is received from view
                                .passwordParameter(PASSWORD_KEY)
                                .loginPage(LOGIN_URL_PATH)
                                .loginProcessingUrl(LOGIN_PROCESS_URL_PATH) // Request receiving  from form submit
                                .successHandler(new SignInSuccessHandler(SUCCESS_REDIRECT_URL))
                                .failureHandler(new SignInFailureHandler(LOGIN_URL_PATH))
                .and()
                    .logout().logoutUrl(LOGOUT_URL_PATH)
                    .clearAuthentication(true) // Authentication object remove from securityContext
                    .invalidateHttpSession(true) // HttpSession remove
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl(LOGIN_URL_PATH);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String account) throws UsernameNotFoundException {
                Member member = authRepository.findByAccount(account)
                        .orElseThrow(() -> new UsernameNotFoundException(account));
                Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
                grantedAuthorities.add(new SimpleGrantedAuthority(Role.USER.getValue()));

                return new User(member.getAccount(), member.getPassword(), grantedAuthorities);
            }
        };
    }
}

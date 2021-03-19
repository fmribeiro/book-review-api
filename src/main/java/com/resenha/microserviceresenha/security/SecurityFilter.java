package com.resenha.microserviceresenha.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.resenha.microserviceresenha.security.model.Credentials;
import com.resenha.microserviceresenha.security.model.SecurityProperties;
import com.resenha.microserviceresenha.security.model.User;
import com.resenha.microserviceresenha.security.utils.CookieUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CookieUtils cookieUtils;

    @Autowired
    private SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {
        verifyToken(httpServletRequest);
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void verifyToken(HttpServletRequest httpServletRequest) {
        String sessionCookieValue = null;
        FirebaseToken decodedToken = null;
        Credentials.CredentialType type = null;
        boolean strictServerSessionEnabled = securityProperties.getFirebaseProps().isEnableCheckSessionRevoked();
        Cookie sessionCookie = cookieUtils.getCookie("session");
        String token = securityService.getBearerToken(httpServletRequest);

        try{
            if(sessionCookie != null){
                sessionCookieValue = sessionCookie.getValue();
                decodedToken = FirebaseAuth.getInstance()
                        .verifySessionCookie(sessionCookieValue, securityProperties.getFirebaseProps().isEnableCheckSessionRevoked());
                type = Credentials.CredentialType.SESSION;
            }else if( !strictServerSessionEnabled){
                if(token != null && !token.equalsIgnoreCase("undefined")){
                    decodedToken = FirebaseAuth.getInstance().verifyIdToken(token);
                    type = Credentials.CredentialType.ID_TOKEN;
                }
            }
        }catch (FirebaseAuthException e){
            log.error("Firebase Exception: {}", e.getLocalizedMessage());
        }

        User user = firebaseTokenToUserDTO(decodedToken);
        if(user != null){
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user, new Credentials(type, decodedToken, token, sessionCookieValue), null);
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

    }

    private User firebaseTokenToUserDTO(FirebaseToken firebaseToken) {
        User user = null;
        if(firebaseToken != null){
            user = new User();
            user.setUid(firebaseToken.getUid());
            user.setName(firebaseToken.getName());
            user.setEmail(firebaseToken.getEmail());
            user.setPicture(firebaseToken.getPicture());
            user.setIssuer(firebaseToken.getIssuer());
            user.setEmailVerified(firebaseToken.isEmailVerified());
        }
        return user;
    }
}

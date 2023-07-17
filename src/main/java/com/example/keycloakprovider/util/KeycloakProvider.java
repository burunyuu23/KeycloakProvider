package com.example.keycloakprovider.util;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KeycloakProvider {

    private static final String SERVER_URL = AppConfig.SERVER_URL;
    private static final String REALM_NAME = AppConfig.REALM_NAME;
    private static final String REALM_MASTER = AppConfig.REALM_MASTER;
    private static final String ADMIN_CLI = AppConfig.ADMIN_CLI;
    private static final String USER_CONSOLE = AppConfig.USER_CONSOLE;
    private static final String PASSWORD_CONSOLE = AppConfig.PASSWORD_CONSOLE;
    private static final String CLIENT_SECRET = AppConfig.CLIENT_SECRET;

    public static RealmResource getRealmResource() {

        System.out.println(SERVER_URL);
        System.out.println(REALM_NAME);
        System.out.println(REALM_MASTER);
        System.out.println(ADMIN_CLI);
        System.out.println(USER_CONSOLE);
        System.out.println(PASSWORD_CONSOLE);
        System.out.println(CLIENT_SECRET);

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(PASSWORD_CONSOLE)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(new ResteasyClientBuilderImpl()
                        .connectionPoolSize(10)
                        .build())
                .build();

        return keycloak.realm(REALM_NAME);
    }

    public static UsersResource getUserResource() {
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }

    public static boolean isSameUser(String foreignUserId){
        SecurityContext context = SecurityContextHolder.getContext();
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) context.getAuthentication();
        Jwt jwt = jwtAuthenticationToken.getToken();
        String userId = jwt.getSubject();
        System.out.println(userId);
        System.out.println(foreignUserId);
        return userId.equals(foreignUserId);
    }

    public static boolean isAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) context.getAuthentication();
        return jwtAuthenticationToken.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_admin"));
    }
}

package com.example.keycloakprovider.util;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Component
public class KeycloakProvider {

    private static final String SERVER_URL = AppConfig.SERVER_URL;
    private static final String REALM_NAME = AppConfig.REALM_NAME;
    private static final String REALM_MASTER = AppConfig.REALM_MASTER;
    private static final String ADMIN_CLI = AppConfig.ADMIN_CLI;
    private static final String CLIENT_ID = AppConfig.CLIENT_ID;
    private static final String USER_CONSOLE = AppConfig.USER_CONSOLE;
    private static final String PASSWORD_CONSOLE = AppConfig.PASSWORD_CONSOLE;
    private static final String CLIENT_SECRET = AppConfig.CLIENT_SECRET;

    public static RealmResource getRealmResource() {

        ResteasyClient client = new ResteasyClientBuilderImpl()
                .connectionPoolSize(10)
                .build();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_MASTER)
                .clientId(ADMIN_CLI)
                .username(USER_CONSOLE)
                .password(PASSWORD_CONSOLE)
                .clientSecret(CLIENT_SECRET)
                .resteasyClient(client)
                .build();

        return keycloak.realm(REALM_NAME);
    }

    public static AccessTokenResponse authenticateUser(String username, String password) {
        ResteasyClient client = new ResteasyClientBuilderImpl()
                .connectionPoolSize(1)
                .build();

        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(SERVER_URL)
                .realm(REALM_NAME)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .username(username)
                .password(password)
                .grantType(OAuth2Constants.PASSWORD)
                .resteasyClient(client)
                .build();
        return keycloak.tokenManager().getAccessToken();
    }

    public static AccessTokenResponse refreshAccessToken(String refreshToken) {

        ResteasyClient client = new ResteasyClientBuilderImpl().build();
        ResteasyWebTarget target = client.target(SERVER_URL + "/realms/" + REALM_NAME + "/protocol/openid-connect/token");

        Form form = new Form()
                .param("client_id", CLIENT_ID)
                .param("client_secret", CLIENT_SECRET)
                .param("grant_type", "refresh_token")
                .param("refresh_token", refreshToken);

        AccessTokenResponse response = target.request()
                .header("Authorization", "Basic " + encodedClientCredentials())
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED), AccessTokenResponse.class);

        client.close();

        return response;
    }

    public static String getAdminToken() {
        return authenticateUser(USER_CONSOLE, PASSWORD_CONSOLE).getToken();
    }

    public static String encodedClientCredentials(){
        return Base64.getEncoder()
                .encodeToString((ADMIN_CLI + ":" + CLIENT_SECRET).getBytes());
    }

    public static UsersResource getUserResource() {
        RealmResource realmResource = getRealmResource();
        return realmResource.users();
    }

    public static boolean isSameUser(String foreignUserId) {
        SecurityContext context = SecurityContextHolder.getContext();
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) context.getAuthentication();
        Jwt jwt = jwtAuthenticationToken.getToken();
        String userId = jwt.getSubject();
        return userId.equals(foreignUserId);
    }

    public static boolean isAdmin() {
        SecurityContext context = SecurityContextHolder.getContext();
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) context.getAuthentication();
        return jwtAuthenticationToken.getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_admin"));
    }


    public static List<UserRepresentation> findUserByUsername(String username) {
        return getRealmResource()
                .users()
                .searchByUsername(username, true);
    }

    public static List<UserRepresentation> findUserByEmail(String email) {
        return getRealmResource()
                .users()
                .searchByEmail(email, true);
    }
}

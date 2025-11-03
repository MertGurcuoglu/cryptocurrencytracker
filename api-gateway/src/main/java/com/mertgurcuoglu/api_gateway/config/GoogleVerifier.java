package com.mertgurcuoglu.api_gateway.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Component
public class GoogleVerifier {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    public String verify(String idTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(googleClientId))
            .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                return payload.getEmail();
            }
        } catch (GeneralSecurityException | IOException e) {
            System.err.println("Google ID Token doğrulaması sırasında kritik hata: " + e.getMessage());
        }
                return null;
    }
}

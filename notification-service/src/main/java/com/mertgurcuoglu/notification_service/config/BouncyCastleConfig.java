package com.mertgurcuoglu.notification_service.config;

import jakarta.annotation.PostConstruct;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.context.annotation.Configuration;

import java.security.Security;

/**
 * Bu sınıf, Bouncy Castle güvenlik sağlayıcısını uygulama başlangıcında
 * Java Güvenlik listesine ekler. Bu, web-push kütüphanesinin ihtiyaç duyduğu
 * kriptografik algoritmaları bulabilmesini garanti eder.
 */
@Configuration
public class BouncyCastleConfig {

    @PostConstruct
    public void addBouncyCastleAsSecurityProvider() {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
}

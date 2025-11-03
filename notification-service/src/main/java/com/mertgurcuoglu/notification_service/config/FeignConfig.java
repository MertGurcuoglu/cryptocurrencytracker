package com.mertgurcuoglu.notification_service.config;

import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Bu sınıf, OpenFeign'in düzgün çalışması için ihtiyaç duyduğu,
 * ancak WebFlux projesinde otomatik olarak oluşturulmayan
 * HttpMessageConverters bean'ini manuel olarak oluşturur.
 */
@Configuration
public class FeignConfig {

    /**
     * Bu @Bean, Feign'in Java nesnelerini JSON'a ve JSON'u Java nesnelerine
     * çevirmek için kullanacağı "tercümanları" (message converters) sağlar.
     * @return Varsayılan mesaj dönüştürücülerini içeren bir HttpMessageConverters nesnesi.
     */
    @Bean
    public HttpMessageConverters customConverters() {
        return new HttpMessageConverters();
    }
}

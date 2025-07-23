package com.nicoGuti.optica.configuraciones;

import com.mercadopago.core.MPRequestOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class MercadoPagoConfig {

    private MPRequestOptions requestOptions;

    @PostConstruct
    public void init() {
        requestOptions = MPRequestOptions.builder()
                .accessToken("APP_USR-8523288184031418-071512-cb77c8a8a6b3c3dbab8b11f4d0f5f842-2562641270")
                .build();
    }

    public MPRequestOptions getRequestOptions() {
        return requestOptions;
    }
}
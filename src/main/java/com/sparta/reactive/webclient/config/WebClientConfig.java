package com.sparta.reactive.webclient.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class WebClientConfig {

    private static final String CLIENT_REGISTRATION_ID = "springauth";

    @Value("${webclient.rooturl}")
    private String rootUrl;

    @Bean
    public WebClient.Builder webClientBuilder(
            ReactiveOAuth2AuthorizedClientManager authorizedClientManager
    ) {
        log.info("Creating WebClient.Builder with base URL {}", rootUrl);

        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        oauth2.setDefaultClientRegistrationId(CLIENT_REGISTRATION_ID);

        return WebClient.builder()
                .baseUrl(rootUrl)
                .filter(oauth2)
                .filter(logRequest())
                .filter(logResponse());
    }

    private ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("WebClient Request: {} {}", request.method(), request.url());

            request.headers().forEach((name, values) ->
                    values.forEach(value ->
                            log.debug("Request Header: {}={}", name, value)
                    )
            );

            return Mono.just(request);
        });
    }

    private ExchangeFilterFunction logResponse() {
        return ExchangeFilterFunction.ofResponseProcessor(response -> {
            log.info("WebClient Response Status: {}", response.statusCode());

            response.headers().asHttpHeaders().forEach((name, values) ->
                    values.forEach(value ->
                            log.debug("Response Header: {}={}", name, value)
                    )
            );

            return Mono.just(response);
        });
    }
}
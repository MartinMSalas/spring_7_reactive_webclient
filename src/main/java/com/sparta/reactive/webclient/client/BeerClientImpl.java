package com.sparta.reactive.webclient.client;


import com.sparta.reactive.webclient.model.BeerDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.Map;
import java.util.Set;

/*
 * Author: M
 * Date: 11-Apr-26
 * Project Name: webclient
 * Description: beExcellent
 */
@Service
@Slf4j
public class BeerClientImpl implements BeerClient {

    public static final String BEER_PATH = "/api/v3/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";

    private final WebClient webClient;
    private final Validator validator;

    public BeerClientImpl(WebClient.Builder webClientBuilder, Validator validator) {
        this.webClient = webClientBuilder.build();
        this.validator = validator;
    }

    @Override
    public Mono<BeerDTO> createBeer(BeerDTO beerDTO) {
        return webClient.post()
                .uri(BEER_PATH)
                .bodyValue(beerDTO)
                .retrieve()
                .bodyToMono(BeerDTO.class)
                .map(this::validateBeerDto)
                .doOnSubscribe(subscription -> log.info("Calling POST {}", BEER_PATH))
                .doOnSuccess(savedBeer -> log.info("Created beer with id={}",
                        savedBeer != null ? savedBeer.getBeerId() : null))
                .doOnError(error -> log.error("Create beer error", error));
    }

    @Override
    public Mono<BeerDTO> getBeerById(String beerId) {
        return webClient.get()
                .uri(BEER_PATH_ID, beerId)
                .retrieve()
                .bodyToMono(BeerDTO.class)
                .map(this::validateBeerDto)
                .doOnSubscribe(subscription -> log.info("Calling GET {}/{}", BEER_PATH, beerId))
                .doOnSuccess(beer -> log.info("Validated beer received for id={}", beerId))
                .doOnError(error -> log.error("Beer by id error", error));
    }

    @Override
    public Flux<BeerDTO> listBeersDto() {
        return listBeersDto(null, null);
    }

    @Override
    public Flux<BeerDTO> listBeersByBeerName(String beerName) {
        return listBeersDto(beerName, null);
    }

    @Override
    public Flux<BeerDTO> listBeersByBeerStyle(String beerStyle) {
        return listBeersDto(null, beerStyle);
    }

    @Override
    public Flux<BeerDTO> listBeersDto(String beerName, String beerStyle) {
        return webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path(BEER_PATH);

                    if (StringUtils.hasText(beerName)) {
                        builder.queryParam("beerName", beerName);
                    }

                    if (StringUtils.hasText(beerStyle)) {
                        builder.queryParam("beerStyle", beerStyle);
                    }

                    return builder.build();
                })
                .retrieve()
                .bodyToFlux(BeerDTO.class)
                .map(this::validateBeerDto)
                .doOnSubscribe(subscription ->
                        log.info("Calling GET {} with filters beerName='{}', beerStyle='{}'",
                                BEER_PATH, beerName, beerStyle))
                .doOnNext(beer -> log.info("Validated beer received: {}", beer.getBeerName()))
                .doOnComplete(() -> log.info("Beer stream completed"))
                .doOnError(error -> log.error("Beer stream error", error));
    }

    @Override
    public Mono<BeerDTO> updateBeer(String beerId, BeerDTO beerDTO) {
        return webClient.put()
                .uri(BEER_PATH_ID, beerId)
                .bodyValue(beerDTO)
                .retrieve()
                .bodyToMono(BeerDTO.class)
                .map(this::validateBeerDto)
                .doOnSubscribe(subscription -> log.info("Calling PUT {}/{}", BEER_PATH, beerId))
                .doOnSuccess(updatedBeer -> log.info("Updated beer with id={}",
                        updatedBeer != null ? updatedBeer.getBeerId() : null))
                .doOnError(error -> log.error("Update beer error", error));
    }

    @Override
    public Mono<BeerDTO> patchBeer(String beerId, BeerDTO beerDTO) {
        return webClient.patch()
                .uri(BEER_PATH_ID, beerId)
                .bodyValue(beerDTO)
                .retrieve()
                .bodyToMono(BeerDTO.class)
                .map(this::validateBeerDto)
                .doOnSubscribe(subscription -> log.info("Calling PATCH {}/{}", BEER_PATH, beerId))
                .doOnSuccess(patchedBeer -> log.info("Patched beer with id={}",
                        patchedBeer != null ? patchedBeer.getBeerId() : null))
                .doOnError(error -> log.error("Patch beer error", error));
    }

    @Override
    public Mono<BeerDTO> deleteBeer(String beerId) {
        return webClient.delete()
                .uri(BEER_PATH_ID, beerId)
                .retrieve()
                .bodyToMono(BeerDTO.class)
                .map(this::validateBeerDto)
                .doOnSubscribe(subscription -> log.info("Calling DELETE {}/{}", BEER_PATH, beerId))
                .doOnSuccess(deletedBeer -> log.info("Deleted beer with id={}",
                        deletedBeer != null ? deletedBeer.getBeerId() : null))
                .doOnError(error -> log.error("Delete beer error", error));
    }

    private BeerDTO validateBeerDto(BeerDTO beerDTO) {
        Set<ConstraintViolation<BeerDTO>> violations = validator.validate(beerDTO);

        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(
                    "Invalid BeerDTO received from remote service",
                    violations
            );
        }

        return beerDTO;
    }

    @Override
    @Deprecated(since = "0.0.1", forRemoval = false)
    public Flux<String> listBeers() {
        return webClient.get()
                .uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(subscription -> log.info("Calling GET {} as String", BEER_PATH))
                .doOnNext(item -> log.info("String item received: {}", item))
                .doOnComplete(() -> log.info("String stream completed"))
                .doOnError(error -> log.error("String stream error", error));
    }

    // Test methods
    @Override
    public Flux<JsonNode> listBeersJsonNode() {
        return webClient.get()
                .uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnSubscribe(subscription -> log.info("Calling GET {} as JsonNode", BEER_PATH))
                .doOnNext(item -> log.info("JsonNode item received: {}", item))
                .doOnComplete(() -> log.info("JsonNode stream completed"))
                .doOnError(error -> log.error("JsonNode stream error", error));
    }

    @Override
    public Flux<Map<String, Object>> listBeerMap() {
        return webClient.get()
                .uri(BEER_PATH)
                .retrieve()
                .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                .doOnSubscribe(subscription -> log.info("Calling GET {} as Map", BEER_PATH))
                .doOnNext(item -> log.info("Map item received: {}", item))
                .doOnComplete(() -> log.info("Map stream completed"))
                .doOnError(error -> log.error("Map stream error", error));
    }

    @Override
    public Mono<String> listBeersRaw() {
        return webClient.get()
                .uri(BEER_PATH)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSubscribe(subscription -> log.info("Calling GET {} as raw body", BEER_PATH))
                .doOnNext(body -> log.info("Raw body received: {}", body))
                .doOnSuccess(body -> log.info("Raw body completed"))
                .doOnError(error -> log.error("Raw body error", error));
    }
}

package com.sparta.reactive.webclient.client;

import com.sparta.reactive.webclient.model.BeerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.Map;

/*
 * Author: M
 * Date: 11-Apr-26
 * Project Name: webclient
 * Description: beExcellent
 */
public interface BeerClient {

    Mono<BeerDTO> createBeer(BeerDTO beerDTO);

    Mono<BeerDTO> getBeerById(String beerId);

    Flux<BeerDTO> listBeersDto();
    Flux<BeerDTO> listBeersDto(String beerName, String beerStyle);
    Flux<BeerDTO> listBeersByBeerName(String beerName);
    Flux<BeerDTO> listBeersByBeerStyle(String beerStyle);

    Mono<BeerDTO> updateBeer(String beerId, BeerDTO beerDTO);

    Mono<BeerDTO> patchBeer(String beerId, BeerDTO beerDTO);

    Mono<BeerDTO> deleteBeer(String beerId);

    @Deprecated(since = "0.0.1", forRemoval = false)
    Flux<String> listBeers();
    @Deprecated(since = "0.0.1", forRemoval = false)
    Flux<JsonNode> listBeersJsonNode();
    @Deprecated(since = "0.0.1", forRemoval = false)
    Flux<Map<String, Object>> listBeerMap();
    @Deprecated(since = "0.0.1", forRemoval = false)
    Mono<String> listBeersRaw();
}

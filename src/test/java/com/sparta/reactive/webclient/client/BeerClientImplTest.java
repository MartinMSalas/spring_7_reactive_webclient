package com.sparta.reactive.webclient.client;

import com.sparta.reactive.webclient.model.BeerDTO;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
/*
 * Author: M
 * Date: 11-Apr-26
 * Project Name: webclient
 * Description: Integration-style tests for BeerClientImpl against the remote beer service.
 *
 * Notes:
 * - These tests do not assume pre-existing beers in the remote service.
 * - Tests prepare their own data when needed.
 * - The preferred client contract is BeerDTO-based consumption.
 */
@SpringBootTest
@Slf4j
class BeerClientImplTest {

    @Autowired
    private BeerClient beerClient;



    /**
     * Given a beer created specifically for this test,
     * when the client lists beers as BeerDTO,
     * then the created beer should be present in the returned collection as a valid BeerDTO.
     */
    @Test
    @DisplayName("given saved beer when list beers dto then return collection containing valid saved beer dto")
    void givenSavedBeer_whenListBeersDto_thenReturnCollectionContainingValidSavedBeerDto() {
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity();

        StepVerifier.create(
                        beerClient.listBeersDto()
                                .filter(beerDTO -> savedBeer.getBeerId().equals(beerDTO.getBeerId()))
                )
                .assertNext(foundBeer -> {
                    log.info("Matching BeerDTO item: {}", foundBeer);

                    assertValidBeerDto(foundBeer);
                    assertThat(foundBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                    assertThat(foundBeer.getBeerName()).isEqualTo(savedBeer.getBeerName());
                    assertThat(foundBeer.getBeerStyle()).isEqualTo(savedBeer.getBeerStyle());
                    assertThat(foundBeer.getUpc()).isEqualTo(savedBeer.getUpc());
                })
                .verifyComplete();
    }

    /**
     * Given a beer created with a unique name,
     * when the client filters by beer name,
     * then the response should contain the expected saved beer.
     */
    @Test
    @DisplayName("given saved beer with unique name when list beers by beer name then return matching beer dto")
    void givenSavedBeerWithUniqueName_whenListBeersByBeerName_thenReturnMatchingBeerDto() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String uniqueBeerName = "Name-" + uniqueSuffix;
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity(uniqueBeerName, "IPA");

        StepVerifier.create(
                        beerClient.listBeersByBeerName(uniqueBeerName)
                                .filter(beerDTO -> savedBeer.getBeerId().equals(beerDTO.getBeerId()))
                )
                .assertNext(foundBeer -> {
                    log.info("Matching BeerDTO by name: {}", foundBeer);

                    assertValidBeerDto(foundBeer);
                    assertThat(foundBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                    assertThat(foundBeer.getBeerName()).isEqualTo(uniqueBeerName);
                })
                .verifyComplete();
    }

    /**
     * Given a beer created with a unique style,
     * when the client filters by beer style,
     * then the response should contain the expected saved beer.
     */
    @Test
    @DisplayName("given saved beer with unique style when list beers by beer style then return matching beer dto")
    void givenSavedBeerWithUniqueStyle_whenListBeersByBeerStyle_thenReturnMatchingBeerDto() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String uniqueBeerStyle = "Style-" + uniqueSuffix;
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity("StyleTest-" + uniqueSuffix, uniqueBeerStyle);

        StepVerifier.create(
                        beerClient.listBeersByBeerStyle(uniqueBeerStyle)
                                .filter(beerDTO -> savedBeer.getBeerId().equals(beerDTO.getBeerId()))
                )
                .assertNext(foundBeer -> {
                    log.info("Matching BeerDTO by style: {}", foundBeer);

                    assertValidBeerDto(foundBeer);
                    assertThat(foundBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                    assertThat(foundBeer.getBeerStyle()).isEqualTo(uniqueBeerStyle);
                })
                .verifyComplete();
    }

    /**
     * Given both filters are provided and both match a saved beer,
     * when the client calls listBeersDto with both query parameters,
     * then the response should contain the expected saved beer.
     */
    @Test
    @DisplayName("given both filters when both match then list beers dto returns matching beer dto")
    void givenBothFilters_whenBothMatch_thenListBeersDtoReturnsMatchingBeerDto() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
        String uniqueBeerName = "PriorityName-" + uniqueSuffix;
        String savedBeerStyle = "PriorityStyle-" + uniqueSuffix;
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity(uniqueBeerName, savedBeerStyle);

        StepVerifier.create(
                        beerClient.listBeersDto(uniqueBeerName, savedBeerStyle)
                                .filter(beerDTO -> savedBeer.getBeerId().equals(beerDTO.getBeerId()))
                )
                .assertNext(foundBeer -> {
                    log.info("Matching BeerDTO by combined filters: {}", foundBeer);

                    assertValidBeerDto(foundBeer);
                    assertThat(foundBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                    assertThat(foundBeer.getBeerName()).isEqualTo(uniqueBeerName);
                    assertThat(foundBeer.getBeerStyle()).isEqualTo(savedBeerStyle);
                })
                .verifyComplete();
    }

    /**
     * Given a beer created specifically for this test,
     * when the client retrieves the beer by id,
     * then the client should return the matching validated BeerDTO.
     */
    @Test
    @DisplayName("given saved beer when get beer by id then return matching validated beer dto")
    void givenSavedBeer_whenGetBeerById_thenReturnMatchingValidatedBeerDto() {
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity();

        StepVerifier.create(beerClient.getBeerById(savedBeer.getBeerId()))
                .assertNext(foundBeer -> {
                    log.info("Beer found by id: {}", foundBeer);

                    assertValidBeerDto(foundBeer);
                    assertThat(foundBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                    assertThat(foundBeer.getBeerName()).isEqualTo(savedBeer.getBeerName());
                    assertThat(foundBeer.getBeerStyle()).isEqualTo(savedBeer.getBeerStyle());
                    assertThat(foundBeer.getUpc()).isEqualTo(savedBeer.getUpc());
                })
                .verifyComplete();
    }

    /**
     * Given a missing beer id,
     * when the client retrieves the beer by id,
     * then the client should emit an error signal.
     */
    @Test
    @DisplayName("given missing beer id when get beer by id then return error")
    void givenMissingBeerId_whenGetBeerById_thenReturnError() {
        StepVerifier.create(beerClient.getBeerById("missing-id"))
                .expectError()
                .verify();
    }

    /**
     * Given a saved beer and valid updated values,
     * when the client updates the beer,
     * then the client should return the updated validated BeerDTO.
     */
    @Test
    @DisplayName("given saved beer and valid updated values when update beer then return updated validated beer dto")
    void givenSavedBeerAndValidUpdatedValues_whenUpdateBeer_thenReturnUpdatedValidatedBeerDto() {
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity();

        savedBeer.setBeerName("Updated Name");
        savedBeer.setBeerStyle("Double IPA");
        savedBeer.setUpc("UPDATED-UPC-123");
        savedBeer.setPrice(new BigDecimal("15.99"));
        savedBeer.setQuantityOnHand(25);

        StepVerifier.create(beerClient.updateBeer(savedBeer.getBeerId(), savedBeer))
                .assertNext(updatedBeer -> {
                    log.info("Updated beer: {}", updatedBeer);

                    assertValidBeerDto(updatedBeer);
                    assertThat(updatedBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                    assertThat(updatedBeer.getBeerName()).isEqualTo(savedBeer.getBeerName());
                    assertThat(updatedBeer.getBeerStyle()).isEqualTo(savedBeer.getBeerStyle());
                    assertThat(updatedBeer.getUpc()).isEqualTo(savedBeer.getUpc());
                    assertThat(updatedBeer.getPrice()).isEqualByComparingTo(savedBeer.getPrice());
                    assertThat(updatedBeer.getQuantityOnHand()).isEqualTo(savedBeer.getQuantityOnHand());
                })
                .verifyComplete();
    }

    /**
     * Given a saved beer and a partial payload,
     * when the client patches the beer,
     * then the client should return the patched BeerDTO preserving the remaining fields.
     */
    @Test
    @DisplayName("given saved beer and partial payload when patch beer then return patched beer dto")
    void givenSavedBeerAndPartialPayload_whenPatchBeer_thenReturnPatchedBeerDto() {
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity();
        BeerDTO patchPayload = createPatchBeerDto();

        StepVerifier.create(beerClient.patchBeer(savedBeer.getBeerId(), patchPayload))
                .assertNext(patchedBeer -> {
                    log.info("Patched beer: {}", patchedBeer);

                    assertValidBeerDto(patchedBeer);
                    assertThat(patchedBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                    assertThat(patchedBeer.getBeerName()).isEqualTo(patchPayload.getBeerName());
                    assertThat(patchedBeer.getBeerStyle()).isEqualTo(savedBeer.getBeerStyle());
                    assertThat(patchedBeer.getUpc()).isEqualTo(savedBeer.getUpc());
                })
                .verifyComplete();
    }

    /**
     * Given a saved beer,
     * when the client deletes the beer,
     * then the client should return the deleted BeerDTO and the resource should no longer be retrievable.
     */
    @Test
    @DisplayName("given saved beer when delete beer then return deleted beer and resource is no longer retrievable")
    void givenSavedBeer_whenDeleteBeer_thenReturnDeletedBeerAndResourceIsNoLongerRetrievable() {
        BeerDTO savedBeer = createTestBeerAndReturnSavedEntity();

        StepVerifier.create(beerClient.deleteBeer(savedBeer.getBeerId()))
                .assertNext(deletedBeer -> {
                    log.info("Deleted beer: {}", deletedBeer);

                    assertValidBeerDto(deletedBeer);
                    assertThat(deletedBeer.getBeerId()).isEqualTo(savedBeer.getBeerId());
                })
                .verifyComplete();

        StepVerifier.create(beerClient.getBeerById(savedBeer.getBeerId()))
                .expectError()
                .verify();
    }

    /**
     * Given a missing beer id,
     * when the client updates the beer,
     * then the client should emit an error signal.
     */
    @Test
    @DisplayName("given missing beer id when update beer then return error")
    void givenMissingBeerId_whenUpdateBeer_thenReturnError() {
        StepVerifier.create(beerClient.updateBeer("missing-id", getTestBeerDto()))
                .expectError()
                .verify();
    }

    /**
     * Given a missing beer id,
     * when the client patches the beer,
     * then the client should emit an error signal.
     */
    @Test
    @DisplayName("given missing beer id when patch beer then return error")
    void givenMissingBeerId_whenPatchBeer_thenReturnError() {
        StepVerifier.create(beerClient.patchBeer("missing-id", createPatchBeerDto()))
                .expectError()
                .verify();
    }

    /**
     * Given a missing beer id,
     * when the client deletes the beer,
     * then the client should emit an error signal.
     */
    @Test
    @DisplayName("given missing beer id when delete beer then return error")
    void givenMissingBeerId_whenDeleteBeer_thenReturnError() {
        StepVerifier.create(beerClient.deleteBeer("missing-id"))
                .expectError()
                .verify();
    }

    /**
     * Returns a valid BeerDTO fixture for client integration tests.
     * A unique suffix is used to avoid collisions across repeated test executions.
     */
    private BeerDTO getTestBeerDto() {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        return getTestBeerDto("Space Dust " + uniqueSuffix, "IPA");
    }

    /**
     * Returns a valid BeerDTO fixture with explicit name and style values.
     */
    private BeerDTO getTestBeerDto(String beerName, String beerStyle) {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);

        return BeerDTO.builder()
                .beerName(beerName)
                .beerStyle(beerStyle)
                .upc("UPC-" + uniqueSuffix)
                .price(new BigDecimal("10.99"))
                .quantityOnHand(12)
                .build();
    }

    /**
     * Returns a partial payload for patch operations.
     */
    private BeerDTO createPatchBeerDto() {
        return BeerDTO.builder()
                .beerName("Patched Beer Name")
                .build();
    }

    /**
     * Creates a beer through the remote beer API and returns the saved entity.
     */
    private BeerDTO createTestBeerAndReturnSavedEntity() {
        return createTestBeerAndReturnSavedEntity(null, null);
    }

    /**
     * Creates a beer through the remote beer API using provided name/style values.
     */
    private BeerDTO createTestBeerAndReturnSavedEntity(String beerName, String beerStyle) {
        BeerDTO testBeerDto = (beerName == null || beerStyle == null)
                ? getTestBeerDto()
                : getTestBeerDto(beerName, beerStyle);

        BeerDTO savedBeer = beerClient.createBeer(testBeerDto).block();

        if (savedBeer == null) {
            throw new IllegalStateException("Expected saved beer from remote service, but received null");
        }

        if (savedBeer.getBeerId() == null || savedBeer.getBeerId().isBlank()) {
            throw new IllegalStateException("Expected saved beer to contain a valid beerId");
        }

        return savedBeer;
    }

    /**
     * Validates the core expectations for a BeerDTO consumed from the remote service.
     */
    private void assertValidBeerDto(BeerDTO beerDTO) {
        assertThat(beerDTO).isNotNull();
        assertThat(beerDTO.getBeerId()).isNotNull();
        assertThat(beerDTO.getBeerId()).isNotBlank();
        assertThat(beerDTO.getBeerName()).isNotNull();
        assertThat(beerDTO.getBeerName()).isNotBlank();
        assertThat(beerDTO.getBeerStyle()).isNotNull();
        assertThat(beerDTO.getBeerStyle()).isNotBlank();
        assertThat(beerDTO.getUpc()).isNotNull();
        assertThat(beerDTO.getUpc()).isNotBlank();
        assertThat(beerDTO.getPrice()).isNotNull();
        assertThat(beerDTO.getQuantityOnHand()).isNotNull();
        assertThat(beerDTO.getQuantityOnHand()).isGreaterThanOrEqualTo(0);
    }
}

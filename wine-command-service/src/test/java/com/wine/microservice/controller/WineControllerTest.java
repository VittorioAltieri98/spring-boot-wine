package com.wine.microservice.controller;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.model.Wine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class WineControllerTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    TestRestTemplate testRestTemplate;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Test
    void canEstablishConnection(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldCreateWine(){
        WineDTO wineDTO = WineDTO.builder()
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        ResponseEntity<WineDTO> createWineResponse = testRestTemplate.exchange(
               "/wine/create",
               POST,
                new HttpEntity<>(wineDTO),
                WineDTO.class
        );

        assertThat(createWineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WineDTO> createdWine = testRestTemplate.exchange(
                "/wine/" + createWineResponse.getBody().getId(),
                GET,
                null,
                WineDTO.class
        );

        assertThat(createdWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(createdWine.getBody().getWineType()).isEqualTo(wineDTO.getWineType());
        assertThat(createdWine.getBody()).isNotNull();
    }

    @Test
    void shouldUpdateWine() {

        WineDTO dataWineDTO = WineDTO.builder()
                .wineName("Gojo")
                .wineType("Purple")
                .grape("Strongest")
                .region("Tokyo")
                .denomination("DOC")
                .year(1992)
                .alcoholPercentage(17.0)
                .wineDescription("Nah, i'd win")
                .build();

        WineDTO updatedWineDTO = WineDTO.builder()
                .wineName("Tavernello Upa")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2021)
                .alcoholPercentage(17.0)
                .wineDescription("Buonnissimo mamma mia")
                .build();

        ResponseEntity<WineDTO> createWineResponse = testRestTemplate.exchange(
                "/wine/create",
                POST,
                new HttpEntity<>(updatedWineDTO),
                WineDTO.class
        );

        assertThat(createWineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "/wine/" + createWineResponse.getBody().getId(),
                GET,
                null,
                WineDTO.class
        );

        ResponseEntity<WineDTO> updatedWineResponse = testRestTemplate.exchange(
                "/wine/"+ gottenWine.getBody().getId() +"/update",
                PUT,
                new HttpEntity<>(dataWineDTO),
                WineDTO.class
        );

        assertThat(updatedWineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WineDTO> gottenAfterUpdateWine = testRestTemplate.exchange(
                "/wine/" + updatedWineResponse.getBody().getId(),
                GET,
                null,
                WineDTO.class
        );

        assertThat(gottenAfterUpdateWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(gottenWine.getBody().getWineType()).isEqualTo(gottenWine.getBody().getWineType());
        assertThat(gottenAfterUpdateWine.getBody().getWineName()).isNotEqualTo(gottenWine.getBody().getWineType());
    }

    @Test
    void shouldDeleteWine() {
        WineDTO wineDTO = WineDTO.builder()
                .wineName("Sauvignon")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonnissimo mamma mia")
                .build();

        Wine wine = Wine.builder()
                .wineName("Sauvignon")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonnissimo mamma mia")
                .build();

        ResponseEntity<WineDTO> createWineResponse = testRestTemplate.exchange(
                "/wine/create",
                POST,
                new HttpEntity<>(wineDTO),
                WineDTO.class
        );

        assertThat(createWineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "/wine/" + createWineResponse.getBody().getId(),
                GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> deletedWineResponse = testRestTemplate.exchange(
                "/wine/"+ gottenWine.getBody().getId() +"/delete",
                DELETE,
                null,
                Void.class
        );

        assertThat(deletedWineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WineDTO> deletedWine = testRestTemplate.exchange(
                "/wine/" + gottenWine.getBody().getId(),
                GET,
                null,
                WineDTO.class
        );

        assertThat(deletedWine.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldAddLinkToWine() {
        List<String> newLinks = new ArrayList<>();

        WineDTO wineWithLinkDTO = WineDTO.builder()
                .wineName("Peppino")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonnissimo mamma mia")
                .purchaseLinks(newLinks)
                .build();

        ResponseEntity<WineDTO> createWineResponse = testRestTemplate.exchange(
                "/wine/create",
                POST,
                new HttpEntity<>(wineWithLinkDTO),
                WineDTO.class
        );

        assertThat(createWineResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "/wine/" + createWineResponse.getBody().getId(),
                GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WineDTO> addLinkToWine = testRestTemplate.exchange(
                "/wine/"+ createWineResponse.getBody().getId() + "/addLink",
                POST,
                new HttpEntity<>("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html"),
                WineDTO.class
        );

        assertThat(addLinkToWine.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WineDTO> gottenWineWithLink = testRestTemplate.exchange(
                "/wine/" + createWineResponse.getBody().getId(),
                GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWineWithLink.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(gottenWineWithLink.getBody().getPurchaseLinks()).hasSize(1);
        assertThat(gottenWineWithLink.getBody().getPurchaseLinks()).contains("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");
    }
}
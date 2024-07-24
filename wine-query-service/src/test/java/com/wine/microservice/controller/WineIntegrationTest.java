package com.wine.microservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.wine.microservice.WineQueryApplication;
import com.wine.microservice.dto.WineDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.POST;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {WineQueryApplication.class})
@Testcontainers
class WineIntegrationTest {

    @Container
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    TestRestTemplate testRestTemplate;

    private static WireMockServer wireMockServer;


    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }
    @BeforeAll
    public static void startWireMockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8083));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8083);
    }

    @AfterAll
    public static void stopWireMockServer() {
        if(wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void canEstablishConnection(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldGetWineById() {

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

        long wineId = 1L;

        wireMockServer.stubFor(post(urlEqualTo("/wine/create"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Barolo\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> createdWine = testRestTemplate.exchange(
                "http://localhost:8083/wine/create",
                HttpMethod.POST,
                new HttpEntity<>(wineDTO),
                WineDTO.class
        );

        System.out.println("CREAZIONE: " + createdWine.getBody());

        assertThat(createdWine.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<WineDTO> retrievedWine = testRestTemplate.exchange(
                "/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );
        

        System.out.println("RESTITUITO: " + retrievedWine.getBody());

        assertThat(retrievedWine.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(retrievedWine.getBody()).isNotNull();
    }

    @Test
    @Disabled
    void getAllWines() {
    }

    @Test
    @Disabled
    void getWineDetailsWithPairings() {
    }
}
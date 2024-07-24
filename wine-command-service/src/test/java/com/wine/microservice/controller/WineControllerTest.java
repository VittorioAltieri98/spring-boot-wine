package com.wine.microservice.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.model.Wine;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
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

    private static WireMockServer wireMockServer;

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeAll
    public static void startWireMockServer() {
        wireMockServer = new WireMockServer(WireMockConfiguration.wireMockConfig().port(8084));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8084);
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Barolo\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> createdWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(createdWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(createdWine.getBody().getWineType()).isEqualTo(wineDTO.getWineType());
        assertThat(createdWine.getBody()).isNotNull();
    }

    @Test
    void shouldNotCreateWineWhenFieldIsEmpty(){
        WineDTO wineDTO = WineDTO.builder()
                .wineName("")
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

        assertThat(createWineResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotCreateWineWhenAlreadyExists(){
        WineDTO wineDTO = WineDTO.builder()
                .wineName("Chianti")
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Chianti\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        WineDTO sameWineDTO = WineDTO.builder()
                .wineName("Chianti")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        ResponseEntity<WineDTO> createSameWineResponse = testRestTemplate.exchange(
                "/wine/create",
                POST,
                new HttpEntity<>(sameWineDTO),
                WineDTO.class
        );

        assertThat(createSameWineResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Tavernello Upa\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WineDTO> updatedWineResponse = testRestTemplate.exchange(
                "/wine/"+ gottenWine.getBody().getId() +"/update",
                PUT,
                new HttpEntity<>(dataWineDTO),
                WineDTO.class
        );

        assertThat(updatedWineResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        Long otherWineId = updatedWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + otherWineId + "\", \"wineName\": \"Tavernello Upa\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenAfterUpdateWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenAfterUpdateWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(gottenWine.getBody().getWineType()).isEqualTo(gottenWine.getBody().getWineType());
        assertThat(gottenAfterUpdateWine.getBody().getWineName()).isNotEqualTo(gottenWine.getBody().getWineType());
    }

    @Test
    void shouldNotUpdateWineWithEmptyFields() {

        WineDTO dataWineDTO = WineDTO.builder()
                .wineName("")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        WineDTO wineDTO = WineDTO.builder()
                .wineName("Montepulciano")
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Montepulciano\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WineDTO> updatedWineResponse = testRestTemplate.exchange(
                "/wine/"+ gottenWine.getBody().getId() +"/update",
                PUT,
                new HttpEntity<>(dataWineDTO),
                WineDTO.class
        );

        assertThat(updatedWineResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void shouldNotUpdateWineNotExisting() {
        WineDTO wineDTO = WineDTO.builder()
                .wineName("Nero D'Avola")
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

        ResponseEntity<WineDTO> updatedWineResponse = testRestTemplate.exchange(
                "/wine/"+ 100L +"/update",
                PUT,
                new HttpEntity<>(wineDTO),
                WineDTO.class
        );

        assertThat(updatedWineResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Sauvignon\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
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

        Long deletedWineId = gottenWine.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + deletedWineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        ResponseEntity<WineDTO> deletedWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + deletedWineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(deletedWine.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotDeleteWineNotExisting(){
        WineDTO wineDTO = WineDTO.builder()
                .wineName("Federico")
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Federico\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<Void> deletedWineResponse = testRestTemplate.exchange(
                "/wine/"+ 100L +"/delete",
                DELETE,
                null,
                Void.class
        );

        assertThat(deletedWineResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Peppino\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
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

        Long wineWithLinkId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineWithLinkId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineWithLinkId + "\", \"wineName\": \"Peppino\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\", \"purchaseLinks\": [\"https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html\"] }")));

        ResponseEntity<WineDTO> gottenWineWithLink = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWineWithLink.getStatusCode()).isEqualTo(HttpStatus.OK);

        assertThat(gottenWineWithLink.getBody().getPurchaseLinks()).hasSize(1);
        assertThat(gottenWineWithLink.getBody().getPurchaseLinks()).contains("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");
    }

    @Test
    void shouldNotAddInvalidLinkToWine(){
        List<String> newLinks = new ArrayList<>();

        WineDTO wineWithLinkDTO = WineDTO.builder()
                .wineName("Alfredo")
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Alfredo\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WineDTO> addLinkToWine = testRestTemplate.exchange(
                "/wine/"+ createWineResponse.getBody().getId() + "/addLink",
                POST,
                new HttpEntity<>("hhhhh://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html"),
                WineDTO.class
        );

        assertThat(addLinkToWine.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldNotAddLinkAlreadyAddedToWine() {
        List<String> newLinks = new ArrayList<>();

        WineDTO wineWithLinkDTO = WineDTO.builder()
                .wineName("Gianni")
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

        Long wineId = createWineResponse.getBody().getId();

        stubFor(get(urlEqualTo("/wine/" + wineId))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{ \"id\": \"" + wineId + "\", \"wineName\": \"Gianni\", \"wineType\": \"Red\", \"grape\": \"Uva\", \"region\": \"Campania\", \"denomination\": \"DOC\", \"year\": 2020, \"alcoholPercentage\": 17.0, \"wineDescription\": \"Buonissimo mamma mia\" }")));

        ResponseEntity<WineDTO> gottenWine = testRestTemplate.exchange(
                "http://localhost:8084/wine/" + wineId,
                HttpMethod.GET,
                null,
                WineDTO.class
        );

        assertThat(gottenWine.getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<WineDTO> addLinkToWine = testRestTemplate.exchange(
                "/wine/"+ 100L + "/addLink",
                POST,
                new HttpEntity<>("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html"),
                WineDTO.class
        );

        assertThat(addLinkToWine.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
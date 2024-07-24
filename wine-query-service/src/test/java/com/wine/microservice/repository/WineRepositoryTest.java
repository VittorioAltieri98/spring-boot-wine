package com.wine.microservice.repository;

import com.wine.microservice.WineQueryApplication;
import com.wine.microservice.model.Wine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@ContextConfiguration(classes = WineQueryApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WineRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    WineRepository wineRepository;

    Wine wine;

    @BeforeEach
    void setUp() {
         wine = Wine.builder()
                .wineName("Tavernello")
                .wineType("Rosso")
                .grape("Uva")
                .region("Piemonte")
                .denomination("DOC")
                .year(1945)
                .alcoholPercentage(17.0)
                .wineDescription("Azz")
                .build();

        wineRepository.save(wine);
    }

    @AfterEach
    void tearDown() {
        wineRepository.deleteAll();
    }

    @Test
    void canEstablishConnection(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldReturnWineWhenFindByWineName() {
        Optional<Wine> wineByName = wineRepository.findByWineName("Tavernello");

        assertThat(wineByName).isPresent();
    }

    @Test
    void shouldNotReturnWineWhenFindByWineNameIsNotPresent() {
        Optional<Wine> wineByName = wineRepository.findByWineName("Pefforza");

        assertThat(wineByName).isNotPresent();
    }

    @Test
    void shouldFindWineById() {
        Optional<Wine> wineById = wineRepository.findById(wine.getId());

        assertThat(wineById).isPresent();
        assertThat(wineById.get().getWineName()).isEqualTo(wine.getWineName());
    }

    @Test
    void shouldNotFindWineById() {
        Optional<Wine> notFoundWine = wineRepository.findById(10L);

        assertThat(notFoundWine).isNotPresent();
    }

    @Test
    void shouldFindAllWines() {
        Wine test_wine1 = Wine.builder()
                .wineName("Tavernello")
                .wineType("Rosso")
                .grape("Uva")
                .region("Piemonte")
                .denomination("DOC")
                .year(1945)
                .alcoholPercentage(17.0)
                .wineDescription("Azzwqs")
                .build();

        Wine test_wine2 = Wine.builder()
                .wineName("Barolo")
                .wineType("Rosso")
                .grape("Uva")
                .region("Lazio")
                .denomination("DOCG")
                .year(2020)
                .alcoholPercentage(17.4)
                .wineDescription("qwqsqsq")
                .build();

        wineRepository.save(test_wine1);
        wineRepository.save(test_wine2);
        List<Wine> wines = wineRepository.findAll();

        assertThat(wines).hasSize(3);
    }
}
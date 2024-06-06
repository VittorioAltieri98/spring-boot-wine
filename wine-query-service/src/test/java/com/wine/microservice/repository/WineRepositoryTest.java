package com.wine.microservice.repository;

import com.wine.microservice.model.Wine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WineRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    WineRepository wineRepository;

    @BeforeEach
    void setUp() {
        Wine wine = Wine.builder()
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
        //given
        //when
        Optional<Wine> wineByName = wineRepository.findByWineName("Tavernello");
        //then
        assertThat(wineByName).isPresent();
    }

    @Test
    void shouldNotReturnWineWhenFindByWineNameIsNotPresent() {
        //given
        //when
        Optional<Wine> wineByName = wineRepository.findByWineName("Pefforza");
        //then
        assertThat(wineByName).isNotPresent();
    }
}
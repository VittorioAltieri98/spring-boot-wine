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

import java.util.List;
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
        wineRepository.save(wine);
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


    @Test
    void shouldFindWineById() {
        Wine savedWine = wineRepository.save(wine);
        Optional<Wine> wineById = wineRepository.findById(savedWine.getId());

        assertThat(wineById).isPresent();
        assertThat(wineById.get().getWineName()).isEqualTo(savedWine.getWineName());
    }

    @Test
    void shouldNotFindWineById() {
        Optional<Wine> wineById = wineRepository.findById(5L);

        assertThat(wineById).isNotPresent();
    }

    @Test
    void shouldSaveWine() {
        Wine newWine = Wine.builder()
                .wineName("Tavernello")
                .wineType("Rosso")
                .grape("Uva")
                .region("Piemonte")
                .denomination("DOC")
                .year(1945)
                .alcoholPercentage(17.0)
                .wineDescription("Azz")
                .build();

        Wine newWineSaved = wineRepository.save(newWine);
        Optional<Wine> retrievedWine = wineRepository.findById(newWine.getId());

        assertThat(retrievedWine).isPresent();
        assertThat(newWineSaved.getId()).isEqualTo(retrievedWine.get().getId());
    }

    @Test
    void shouldDeleteWine() {
        Wine wineToDelete = wineRepository.save(wine);
        wineRepository.delete(wineToDelete);
        Wine foundWine = wineRepository.findById(wineToDelete.getId()).orElse(null);
        assertThat(foundWine).isNull();
    }

    @Test
    void shouldUpdateWine() {
        Wine test_wineToUpdate = Wine.builder()
                .wineName("Barolo")
                .wineType("Rosso")
                .grape("Uva")
                .region("Lazio")
                .denomination("DOCG")
                .year(2020)
                .alcoholPercentage(17.4)
                .wineDescription("qwqsqsq")
                .build();
        //Salvo il wine presente nel setUp
        Wine wineToUpdate = wineRepository.save(wine);
        //Aggiorno il nome con il nome del vino test_wineToUpdate
        wineToUpdate.setWineName(test_wineToUpdate.getWineName());
        //Salvo il vino aggiornato
        Wine updatedWine = wineRepository.save(wineToUpdate);

        assertThat(updatedWine.getWineName()).isEqualTo(test_wineToUpdate.getWineName());
    }
}
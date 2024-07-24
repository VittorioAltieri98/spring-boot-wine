package com.wine.ai_service.repository;

import com.wine.ai_service.AiServiceApplication;
import com.wine.ai_service.model.WinePairing;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Array;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@ContextConfiguration(classes = AiServiceApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class WinePairingRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    WinePairingRepository winePairingRepository;

    WinePairing winePairing;

    @BeforeEach
    void setUp() {

        List<String> foodPairings = new ArrayList<>();
        Map<String ,String> foodsAndNameDescriptions = new HashMap<>();

        winePairing = WinePairing.builder()
                .wineName("Tavernello")
                .wineType("Red")
                .region("Campania")
                .wineDescription("Popt Buon")
                .foodPairings(foodPairings)
                .foodsNameAndDescriptionOfWhyThePairingIsRecommended(foodsAndNameDescriptions)
                .wineId(5L)
                .build();
        winePairingRepository.save(winePairing);
    }

    @AfterEach
    void tearDown() {
        winePairingRepository.deleteAll();
    }

    @Test
    void canEstablishConnection(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldReturnWinePairingWhenFindByWineId() {

        Optional<WinePairing> winePairingByWineId = winePairingRepository.findByWineId(5L);

        assertThat(winePairingByWineId).isPresent();
    }

    @Test
    void shouldNotReturnWineWhenFindByWineNameIsNotPresent() {
        Optional<WinePairing> winePairingByWineId = winePairingRepository.findByWineId(10L);

        assertThat(winePairingByWineId).isNotPresent();
    }

    @Test
    void shouldFindWineById() {
        Optional<WinePairing> winePairingById = winePairingRepository.findById(winePairing.getId());

        assertThat(winePairingById).isPresent();
        assertThat(winePairingById.get().getWineName()).isEqualTo(winePairing.getWineName());
    }

    @Test
    void shouldNotFindWineById() {
        Optional<WinePairing> notFoundWinePairing = winePairingRepository.findById(10L);

        assertThat(notFoundWinePairing).isNotPresent();
    }
}
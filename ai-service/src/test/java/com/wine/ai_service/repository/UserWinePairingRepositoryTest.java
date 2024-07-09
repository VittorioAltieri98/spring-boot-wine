package com.wine.ai_service.repository;

import com.wine.ai_service.AiServiceApplication;
import com.wine.ai_service.model.UserWinePairing;
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

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@ContextConfiguration(classes = AiServiceApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserWinePairingRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    UserWinePairingRepository userWinePairingRepository;

    UserWinePairing userWinePairing;

    @BeforeEach
    void setUp() {

        List<String> foodPairings = new ArrayList<>();
        Map<String ,String> foodsAndNameDescriptions = new HashMap<>();

        userWinePairing = UserWinePairing.builder()
                .wineName("Tavernello")
                .wineType("Red")
                .region("Campania")
                .denomination("DOCG")
                .wineDescription("Popt Buon")
                .foodPairings(foodPairings)
                .serviceTemperature("16°")
                .pairingDate(LocalDateTime.now())
                .foodsNameAndDescriptionOfWhyThePairingIsRecommended(foodsAndNameDescriptions)
                .userId("asdSWWESQSLA213sl")
                .build();

        userWinePairingRepository.save(userWinePairing);
    }

    @AfterEach
    void tearDown() {
        userWinePairingRepository.deleteAll();
    }

    @Test
    void canEstablishConnection(){
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void shouldReturnAllUserWinePairingsByUserId() {
        List<UserWinePairing> results = userWinePairingRepository.findAllByUserId("asdSWWESQSLA213sl");
        assertThat(results).hasSize(1);
    }

    @Test
    void shouldReturnEmptyListOfAllUserWinePairingsByUserId() {
        List<UserWinePairing> results = userWinePairingRepository.findAllByUserId("asasasa");
        assertThat(results).hasSize(0);
    }

    @Test
    void shouldReturnUserWinePairingWhenFindByWineNameAndUserId() {
        Optional<UserWinePairing> userWinePairingByWineNameAndUserId = userWinePairingRepository.findByWineNameAndUserId("Tavernello","asdSWWESQSLA213sl");
        assertThat(userWinePairingByWineNameAndUserId).isPresent();
    }

    @Test
    void shouldDeleteUserWinePairingByUserId() {
        userWinePairingRepository.deleteByUserId("asdSWWESQSLA213sl");
        UserWinePairing foundedUserWinePairing = userWinePairingRepository.findById(userWinePairing.getId()).orElse(null);
        assertThat(foundedUserWinePairing).isNull();
    }
}
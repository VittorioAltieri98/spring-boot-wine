package com.wine.ai_service.repository;

import com.wine.ai_service.AiServiceApplication;
import com.wine.ai_service.model.PairingRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@ContextConfiguration(classes = AiServiceApplication.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PairingRequestRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));


    @Autowired
    PairingRequestRepository pairingRequestRepository;

    PairingRequest pairingRequest1;
    PairingRequest pairingRequest2;
    PairingRequest pairingRequest3;


    @BeforeEach
    void setUp() {

        pairingRequest1 = PairingRequest.builder()
                .userId("asdSWWESQSLA213sl")
                .wineType("Rosso")
                .region("Marche")
                .requestDate(LocalDateTime.now())
                .build();

        pairingRequest2 = PairingRequest.builder()
                .userId("user2")
                .wineType("Rosso")
                .region("Toscana")
                .requestDate(LocalDateTime.now())
                .build();

        pairingRequest3 = PairingRequest.builder()
                .userId("user3")
                .wineType("Bianco")
                .region("Marche")
                .requestDate(LocalDateTime.now())
                .build();


        pairingRequestRepository.save(pairingRequest1);
        pairingRequestRepository.save(pairingRequest2);
        pairingRequestRepository.save(pairingRequest3);
    }

    @Test
    void findGroupedPairingRequests() {

        List<Object[]> results = pairingRequestRepository.findGroupedPairingRequests();

        assertNotNull(results);
        assertEquals(3, results.size());



        assertEquals("Rosso", results.get(0)[0]);
        assertEquals(1L, results.get(0)[2]);

        assertEquals("Rosso", results.get(1)[0]);
        assertEquals(1L, results.get(1)[2]);

        assertEquals("Bianco", results.get(2)[0]);
        assertEquals(1L, results.get(2)[2]);
    }
}
package com.wine.ai_service.mapper;

import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.model.WinePairing;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WinePairingMapperTest {

    WinePairingMapper winePairingMapper = Mappers.getMapper(WinePairingMapper.class);

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
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldMapWinePairingToWinePairingDTO() {
        WinePairingDTO winePairingDTO = winePairingMapper.winePairingToWinePairingDTO(winePairing);

        assertNotNull(winePairing);
        assertThat(winePairingDTO.getWineName()).isEqualTo(winePairing.getWineName());
        assertThat(winePairingDTO.getWineType()).isEqualTo(winePairing.getWineType());
        assertThat(winePairingDTO.getRegion()).isEqualTo(winePairing.getRegion());
        assertThat(winePairingDTO.getWineDescription()).isEqualTo(winePairing.getWineDescription());
        assertThat(winePairingDTO.getFoodPairings()).isEqualTo(winePairing.getFoodPairings());
        assertThat(winePairingDTO.getFoodsNameAndDescriptionOfWhyThePairingIsRecommended()).isEqualTo(winePairing.getFoodsNameAndDescriptionOfWhyThePairingIsRecommended());
    }
}
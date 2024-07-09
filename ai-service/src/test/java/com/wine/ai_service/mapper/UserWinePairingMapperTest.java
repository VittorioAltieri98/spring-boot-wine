package com.wine.ai_service.mapper;

import com.wine.ai_service.dto.UserWinePairingDTO;
import com.wine.ai_service.model.UserWinePairing;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserWinePairingMapperTest {

    UserWinePairingMapper userWinePairingMapper = Mappers.getMapper(UserWinePairingMapper.class);

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
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void shouldMapWinePairingToWinePairingDTO() {
        UserWinePairingDTO userWinePairingDTO = userWinePairingMapper.userWinePairingToUserWinePairingDTO(userWinePairing);

        assertNotNull(userWinePairing);
        assertThat(userWinePairingDTO.getWineName()).isEqualTo(userWinePairing.getWineName());
        assertThat(userWinePairingDTO.getWineType()).isEqualTo(userWinePairing.getWineType());
        assertThat(userWinePairingDTO.getRegion()).isEqualTo(userWinePairing.getRegion());
        assertThat(userWinePairingDTO.getDenomination()).isEqualTo(userWinePairing.getDenomination());
        assertThat(userWinePairingDTO.getWineDescription()).isEqualTo(userWinePairing.getWineDescription());
        assertThat(userWinePairingDTO.getFoodPairings()).isEqualTo(userWinePairing.getFoodPairings());
        assertThat(userWinePairingDTO.getServiceTemperature()).isEqualTo(userWinePairing.getServiceTemperature());
        assertThat(userWinePairingDTO.getFoodsNameAndDescriptionOfWhyThePairingIsRecommended()).isEqualTo(userWinePairing.getFoodsNameAndDescriptionOfWhyThePairingIsRecommended());
    }
}
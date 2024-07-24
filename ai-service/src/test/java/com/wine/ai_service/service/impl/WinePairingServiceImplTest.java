package com.wine.ai_service.service.impl;

import com.wine.ai_service.dto.PopularPairing;
import com.wine.ai_service.dto.UserWinePairingDTO;
import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.exception.UserWinePairingNotFoundException;
import com.wine.ai_service.exception.WinePairingNotFoundException;
import com.wine.ai_service.mapper.UserWinePairingMapper;
import com.wine.ai_service.mapper.WinePairingMapper;
import com.wine.ai_service.model.UserWinePairing;
import com.wine.ai_service.model.WinePairing;
import com.wine.ai_service.repository.PairingRequestRepository;
import com.wine.ai_service.repository.UserWinePairingRepository;
import com.wine.ai_service.repository.WinePairingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WinePairingServiceImplTest {

    @InjectMocks
    WinePairingServiceImpl winePairingService;

    @Mock
    PairingRequestRepository pairingRequestRepository;

    @Mock
    WinePairingRepository winePairingRepository;

    @Mock
    UserWinePairingRepository userWinePairingRepository;

    @Mock
    WinePairingMapper winePairingMapper;

    @Mock
    UserWinePairingMapper userWinePairingMapper;


    WinePairing winePairing;

    WinePairingDTO winePairingDTO;

    UserWinePairing userWinePairing;

    UserWinePairingDTO userWinePairingDTO;

    WineDTO wineDTO;

    private Jwt jwt;


    @BeforeEach
    void setUp(){

        userWinePairingDTO = UserWinePairingDTO.builder()
                .wineName("Tavernello")
                .wineType("Red")
                .region("Campania")
                .denomination("DOCG")
                .serviceTemperature("16°")
                .wineDescription("Popt Buon")
                .build();

        userWinePairing = UserWinePairing.builder()
                .id(5L)
                .userId("user-id")
                .wineName(userWinePairingDTO.getWineName())
                .wineType(userWinePairingDTO.getWineType())
                .region(userWinePairingDTO.getRegion())
                .denomination(userWinePairingDTO.getDenomination())
                .serviceTemperature(userWinePairingDTO.getServiceTemperature())
                .wineDescription(userWinePairingDTO.getWineDescription())
                .build();


        List<String> foodPairings = new ArrayList<>();
        Map<String ,String> foodsAndNameDescriptions = new HashMap<>();

        winePairingDTO = WinePairingDTO.builder()
                .wineName("Tavernello")
                .wineType("Red")
                .region("Campania")
                .wineDescription("Popt Buon")
                .foodPairings(foodPairings)
                .foodsNameAndDescriptionOfWhyThePairingIsRecommended(foodsAndNameDescriptions)
                .build();

        winePairing = WinePairing.builder()
                .id(5L)
                .wineName(winePairingDTO.getWineName())
                .wineType(winePairingDTO.getWineType())
                .region(winePairingDTO.getRegion())
                .wineDescription(winePairingDTO.getWineDescription())
                .foodPairings(winePairingDTO.getFoodPairings())
                .foodsNameAndDescriptionOfWhyThePairingIsRecommended(winePairingDTO.getFoodsNameAndDescriptionOfWhyThePairingIsRecommended())
                .wineId(5L)
                .build();

        wineDTO = WineDTO.builder()
                .id(winePairing.getWineId())
                .wineName(winePairing.getWineName())
                .wineType(winePairing.getWineType())
                .grape("Uva")
                .region(winePairing.getRegion())
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription(winePairing.getWineDescription())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void deleteUserWinePairing_ShouldDeleteWhenUserIdMatches() throws UserWinePairingNotFoundException {
        when(userWinePairingRepository.findById(5L)).thenReturn(Optional.of(userWinePairing));

        winePairingService.deleteUserWinePairing(5L, "user-id");

        verify(userWinePairingRepository, times(1)).delete(userWinePairing);
    }

    @Test
    void deleteUserWinePairing_ShouldThrowExceptionWhenUserIdDoesNotMatch() {
        when(userWinePairingRepository.findById(5L)).thenReturn(Optional.of(userWinePairing));

        assertThrows(UserWinePairingNotFoundException.class, () -> {
            winePairingService.deleteUserWinePairing(5L, "wrong-user-id");
        });

        verify(userWinePairingRepository, never()).delete(any(UserWinePairing.class));
    }

    @Test
    void deleteUserWinePairing_ShouldThrowExceptionWhenUserWinePairingNotFound() {
        when(userWinePairingRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(UserWinePairingNotFoundException.class, () -> {
            winePairingService.deleteUserWinePairing(5L, "user-id");
        });

        verify(userWinePairingRepository, never()).delete(any(UserWinePairing.class));
    }

    @Test
    void getTopPopularPairings_ShouldReturnListOfPopularPairings() {
        List<Object[]> mockResults = Arrays.asList(
                new Object[]{"Rosso", "Campania", 10L},
                new Object[]{"Bianco", "Toscana", 5L}
        );

        when(pairingRequestRepository.findGroupedPairingRequests()).thenReturn(mockResults);

        List<PopularPairing> result = winePairingService.getTopPopularPairings();

        assertEquals(2, result.size());

        assertEquals("Rosso", result.get(0).getWineType());
        assertEquals("Campania", result.get(0).getRegion());
        assertEquals(10L, result.get(0).getRequestCount());

        assertEquals("Bianco", result.get(1).getWineType());
        assertEquals("Toscana", result.get(1).getRegion());
        assertEquals(5L, result.get(1).getRequestCount());

        verify(pairingRequestRepository, times(1)).findGroupedPairingRequests();
    }

    @Test
    void getUserWinePairings_ShouldReturnListOfUserWinePairingDTOs() {
        jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn("user-id");

        when(userWinePairingRepository.findAllByUserId(anyString())).thenReturn(Collections.singletonList(userWinePairing));
        when(userWinePairingMapper.userWinePairingToUserWinePairingDTO(any(UserWinePairing.class))).thenReturn(userWinePairingDTO);

        List<UserWinePairingDTO> result = winePairingService.getUserWinePairings(jwt);

        assertEquals(1, result.size());
        assertEquals("Tavernello", result.get(0).getWineName());
        verify(userWinePairingRepository, times(1)).findAllByUserId("user-id");
        verify(userWinePairingMapper, times(1)).userWinePairingToUserWinePairingDTO(userWinePairing);
    }


    @Test
    void getWinePairingById() {
        when(winePairingRepository.findById(winePairing.getId())).thenReturn(Optional.of(winePairing));
        when(winePairingMapper.winePairingToWinePairingDTO(any(WinePairing.class))).thenReturn(winePairingDTO);

        WinePairingDTO result = winePairingService.getWinePairingById(winePairing.getId());

        verify(winePairingRepository).findById(winePairing.getId());
        verify(winePairingMapper).winePairingToWinePairingDTO(winePairing);

        assertThat(result).isNotNull();
        assertThat(result.getWineName()).isEqualTo(winePairing.getWineName());
    }

    @Test
    void shouldNotReturnWinePairingByIdAndThrowExceptionWinePairingNotFoundException() {
        //given
        Long id = 70L;
        when(winePairingRepository.findById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> winePairingService.getWinePairingById(id))
                .isInstanceOf(WinePairingNotFoundException.class)
                .hasMessageContaining("WinePairing not found with id " + id);

        verify(winePairingRepository).findById(id);
    }

    @Test
    void getWinePairingByWineId() {
        when(winePairingRepository.findByWineId(winePairing.getWineId())).thenReturn(Optional.of(winePairing));
        when(winePairingMapper.winePairingToWinePairingDTO(any(WinePairing.class))).thenReturn(winePairingDTO);

        WinePairingDTO result = winePairingService.getWinePairingByWineId(winePairing.getWineId());

        verify(winePairingRepository).findByWineId(winePairing.getWineId());
        verify(winePairingMapper).winePairingToWinePairingDTO(winePairing);

        assertThat(result).isNotNull();
        assertThat(result.getWineName()).isEqualTo(winePairing.getWineName());
    }

    @Test
    void shouldNotReturnWinePairingByWineIdAndThrowExceptionWinePairingNotFoundException() {
        //given
        Long wineId = 70L;
        when(winePairingRepository.findByWineId(wineId)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> winePairingService.getWinePairingByWineId(wineId))
                .isInstanceOf(WinePairingNotFoundException.class)
                .hasMessageContaining("WinePairing not found with id " + wineId);

        verify(winePairingRepository).findByWineId(wineId);
    }
}
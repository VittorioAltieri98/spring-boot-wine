package com.wine.ai_service.service.impl;

import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.exception.WinePairingNotFoundException;
import com.wine.ai_service.mapper.WinePairingMapper;
import com.wine.ai_service.model.WinePairing;
import com.wine.ai_service.repository.WinePairingRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WinePairingServiceImplTest {

    @InjectMocks
    WinePairingServiceImpl winePairingService;

    @Mock
    WinePairingRepository winePairingRepository;

    @Mock
    WinePairingMapper winePairingMapper;


    WinePairing winePairing;

    WinePairingDTO winePairingDTO;

    WineDTO wineDTO;


    @BeforeEach
    void setUp(){
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
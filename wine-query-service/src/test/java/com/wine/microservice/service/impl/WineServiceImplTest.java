package com.wine.microservice.service.impl;

import com.wine.microservice.client.WinePairingServiceClient;
import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.mapper.WineMapper;
import com.wine.microservice.model.Wine;
import com.wine.microservice.repository.WineRepository;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WineServiceImplTest {

   @InjectMocks
   WineServiceImpl wineService;

   @Mock
   WineRepository wineRepository;

   @Mock
   WinePairingServiceClient winePairingServiceClient;

   @Mock
   WineMapper wineMapper;

   Wine wine;
   Wine new_wine;

   WineDTO  wineDTO;
   WineDTO new_wineDTO;


    @BeforeEach
    void setUp() {

        List<String> links = new ArrayList<>();
        List<String> new_wine_links = new ArrayList<>();

        wineDTO = new WineDTO();
        wineDTO.setId(5L);
        wineDTO.setWineName("Barolo");
        wineDTO.setWineType("Red");
        wineDTO.setGrape("Uva");
        wineDTO.setRegion("Tuscany");
        wineDTO.setDenomination("DOCG");
        wineDTO.setYear(2020);
        wineDTO.setAlcoholPercentage(13.5);
        wineDTO.setWineDescription("A test wine description.");
        wineDTO.setPurchaseLinks(links);

        wine = Wine.builder()
                .id(wineDTO.getId())
                .wineName(wineDTO.getWineName())
                .wineType(wineDTO.getWineType())
                .grape(wineDTO.getGrape())
                .region(wineDTO.getRegion())
                .denomination(wineDTO.getDenomination())
                .year(wineDTO.getYear())
                .alcoholPercentage(wineDTO.getAlcoholPercentage())
                .wineDescription(wineDTO.getWineDescription())
                .purchaseLinks(wineDTO.getPurchaseLinks())
                .build();

        new_wineDTO = new WineDTO();
        new_wineDTO.setId(7L);
        new_wineDTO.setWineName("Merlot");
        new_wineDTO.setWineType("Red");
        new_wineDTO.setGrape("Uva");
        new_wineDTO.setRegion("Region");
        new_wineDTO.setDenomination("DOC");
        new_wineDTO.setYear(2023);
        new_wineDTO.setAlcoholPercentage(15.5);
        new_wineDTO.setWineDescription("A test wine description.");
        new_wineDTO.setPurchaseLinks(new_wine_links);

        new_wine = Wine.builder()
                .id(new_wineDTO.getId())
                .wineName(new_wineDTO.getWineName())
                .wineType(new_wineDTO.getWineType())
                .grape(new_wineDTO.getGrape())
                .region(new_wineDTO.getRegion())
                .denomination(new_wineDTO.getDenomination())
                .year(new_wineDTO.getYear())
                .alcoholPercentage(new_wineDTO.getAlcoholPercentage())
                .wineDescription(new_wineDTO.getWineDescription())
                .purchaseLinks(new_wineDTO.getPurchaseLinks())
                .build();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldReturnWineById() {
        when(wineRepository.findById(wine.getId())).thenReturn(Optional.of(wine));
        when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(wineDTO);

        WineDTO result = wineService.getWineById(wine.getId());

        verify(wineRepository).findById(wine.getId());
        verify(wineMapper).wineToWineDTO(wine);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(wine.getId());
    }

    @Test
    void shouldNotReturnWineAndThrowExceptionWhenIdDoesNotExists() {
        //given
        Long id = 1L;
        when(wineRepository.findById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> wineService.getWineById(id))
                .isInstanceOf(WineNotFoundException.class)
                .hasMessageContaining("Vino non trovato con l'id: " + id);

        verify(wineRepository).findById(id);
    }

    @Test
    void shouldGetAllWines() {

        List<Wine> wines = new ArrayList<>();
        wines.add(wine);
        wines.add(new_wine);

        when(wineRepository.findAll()).thenReturn(wines);
        when(wineMapper.wineToWineDTO(wine)).thenReturn(wineDTO);
        when(wineMapper.wineToWineDTO(new_wine)).thenReturn(new_wineDTO);
        //when
        List<WineDTO> winesDTO = wineService.getAllWines();
        //then
        verify(wineRepository).findAll();
        verify(wineMapper).wineToWineDTO(wine);
        verify(wineMapper).wineToWineDTO(new_wine);

        assertThat(winesDTO).hasSize(2);
    }
    //    @Test
//    void getWineDetailsWithPairings() {
//    }







//    @Test
//    void shouldSearchWinesWithGivenFilters() {
//        // given
//        String wineName = "Barolo";
//        String wineType = "Red";
//        String grape = "Nebbiolo";
//        String region = "Piedmont";
//        String denomination = "DOCG";
//        int year = 2020;
//        double alcoholPercentage = 14.0;
//
//        Wine matchingWine = new Wine();
//        matchingWine.setId(1L);
//        matchingWine.setWineName(wineName);
//        matchingWine.setWineType(wineType);
//        matchingWine.setGrape(grape);
//        matchingWine.setRegion(region);
//        matchingWine.setDenomination(denomination);
//        matchingWine.setYear(year);
//        matchingWine.setAlcoholPercentage(alcoholPercentage);
//
//        Wine nonMatchingWine = new Wine();
//        nonMatchingWine.setId(2L);
//        nonMatchingWine.setWineName("Chardonnay");
//        nonMatchingWine.setWineType("White");
//        nonMatchingWine.setGrape("Chardonnay");
//        nonMatchingWine.setRegion("Burgundy");
//        nonMatchingWine.setDenomination("AOC");
//        nonMatchingWine.setYear(2019);
//        nonMatchingWine.setAlcoholPercentage(13.5);
//
//        WineDTO matchingWineDTO = new WineDTO();
//        matchingWineDTO.setId(1L);
//        matchingWineDTO.setWineName(wineName);
//        matchingWineDTO.setWineType(wineType);
//        matchingWineDTO.setGrape(grape);
//        matchingWineDTO.setRegion(region);
//        matchingWineDTO.setDenomination(denomination);
//        matchingWineDTO.setYear(year);
//        matchingWineDTO.setAlcoholPercentage(alcoholPercentage);
//
//        List<Wine> expectedWines = Collections.singletonList(matchingWine);
//
//        Specification<Wine> spec = mock(Specification.class);
//        when(wineRepository.findAll(any(Specification.class))).thenReturn(expectedWines);
//        when(wineMapper.wineToWineDTO(matchingWine)).thenReturn(matchingWineDTO);
//
//        // when
//        List<WineDTO> result = wineService.searchWines(wineName, wineType, grape, region, denomination, year, alcoholPercentage);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//        assertThat(result).containsExactly(matchingWineDTO);
//
//        verify(wineRepository, times(1)).findAll(any(Specification.class));
//        verify(wineMapper, times(1)).wineToWineDTO(matchingWine);
//    }

//    @Test
//    void shouldSearchOnlyOneWineWithOneGivenFilter() {
//        // given
//        String wineName = "Barolo";
//        String wineType = "Red";
//        String grape = "Nebbiolo";
//        String region = "Piedmont";
//        String denomination = "DOCG";
//        int year = 2020;
//        double alcoholPercentage = 14.0;
//
//        Wine matchingWine_one = new Wine();
//        matchingWine_one.setId(1L);
//        matchingWine_one.setWineName(wineName);
//        matchingWine_one.setWineType(wineType);
//        matchingWine_one.setGrape(grape);
//        matchingWine_one.setRegion(region);
//        matchingWine_one.setDenomination(denomination);
//        matchingWine_one.setYear(year);
//        matchingWine_one.setAlcoholPercentage(alcoholPercentage);
//
//        Wine matchingWine_two = new Wine();
//        matchingWine_two.setId(2L);
//        matchingWine_two.setWineName("Chardonnay");
//        matchingWine_two.setWineType("Red");
//        matchingWine_two.setGrape("Chardonnay");
//        matchingWine_two.setRegion("Burgundy");
//        matchingWine_two.setDenomination("AOC");
//        matchingWine_two.setYear(2019);
//        matchingWine_two.setAlcoholPercentage(13.5);
//
//        WineDTO matchingWineDTO_one = new WineDTO();
//        matchingWineDTO_one.setId(1L);
//        matchingWineDTO_one.setWineName(wineName);
//        matchingWineDTO_one.setWineType(wineType);
//        matchingWineDTO_one.setGrape(grape);
//        matchingWineDTO_one.setRegion(region);
//        matchingWineDTO_one.setDenomination(denomination);
//        matchingWineDTO_one.setYear(year);
//        matchingWineDTO_one.setAlcoholPercentage(alcoholPercentage);
//
//        WineDTO matchingWineDTO_two = new WineDTO();
//        matchingWineDTO_two.setId(2L);
//        matchingWineDTO_two.setWineName("Chardonnay");
//        matchingWineDTO_two.setWineType("Red");
//        matchingWineDTO_two.setGrape("Chardonnay");
//        matchingWineDTO_two.setRegion("Burgundy");
//        matchingWineDTO_two.setDenomination("AOC");
//        matchingWineDTO_two.setYear(2019);
//        matchingWineDTO_two.setAlcoholPercentage(13.5);
//
//        List<Wine> expectedWines = Collections.singletonList(matchingWine_one);
//
//        Specification<Wine> spec = mock(Specification.class);
//        when(wineRepository.findAll(any(Specification.class))).thenReturn(expectedWines);
//        when(wineMapper.wineToWineDTO(matchingWine_one)).thenReturn(matchingWineDTO_one);
//
//        // when
//        List<WineDTO> result = wineService.searchWines(wineName, null, null, null, null, 0, 0.0);
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result).hasSize(1);
//        assertThat(result).containsExactly(matchingWineDTO_one);
//
//        verify(wineRepository, times(1)).findAll(any(Specification.class));
//        verify(wineMapper, times(1)).wineToWineDTO(matchingWine_one);
//    }

//    @Test
//    void searchWines() {
//    }

}
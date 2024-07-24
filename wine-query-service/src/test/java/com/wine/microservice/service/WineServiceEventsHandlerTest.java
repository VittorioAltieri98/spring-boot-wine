package com.wine.microservice.service;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineEvent;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.mapper.WineMapper;
import com.wine.microservice.model.Wine;
import com.wine.microservice.repository.WineRepository;
import com.wine.microservice.utils.EventType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WineServiceEventsHandlerTest {

    @InjectMocks
    WineServiceEventsHandler wineServiceEventsHandler;

    @Mock
    WineRepository wineRepository;

    @Mock
    WineMapper wineMapper;

    WineDTO  wineDTO;
    Wine wine;


    @BeforeEach
    void setUp() {

        List<String> links = new ArrayList<>();

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
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void shouldProcessCreateWineEvents() {
        WineEvent createEvent = WineEvent.builder().eventType(EventType.CREATE_WINE).wineDTO(wineDTO).build();

        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        when(wineMapper.wineDTOtoWine(any(WineDTO.class))).thenReturn(wine);
        wineServiceEventsHandler.processWineEvents(createEvent);
    }

    @Test
    void shouldProcessUpdateWineEvents() {
        WineEvent updateEvent = WineEvent.builder().eventType(EventType.UPDATE_WINE).wineDTO(wineDTO).build();

        when(wineRepository.findById(updateEvent.getWineDTO().getId())).thenReturn(Optional.of(wine));
        when(wineRepository.save(wine)).thenReturn(wine);
        wineServiceEventsHandler.processWineEvents(updateEvent);
    }

    @Test
    void shouldProcessDeleteWineEvents() {
        WineEvent deleteEvent = WineEvent.builder().eventType(EventType.DELETE_WINE).wineDTO(wineDTO).build();

        when(wineRepository.findById(deleteEvent.getWineDTO().getId())).thenReturn(Optional.of(wine));
        wineServiceEventsHandler.processWineEvents(deleteEvent);
    }

    @Test
    void shouldProcessAddLinkWineEvents() {
        WineEvent addLinkEvent = WineEvent.builder()
                .eventType(EventType.ADD_LINK_WINE)
                .wineDTO(wineDTO)
                .wineLink("https://www.tannico.it/marlborough-sauvignon-blanc-2023-cloudy-bay-tappo-a-vite.html")
                .build();

        when(wineRepository.findById(addLinkEvent.getWineDTO().getId())).thenReturn(Optional.of(wine));
        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        wineServiceEventsHandler.processWineEvents(addLinkEvent);
    }



    @Test
    void shouldProcessCreateWineEvent() {
        WineEvent wineEvent = WineEvent.builder()
                .eventType(EventType.CREATE_WINE)
                .wineDTO(wineDTO)
                .build();

        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        when(wineMapper.wineDTOtoWine(any(WineDTO.class))).thenReturn(wine);

        wineServiceEventsHandler.processCreateWineEvent(wineEvent);

        verify(wineRepository).save(wine);
        verify(wineMapper).wineDTOtoWine(wineDTO);
    }

    @Test
    void shouldProcessUpdateWineEvent() {
        WineEvent wineEvent = WineEvent.builder()
                .eventType(EventType.UPDATE_WINE)
                .wineDTO(wineDTO)
                .build();

        when(wineRepository.findById(wineDTO.getId())).thenReturn(Optional.of(wine));
        when(wineRepository.save(any(Wine.class))).thenReturn(wine);


        wineServiceEventsHandler.processUpdateWineEvent(wineEvent);

        verify(wineRepository).findById(wineDTO.getId());
        verify(wineRepository).save(wine);

        assertThat(wineEvent.getWineDTO().getWineName()).isEqualTo(wine.getWineName());
    }

    @Test
    void shouldProcessDeleteWineEvent() {
        WineEvent wineEvent = WineEvent.builder()
                .eventType(EventType.DELETE_WINE)
                .wineDTO(wineDTO)
                .build();

        when(wineRepository.findById(wineDTO.getId())).thenReturn(Optional.of(wine));

        wineServiceEventsHandler.processDeleteWineEvent(wineEvent);

        verify(wineRepository).findById(wineDTO.getId());
        verify(wineRepository).delete(wine);
    }

    @Test
    void shouldProcessAddLinkToWineEvent() {
        WineEvent wineEvent = WineEvent.builder()
                .eventType(EventType.ADD_LINK_WINE)
                .wineDTO(wineDTO)
                .wineLink("https://www.tannico.it/marlborough-sauvignon-blanc-2023-cloudy-bay-tappo-a-vite.html")
                .build();

        when(wineRepository.findById(wineDTO.getId())).thenReturn(Optional.of(wine));

        when(wineRepository.save(any(Wine.class))).thenReturn(wine);

        wineServiceEventsHandler.processAddLinkToWineEvent(wineEvent);

        verify(wineRepository).findById(wineDTO.getId());
        verify(wineRepository).save(wine);

        assertThat(wine.getPurchaseLinks()).hasSize(1);
    }
}
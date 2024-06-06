package com.wine.microservice.service.impl;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineEvent;
import com.wine.microservice.exception.WineAlreadyExistsException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "kafka.wine.topic.name=test-topic")
class WineServiceImplTest {

    @InjectMocks
    WineServiceImpl wineService;

    @Mock
    WineRepository wineRepository;

    @Mock
    WineMapper wineMapper;

    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;

    WineDTO wineDTO;
    Wine wine;

    @Value("${kafka.wine.topic.name}")
    private String wineTopicName;

    @BeforeEach
    void setUp() {

        wineDTO = new WineDTO();
        wineDTO.setWineName("Test Wine");
        wineDTO.setWineType("Red");
        wineDTO.setGrape("Merlot");
        wineDTO.setRegion("Tuscany");
        wineDTO.setDenomination("DOCG");
        wineDTO.setYear(2020);
        wineDTO.setAlcoholPercentage(13.5);
        wineDTO.setWineDescription("A test wine description.");

        wine = Wine.builder()
                .wineName(wineDTO.getWineName())
                .wineType(wineDTO.getWineType())
                .grape(wineDTO.getGrape())
                .region(wineDTO.getRegion())
                .denomination(wineDTO.getDenomination())
                .year(wineDTO.getYear())
                .alcoholPercentage(wineDTO.getAlcoholPercentage())
                .wineDescription(wineDTO.getWineDescription())
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void testCreateWineSuccess() throws WineAlreadyExistsException {

        when(wineRepository.findByWineName(wineDTO.getWineName())).thenReturn(Optional.empty());
        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(wineDTO);

        WineDTO result = wineService.createWine(wineDTO);

        verify(wineRepository).findByWineName(wineDTO.getWineName());
        verify(wineRepository).save(any(Wine.class));
        verify(wineMapper, times(2)).wineToWineDTO(any(Wine.class));

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<WineEvent> eventCaptor = ArgumentCaptor.forClass(WineEvent.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

        assertEquals(wineTopicName, topicCaptor.getValue());
        WineEvent capturedEvent = eventCaptor.getValue();
        assertEquals(EventType.CREATE_WINE, capturedEvent.getEventType());
        assertEquals(wineDTO, capturedEvent.getWineDTO());

        assertThat(result.getWineType()).isEqualTo(wineDTO.getWineType());
        assertThat(result.getGrape()).isEqualTo(wineDTO.getGrape());

        assertEquals(wineDTO, result);
    }

    @Test
    void shouldNotCreateWineAndThrowExceptionWhenWineFindByWineNameIsPresent() {
        //given
        //when
        when(wineRepository.findByWineName(anyString())).thenReturn(Optional.of(new Wine()));
        //then
        assertThatThrownBy(() ->
                wineService.createWine(wineDTO))
                .isInstanceOf(WineAlreadyExistsException.class)
                .hasMessageContaining("Il vino " + wineDTO.getWineName() + " è già esistente.");
    }

    @Test
    void shouldThrowNotFoundWhenGivenInvalidWhileUpdateWine() {
        //given
        Long id = 5L;
        //when
        when(wineRepository.findById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() ->
                wineService.updateWine(id, wineDTO)
        ).isInstanceOf(WineNotFoundException.class)
                .hasMessage("Vino non trovato con l'id: " + id);

        verify(wineRepository, never()).save(any());
    }

   @Test
   void shouldOnlyUpdateWineName() {
       //given
       long id = 5L;
       String newName = "Vittorio";

       WineDTO newWineDTO = new WineDTO();
       newWineDTO.setWineName(newName);
       newWineDTO.setWineType("Bianco");

       Wine newWine = Wine.builder()
               .id(id)
               .wineName("Federico")
               .wineType("Bianco")
               .grape("Federico")
               .region("Federico")
               .denomination("DOC")
               .year(1998)
               .alcoholPercentage(14.0)
               .wineDescription("Federico")
               .build();

       when(wineRepository.findById(id)).thenReturn(Optional.of(newWine));
       when(wineRepository.save(any(Wine.class))).thenReturn(newWine);
       when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(newWineDTO);
       //when
       WineDTO result = wineService.updateWine(newWine.getId(), newWineDTO);
       //then
       verify(wineRepository).findById(id);
       verify(wineRepository).save(any(Wine.class));
       verify(wineMapper, times(2)).wineToWineDTO(any(Wine.class));

       ArgumentCaptor<Wine> wineCaptor = ArgumentCaptor.forClass(Wine.class);
       verify(wineRepository).save(wineCaptor.capture());
       Wine capturedWine = wineCaptor.getValue();

       ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
       ArgumentCaptor<WineEvent> eventCaptor = ArgumentCaptor.forClass(WineEvent.class);
       verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

       assertEquals(wineTopicName, topicCaptor.getValue());
       WineEvent capturedEvent = eventCaptor.getValue();
       assertEquals(EventType.UPDATE_WINE, capturedEvent.getEventType());
       assertEquals(newWineDTO, capturedEvent.getWineDTO());

       assertThat(capturedWine.getWineName()).isEqualTo(newName);
       assertThat(capturedWine.getWineType()).isEqualTo(newWine.getWineType());
       assertThat(capturedWine.getGrape()).isEqualTo(newWine.getGrape());
       assertThat(capturedWine.getRegion()).isEqualTo(newWine.getRegion());
       assertThat(capturedWine.getDenomination()).isEqualTo(newWine.getDenomination());
       assertThat(capturedWine.getYear()).isEqualTo(newWine.getYear());
       assertThat(capturedWine.getAlcoholPercentage()).isEqualTo(newWine.getAlcoholPercentage());
       assertThat(capturedWine.getWineDescription()).isEqualTo(newWine.getWineDescription());

       assertThat(result.getWineName()).isEqualTo(newName);
       assertThat(result.getWineType()).isEqualTo(newWine.getWineType());
   }

    @Test
    void shouldDeleteWine() {
        Long id = 1L;

        when(wineRepository.findById(id)).thenReturn(Optional.of(wine));

        wineService.deleteWine(id);

        verify(wineRepository).delete(wine);
    }

    @Test
    void shouldNotDeleteWineAndThrowExceptionWhenIdDoesNotExists() {
        //given
        Long id = 5L;
        //when
        when(wineRepository.findById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() ->
                wineService.deleteWine(id))
                .isInstanceOf(WineNotFoundException.class)
                .hasMessageContaining("Vino non trovato con l'id: " + id);
        verify(wineRepository, never()).delete(any(Wine.class));
    }

    @Test
    @Disabled
    void addLinkToWine() {
    }

    @Test
    @Disabled
    void isValidLink() {
    }
}
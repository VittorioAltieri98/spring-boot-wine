package com.wine.microservice.service.impl;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.dto.WineEvent;
import com.wine.microservice.exception.LinkAlreadyExistsException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang.StringUtils.substring;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
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

        List<String> links = new ArrayList<>();

        wineDTO = new WineDTO();
        wineDTO.setId(5L);
        wineDTO.setWineName("Test Wine");
        wineDTO.setWineType("Red");
        wineDTO.setGrape("Merlot");
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
    public void testCreateWineSendsKafkaEvent() throws WineAlreadyExistsException {
        // Arrange
        when(wineRepository.findByWineName(wineDTO.getWineName())).thenReturn(Optional.empty());
        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(wineDTO);

        // Act
        wineService.createWine(wineDTO);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<WineEvent> eventCaptor = ArgumentCaptor.forClass(WineEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

        WineEvent capturedEvent = eventCaptor.getValue();

        assertEquals(wineTopicName, topicCaptor.getValue());
        assertEquals(EventType.CREATE_WINE, capturedEvent.getEventType());
        assertEquals(wineDTO, capturedEvent.getWineDTO());
    }

    @Test
    public void testCreateWineSavesWine() throws WineAlreadyExistsException {

        when(wineRepository.findByWineName(wineDTO.getWineName())).thenReturn(Optional.empty());
        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(wineDTO);

        WineDTO result = wineService.createWine(wineDTO);

        // Assert
        verify(wineRepository).findByWineName(wineDTO.getWineName());
        verify(wineRepository).save(any(Wine.class));

        assertEquals(wineDTO, result);
        assertThat(result.getWineType()).isEqualTo(wineDTO.getWineType());
        assertThat(result.getGrape()).isEqualTo(wineDTO.getGrape());
    }

    @Test
    void shouldNotCreateWineAndThrowExceptionWhenWineFindByWineNameIsPresent() throws WineAlreadyExistsException {
        //given -- Simula il caso in cui il repository contenga già un vino per qualsiasi nome
        when(wineRepository.findByWineName(anyString())).thenReturn(Optional.of(new Wine()));

        //when and then
        assertThatThrownBy(() -> wineService.createWine(wineDTO))
                .isInstanceOf(WineAlreadyExistsException.class)
                .hasMessageContaining("Il vino " + wineDTO.getWineName() + " è già esistente.");

        // Verifica che il metodo findByWineName sia stato chiamato una volta
        verify(wineRepository).findByWineName(anyString());
        // Verifica che il metodo save non sia stato mai chiamato
        verify(wineRepository, never()).save(any(Wine.class));
        // Verifica che l'evento Kafka non sia stato inviato
        verify(kafkaTemplate, never()).send(anyString(), any(WineEvent.class));
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
        //given
        when(wineRepository.findById(wine.getId())).thenReturn(Optional.of(wine));
        //when
        wineService.deleteWine(wine.getId());
        //then
        verify(wineRepository).findById(wine.getId());
        verify(wineRepository).delete(wine);
        verify(wineRepository).delete(eq(wine));
    }

    @Test
    void shouldNotDeleteWineAndThrowExceptionWhenIdDoesNotExists() {
        //given
        Long id = 5L;
        when(wineRepository.findById(id)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(() -> wineService.deleteWine(id))
                .isInstanceOf(WineNotFoundException.class)
                .hasMessageContaining("Vino non trovato con l'id: " + id);

        verify(wineRepository, never()).delete(any(Wine.class));
        verify(kafkaTemplate, never()).send(anyString(), any(WineEvent.class));
    }

    @Test
    void shouldAddLinkToWine() {
        Long id = 5L;
        String link = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        when(wineRepository.findById(id)).thenReturn(Optional.of(wine));
        when(wineRepository.save(wine)).thenReturn(wine);
        when(wineMapper.wineToWineDTO(wine)).thenReturn(wineDTO);

        WineDTO result = wineService.addLinkToWine(id, link);

        verify(wineRepository).findById(id);
        verify(wineRepository).save(wine);
        verify(wineMapper, times(2)).wineToWineDTO(wine);

        assertThat(result.getPurchaseLinks().get(0)).isEqualTo(link);
    }

    @Test
    public void shouldAddLinkTiWineAndSendsKafkaEvent() {
        Long id = 5L;
        String link = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        when(wineRepository.findById(id)).thenReturn(Optional.of(wine));
        when(wineRepository.save(wine)).thenReturn(wine);
        when(wineMapper.wineToWineDTO(wine)).thenReturn(wineDTO);

        WineDTO result = wineService.addLinkToWine(id, link);

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<WineEvent> eventCaptor = ArgumentCaptor.forClass(WineEvent.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

        WineEvent capturedEvent = eventCaptor.getValue();

        assertEquals(wineTopicName, topicCaptor.getValue());
        assertEquals(EventType.ADD_LINK_WINE, capturedEvent.getEventType());
        assertEquals(wineDTO, capturedEvent.getWineDTO());
    }

    @Test
    void shouldNotAddLinkToWineAndThrowExceptionWhenIdIsNotFound() {
        Long id = 10L;
        List<String> purchaseLinks = wine.getPurchaseLinks();
        purchaseLinks.add("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");


        when(wineRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> wineService.addLinkToWine(id, purchaseLinks.get(0)))
                .isInstanceOf(WineNotFoundException.class)
                .hasMessageContaining("Vino non trovato con l'id:" + id);

        verify(wineRepository, never()).save(wine);
    }

    @Test
    void shouldNotAddLinkToWineAndThrowExceptionWhenLinkAlreadyExists() {
        Long id = 6L;
        List<String> purchaseLinks = wine.getPurchaseLinks();
        purchaseLinks.add("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");


        when(wineRepository.findById(id)).thenReturn(Optional.of(wine));
        assertThatThrownBy(() -> wineService.addLinkToWine(id, purchaseLinks.get(0)))
                .isInstanceOf(LinkAlreadyExistsException.class)
                .hasMessageContaining("Link already exists: " + purchaseLinks.get(0).trim());

        verify(wineRepository, never()).save(wine);
    }

    @Test
    void shouldNotAddLinkToWineAndThrowExceptionWhenInvalidLinkFormat() {
        Long id = 6L;
        List<String> purchaseLinks = wine.getPurchaseLinks();
        purchaseLinks.add("httsdfdfd://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");


        when(wineRepository.findById(id)).thenReturn(Optional.of(wine));
        assertThatThrownBy(() -> wineService.addLinkToWine(id, purchaseLinks.get(0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid link format: " + purchaseLinks.get(0).trim());

        verify(wineRepository, never()).save(wine);
    }

    @Test
    void isValidLink() {
        List<String> purchaseLinks = wine.getPurchaseLinks();
        purchaseLinks.add("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");
        purchaseLinks.add("http://www.tannico.it/marlborough-sauvignon-blanc-2023-cloudy-bay-tappo-a-vite.html");

        String link = purchaseLinks.get(0);
        String link2 = purchaseLinks.get(1);
        //String substringLink = link.substring(0, 7);
        //assertEquals("https://", substringLink);
        assertTrue(link.startsWith("https://"));
        assertTrue(link2.startsWith("http://"));

    }

    @Test
    void isNotValidLink() {
        List<String> purchaseLinks = wine.getPurchaseLinks();
        purchaseLinks.add("hts://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");
        String link = purchaseLinks.get(0);

        assertFalse(link.startsWith("https://"));
        assertFalse(link.startsWith("http://"));
    }
}
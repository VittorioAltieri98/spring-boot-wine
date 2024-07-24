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
        wineDTO.setWineName("Barolo");
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
        assertThatThrownBy(() -> wineService.updateWine(id, wineDTO))
                .isInstanceOf(WineNotFoundException.class)
                .hasMessage("Vino non trovato con l'id: " + id);

        verify(wineRepository, never()).save(any());
    }

   @Test
   void shouldOnlyUpdateWineName() {
       //given
       String newName = "Sauvignon";

       WineDTO newWineDTO = WineDTO.builder()
               .id(wineDTO.getId())
               .wineName(newName)
               .wineType("Red")
               .grape("Merlot")
               .region("Tuscany")
               .denomination("DOCG")
               .year(2020)
               .alcoholPercentage(13.5)
               .wineDescription("A test wine description.")
               .purchaseLinks(wineDTO.getPurchaseLinks())
               .build();

       when(wineRepository.findById(wine.getId())).thenReturn(Optional.of(wine));
       when(wineRepository.save(any(Wine.class))).thenReturn(wine);
       when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(newWineDTO);
       //when
       WineDTO result = wineService.updateWine(wine.getId(), newWineDTO);
       //then
       verify(wineRepository).findById(wine.getId());
       verify(wineRepository).save(any(Wine.class));
       verify(wineMapper, times(2)).wineToWineDTO(any(Wine.class));

       ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
       ArgumentCaptor<WineEvent> eventCaptor = ArgumentCaptor.forClass(WineEvent.class);
       verify(kafkaTemplate).send(topicCaptor.capture(), eventCaptor.capture());

       assertEquals(wineTopicName, topicCaptor.getValue());
       WineEvent capturedEvent = eventCaptor.getValue();
       assertEquals(EventType.UPDATE_WINE, capturedEvent.getEventType());
       assertEquals(newWineDTO, capturedEvent.getWineDTO());

       assertThat(result.getWineName()).isEqualTo(newName);

       assertThat(result.getWineName()).isNotEqualTo(wineDTO.getWineName());
       assertThat(result).isNotEqualTo(wineDTO);
   }

    @Test
    void shouldUpdatePurchaseLinks() {
        //given
        String link = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";
        ArrayList<String> links = new ArrayList<>();

        WineDTO newWineDTO = WineDTO.builder()
                .id(wineDTO.getId())
                .wineName("Barolo")
                .wineType("Red")
                .grape("Merlot")
                .region("Tuscany")
                .denomination("DOCG")
                .year(2020)
                .alcoholPercentage(13.5)
                .wineDescription("A test wine description.")
                .purchaseLinks(links)
                .build();

        newWineDTO.getPurchaseLinks().add(link);

        when(wineRepository.findById(wine.getId())).thenReturn(Optional.of(wine));
        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(newWineDTO);
        //when
        WineDTO result = wineService.updateWine(wine.getId(), newWineDTO);
        //then
        verify(wineRepository).findById(wine.getId());
        verify(wineRepository).save(any(Wine.class));
        verify(wineMapper, times(2)).wineToWineDTO(any(Wine.class));

        assertThat(wineDTO.getPurchaseLinks().size()).isNotEqualTo(result.getPurchaseLinks().size());
        assertThat(result.getPurchaseLinks().size()).isEqualTo(1);
    }

    @Test
    void shouldUpdateAllWineFields() {

        ArrayList<String> links = new ArrayList<>();
        String link = "https://www.tannico.it/marlborough-sauvignon-blanc-2023-cloudy-bay-tappo-a-vite.html";

        WineDTO newWineDTO = WineDTO.builder()
                .id(wineDTO.getId())
                .wineName("Sauvignon")
                .wineType("Bianco")
                .grape("Sauvignon")
                .region("Marlborough")
                .denomination("DOC")
                .year(2021)
                .alcoholPercentage(14.5)
                .wineDescription("Si va a letto.")
                .purchaseLinks(links)
                .build();

        newWineDTO.getPurchaseLinks().add(link);

        when(wineRepository.findById(wine.getId())).thenReturn(Optional.of(wine));
        when(wineRepository.save(any(Wine.class))).thenReturn(wine);
        when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(newWineDTO);
        //when
        WineDTO result = wineService.updateWine(wine.getId(), newWineDTO);
        //then
        verify(wineRepository).findById(wine.getId());
        verify(wineRepository).save(any(Wine.class));
        verify(wineMapper, times(2)).wineToWineDTO(any(Wine.class));

        assertThat(result.getWineName()).isNotEqualTo(wineDTO.getWineName());
        assertThat(result.getWineType()).isNotEqualTo(wineDTO.getWineType());
        assertThat(result.getGrape()).isNotEqualTo(wineDTO.getGrape());
        assertThat(result.getRegion()).isNotEqualTo(wineDTO.getRegion());
        assertThat(result.getDenomination()).isNotEqualTo(wineDTO.getDenomination());
        assertThat(result.getYear()).isNotEqualTo(wineDTO.getYear());
        assertThat(result.getAlcoholPercentage()).isNotEqualTo(wineDTO.getAlcoholPercentage());
        assertThat(result.getWineDescription()).isNotEqualTo(wineDTO.getWineDescription());
        assertThat(wineDTO.getPurchaseLinks().size()).isNotEqualTo(result.getPurchaseLinks().size());
        assertThat(result.getPurchaseLinks().size()).isEqualTo(1);
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
        //when(wine.getPurchaseLinks().equals(null)).thenReturn(new ArrayList<>(wine.getPurchaseLinks()));

        WineDTO result = wineService.addLinkToWine(id, link);

        verify(wineRepository).findById(id);
        verify(wineRepository).save(wine);
        verify(wineMapper, times(2)).wineToWineDTO(wine);

        assertThat(result.getPurchaseLinks().get(0)).isEqualTo(link);
    }

    @Test
    void shouldCreateNewListWhenPurchaseLinkIsNull() throws WineNotFoundException, LinkAlreadyExistsException {
        Long id = 5L;
        String link = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        // Crea oggetto Wine con lista link null
        Wine wineWithoutLinks = Wine.builder()
                .id(id)
                .wineName("Test Wine")
                .wineType("Red")
                .grape("Merlot")
                .region("Tuscany")
                .denomination("DOCG")
                .year(2020)
                .alcoholPercentage(13.5)
                .wineDescription("A test wine description.")
                .purchaseLinks(null)
                .build();

        // Crea oggetto WineDTO che verrà restituito dal mapper
        WineDTO wineDTOWithoutLinks = WineDTO.builder()
                .id(id)
                .wineName("Test Wine")
                .wineType("Red")
                .grape("Merlot")
                .region("Tuscany")
                .denomination("DOCG")
                .year(2020)
                .alcoholPercentage(13.5)
                .wineDescription("A test wine description.")
                .purchaseLinks(new ArrayList<>())  // Initially empty list
                .build();
        // Crea oggetto Wine che verrà restituito dal repository dopo averlo salvato
        Wine updatedWine = Wine.builder()
                .id(id)
                .wineName("Test Wine")
                .wineType("Red")
                .grape("Merlot")
                .region("Tuscany")
                .denomination("DOCG")
                .year(2020)
                .alcoholPercentage(13.5)
                .wineDescription("A test wine description.")
                .purchaseLinks(new ArrayList<>(List.of(link)))
                .build();

        // Crea oggetto WineDTO aggiornato dopo l'aggiunta del link
        WineDTO wineDTOWithLink = WineDTO.builder()
                .id(id)
                .wineName("Test Wine")
                .wineType("Red")
                .grape("Merlot")
                .region("Tuscany")
                .denomination("DOCG")
                .year(2020)
                .alcoholPercentage(13.5)
                .wineDescription("A test wine description.")
                .purchaseLinks(new ArrayList<>(List.of(link)))
                .build();

        when(wineRepository.findById(id)).thenReturn(Optional.of(wineWithoutLinks));
        when(wineRepository.save(any(Wine.class))).thenReturn(updatedWine);
        when(wineMapper.wineToWineDTO(any(Wine.class))).thenReturn(wineDTOWithLink);

        WineDTO result = wineService.addLinkToWine(id, link);

        verify(wineRepository).findById(id);
        verify(wineRepository).save(any(Wine.class));
        verify(wineMapper, times(2)).wineToWineDTO(any(Wine.class));

        assertNotNull(result.getPurchaseLinks());
        assertThat(result.getPurchaseLinks()).contains(link);
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
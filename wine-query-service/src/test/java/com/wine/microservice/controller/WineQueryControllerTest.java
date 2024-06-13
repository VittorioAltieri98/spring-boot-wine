package com.wine.microservice.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.microservice.WineQueryApplication;
import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.service.WineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = WineController.class)
@ContextConfiguration(classes = WineQueryApplication.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class WineQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WineService wineService;

    @Autowired
    private ObjectMapper objectMapper;


    private WineDTO wineDTO;

    @BeforeEach
    void init() {
        List<String> links = new ArrayList<>();

        wineDTO = WineDTO.builder()
                .id(5L)
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .purchaseLinks(links)
                .build();
    }

    @Test
    void shouldReturnAllWines() throws Exception {

        List<String> links_1 = new ArrayList<>();
        links_1.add("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html");
        List<String> links_2 = new ArrayList<>();

        WineDTO wineDTO_1 = WineDTO.builder()
                .id(1L)
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .purchaseLinks(links_1)
                .build();

        WineDTO wineDTO_2 = WineDTO.builder()
                .id(2L)
                .wineName("Merlot")
                .wineType("Red")
                .grape("Uva")
                .region("Francia")
                .denomination("DOC")
                .year(2023)
                .alcoholPercentage(17.8)
                .wineDescription("Buonissimo")
                .purchaseLinks(links_2)
                .build();

        List<WineDTO> winesDTO = new ArrayList<>();
        winesDTO.add(wineDTO_1);
        winesDTO.add(wineDTO_2);

        Mockito.when(wineService.getAllWines()).thenReturn(winesDTO);

        ResultActions response = mockMvc.perform(get("/wine/all")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(winesDTO.size()))
                .andExpect(jsonPath("$[0].wineName").value("Barolo"))
                .andExpect(jsonPath("$[1].wineName").value("Merlot"))
                .andExpect(jsonPath("$[0].purchaseLinks.length()").value(links_1.size()))
                .andExpect(jsonPath("$[0].purchaseLinks", hasItem("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html")));
    }

    @Test
    void shouldReturnWineById() throws Exception {
        Mockito.when(wineService.getWineById(wineDTO.getId())).thenReturn(wineDTO);

        ResultActions response = mockMvc.perform(get("/wine/{id}", wineDTO.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.wineName", is(wineDTO.getWineName())))
                .andDo(print());

    }

    @Test
    void shouldNotReturnWineWithInvalidId() throws Exception {
        Mockito.when(wineService.getWineById(20L)).thenThrow(new WineNotFoundException("Vino non trovato con l'id: " + 20L));

        ResultActions response = mockMvc.perform(get("/wine/20")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("Vino non trovato con l'id: " + 20L))
                .andDo(print());
    }

    @Test
    @Disabled
    void shouldSearchWines() {

    }

    @Test
    @Disabled
    void shouldGetWineDetailsWithPairings() {

    }

    @Test
    @Disabled
    void shouldNotGetWineDetailsWithPairingsWhenInvalidWineId() {

    }
}

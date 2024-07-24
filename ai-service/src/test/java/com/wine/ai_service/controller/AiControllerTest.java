package com.wine.ai_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.ai_service.dto.WineDTO;
import com.wine.ai_service.dto.WineInfo;
import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.exception.WinePairingNotFoundException;
import com.wine.ai_service.model.WinePairing;
import com.wine.ai_service.service.WinePairingService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AiController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WinePairingService winePairingService;

    @Autowired
    private ObjectMapper objectMapper;

    WineDTO wineDTO;

    WinePairingDTO winePairingDTO;

    WinePairing winePairing;

    WineInfo wineInfo;

    @BeforeEach
    void setUp() {
        List<String> foodPairing = new ArrayList<>();
        Map<String, String> foodsNameAndDescriptions = new HashMap<>();
        String food_1 = "Pizza";
        String food_2 = "Pan Di Stelle";
        String description_1 = "Azz che buono";
        String description_2 = "Maronn che buon";
        foodPairing.add(food_1);
        foodPairing.add(food_2);
        foodsNameAndDescriptions.put(food_1, description_1);
        foodsNameAndDescriptions.put(food_2, description_2);

        wineDTO = WineDTO.builder()
                .id(1L)
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        winePairingDTO = WinePairingDTO.builder()
                .wineName("Barolo")
                .wineType("Red")
                .region("Campania")
                .wineDescription("Buonissimo mamma mia")
                .foodPairings(foodPairing)
                .foodsNameAndDescriptionOfWhyThePairingIsRecommended(foodsNameAndDescriptions)
                .build();

        winePairing = WinePairing.builder()
                .id(1L)
                .wineName("Barolo")
                .wineType("Red")
                .region("Campania")
                .wineDescription("Buonissimo mamma mia")
                .foodPairings(foodPairing)
                .foodsNameAndDescriptionOfWhyThePairingIsRecommended(foodsNameAndDescriptions)
                .wineId(1L)
                .build();

        wineInfo = WineInfo.builder()
                .wineName("Aglianico del Vulture")
                .wineType("Red")
                .region("Campania")
                .denomination("DOC")
                .foodPairings(foodPairing)
                .serviceTemperature("16-18 °C")
                .wineDescription("L'Aglianico del Vulture è un vino rosso corposo e strutturato, caratterizzato da aromi di frutti di bosco scuri, spezie e liquirizia. Al palato è secco, con tannini decisi e un finale lungo e persistente.")
                .foodsNameAndDescriptionOfWhyThePairingIsRecommended(foodsNameAndDescriptions)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldPair() throws Exception {
        Mockito.when(winePairingService.generateWinePairing(wineDTO.getId())).thenReturn(winePairingDTO);

        ResultActions response = mockMvc.perform(get("/ai/" + wineDTO.getId() + "/pairing")
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldGetWinePairingByWineId() throws Exception {
        Mockito.when(winePairingService.getWinePairingByWineId(winePairing.getWineId())).thenReturn(winePairingDTO);

        ResultActions response = mockMvc.perform(get("/ai/winePairing/by-wine-id/" + winePairing.getWineId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.wineName", is(wineDTO.getWineName())))
                .andDo(print());
    }

    @Test
    void shouldNotGetWinePairingByWineId() throws Exception {
        Mockito.when(winePairingService.getWinePairingByWineId(20L)).thenThrow(new WinePairingNotFoundException("WinePairing not found with id " + 20L));

        ResultActions response = mockMvc.perform(get("/ai/winePairing/by-wine-id/" + 20L)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("WinePairing not found with id " + 20L))
                .andDo(print());
    }

    @Test
    void shouldGeneratePairingsByFoodMessage() throws Exception {
        String foodResponse = "Pizza e vino rosso sono un abbinamento classico, come un Chianti Classico o un Nero d'Avola. Se preferisci il vino bianco, un Vermentino o un Pinot Grigio saranno ottimi.";

        Mockito.when(winePairingService.generatePairingsByFoodMessage("Pizza")).thenReturn(foodResponse);

        ResultActions response = mockMvc.perform(get("/ai/generate/food-message")
                .contentType(MediaType.APPLICATION_JSON).param("message", "Pizza"));

        response.andExpect(status().isOk())
                .andExpect(content().string(foodResponse))
                .andDo(print());
    }

    @Test
    void shouldGetWinePairingById() throws Exception {
        Mockito.when(winePairingService.getWinePairingById(winePairing.getId())).thenReturn(winePairingDTO);

        ResultActions response = mockMvc.perform(get("/ai/winePairing/by-id/" + winePairing.getId())
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.wineName", is(wineDTO.getWineName())))
                .andDo(print());
    }

    @Test
    void shouldNotGetWinePairingById() throws Exception {
        Mockito.when(winePairingService.getWinePairingById(20L)).thenThrow(new WinePairingNotFoundException("WinePairing not found with id " + 20L));

        ResultActions response = mockMvc.perform(get("/ai/winePairing/by-id/" + 20L)
                .contentType(MediaType.APPLICATION_JSON));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("WinePairing not found with id " + 20L))
                .andDo(print());
    }

    @Test
    void shouldGenerateInfoWithFilters() throws Exception {
        String info = "Rosso rubino brillante con riflessi granato";

        Mockito.when(winePairingService.generateInfoWithFilters("Red", "Campania")).thenReturn(info);

        ResultActions response = mockMvc.perform(get("/ai/generate/info/with-filter")
                .contentType(MediaType.APPLICATION_JSON)
                .param("wineType", "Red")
                .param("region", "Campania")
        );

        response.andExpect(status().isOk())
                .andExpect(content().string(info))
                .andDo(print());
    }

    @Test
    void generateWineInfoWithFilters() throws Exception {

        Mockito.when(winePairingService.generateWineInfoWithFilters("Red", "Campania")).thenReturn(wineInfo);

        ResultActions response = mockMvc.perform(get("/ai/generate/wine-info/with-filter")
                .contentType(MediaType.APPLICATION_JSON)
                .param("wineType", "Red")
                .param("region", "Campania")
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.wineName", is(wineInfo.getWineName())))
                .andDo(print());
    }
}
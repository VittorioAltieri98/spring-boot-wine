package com.wine.microservice.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.exception.WineAlreadyExistsException;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.model.Wine;
import com.wine.microservice.service.WineService;
import org.junit.jupiter.api.BeforeEach;
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
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = WineController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
class WineCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WineService wineService;

    @Autowired
    private ObjectMapper objectMapper;

    private WineDTO wineDTO;
    private Wine wine;
    private WineDTO updatedWineDTO;

    @BeforeEach
    void init() {
        List<String> links = new ArrayList<>();

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
                 .purchaseLinks(links)
                .build();

         wine = Wine.builder()
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        updatedWineDTO = WineDTO.builder()
                .id(1L)
                .wineName("Merlot")
                .wineType("White")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();
    }

    @Test
    void shouldCreateAndReturnWine() throws Exception {
        //given(wineService.createWine(ArgumentMatchers.any())).willAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(wineService.createWine(any(WineDTO.class))).thenReturn(wineDTO);

        ResultActions response = mockMvc.perform(post("/wine/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wineDTO))
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.wineName", is(wineDTO.getWineName())))
                .andDo(print());
    }

    @Test
    void shouldNotCreateWineWhenValidationFails() throws Exception {

        WineDTO wineDTOWithNoName = WineDTO.builder()
                .wineName(null)
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        ResultActions response = mockMvc.perform(post("/wine/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wineDTOWithNoName))
        );

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.wineName").value("Il nome non può essere lasciato vuoto."));
    }

    @Test
    void shouldNotCreateWineThatAlreadyExists() throws Exception {

        WineDTO wineDTOWithSameName = WineDTO.builder()
                .id(2L)
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        Mockito.when(wineService.createWine(any(WineDTO.class)))
                .thenThrow(new WineAlreadyExistsException("Il vino " + wineDTOWithSameName.getWineName() + " è già esistente."));

        ResultActions conflictWineResponse = mockMvc.perform(post("/wine/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wineDTOWithSameName))
        );

        conflictWineResponse.andExpect(status().isConflict())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("Il vino " + wineDTOWithSameName.getWineName() + " è già esistente."))
                .andDo(print());
    }

    @Test
    void shouldUpdateAndReturnWine() throws Exception {

        Mockito.when(wineService.updateWine(wineDTO.getId(), updatedWineDTO)).thenReturn(updatedWineDTO);

        ResultActions response = mockMvc.perform(put("/wine/"+ wineDTO.getId() +"/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedWineDTO))
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.wineName", is("Merlot")))
                .andDo(print());
    }

    @Test
    void shouldNotUpdateWineWhenValidationFails() throws Exception {
        WineDTO wineDTOWithNoName = WineDTO.builder()
                .wineName(null)
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .build();

        Mockito.when(wineService.updateWine(wineDTO.getId(), wineDTOWithNoName)).thenReturn(wineDTOWithNoName);

        ResultActions response = mockMvc.perform(put("/wine/"+ wineDTO.getId() +"/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wineDTOWithNoName))
        );

        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.wineName").value("Il nome non può essere lasciato vuoto."));
    }

    @Test
    void shouldNotUpdateWhenInvalidWineId() throws Exception {
        Mockito.when(wineService.updateWine(20L, wineDTO)).thenThrow(new WineNotFoundException("Vino non trovato con l'id: " + 20L));

        ResultActions response = mockMvc.perform(put("/wine/20/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wineDTO)));

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("Vino non trovato con l'id: " + 20L))
                .andDo(print());
    }
}


package com.wine.microservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.exception.LinkAlreadyExistsException;
import com.wine.microservice.exception.WineAlreadyExistsException;
import com.wine.microservice.exception.WineNotFoundException;
import com.wine.microservice.service.WineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = WineController.class)
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class WineCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WineService wineService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    private WineDTO wineDTO;
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
    void shouldNotCreateAndReturnWineWithoutAuthentication() throws Exception {
        when(wineService.createWine(any(WineDTO.class))).thenReturn(wineDTO);

        ResultActions response = mockMvc.perform(post("/wine-command/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wineDTO))
        );

        response.andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void shouldCreateWineAsAdmin() throws Exception {
        when(wineService.createWine(any(WineDTO.class))).thenReturn(wineDTO);

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wineDTO))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void shouldNotCreateWineAsUser() throws Exception {
        when(wineService.createWine(any(WineDTO.class))).thenReturn(wineDTO);

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        AccessTokenResponse tokenResponse = getAccessToken("Weakest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wineDTO))
                )
                .andExpect(status().isForbidden())
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

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/create")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wineDTOWithNoName))
        )
                .andExpect(status().isBadRequest())
                .andDo(print());
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

        when(wineService.createWine(any(WineDTO.class)))
                .thenThrow(new WineAlreadyExistsException("Il vino " + wineDTOWithSameName.getWineName() + " è già esistente."));

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/create")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wineDTOWithSameName))
                )
                .andExpect(status().isConflict())
                .andDo(print());
    }

    @Test
    void shouldUpdateAndReturnWine() throws Exception {

        when(wineService.updateWine(wineDTO.getId(), updatedWineDTO)).thenReturn(updatedWineDTO);

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(put("/wine-command/" + wineDTO.getId() +"/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedWineDTO))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.wineName", is("Merlot")))
                .andDo(print());
    }

    @Test
    void shouldNotUpdateAndReturnWineAsUser() throws Exception {

        when(wineService.updateWine(wineDTO.getId(), updatedWineDTO)).thenReturn(updatedWineDTO);

        AccessTokenResponse tokenResponse = getAccessToken("Weakest", "Megumi1@!");

        mockMvc.perform(put("/wine-command/" + wineDTO.getId() +"/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedWineDTO))
                )
                .andExpect(status().isForbidden())
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

        when(wineService.updateWine(wineDTO.getId(), wineDTOWithNoName)).thenReturn(wineDTOWithNoName);

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(put("/wine-command/" + wineDTO.getId() +"/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wineDTOWithNoName))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.wineName").value("Il nome non può essere lasciato vuoto."))
                .andDo(print());
    }

    @Test
    void shouldNotUpdateWhenInvalidWineId() throws Exception {
        when(wineService.updateWine(20L, wineDTO)).thenThrow(new WineNotFoundException("Vino non trovato con l'id: " + 20L));

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(put("/wine-command/20/update")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wineDTO))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Messaggio di errore']").value("Vino non trovato con l'id: " + 20L))
                .andDo(print());
    }

    @Test
    void shouldDeleteWine() throws Exception {
        doNothing().when(wineService).deleteWine(wineDTO.getId());

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(delete("/wine-command/" + wineDTO.getId() + "/delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void shouldNotDeleteWineAsUser() throws Exception {
        doNothing().when(wineService).deleteWine(wineDTO.getId());

        AccessTokenResponse tokenResponse = getAccessToken("Weakest", "Megumi1@!");

        mockMvc.perform(delete("/wine-command/" + wineDTO.getId() + "/delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void shouldNotDeleteWineWhenInvalidWineId() throws Exception {
        doThrow(new WineNotFoundException("Vino non trovato con l'id: " + 20L))
                .when(wineService).deleteWine(20L);

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(delete("/wine-command/" + 20L + "/delete")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Messaggio di errore']").value("Vino non trovato con l'id: " + 20L))
                .andDo(print());
    }

    @Test
    void shouldAddLinkToWine() throws Exception {

        List<String> wineLinks = new ArrayList<>();

        WineDTO wineDTOWithLink = WineDTO.builder()
                .id(12L)
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .purchaseLinks(wineLinks)
                .build();
        String link = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        wineDTOWithLink.getPurchaseLinks().add(link);

        when(wineService.addLinkToWine(wineDTOWithLink.getId(), link)).thenReturn(wineDTOWithLink);

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/" + wineDTOWithLink.getId() + "/addLink")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(link)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.purchaseLinks.length()").value(wineDTOWithLink.getPurchaseLinks().size()))
                .andExpect(jsonPath("$.purchaseLinks", hasItem("https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html")))
                .andDo(print());
    }

    @Test
    void shouldNotAddLinkToWineAsUser() throws Exception {

        List<String> wineLinks = new ArrayList<>();

        WineDTO wineDTOWithLink = WineDTO.builder()
                .id(12L)
                .wineName("Barolo")
                .wineType("Red")
                .grape("Uva")
                .region("Campania")
                .denomination("DOC")
                .year(2020)
                .alcoholPercentage(17.0)
                .wineDescription("Buonissimo mamma mia")
                .purchaseLinks(wineLinks)
                .build();
        String link = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        wineDTOWithLink.getPurchaseLinks().add(link);

        when(wineService.addLinkToWine(wineDTOWithLink.getId(), link)).thenReturn(wineDTOWithLink);

        AccessTokenResponse tokenResponse = getAccessToken("Weakest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/" + wineDTOWithLink.getId() + "/addLink")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(link)
                )
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    void shouldNotAddLinkWhenInvalidWineId() throws Exception {
        String link = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        when(wineService.addLinkToWine(50L, link)).thenThrow(new WineNotFoundException("Vino non trovato con l'id: " + 50L));

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/50/addLink")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(link)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("Vino non trovato con l'id: " + 50L))
                .andDo(print());
    }

    @Test
    void shouldNotAddLinkWhenInvalidLink() throws Exception {
        String invalidLink = "htpppppps://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        when(wineService.addLinkToWine(50L, invalidLink)).thenThrow(new IllegalArgumentException("Invalid link format: " + invalidLink));

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/50/addLink")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(invalidLink)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("Invalid link format: " + invalidLink))
                .andDo(print());
    }

    @Test
    void shouldNotAddLinkWhenLinkAlreadyExists() throws Exception {
        String invalidLink = "https://www.tannico.it/barolo-riserva-docg-2015-marchesi-di-barolo.html";

        when(wineService.addLinkToWine(50L, invalidLink)).thenThrow(new LinkAlreadyExistsException("Link already exists: " + invalidLink));

        AccessTokenResponse tokenResponse = getAccessToken("Strongest", "Megumi1@!");

        mockMvc.perform(post("/wine-command/50/addLink")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getToken())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(invalidLink)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.['Messaggio di errore']")
                        .value("Link already exists: " + invalidLink))
                .andDo(print());
    }

    private AccessTokenResponse getAccessToken(String username, String password) {
        Keycloak keycloak = Keycloak.getInstance(
                "http://localhost:8081/",
                "springboot-microservice-realm",
                username,
                password,
                "admin-cli",
                "**********"
        );
        AccessTokenResponse accessTokenResponse = keycloak.tokenManager().getAccessToken();
        return accessTokenResponse;
    }
}


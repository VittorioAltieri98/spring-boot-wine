package com.wine.microservice.service.impl;

import com.wine.microservice.client.WinePairingServiceClient;
import com.wine.microservice.mapper.WineMapper;
import com.wine.microservice.repository.WineRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WineServiceImplTest {

   WineServiceImpl wineService;

   @Mock
   WineRepository wineRepository;

   WinePairingServiceClient winePairingServiceClient;

    WineMapper wineMapper;

    @BeforeEach
    void setUp() {
        wineService = new WineServiceImpl(wineRepository, winePairingServiceClient,wineMapper );
    }

    @AfterEach
    void tearDown() {
    }

//    @Test
//    void getWineById() {
//    }

    @Test
    void shouldGetAllWines() {
        //given
        //when
        wineService.getAllWines();
        //then
        verify(wineRepository).findAll();
    }

//    @Test
//    void searchWines() {
//    }

//    @Test
//    void getWineDetailsWithPairings() {
//    }
}
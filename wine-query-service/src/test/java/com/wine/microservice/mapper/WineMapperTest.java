package com.wine.microservice.mapper;

import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.model.Wine;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WineMapperTest {


    WineMapper wineMapper = Mappers.getMapper(WineMapper.class);

    @Test
    void shouldProperlyMapWineToWineDTO() {
        Wine wine = Wine.builder()
                .wineName("Tavernello")
                .wineType("Rosso")
                .grape("Uva")
                .region("Piemonte")
                .denomination("DOC")
                .year(1945)
                .alcoholPercentage(17.0)
                .wineDescription("Azz")
                .build();
        WineDTO wineDTO = wineMapper.wineToWineDTO(wine);

        assertNotNull(wineDTO);
        assertThat(wine.getId()).isEqualTo(wineDTO.getId());
        assertThat(wine.getWineName()).isEqualTo(wineDTO.getWineName());
        assertThat(wine.getWineType()).isEqualTo(wineDTO.getWineType());
        assertThat(wine.getRegion()).isEqualTo(wineDTO.getRegion());
        assertThat(wine.getDenomination()).isEqualTo(wineDTO.getDenomination());
        assertThat(wine.getYear()).isEqualTo(wineDTO.getYear());
        assertThat(wine.getAlcoholPercentage()).isEqualTo(wineDTO.getAlcoholPercentage());
        assertThat(wine.getWineDescription()).isEqualTo(wineDTO.getWineDescription());
    }

    @Test
    void shouldProperlyMapWineDTOtoWine() {
        WineDTO wineDTO = WineDTO.builder()
                .wineName("Tavernello")
                .wineType("Rosso")
                .grape("Uva")
                .region("Piemonte")
                .denomination("DOC")
                .year(1945)
                .alcoholPercentage(17.0)
                .wineDescription("Azz")
                .build();

        Wine wine = wineMapper.wineDTOtoWine(wineDTO);

        assertNotNull(wine);
        assertThat(wineDTO.getId()).isEqualTo(wine.getId());
        assertThat(wineDTO.getWineName()).isEqualTo(wine.getWineName());
        assertThat(wineDTO.getWineType()).isEqualTo(wine.getWineType());
        assertThat(wineDTO.getRegion()).isEqualTo(wine.getRegion());
        assertThat(wineDTO.getDenomination()).isEqualTo(wine.getDenomination());
        assertThat(wineDTO.getYear()).isEqualTo(wine.getYear());
        assertThat(wineDTO.getAlcoholPercentage()).isEqualTo(wine.getAlcoholPercentage());
        assertThat(wineDTO.getWineDescription()).isEqualTo(wine.getWineDescription());
    }
}
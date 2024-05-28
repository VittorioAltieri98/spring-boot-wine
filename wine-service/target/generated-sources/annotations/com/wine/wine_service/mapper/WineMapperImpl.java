package com.wine.wine_service.mapper;

import com.wine.wine_service.dto.WineDTO;
import com.wine.wine_service.model.Wine;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-28T15:13:00+0200",
    comments = "version: 1.4.2.Final, compiler: javac, environment: Java 21.0.3 (Oracle Corporation)"
)
@Component
public class WineMapperImpl implements WineMapper {

    @Override
    public WineDTO wineToWineDTO(Wine wine) {
        if ( wine == null ) {
            return null;
        }

        WineDTO wineDTO = new WineDTO();

        wineDTO.setId( wine.getId() );
        wineDTO.setWineName( wine.getWineName() );
        wineDTO.setWineType( wine.getWineType() );
        wineDTO.setGrape( wine.getGrape() );
        wineDTO.setRegion( wine.getRegion() );
        wineDTO.setDenomination( wine.getDenomination() );
        wineDTO.setYear( wine.getYear() );
        wineDTO.setAlcoholPercentage( wine.getAlcoholPercentage() );
        wineDTO.setWineDescription( wine.getWineDescription() );
        List<String> list = wine.getPurchaseLinks();
        if ( list != null ) {
            wineDTO.setPurchaseLinks( new ArrayList<String>( list ) );
        }

        return wineDTO;
    }
}

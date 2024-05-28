package com.wine.wine_service.mapper;


import com.wine.wine_service.dto.WineDTO;
import com.wine.wine_service.model.Wine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WineMapper {

    WineDTO wineToWineDTO(Wine wine);

}

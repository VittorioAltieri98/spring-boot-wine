package com.wine.wine_command.mapper;


import com.wine.wine_command.dto.WineDTO;
import com.wine.wine_command.model.Wine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WineMapper {

    WineDTO wineToWineDTO(Wine wine);

}

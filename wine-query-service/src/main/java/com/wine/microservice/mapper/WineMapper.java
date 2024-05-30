package com.wine.microservice.mapper;



import com.wine.microservice.dto.WineDTO;
import com.wine.microservice.model.Wine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WineMapper {

    WineDTO wineToWineDTO(Wine wine);
    Wine wineDTOtoWine(WineDTO wineDTO);

}

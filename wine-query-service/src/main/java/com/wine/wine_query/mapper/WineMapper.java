package com.wine.wine_query.mapper;



import com.wine.wine_query.dto.WineDTO;
import com.wine.wine_query.model.Wine;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WineMapper {

    WineDTO wineToWineDTO(Wine wine);

}

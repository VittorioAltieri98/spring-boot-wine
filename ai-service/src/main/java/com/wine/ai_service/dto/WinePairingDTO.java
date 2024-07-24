package com.wine.ai_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WinePairingDTO {

    private String wineName;
    private String wineType;
    private String region;
    private String wineDescription;

    private List<String> foodPairings;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> foodsNameAndDescriptionOfWhyThePairingIsRecommended;

}

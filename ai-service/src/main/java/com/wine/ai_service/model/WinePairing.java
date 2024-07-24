package com.wine.ai_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Entity
@Builder
public class WinePairing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wineName;
    private String wineType;
    private String region;
    private String wineDescription;

    private List<String> foodPairings;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> foodsNameAndDescriptionOfWhyThePairingIsRecommended;

    private Long wineId;

}

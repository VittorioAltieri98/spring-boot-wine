package com.wine.ai_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class UserWinePairing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String wineName;
    private String wineType;
    private String region;
    private String denomination;
    private List<String> foodPairings;
    private String serviceTemperature;
    private String wineDescription;
    private LocalDateTime pairingDate;

    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> foodsNameAndDescriptionOfWhyThePairingIsRecommended;

    private String userId;
}

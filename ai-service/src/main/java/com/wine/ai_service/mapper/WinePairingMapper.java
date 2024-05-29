package com.wine.ai_service.mapper;

import com.wine.ai_service.dto.WinePairingDTO;
import com.wine.ai_service.model.WinePairing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WinePairingMapper {

    WinePairingDTO winePairingToWinePairingDTO(WinePairing winePairing);

}

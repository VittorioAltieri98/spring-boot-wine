package com.wine.ai_service.mapper;

import com.wine.ai_service.dto.UserWinePairingDTO;
import com.wine.ai_service.model.UserWinePairing;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserWinePairingMapper {

    UserWinePairingDTO userWinePairingToUserWinePairingDTO(UserWinePairing userWinePairing);


}

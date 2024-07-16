package com.wine.user.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponse {
    String username;
    String email;
    String firstName;
    String lastName;
}

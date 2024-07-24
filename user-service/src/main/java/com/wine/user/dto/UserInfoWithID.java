package com.wine.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfoWithID {

    private String userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
}

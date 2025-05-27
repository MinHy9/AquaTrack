package com.aquatrack.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRandomPasswordRequest {
    private String newPassword;
}

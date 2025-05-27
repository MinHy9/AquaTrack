package com.aquatrack.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangeRandomPasswordRequest {
	
	
	@Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다.")
    @Pattern(
        regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])(?!.*(.)\\1{3,}).*$",
        message = "비밀번호는 영문자, 숫자, 특수문자를 각각 포함하고, 같은 문자를 4번 이상 반복할 수 없습니다."
    )
    private String password;
}

package com.bookswap.bookswapapp.dtos.manageusers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangePassword {
    private String oldPassword;
    private String newPassword;
}

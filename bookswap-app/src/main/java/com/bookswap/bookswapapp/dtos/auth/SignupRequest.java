package com.bookswap.bookswapapp.dtos.auth;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {

    private String username;
    private String email;
    private Set<String> role;
    private String password;
    private String name;
    private String surname;
    private String phone;
    private String postalCode;
    private String post;
    private String city;
    private String street;
    private String buildingNumber;
    private String doorNumber;

}

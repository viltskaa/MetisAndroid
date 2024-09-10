package com.example.metiscameras.api.bodies;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LoginBody {
    private String login;
    private String password;
}

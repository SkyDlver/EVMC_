package com.mycompany.evmc.service;

import com.mycompany.evmc.dto.LoginRequest;
import com.mycompany.evmc.dto.LoginResponse;
import com.mycompany.evmc.dto.RegisterRequest;
import com.mycompany.evmc.dto.RegisterResponse;

public interface AuthService {

    LoginResponse loginUser(LoginRequest loginRequest);

    RegisterResponse registerUser(RegisterRequest registerRequest);
}

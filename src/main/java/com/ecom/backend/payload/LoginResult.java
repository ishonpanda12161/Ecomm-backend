package com.ecom.backend.payload;

import org.springframework.http.ResponseCookie;

public record LoginResult(LoginResponse loginResponse,
                          ResponseCookie responseCookie) {
}

package com.zerobase.apigateway.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponse {
    private String accessJwt;
    private String refreshJwt;
}

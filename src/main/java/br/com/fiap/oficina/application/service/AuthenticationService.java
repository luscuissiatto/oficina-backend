package br.com.fiap.oficina.application.service;

import br.com.fiap.oficina.application.dto.AuthenticationRequestDto;
import br.com.fiap.oficina.application.dto.AuthenticationResponseDto;
import br.com.fiap.oficina.infrastructure.persistence.entity.UserData;
import br.com.fiap.oficina.infrastructure.security.TokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthenticationService(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto dto) {
        // 1. Cria o token de autenticação com usuário e senha
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());

        // 2. Autentica o usuário usando o manager do Spring Security
        var auth = this.authenticationManager.authenticate(usernamePassword);

        // 3. Gera o token JWT com base no usuário autenticado
        var token = tokenService.generateToken((UserData) auth.getPrincipal());

        // 4. Retorna o DTO de resposta com o token
        return new AuthenticationResponseDto(token);
    }
}
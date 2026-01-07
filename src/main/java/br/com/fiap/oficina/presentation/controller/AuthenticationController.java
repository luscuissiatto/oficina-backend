// package br.com.fiap.oficina.presentation.controller;

// import br.com.fiap.oficina.application.dto.AuthenticationRequestDto;
// import br.com.fiap.oficina.application.dto.AuthenticationResponseDto;
// import br.com.fiap.oficina.application.service.AuthenticationService;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// @RestController
// @RequestMapping("/login")
// @Tag(name = "Authentication", description = "Endpoint para obter o token de autenticação")
// public class AuthenticationController {

//     private final AuthenticationService authenticationService;

//     public AuthenticationController(AuthenticationService authenticationService) {
//         this.authenticationService = authenticationService;
//     }

//     @PostMapping
//     public ResponseEntity<AuthenticationResponseDto> login(@RequestBody AuthenticationRequestDto dto) {
//         var responseDto = authenticationService.login(dto);
//         return ResponseEntity.ok(responseDto);
//     }
// }
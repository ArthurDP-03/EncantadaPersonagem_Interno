//package br.com.encantada.personageminterno.service;
//
//import br.com.encantada.personageminterno.domain.entity.Administrador;
//import br.com.encantada.personageminterno.domain.entity.Ator;
//import br.com.encantada.personageminterno.repository.AdministradorRepository;
//import br.com.encantada.personageminterno.repository.AtorRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class SecurityServiceTest {
//
//    @Mock
//    private AdministradorRepository administradorRepository;
//
//    @Mock
//    private AtorRepository atorRepository;
//
//    @InjectMocks
//    private SecurityService service;
//
//    @Test
//    void deveRetornarTrueQuandoAdministradorForDono() {
//
//        var auth =
//                new UsernamePasswordAuthenticationToken(
//                        "admin@email.com",
//                        null
//                );
//
//        Administrador admin = Administrador.builder()
//                .id(1)
//                .email("admin@email.com")
//                .build();
//
//        when(administradorRepository.findByEmail("admin@email.com"))
//                .thenReturn(Optional.of(admin));
//
//        assertTrue(service.isOwner(1, auth));
//    }
//
//    @Test
//    void deveRetornarFalseQuandoNaoForDono() {
//
//        var auth =
//                new UsernamePasswordAuthenticationToken(
//                        "admin@email.com",
//                        null
//                );
//
//        Administrador admin = Administrador.builder()
//                .id(2)
//                .email("admin@email.com")
//                .build();
//
//        when(administradorRepository.findByEmail("admin@email.com"))
//                .thenReturn(Optional.of(admin));
//
//        assertFalse(service.isOwner(1, auth));
//    }
//
//    @Test
//    void deveRetornarTrueQuandoAtorForDono() {
//
//        var auth =
//                new UsernamePasswordAuthenticationToken(
//                        "ator@email.com",
//                        null
//                );
//
//        Ator ator = Ator.builder()
//                .id(1)
//                .email("ator@email.com")
//                .build();
//
//        when(atorRepository.findByEmail("ator@email.com"))
//                .thenReturn(Optional.of(ator));
//
//        assertTrue(service.isAtorOwner(1, auth));
//    }
//
//    @Test
//    void deveRetornarTrueQuandoForAdmin() {
//
//        var auth =
//                new UsernamePasswordAuthenticationToken(
//                        "admin@email.com",
//                        null,
//                        List.of(() -> "ROLE_ADMIN")
//                );
//
//        assertTrue(service.isAdmin(auth));
//    }
//
//    @Test
//    void deveRetornarFalseQuandoNaoForAdmin() {
//
//        var auth =
//                new UsernamePasswordAuthenticationToken(
//                        "admin@email.com",
//                        null,
//                        List.of(() -> "ROLE_ATOR")
//                );
//
//        assertFalse(service.isAdmin(auth));
//    }
//}
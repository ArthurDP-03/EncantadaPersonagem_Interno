package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.web.dto.ator.AtorRequest;
import br.com.encantada.personageminterno.web.dto.ator.AtorResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtorServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private AtorRepository atorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AtorService service;

    @Test
    void deveCriarAtor() {

        AtorRequest request = mock(AtorRequest.class);

        when(request.email()).thenReturn("ator@email.com");
        when(request.nome()).thenReturn("João");
        when(request.senha()).thenReturn("123");
        when(request.ativo()).thenReturn(true);

        when(passwordEncoder.encode("123")).thenReturn("senhaHash");

        Ator atorSalvo = Ator.builder()
                .id(1)
                .nome("João")
                .email("ator@email.com")
                .senha("senhaHash")
                .ativo(true)
                .build();

        when(atorRepository.save(any())).thenReturn(atorSalvo);

        AtorResponse response = service.criar(request);

        assertEquals(1, response.id());
        assertEquals("João", response.nome());
    }

    @Test
    void deveLancarConflitoQuandoEmailAtorExiste() {

        AtorRequest request = mock(AtorRequest.class);

        when(request.email()).thenReturn("email@email.com");
        when(atorRepository.existsByEmail("email@email.com"))
                .thenReturn(true);

        assertThrows(
                ConflictException.class,
                () -> service.criar(request)
        );
    }

    @Test
    void deveBuscarPorId() {

        Ator ator = Ator.builder()
                .id(1)
                .nome("João")
                .email("email@email.com")
                .build();

        when(atorRepository.findById(1))
                .thenReturn(Optional.of(ator));

        AtorResponse response = service.buscarPorId(1);

        assertEquals(1, response.id());
    }

    @Test
    void deveLancarExcecaoQuandoAtorNaoExiste() {

        when(atorRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> service.buscarPorId(1)
        );
    }

    @Test
    void deveDeletarAtor() {

        when(atorRepository.existsById(1))
                .thenReturn(true);

        service.deletar(1);

        verify(atorRepository).deleteById(1);
    }
}
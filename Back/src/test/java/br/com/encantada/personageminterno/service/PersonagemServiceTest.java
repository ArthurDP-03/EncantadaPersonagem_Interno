//package br.com.encantada.personageminterno.service;
//
//import br.com.encantada.personageminterno.domain.entity.Personagem;
//import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
//import br.com.encantada.personageminterno.repository.PersonagemRepository;
//import br.com.encantada.personageminterno.web.dto.personagem.PersonagemRequest;
//import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PersonagemServiceTest {
//
//    @Mock
//    private PersonagemRepository repository;
//
//    @InjectMocks
//    private PersonagemService service;
//
//    @Test
//    void deveListarPersonagens() {
//
//        Personagem personagem = Personagem.builder()
//                .id(1)
//                .nome("Alice")
//                .descricao("Descrição")
//                .foto("foto.jpg")
//                .build();
//
//        when(repository.findAll())
//                .thenReturn(List.of(personagem));
//
//        List<PersonagemResponse> resultado = service.listar();
//
//        assertEquals(1, resultado.size());
//        assertEquals("Alice", resultado.get(0).nome());
//    }
//
//    @Test
//    void deveBuscarPorId() {
//
//        Personagem personagem = Personagem.builder()
//                .id(1)
//                .nome("Alice")
//                .build();
//
//        when(repository.findById(1))
//                .thenReturn(Optional.of(personagem));
//
//        PersonagemResponse response =
//                service.buscarPorId(1);
//
//        assertEquals(1, response.id());
//        assertEquals("Alice", response.nome());
//    }
//
//    @Test
//    void deveLancarExcecaoAoBuscarIdInexistente() {
//
//        when(repository.findById(1))
//                .thenReturn(Optional.empty());
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> service.buscarPorId(1)
//        );
//    }
//
//    @Test
//    void deveCriarPersonagem() {
//
//        PersonagemRequest request =
//                new PersonagemRequest(
//                        "Alice",
//                        "Descrição",
//                        "foto.jpg"
//                );
//
//        Personagem salvo = Personagem.builder()
//                .id(1)
//                .nome("Alice")
//                .descricao("Descrição")
//                .foto("foto.jpg")
//                .build();
//
//        when(repository.save(any(Personagem.class)))
//                .thenReturn(salvo);
//
//        PersonagemResponse response =
//                service.criar(request);
//
//        assertEquals(1, response.id());
//        assertEquals("Alice", response.nome());
//    }
//
//    @Test
//    void deveAtualizarPersonagem() {
//
//        Personagem personagem = Personagem.builder()
//                .id(1)
//                .nome("Antigo")
//                .build();
//
//        PersonagemRequest request =
//                new PersonagemRequest(
//                        "Novo",
//                        "Nova descrição",
//                        "foto.jpg"
//                );
//
//        when(repository.findById(1))
//                .thenReturn(Optional.of(personagem));
//
//        when(repository.save(any()))
//                .thenAnswer(i -> i.getArgument(0));
//
//        PersonagemResponse response =
//                service.atualizar(1, request);
//
//        assertEquals("Novo", response.nome());
//    }
//
//    @Test
//    void deveDeletarPersonagem() {
//
//        when(repository.existsById(1))
//                .thenReturn(true);
//
//        service.deletar(1);
//
//        verify(repository).deleteById(1);
//    }
//
//    @Test
//    void deveLancarExcecaoAoDeletarInexistente() {
//
//        when(repository.existsById(1))
//                .thenReturn(false);
//
//        assertThrows(
//                ResourceNotFoundException.class,
//                () -> service.deletar(1)
//        );
//    }
//}
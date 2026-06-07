//package br.com.encantada.personageminterno.web.controller;
//
//import br.com.encantada.personageminterno.service.PersonagemService;
//import br.com.encantada.personageminterno.web.dto.personagem.PersonagemResponse;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(PersonagemController.class)
//class PersonagemControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PersonagemService service;
//
//    @Test
//    void deveRetornarListaDePersonagens() throws Exception {
//
//        when(service.listar())
//                .thenReturn(List.of(
//                        new PersonagemResponse(
//                                1,
//                                "Alice",
//                                "Descrição",
//                                "foto.jpg"
//                        )
//                ));
//
//        mockMvc.perform(get("/personagens"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1))
//                .andExpect(jsonPath("$[0].nome").value("Alice"));
//    }
//
//    @Test
//    void deveBuscarPorId() throws Exception {
//
//        when(service.buscarPorId(1))
//                .thenReturn(
//                        new PersonagemResponse(
//                                1,
//                                "Alice",
//                                "Descrição",
//                                "foto.jpg"
//                        )
//                );
//
//        mockMvc.perform(get("/personagens/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nome")
//                        .value("Alice"));
//    }
//}
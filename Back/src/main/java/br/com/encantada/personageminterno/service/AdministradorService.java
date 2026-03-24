package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministradorService {

    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AdministradorResponse> listar() {
        return administradorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AdministradorResponse criar(AdministradorRequest request) {
        if (administradorRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe administrador com esse email");
        }

        Administrador administrador = Administrador.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .telefone(request.telefone())
                .tipo(defaultTipo(request.tipo()))
                .build();

        return toResponse(administradorRepository.save(administrador));
    }

    private AdministradorResponse toResponse(Administrador administrador) {
        return new AdministradorResponse(
                administrador.getId(),
                administrador.getNome(),
                administrador.getEmail(),
                administrador.getTelefone(),
                administrador.getTipo()
        );
    }

    private String defaultTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "ADMIN";
        }
        return tipo.trim().toUpperCase();
    }
}

package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ForbiddenException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException.Conflict;

@Service
public class AdministradorService {

    private final AtorRepository atorRepository;
    private final AdministradorRepository administradorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdministradorService(AdministradorRepository administradorRepository, PasswordEncoder passwordEncoder,
            AtorRepository atorRepository) {
        this.administradorRepository = administradorRepository;
        this.atorRepository = atorRepository;
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
            throw new ConflictException("Ja existe administrador com esse email");
        }
        if (atorRepository.existsByEmail(request.email())) {
            throw new ConflictException("Ja existe ator com esse email");
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
                administrador.getTipo());
    }

    private String defaultTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "ADMIN";
        }
        return tipo.trim().toUpperCase();
    }

    @Transactional(readOnly = true)
    public AdministradorResponse buscarPorId(int id) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado com o id: " + id));
        return toResponse(administrador);

    }

    @Transactional
    public AdministradorResponse atualizar(int id, AdministradorRequest request, String administradorEmail) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado com id: " + id));

        Administrador adminLogado = administradorRepository.findByEmail(administradorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador logado não encontrado"));

        // ADICIONE ESTA VALIDAÇÃO 👇
        if (!administrador.getId().equals(adminLogado.getId()) &&
                !"SUPER_ADMIN".equals(adminLogado.getTipo())) {
            throw new ForbiddenException("Você não tem permissão para atualizar dados de outro administrador");
        }

        // Verifica se o email já existe em outro registro
        if (!administrador.getEmail().equals(request.email())) {
            if (administradorRepository.existsByEmail(request.email())) {
                throw new ConflictException("Já existe administrador com esse email");
            }
            if (atorRepository.existsByEmail(request.email())) {
                throw new ConflictException("Já existe ator com esse email");
            }
        }

        administrador.setNome(request.nome());
        administrador.setEmail(request.email());
        administrador.setTelefone(request.telefone());
        administrador.setTipo(defaultTipo(request.tipo()));

        if (request.senha() != null && !request.senha().isBlank()) {
            administrador.setSenha(passwordEncoder.encode(request.senha()));
        }

        return toResponse(administradorRepository.save(administrador));
    }

    @Transactional
    public void deletar(int id, String administradorEmail) {
        Administrador administrador = administradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador não encontrado"));

        Administrador adminLogado = administradorRepository.findByEmail(administradorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador autenticado não encontrado"));

        if (administrador.getId().equals(adminLogado.getId())) {
            throw new ForbiddenException("Você não pode deletar sua própria conta");
        }

        administradorRepository.deleteById(id);
    }
}

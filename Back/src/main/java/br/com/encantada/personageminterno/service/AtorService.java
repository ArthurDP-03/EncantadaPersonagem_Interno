package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.exception.ConflictException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.web.dto.ator.AtorRequest;
import br.com.encantada.personageminterno.web.dto.ator.AtorResponse;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtorService {

    private final AdministradorRepository administradorRepository;
    private final AtorRepository atorRepository;
    private final PasswordEncoder passwordEncoder;

    public AtorService(
            AdministradorRepository administradorRepository,
            AtorRepository atorRepository,
            PasswordEncoder passwordEncoder) {
        this.administradorRepository = administradorRepository;
        this.atorRepository = atorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AtorResponse> listar() {
        return atorRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AtorResponse criar(AtorRequest request) {
        validarEmailDisponivel(request.email(), null);

        Ator ator = Ator.builder()
                .nome(request.nome())
                .email(request.email())
                .senha(passwordEncoder.encode(request.senha()))
                .telefone(request.telefone())
                .genero(request.genero())
                .altura(request.altura())
                .peso(request.peso())
                .observacao(request.observacao())
                .ativo(request.ativo() == null ? Boolean.TRUE : request.ativo())
                .build();

        return toResponse(atorRepository.save(ator));
    }

    @Transactional(readOnly = true)
    public AtorResponse buscarPorId(int id) {
        Ator ator = atorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ator nao encontrado com id: " + id));
        return toResponse(ator);
    }

    @Transactional
    public AtorResponse atualizar(int id, AtorRequest request) {
        Ator ator = atorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ator nao encontrado com id: " + id));

        validarEmailDisponivel(request.email(), id);

        ator.setNome(request.nome());
        ator.setEmail(request.email());
        ator.setTelefone(request.telefone());
        ator.setGenero(request.genero());
        ator.setAltura(request.altura());
        ator.setPeso(request.peso());
        ator.setObservacao(request.observacao());
        ator.setAtivo(request.ativo() == null ? Boolean.TRUE : request.ativo());

        if (request.senha() != null && !request.senha().isBlank()) {
            ator.setSenha(passwordEncoder.encode(request.senha()));
        }

        return toResponse(atorRepository.save(ator));
    }

    @Transactional
    public void deletar(int id) {
        if (!atorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ator nao encontrado");
        }

        atorRepository.deleteById(id);
    }

    private void validarEmailDisponivel(String email, Integer idAtual) {
        boolean emailEmUsoPorOutroAtor = atorRepository.findByEmail(email)
                .filter(ator -> idAtual == null || !ator.getId().equals(idAtual))
                .isPresent();

        if (emailEmUsoPorOutroAtor) {
            throw new ConflictException("Ja existe ator com esse email");
        }

        if (administradorRepository.existsByEmail(email)) {
            throw new ConflictException("Ja existe administrador com esse email");
        }
    }

    private AtorResponse toResponse(Ator ator) {
        return new AtorResponse(
                ator.getId(),
                ator.getNome(),
                ator.getEmail(),
                ator.getTelefone(),
                ator.getGenero(),
                ator.getAltura(),
                ator.getPeso(),
                ator.getObservacao(),
                ator.getAtivo());
    }
}

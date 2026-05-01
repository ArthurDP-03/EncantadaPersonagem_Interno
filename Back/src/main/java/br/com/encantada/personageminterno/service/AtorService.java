package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.ResourceNotFoundException;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import br.com.encantada.personageminterno.web.dto.administrador.AdministradorResponse;
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

    public AtorService(AdministradorRepository administradorRepository, AtorRepository atorRepository, PasswordEncoder passwordEncoder) {
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
        if (atorRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe ator com esse email");
        }
        if (administradorRepository.existsByEmail(request.email())) {
            throw new BusinessException("Ja existe administrador com esse email");
            
        }

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
                ator.getAtivo()
        );
    }
    @Transactional(readOnly = true)
    public AtorResponse buscarPorId(int id) {
        Ator ator = atorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ator nao encontrado com o id: " + id));
        return toResponse(ator);

    }

    @Transactional
    public AtorResponse atualizar(int id, AtorRequest request) {
        Ator ator = atorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ator não encontrado com id: " + id));

        // Verifica se o email já existe em outro registro
        if (!ator.getEmail().equals(request.email())) {
            if (administradorRepository.existsByEmail(request.email())) {
                throw new BusinessException("Já existe administrador com esse email");
            }
            if (atorRepository.existsByEmail(request.email())) {
                throw new BusinessException("Já existe ator com esse email");
            }
        }
    
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
    public void deletar (int id){
        if (!atorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ator não encontrado");
        }
        atorRepository.deleteById(id);
    }
}
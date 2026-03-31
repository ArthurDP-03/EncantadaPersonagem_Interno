package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.repository.AtorRepository;
import br.com.encantada.personageminterno.web.dto.ator.AtorRequest;
import br.com.encantada.personageminterno.web.dto.ator.AtorResponse;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AtorService {

    private final AtorRepository atorRepository;

    public AtorService(AtorRepository atorRepository) {
        this.atorRepository = atorRepository;
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
}

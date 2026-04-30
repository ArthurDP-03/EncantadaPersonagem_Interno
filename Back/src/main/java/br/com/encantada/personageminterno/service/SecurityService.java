package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    private final AdministradorRepository administradorRepository;
    private final AtorRepository atorRepository;

    public SecurityService(AdministradorRepository administradorRepository, AtorRepository atorRepository) {
        this.administradorRepository = administradorRepository;
        this.atorRepository = atorRepository;
    }


    public boolean isOwner(Integer id, Authentication authentication) {
        if (authentication == null || id == null) {
            return false;
        }
        
        String email = authentication.getName();
        return administradorRepository.findByEmail(email)
                .map(admin -> admin.getId().equals(id))
                .orElse(false);
    }

    public boolean isAtorOwner(Integer id, Authentication authentication) {
        if (authentication == null || id == null) {
            return false;
        }
        
        String email = authentication.getName();
        return atorRepository.findByEmail(email)
                .map(ator -> ator.getId().equals(id))
                .orElse(false);
    }

    public boolean isAdmin(Authentication authentication) {
        if (authentication == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
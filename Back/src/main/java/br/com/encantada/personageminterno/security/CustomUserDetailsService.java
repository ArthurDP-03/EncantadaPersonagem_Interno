package br.com.encantada.personageminterno.security;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.domain.entity.Ator;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
import br.com.encantada.personageminterno.repository.AtorRepository;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdministradorRepository administradorRepository;
    private final AtorRepository atorRepository;

    public CustomUserDetailsService(
            AdministradorRepository administradorRepository,
            AtorRepository atorRepository
    ) {
        this.administradorRepository = administradorRepository;
        this.atorRepository = atorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return administradorRepository.findByEmail(username)
                .map(this::buildAdministrador)
                .orElseGet(() -> atorRepository.findByEmail(username)
                        .map(this::buildAtor)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado")));
    }

    private UserDetails buildAdministrador(Administrador administrador) {
        return User.withUsername(administrador.getEmail())
                .password(administrador.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();
    }

    private UserDetails buildAtor(Ator ator) {
        return User.withUsername(ator.getEmail())
                .password(ator.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ATOR")))
                .build();
    }

    private String normalizeRole(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "ADMIN";
        }
        return tipo.trim().toUpperCase().replace(' ', '_');
    }
}
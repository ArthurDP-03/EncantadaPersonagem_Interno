package br.com.encantada.personageminterno.security;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import br.com.encantada.personageminterno.repository.AdministradorRepository;
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

    public CustomUserDetailsService(AdministradorRepository administradorRepository) {
        this.administradorRepository = administradorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        Administrador administrador = administradorRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Administrador nao encontrado"));

        return User.withUsername(administrador.getEmail())
                .password(administrador.getSenha())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + normalizeRole(administrador.getTipo()))))
                .build();
    }

    private String normalizeRole(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "ADMIN";
        }
        return tipo.trim().toUpperCase().replace(' ', '_');
    }
}

package br.com.encantada.personageminterno.security;

import br.com.encantada.personageminterno.domain.entity.Administrador;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.expirationMs = expirationMs;
    }

    public String generateToken(Administrador administrador) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(administrador.getEmail())
                .claims(Map.of(
                        "role", normalizeRole(administrador.getTipo()),
                        "name", administrador.getNome()
                ))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String expectedUsername) {
        Claims claims = extractClaims(token);
        Date expiration = claims.getExpiration();
        return claims.getSubject().equals(expectedUsername) && expiration.after(new Date());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String normalizeRole(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return "ADMIN";
        }
        return tipo.trim().toUpperCase().replace(' ', '_');
    }
}

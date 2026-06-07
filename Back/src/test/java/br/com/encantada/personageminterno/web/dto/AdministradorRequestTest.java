package br.com.encantada.personageminterno.web.dto;

import br.com.encantada.personageminterno.web.dto.administrador.AdministradorRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AdministradorRequestTest {

    private static Validator validator;

    @BeforeAll
    static void configurarValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // ── helper ────────────────────────────────────────────────────────────────

    private Set<ConstraintViolation<AdministradorRequest>> validar(AdministradorRequest req) {
        return validator.validate(req);
    }

    private AdministradorRequest requestValido() {
        return new AdministradorRequest("Ana Costa", "ana@email.com", "senha1234", "(41) 99999-9999");
    }

    // ── request válida ────────────────────────────────────────────────────────

    @Test
    void requestValida_naoDeveRetornarViolacoes() {
        assertThat(validar(requestValido())).isEmpty();
    }

    @Test
    void requestSemTelefone_naoDeveRetornarViolacoes() {
        // telefone não tem @NotBlank — é opcional
        AdministradorRequest req = new AdministradorRequest("Ana Costa", "ana@email.com", "senha1234", null);
        assertThat(validar(req)).isEmpty();
    }

    // ── nome ─────────────────────────────────────────────────────────────────

    @Test
    void nome_nuloDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest(null, "ana@email.com", "senha1234", "41999990000");
        Set<ConstraintViolation<AdministradorRequest>> violacoes = validar(req);

        assertThat(violacoes).isNotEmpty();
        assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("nome"));
    }

    @Test
    void nome_emBrancoDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest("   ", "ana@email.com", "senha1234", "41999990000");
        Set<ConstraintViolation<AdministradorRequest>> violacoes = validar(req);

        assertThat(violacoes).anyMatch(v -> v.getPropertyPath().toString().equals("nome"));
    }

    @Test
    void nome_acimaDe150CaracteresDeveRetornarViolacao() {
        String nomeLongo = "A".repeat(151);
        AdministradorRequest req = new AdministradorRequest(nomeLongo, "ana@email.com", "senha1234", "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("nome"));
    }

    @Test
    void nome_com150CaracteresDeveSerValido() {
        String nomeExato = "A".repeat(150);
        AdministradorRequest req = new AdministradorRequest(nomeExato, "ana@email.com", "senha1234", "41999990000");

        assertThat(validar(req)).isEmpty();
    }

    // ── email ─────────────────────────────────────────────────────────────────

    @Test
    void email_nuloDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest("Nome", null, "senha1234", "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void email_formatoInvalidoDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest("Nome", "nao-e-um-email", "senha1234", "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void email_semDominioDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest("Nome", "email@", "senha1234", "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void email_acimaDe150CaracteresDeveRetornarViolacao() {
        String emailLongo = "a".repeat(140) + "@email.com"; // > 150
        AdministradorRequest req = new AdministradorRequest("Nome", emailLongo, "senha1234", "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    // ── senha ─────────────────────────────────────────────────────────────────

    @Test
    void senha_nulaDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest("Nome", "ana@email.com", null, "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha"));
    }

    @Test
    void senha_emBrancoDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest("Nome", "ana@email.com", "", "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha"));
    }

    @Test
    void senha_com7CaracteresDeveRetornarViolacao() {
        AdministradorRequest req = new AdministradorRequest("Nome", "ana@email.com", "1234567", "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha"));
    }

    @Test
    void senha_com8CaracteresDeveSerValida() {
        AdministradorRequest req = new AdministradorRequest("Nome", "ana@email.com", "12345678", "41999990000");

        assertThat(validar(req)).isEmpty();
    }

    @Test
    void senha_acimaDe255CaracteresDeveRetornarViolacao() {
        String senhaLonga = "A".repeat(256);
        AdministradorRequest req = new AdministradorRequest("Nome", "ana@email.com", senhaLonga, "41999990000");

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("senha"));
    }

    // ── telefone ──────────────────────────────────────────────────────────────

    @Test
    void telefone_acimaDe20CaracteresDeveRetornarViolacao() {
        String telefoneLongo = "4".repeat(21);
        AdministradorRequest req = new AdministradorRequest("Nome", "ana@email.com", "senha1234", telefoneLongo);

        assertThat(validar(req))
                .anyMatch(v -> v.getPropertyPath().toString().equals("telefone"));
    }

    @Test
    void telefone_com20CaracteresDeveSerValido() {
        String telefoneExato = "4".repeat(20);
        AdministradorRequest req = new AdministradorRequest("Nome", "ana@email.com", "senha1234", telefoneExato);

        assertThat(validar(req)).isEmpty();
    }
}

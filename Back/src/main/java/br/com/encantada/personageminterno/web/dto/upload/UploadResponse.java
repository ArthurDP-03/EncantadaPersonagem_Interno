package br.com.encantada.personageminterno.web.dto.upload;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta gerada pelo endpoint de upload de imagem")
public record UploadResponse(
        @Schema(
                description = "URL publica da imagem armazenada",
                example = "http://localhost:8080/api/uploads/550e8400-e29b-41d4-a716-446655440000.jpg"
        )
        String url
) {
}

package br.com.encantada.personageminterno.web.dto.upload;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta gerada pelo endpoint de upload de imagem")
public record UploadResponse(
        @Schema(
                description = "URL publica da imagem armazenada no S3",
                example = "https://encantada-personagens.s3.us-east-2.amazonaws.com/personagens/550e8400-e29b-41d4-a716-446655440000.jpg"
        )
        String url
) {
}

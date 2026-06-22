package br.com.encantada.personageminterno.web.controller;

import br.com.encantada.personageminterno.service.UploadService;
import br.com.encantada.personageminterno.web.dto.upload.UploadResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Upload", description = "Envio e armazenamento local de imagens")
@RestController
@RequestMapping("/upload")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @Operation(summary = "Enviar imagem", description = "Recebe uma imagem via multipart, salva localmente e retorna a URL publica.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Imagem enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Arquivo invalido ou ausente"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente ou invalido"),
            @ApiResponse(responseCode = "413", description = "Arquivo acima do limite permitido")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(@RequestParam("file") MultipartFile file) {
        String url = uploadService.salvar(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UploadResponse(url));
    }
}

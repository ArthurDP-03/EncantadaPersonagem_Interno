package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.UploadStorageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final Path uploadDir;
    private final String baseUrl;

    public UploadService(
            @Value("${app.upload.dir}") String uploadDir,
            @Value("${app.upload.base-url}") String baseUrl) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public String salvar(MultipartFile file) {
        validar(file);

        String filename = UUID.randomUUID() + obterExtensao(file.getContentType());
        Path destination = uploadDir.resolve(filename).normalize();

        if (!destination.startsWith(uploadDir)) {
            throw new BusinessException("Caminho de upload invalido");
        }

        try {
            Files.createDirectories(uploadDir);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination);
            }
        } catch (IOException exception) {
            throw new UploadStorageException("Nao foi possivel salvar a imagem enviada", exception);
        }

        return baseUrl + "/" + filename;
    }

    private void validar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Envie um arquivo de imagem valido");
        }

        if (file.getSize() > MAX_FILE_SIZE_BYTES) {
            throw new BusinessException("A imagem deve ter no maximo 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new BusinessException("Formato invalido. Use JPEG, PNG ou WEBP");
        }
    }

    private String obterExtensao(String contentType) {
        if (contentType == null) {
            throw new BusinessException("Formato invalido. Use JPEG, PNG ou WEBP");
        }

        return switch (contentType.toLowerCase(Locale.ROOT)) {
            case "image/jpeg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> throw new BusinessException("Formato invalido. Use JPEG, PNG ou WEBP");
        };
    }
}

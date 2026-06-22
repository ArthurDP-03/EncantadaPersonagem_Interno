package br.com.encantada.personageminterno.service;

import br.com.encantada.personageminterno.exception.BusinessException;
import br.com.encantada.personageminterno.exception.UploadStorageException;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class UploadService {

    private static final long MAX_FILE_SIZE_BYTES = 5L * 1024L * 1024L;

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );

    private final S3Client s3Client;
    private final String bucket;
    private final String prefix;

    public UploadService(
            S3Client s3Client,
            @org.springframework.beans.factory.annotation.Value("${aws.s3.bucket}") String bucket,
            @org.springframework.beans.factory.annotation.Value("${aws.s3.prefix:personagens}") String prefix
    ) {
        this.s3Client = s3Client;
        this.bucket = bucket;
        this.prefix = prefix;
    }

    public String salvar(MultipartFile file) {
        validar(file);

        String filename = prefix + "/" + UUID.randomUUID() + obterExtensao(file.getContentType());

        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(filename)
                            .contentType(file.getContentType())
                            .build(),
                    software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                            file.getInputStream(),
                            file.getSize())
            );

        } catch (S3Exception exception) {
            throw buildUploadStorageException(exception);
        } catch (SdkClientException exception) {
            throw new UploadStorageException(
                    "Nao foi possivel comunicar com a AWS S3. Verifique credenciais, regiao e endpoint configurados.",
                    exception);
        } catch (IOException exception) {
            throw new UploadStorageException("Nao foi possivel enviar imagem para AWS S3", exception);
        }

        return s3Client.utilities()
                .getUrl(builder -> builder.bucket(bucket).key(filename))
                .toExternalForm();
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

    private UploadStorageException buildUploadStorageException(S3Exception exception) {
        String errorCode = exception.awsErrorDetails() != null
                ? exception.awsErrorDetails().errorCode()
                : null;

        if (exception.statusCode() == 403 || "AccessDenied".equalsIgnoreCase(errorCode)) {
            return new UploadStorageException(
                    "A AWS negou o upload da imagem. Verifique se a credencial possui permissao s3:PutObject no bucket "
                            + bucket + " e no prefixo " + prefix + "/.",
                    exception);
        }

        return new UploadStorageException(
                "Nao foi possivel enviar imagem para AWS S3: "
                        + (errorCode != null ? errorCode : exception.getMessage()),
                exception);
    }
}

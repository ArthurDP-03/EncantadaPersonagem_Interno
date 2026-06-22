package br.com.encantada.personageminterno.config;

import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access-key-id:}")
    private String accessKeyId;

    @Value("${aws.secret-access-key:}")
    private String secretAccessKey;

    @Value("${aws.session-token:}")
    private String sessionToken;

    @Value("${aws.s3.endpoint:}")
    private String endpoint;

    @Value("${aws.s3.force-path-style:false}")
    private boolean forcePathStyle;

    @Bean
    public S3Client s3Client() {
        var builder = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider());

        if (endpoint != null && !endpoint.isBlank()) {
            builder.endpointOverride(URI.create(endpoint));
        }

        if (forcePathStyle) {
            builder.serviceConfiguration(S3Configuration.builder()
                    .pathStyleAccessEnabled(true)
                    .build());
        }

        return builder.build();
    }

    private AwsCredentialsProvider credentialsProvider() {
        if (accessKeyId != null && !accessKeyId.isBlank()
                && secretAccessKey != null && !secretAccessKey.isBlank()) {
            if (sessionToken != null && !sessionToken.isBlank()) {
                return StaticCredentialsProvider.create(
                        AwsSessionCredentials.create(accessKeyId, secretAccessKey, sessionToken));
            }

            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(accessKeyId, secretAccessKey));
        }

        return DefaultCredentialsProvider.create();
    }
}

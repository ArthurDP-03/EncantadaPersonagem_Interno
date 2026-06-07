package br.com.encantada.personageminterno;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class EncantadaPersonagemInternoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EncantadaPersonagemInternoApplication.class, args);
    }
}

# Backend Encantada Personagem Interno

API REST em Java 21 com Spring Boot, Spring Security, JWT e SQL Server.

## Stack

- Java 21
- Spring Boot 3.4.4
- Spring Security
- Spring Data JPA
- Mysql
- JWT
- Bean Validation

## Estrutura

- `src/main/java/br/com/encantada/personageminterno/config`: configuracao de seguranca
- `src/main/java/br/com/encantada/personageminterno/domain`: entidades e enums
- `src/main/java/br/com/encantada/personageminterno/repository`: acesso a dados
- `src/main/java/br/com/encantada/personageminterno/security`: geracao e validacao de JWT
- `src/main/java/br/com/encantada/personageminterno/service`: regras de aplicacao iniciais
- `src/main/java/br/com/encantada/personageminterno/web`: controllers, DTOs e tratamento de erro

## Endpoints iniciais

- `POST /api/auth/login`
- `GET /api/administradores`
- `POST /api/administradores`
- `GET /api/atores`
- `POST /api/atores`
- `GET /api/clientes`
- `POST /api/clientes`
- `GET /api/personagens`
- `POST /api/personagens`
- `GET /api/eventos`
- `POST /api/eventos`

## Configuracao

Defina as variaveis de ambiente abaixo antes de rodar a aplicacao:

- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`

Se nao informar `DB_USERNAME`, o valor padrao sera `sa`.

## Observacoes

- O login atual foi estruturado para Administrador.
- O backend ja possui entidades e repositorios para Convite, Escalacao, EventoPersonagem e PersonagemItem.
- Os fluxos mais especificos desses modulos podem ser implementados na proxima etapa, com regras como conflito de agenda e disponibilidade de item.

## COMO RODAR

- Back end: para rodar o back end siga os passos a seguir
    1 - cd back
    2 - $env:SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/encantada"  (o "encantada é o nome contido no seu banco de dados)
    3 - mvn spring-boot:run
- Front end: para rodar o front siga os seguintes passos
    1 - cd front
    2 - npm run dev


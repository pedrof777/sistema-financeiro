# Financeiro Service — Sistema Financeiro Multi-Tenant com JWT

Sistema de gestão financeira pessoal com isolamento completo de dados por usuário.
Desenvolvido com **Spring Boot 3.5.15** e **Java 21**.

Implementa controle de **contas bancárias**, **categorias** e **transações** com fluxo completo de autenticação JWT. Cada usuário acessa exclusivamente seus próprios dados — nenhuma query é executada sem validação de ownership, eliminando a principal vulnerabilidade de APIs multi-tenant (broken object level authorization).

A arquitetura segue o padrão **Controller → Service → Repository**, com separação de interfaces e implementações em todos os services, **DTOs** para isolamento das entidades JPA, **Records** (Java 17+) para imutabilidade, e **Lombok** para redução de boilerplate.

O **cálculo de saldo** é feito em tempo real (`saldoInicial + receitas pagas − despesas pagas`), sem campo denormalizado no banco — garantindo consistência absoluta mesmo após atualizações de status de transações. Todos os valores monetários utilizam **BigDecimal** para precisão exata.

O banco de dados é **PostgreSQL**, gerenciado via **Docker Compose** — ambiente de desenvolvimento replicável com um único comando.

**Segurança:** Spring Security 6 com autenticação stateless (JWT), BCrypt para hash de senhas, filtro personalizado para validação de tokens em cada requisição. Todas as rotas protegidas exigem token válido; o usuário autenticado é extraído do token via `@AuthenticationPrincipal`, nunca do corpo da requisição.

**Testes:** Testes unitários com JUnit 5 e Mockito cobrindo services (`ContaService`, `CategoriaService`, `TransacaoService`) e camada HTTP (`TransacaoController` via MockMvc), incluindo cenários de segurança multi-tenant e cálculo de saldo com múltiplas transações.

---

## Tecnologias

- **Java 21**
- **Spring Boot 3.5.15**
- **Spring Security 6**
- **Spring Data JPA**
- **JJWT** (geração/validação de tokens)
- **PostgreSQL** (banco de dados)
- **Docker Compose** (ambiente do banco)
- **Lombok**
- **JUnit 5 + Mockito** (testes)
- **Maven** (gerenciador de dependências)

---

## Como rodar

### Pré-requisitos

- Java 21+
- Docker e Docker Compose
- Maven (ou use o wrapper `./mvnw`)

### Passos

```bash
# 1. Clone o repositório
git clone https://github.com/pedrof777/financeiroservice
cd financeiroservice

# 2. Suba o banco de dados PostgreSQL com Docker
docker compose up -d

# 3. Execute a aplicação
./mvnw spring-boot:run

# 4. A aplicação estará disponível em:
http://localhost:8080

# 5. Documentação interativa (Swagger):
http://localhost:8080/swagger-ui/index.html
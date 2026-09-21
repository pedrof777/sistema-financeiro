# Financeiro Service — Sistema Financeiro Multi-Tenant com JWT

Sistema de gestão financeira pessoal com isolamento completo de dados por usuário.
Desenvolvido com **Spring Boot 3.5.15** e **Java 21**.

## 📋 O que o sistema faz

- Controle de **contas bancárias**, **categorias** e **transações**
- Isolamento total por usuário — nenhuma query roda sem validar ownership, 
  eliminando a vulnerabilidade "broken object level authorization" (BOLA)
- **Cálculo de saldo em tempo real** (`saldoInicial + receitas pagas − despesas pagas`), 
  sem campo denormalizado — garante consistência mesmo após updates
- Valores monetários com **BigDecimal** (precisão exata, sem erro de arredondamento)

## 🏗️ Arquitetura

- Padrão **Controller → Service → Repository**
- Interfaces e implementações separadas em todos os services
- **DTOs** isolando as entidades JPA
- **Records** (Java 17+) para imutabilidade
- **Lombok** para reduzir boilerplate

## 🔒 Segurança

- Spring Security 6 com autenticação **stateless (JWT)**
- **BCrypt** para hash de senhas
- Filtro personalizado validando token em cada requisição
- Usuário autenticado extraído do token via `@AuthenticationPrincipal` 
  (nunca do corpo da requisição — evita spoofing de identidade)

## ✅ Testes

JUnit 5 + Mockito cobrindo:
- Services (`ContaService`, `CategoriaService`, `TransacaoService`)
- Camada HTTP (`TransacaoController` via MockMvc)
- Cenários de segurança multi-tenant e cálculo de saldo com múltiplas transações

## 🛠️ Tecnologias

Java 21 · Spring Boot 3.5.15 · Spring Security 6 · Spring Data JPA · JJWT · 
PostgreSQL · Docker Compose · Lombok · JUnit 5 + Mockito · Maven

## 🚀 Como rodar

### Pré-requisitos
- Java 21+
- Docker e Docker Compose
- Maven (ou `./mvnw`)

### Passos

```bash
git clone https://github.com/pedrof777/financeiroservice
cd financeiroservice

# Suba o banco PostgreSQL
docker compose up -d

# Execute a aplicação
./mvnw spring-boot:run
```
Aplicação disponível em `http://localhost:8080`

Swagger: `http://localhost:8080/swagger-ui/index.html`

# Retrieval-Augmented Generation (RAG) API

Uma aplicação backend escalável desenvolvida em Java para ingestão de documentos, vetorização customizada e interação
via chat utilizando Retrieval-Augmented Generation (RAG). A arquitetura foi desenhada com foco em alta coesão, baixo
acoplamento (Clean Architecture) e processamento assíncrono para garantir resiliência.

## Arquitetura e Padrões de Projeto

O projeto adota **Clean Architecture** e princípios **SOLID**, isolando o domínio das integrações externas. A estrutura modular é dividida da seguinte forma:

```text
src/main/java/br/goes/luis/application/
├── Application.java
├── core/                                 # Configurações globais, infraestrutura e abstrações base
│   ├── infrastructure/
│   │   ├── ai/                           # Adaptadores (Vertex, LangChain4j), Providers e Retrievers
│   │   ├── config/                       # Beans do Spring (Modelos, Embeddings, Storage, etc.)
│   │   ├── exception/                    # Tratamento global de erros e exceções de domínio
│   │   └── service/                      # Serviços genéricos (GCS Storage, Extração de PDF)
│   └── shared/                           # Componentes compartilhados para maximizar reuso (DRY)
│       ├── domain/entity/                # BaseEntity
│       ├── helper/                       # Mappers genéricos
│       └── infrastructure/repository/    # BaseRepository
│
└── modules/                              # Módulos de negócio isolados
    ├── chat/                             # Domínio de conversas e histórico
    │   ├── application/useCase/          # Casos de uso isolados de frameworks (Application)
    │   ├── domain/entity/                # Entidades de negócio e regras puras (Domain)
    │   ├── infrastructure/repository/    # Acesso a dados via JPA (Infrastructure)
    │   └── presentation/                 # Controllers e DTOs (Presentation)
    │
    ├── document/                         # Domínio de ingestão e fatiamento de PDFs
    │   ├── application/useCase/          # Orquestração de upload
    │   ├── domain/                       # Entidades e máquinas de estado (Enums)
    │   ├── infrastructure/               # Repositórios e processamento assíncrono em background
    │   └── presentation/                 # Endpoints REST de upload
    │
    └── rag/                              # Orquestração de inferência RAG
        └── infrastructure/service/       # Serviços unificados de recuperação e geração de IA
```

## Pipeline RAG e Processamento

A aplicação implementa um fluxo de RAG avançado com roteamento dinâmico e vetorização customizada.

### 1. Ingestão e Processamento Assíncrono

- O upload armazena o arquivo cru no **Google Cloud Storage**.
- O `DocumentAsyncProcessorService` assume o processamento em background (evitando timeout na API).
- O texto é extraído, limpo e dividido em chunks via **LangChain4j**.
- O status do documento transita via máquina de estados (`DocumentUploadStatusEnum`, `DocumentChunkStatusEnum`).

### 2. Vetorização e Armazenamento (Custom Embedding)

- Os chunks são vetorizados utilizando adaptadores customizados para o GCP Vertex AI: `QwenVertexEmbeddingModelAdapter`e
  `QwenVertexEmbeddingTokenizerAdapter`.
- Os vetores gerados são persistidos no PostgreSQL utilizando a extensão **pgvector**.

### 3. Recuperação e Inferência (Dynamic RAG)

- O **`DynamicRoutingRetriever`** atua como um roteador semântico. Ele avalia a query do usuário e o escopo do chat para
  decidir se a busca vetorial deve focar em documentos de FAQ globais ou em documentos privados anexados à sessão (
  `ChatDocumentEntity`).
- O **`JpaChatMemoryStoreProvider`** injeta o histórico da conversa no prompt, garantindo que o LLM mantenha o contexto
  das interações anteriores.

## Tecnologias

- **Linguagem/Framework:** Java 25+ | Spring Boot 4.x
- **IA/LLM:** LangChain4j | Modelos Qwen (GCP Vertex AI) | HuggingFace Tokenizer
- **Banco de Dados:** PostgreSQL + pgvector | Flyway (Migrações)
- **Infraestrutura/Cloud:** Docker | Google Cloud Storage
- **Design:** Hexagonal/Clean Architecture | SRP | DRY

Bem observado. Provavelmente você quis dizer **Google Cloud CLI (gcloud)**, que é essencial para autenticar e interagir com os serviços do GCP localmente.

Aqui está a seção **Configuração do Ambiente** atualizada com o passo a passo da instalação e autenticação da CLI, além do arquivo `.env`:

## Configuração do Ambiente

Antes de rodar a aplicação, é necessário configurar o ambiente local e as variáveis de ambiente.

### 1. Google Cloud CLI (gcloud)
Como o projeto utiliza serviços do GCP (Vertex AI, Cloud Storage), você precisa da CLI do Google instalada e autenticada.

1. Instale o [Google Cloud CLI](https://cloud.google.com/sdk/docs/install).
2. Inicialize e autentique-se na sua conta do Google Cloud:
   ```bash
   gcloud init
   ```
3. Configure as credenciais padrão de aplicação (Application Default Credentials) para que o Spring Boot consiga acessar seu GCP localmente:
   ```bash
   gcloud auth application-default login
   ```
   *Isso dispensa a necessidade de apontar o caminho do arquivo JSON da Service Account na maioria dos casos locais.*

### 2. Variáveis de Ambiente (.env)
Crie um arquivo `.env` na raiz do projeto contendo as seguintes variáveis:

```env
# Vertex AI / Gemini
VERTEX_PROJECT_ID=seu-projeto-gcp
VERTEX_GEMINI_MODEL_NAME=gemini-2.5-flash-lite
VERTEX_GEMINI_LOCATION=us-central1

# Vertex AI - Custom Embeddings (Qwen)
VERTEX_EMBEDDING_MODEL_DIMENSION=2560
VERTEX_EMBEDDING_LOCATION=us-central1
VERTEX_EMBEDDING_ENDPOINT_ID=seu-endpoint-id
VERTEX_EMBEDDING_DEDICATED_ENDPOINT=seu-endoint-dedicado

# Document AI (Extração de texto)
VERTEX_DOCUMENTAI_PROCESSOR=seu-processor-id
VERTEX_DOCUMENTAI_LOCATION=us

# Google Cloud Storage
GOOGLE_BUCKET_NAME=nome-do-seu-bucket

# Banco de Dados (PostgreSQL + pgvector)
POSTGRES_DB=rag_db
POSTGRES_USER=postgres
POSTGRES_PASSWORD=sua_senha
```

## Execução Local

1. Suba a infraestrutura de banco de dados (o Flyway rodará automaticamente):

```bash
docker compose up -d
```

2. Compile e inicie a aplicação:

```bash
./mvnw clean install -DskipTests
./mvnw spring-boot:run
```

## Referência da API (Endpoints Principais)

### Documentos (`/documents`)

**POST `/documents/upload/private`** | **POST `/documents/upload/faq`**
*Faz o upload do PDF e inicia o job assíncrono.*

- **Content-Type:** `multipart/form-data`
- **Body:** `file` (Binary)
- **Response (202 Accepted):** Retorna o ID do documento para polling de status.

### Chat (`/chats`)

**POST `/chats`**
*Cria uma nova sessão de chat.*

```json
{
  "title": "Dúvidas sobre o projeto"
}
```

**POST `/chats/attach-document/{chatId}`**
*Vincula um ou mais documentos a um chat para restringir/focar o contexto do RAG.*

```json
{
  "documentsId": [
    "uuid-do-documento-01",
    "uuid-do-documento-02"
  ]
}
```

**POST `/chats/{chatId}`**
*Envia um prompt para o LLM.*

- **Body:** `file` (Binary)
- **Produces** = `MediaType.TEXT_EVENT_STREAM_VALUE`
- **Response (200 Ok):** Retorna um `Flux` contendo a resposta da IA.

```json
{
  "message": "Qual a arquitetura descrita neste documento?"
}
```

## Tratamento de Exceções

Implementado via `GlobalExceptionHandler` (`@RestControllerAdvice`). Exceções de domínio estendem `DomainException` (ex:
`EntityNotActivateException`) e são traduzidas para RFC 7807 Problem Details (HTTP 400, 404, 409), mascarando erros
internos (HTTP 500) e gerando logs estruturados.
# 🤖 News AI Daily

Sistema em **Java + Spring Boot** que busca notícias mundiais automaticamente, resume com **Claude AI (Anthropic)** e envia um **email HTML** diariamente no horário que você escolher.

Possui uma **página web** para configurar email, horário, temas e idioma — sem precisar editar código.

---

## ✨ Funcionalidades

- 📰 Busca notícias em tempo real via **NewsAPI**
- 🤖 Resume cada notícia em português com **Claude AI**
- 📧 Envia email HTML formatado todo dia no horário escolhido
- 🌍 Suporte a múltiplos temas: Tecnologia, Negócios, Ciência, Esportes, Saúde e mais
- 🌐 Página web para configurar tudo sem mexer no código
- ⏰ Agendamento dinâmico via cron — mude o horário pela interface

---

## 🗂️ Estrutura do Projeto

```
news-ai-java/
├── pom.xml
├── .env                              ← Suas chaves reais (NÃO vai pro GitHub)
├── .env.example                      ← Modelo para outras pessoas configurarem
├── .gitignore
│
└── src/
    ├── main/
    │   ├── java/com/newsai/
    │   │   ├── NewsAiApplication.java
    │   │   ├── config/
    │   │   │   ├── NewsController.java       ← Endpoints REST + abre index.html
    │   │   │   └── SchedulerConfig.java      ← Agendador dinâmico
    │   │   ├── model/
    │   │   │   ├── NewsArticle.java
    │   │   │   ├── NewsApiResponse.java
    │   │   │   └── UserPreferences.java
    │   │   ├── service/
    │   │   │   ├── NewsApiService.java       ← Busca noticias por categoria
    │   │   │   ├── NewsAIService.java        ← Resume com Claude AI
    │   │   │   └── EmailService.java         ← Envia email HTML
    │   │   └── scheduler/
    │   │       └── NewsScheduler.java        ← Orquestra o pipeline diário
    │   └── resources/
    │       ├── application.properties        ← Lê variáveis do .env
    │       ├── static/
    │       │   └── index.html                ← Página web de configuração
    │       └── templates/
    │           └── email-noticias.html
    └── test/
        └── java/com/newsai/
            └── NewsAiApplicationTests.java
```

---

## 🔑 Pré-requisitos

### 1. NewsAPI (gratuita)
1. Acesse [https://newsapi.org](https://newsapi.org)
2. Clique em **Get API Key** e cadastre-se
3. Copie sua chave

### 2. Anthropic — Claude AI
1. Acesse [https://console.anthropic.com](https://console.anthropic.com)
2. Vá em **API Keys** e gere uma chave
3. Copie sua chave

### 3. Senha de App do Gmail
1. Acesse sua conta Google → **Segurança**
2. Ative a **Verificação em 2 etapas**
3. Vá em **Senhas de app** → crie uma para "NewsAI"
4. Copie os 16 caracteres gerados

---

## ⚙️ Configuração

Copie o arquivo de exemplo e preencha com seus dados:

```bash
cp .env.example .env
```

Edite o `.env`:

```env
MAIL_USERNAME=seu.email@gmail.com
MAIL_PASSWORD=xxxx xxxx xxxx xxxx

NEWSAPI_KEY=sua_chave_newsapi

ANTHROPIC_API_KEY=sk-ant-...

EMAIL_DESTINATARIO=quem.recebe@email.com
EMAIL_REMETENTE_NOME=News AI Daily

NEWS_SCHEDULER_CRON=0 0 8 * * ?
NEWSAPI_PAGE_SIZE=10
```

> ⚠️ **Nunca suba o `.env` para o GitHub.** O `.gitignore` já está configurado para ignorá-lo.

---

## 🚀 Como Rodar

### Pré-requisitos do sistema
- Java 17+
- Maven 3.8+

```bash
# Compilar
mvn clean install -DskipTests

# Rodar
mvn spring-boot:run
```

Ou gerar um JAR executável:
```bash
mvn clean package -DskipTests
java -jar target/news-ai-daily-1.0.0.jar
```

---

## 🌐 Endpoints

| URL | Método | O que faz |
|-----|--------|-----------|
| `http://localhost:8080` | GET | Abre a página web de configuração |
| `http://localhost:8080/configurar` | GET | Abre a página web de configuração |
| `http://localhost:8080/api/health` | GET | Verifica se a aplicação está rodando |
| `http://localhost:8080/api/enviar-agora` | GET | Envia email imediatamente (teste) |
| `http://localhost:8080/api/configurar` | POST | Salva preferências da página web |

---

## 🔄 Fluxo Completo

```
Página web salva preferências
        ↓
⏰ Cron dispara no horário escolhido
        ↓
📡 NewsApiService → busca notícias na NewsAPI (por categoria)
        ↓
🤖 NewsAIService → resume cada notícia com Claude AI (no idioma escolhido)
        ↓
🤖 NewsAIService → gera introdução personalizada para o email
        ↓
📧 EmailService → monta HTML e envia via SMTP (Gmail)
        ↓
✅ Email chega na sua caixa de entrada!
```

---

## 📦 Tecnologias

| Tecnologia | Versão | Para quê |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.2.0 | Framework web e agendamento |
| Spring Mail | 3.2.0 | Envio de emails SMTP |
| OkHttp | 4.12.0 | Chamadas HTTP para APIs externas |
| Gson | 2.10.1 | Parse de JSON |
| Lombok | 1.18.34 | Redução de código repetitivo |
| spring-dotenv | 4.0.0 | Leitura do arquivo .env |

---

## 🧪 Testando

Com a aplicação rodando, acesse no navegador:

```
http://localhost:8080
```

Configure seus temas, email e horário pela interface e clique em **Ativar meu briefing diário**.

Para um teste imediato sem esperar o horário agendado, acesse:

```
http://localhost:8080/api/enviar-agora
```

---

## 👤 Autor

Pedro Castro — [github.com/PedrorCastro](https://github.com/PedrorCastro)
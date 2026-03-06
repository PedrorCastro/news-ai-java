package com.newsai.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.newsai.model.NewsArticle;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class NewsAIService {

    @Value("${anthropic.api.key}")
    private String anthropicApiKey;

    @Value("${anthropic.api.url}")
    private String anthropicApiUrl;

    @Value("${anthropic.model}")
    private String model;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    /**
     * Resume uma noticia no idioma solicitado
     */
    public String resumirNoticia(NewsArticle artigo, String idioma) {
        String instrucaoIdioma = switch (idioma) {
            case "en" -> "in English";
            case "es" -> "en espanol";
            default   -> "em portugues brasileiro";
        };

        String prompt = String.format(
            "Voce e um jornalista. Resuma esta noticia em 2-3 frases claras %s. Seja objetivo e use linguagem simples.\n\nTitulo: %s\nDescricao: %s\n\nForneça apenas o resumo, sem introducoes.",
            instrucaoIdioma,
            artigo.getTitle(),
            artigo.getDescription() != null ? artigo.getDescription() : "Sem descricao"
        );
        return chamarClaude(prompt);
    }

    /**
     * Gera introducao do email no idioma solicitado
     */
    public String gerarIntroducaoEmail(int quantidade, String idioma) {
        String instrucaoIdioma = switch (idioma) {
            case "en" -> "in English";
            case "es" -> "en espanol";
            default   -> "em portugues brasileiro";
        };

        String prompt = String.format(
            "Crie uma introducao calorosa para um email de resumo de %d noticias diarias %s. Use no maximo 2 frases. Tom profissional e amigavel. Apenas texto corrido.",
            quantidade, instrucaoIdioma
        );
        return chamarClaude(prompt);
    }

    private String chamarClaude(String prompt) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("model", model);
            requestBody.addProperty("max_tokens", 1024);

            JsonArray messages = new JsonArray();
            JsonObject msg = new JsonObject();
            msg.addProperty("role", "user");
            msg.addProperty("content", prompt);
            messages.add(msg);
            requestBody.add("messages", messages);

            RequestBody body = RequestBody.create(gson.toJson(requestBody), JSON);
            Request request = new Request.Builder()
                    .url(anthropicApiUrl).post(body)
                    .addHeader("x-api-key", anthropicApiKey)
                    .addHeader("anthropic-version", "2023-06-01")
                    .addHeader("content-type", "application/json")
                    .build();

            Response response = httpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                log.error("Erro Claude API: HTTP {}", response.code());
                return "Resumo nao disponivel.";
            }

            JsonObject jsonResponse = gson.fromJson(response.body().string(), JsonObject.class);
            return jsonResponse.getAsJsonArray("content").get(0)
                    .getAsJsonObject().get("text").getAsString().trim();

        } catch (IOException e) {
            log.error("Falha Claude API: {}", e.getMessage());
            return "Resumo nao disponivel.";
        } catch (Exception e) {
            log.error("Erro inesperado: {}", e.getMessage());
            return "Resumo nao disponivel.";
        }
    }

    /**
     * Enriquece lista de noticias com resumos da IA
     */
    public void enriquecerNoticias(List<NewsArticle> artigos, String idioma) {
        log.info("Gerando resumos para {} noticias (idioma: {})...", artigos.size(), idioma);
        for (int i = 0; i < artigos.size(); i++) {
            NewsArticle artigo = artigos.get(i);
            log.info("  {}/{}: {}", i + 1, artigos.size(), artigo.getTitle());
            artigo.setAiSummary(resumirNoticia(artigo, idioma));
            try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        log.info("Resumos concluidos!");
    }

    // Metodo legado
    public void enriquecerNoticias(List<NewsArticle> artigos) {
        enriquecerNoticias(artigos, "pt");
    }

    public String gerarIntroducaoEmail(int quantidade) {
        return gerarIntroducaoEmail(quantidade, "pt");
    }
}
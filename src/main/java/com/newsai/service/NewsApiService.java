package com.newsai.service;

import com.google.gson.Gson;
import com.newsai.model.NewsApiResponse;
import com.newsai.model.NewsArticle;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class NewsApiService {

    @Value("${newsapi.key}")
    private String apiKey;

    @Value("${newsapi.url}")
    private String apiUrl;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();

    /**
     * Busca noticias com categoria e quantidade dinamicas
     * @param categoria ex: "technology", "sports", null = todas
     * @param quantidade numero de noticias
     */
    public List<NewsArticle> buscarNoticias(String categoria, int quantidade) {
        log.info("Buscando noticias... categoria={} quantidade={}", categoria, quantidade);

        // Monta a URL com os parametros
        String url;
        if (categoria != null && !categoria.isEmpty()) {
            url = String.format("%s?country=us&category=%s&pageSize=%d&apiKey=%s",
                    apiUrl, categoria, quantidade, apiKey);
        } else {
            url = String.format("%s?country=us&pageSize=%d&apiKey=%s",
                    apiUrl, quantidade, apiKey);
        }

        Request request = new Request.Builder()
                .url(url).get()
                .addHeader("User-Agent", "NewsAI-Java/1.0")
                .build();

        try {
            Response response = httpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Erro NewsAPI: HTTP {}", response.code());
                return new ArrayList<>();
            }

            String body = response.body().string();
            NewsApiResponse newsResponse = gson.fromJson(body, NewsApiResponse.class);

            if (!"ok".equals(newsResponse.getStatus())) {
                log.error("NewsAPI status erro: {}", body);
                return new ArrayList<>();
            }

            List<NewsArticle> artigos = new ArrayList<>();
            for (NewsApiResponse.ArticleRaw raw : newsResponse.getArticles()) {
                if (raw.getTitle() == null || raw.getTitle().equals("[Removed]")) continue;
                artigos.add(NewsArticle.builder()
                        .title(raw.getTitle())
                        .description(raw.getDescription())
                        .content(raw.getContent())
                        .url(raw.getUrl())
                        .urlToImage(raw.getUrlToImage())
                        .sourceName(raw.getSource() != null ? raw.getSource().getName() : "Desconhecido")
                        .author(raw.getAuthor())
                        .publishedAt(raw.getPublishedAt())
                        .build());
            }

            log.info("{} noticias carregadas.", artigos.size());
            return artigos;

        } catch (IOException e) {
            log.error("Falha ao conectar NewsAPI: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // Metodo legado (compatibilidade)
    public List<NewsArticle> buscarNoticiasGlobais() {
        return buscarNoticias(null, 10);
    }
}
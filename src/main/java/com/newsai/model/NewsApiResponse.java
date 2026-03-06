package com.newsai.model;

import lombok.Data;
import java.util.List;

/**
 * 📦 NewsApiResponse - Mapeamento da resposta da NewsAPI
 *
 * Quando a NewsAPI retorna o JSON, ele tem este formato:
 * {
 *   "status": "ok",
 *   "totalResults": 38,
 *   "articles": [ { ... }, { ... } ]
 * }
 *
 * O Gson vai converter esse JSON para este objeto automaticamente,
 * desde que os nomes dos campos batam com as chaves do JSON.
 */
@Data
public class NewsApiResponse {

    /** "ok" se a requisição funcionou, "error" se algo deu errado */
    private String status;

    /** Total de artigos disponíveis para a query */
    private int totalResults;

    /** Lista com os artigos retornados */
    private List<ArticleRaw> articles;

    /**
     * Classe interna que representa um artigo como vem bruto da NewsAPI.
     * Depois convertemos para NewsArticle (nosso modelo limpo).
     */
    @Data
    public static class ArticleRaw {
        private Source source;
        private String author;
        private String title;
        private String description;
        private String url;
        private String urlToImage;
        private String publishedAt;
        private String content;

        @Data
        public static class Source {
            private String id;
            private String name;
        }
    }
}

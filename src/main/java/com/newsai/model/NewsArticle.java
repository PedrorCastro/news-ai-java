package com.newsai.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 📰 NewsArticle - Modelo de uma notícia
 *
 * Esta classe representa uma notícia individual.
 * @Data (Lombok) gera automaticamente: getters, setters, toString, equals e hashCode.
 * @Builder permite criar objetos assim: NewsArticle.builder().title("...").build()
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewsArticle {

    /** Título da notícia */
    private String title;

    /** Descrição curta / subtítulo */
    private String description;

    /** Conteúdo completo (pode vir truncado pela NewsAPI gratuita) */
    private String content;

    /** URL para ler a notícia completa no site de origem */
    private String url;

    /** URL da imagem de capa da notícia */
    private String urlToImage;

    /** Nome do veículo/portal que publicou */
    private String sourceName;

    /** Autor da notícia */
    private String author;

    /** Data e hora de publicação (formato ISO 8601: "2025-01-15T08:30:00Z") */
    private String publishedAt;

    /**
     * Resumo gerado pela IA (Claude) — não vem da NewsAPI,
     * é preenchido pelo NewsAIService após a chamada à Anthropic
     */
    private String aiSummary;
}
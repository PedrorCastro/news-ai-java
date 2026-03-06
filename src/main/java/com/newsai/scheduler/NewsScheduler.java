package com.newsai.scheduler;

import com.newsai.model.NewsArticle;
import com.newsai.model.UserPreferences;
import com.newsai.service.EmailService;
import com.newsai.service.NewsAIService;
import com.newsai.service.NewsApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsScheduler {

    private final NewsApiService newsApiService;
    private final NewsAIService newsAIService;
    private final EmailService emailService;

    // Preferencias atuais (atualizadas pela pagina web)
    private volatile UserPreferences preferencias;

    @Value("${email.destinatario}")
    private String emailPadrao;

    @Value("${newsapi.pageSize}")
    private int pageSize;

    /**
     * Atualiza as preferencias quando o usuario salva pela pagina web
     */
    public void atualizarPreferencias(UserPreferences prefs) {
        this.preferencias = prefs;
        log.info("Preferencias atualizadas para: {}", prefs.getEmail());
    }

    /**
     * Execucao agendada pelo cron do application.properties
     */
    @Scheduled(cron = "${news.scheduler.cron}")
    public void executarEnvioNoticiasAgendado() {
        log.info("INICIANDO ENVIO DIARIO DE NOTICIAS");
        executarPipeline();
    }

    /**
     * Execucao manual (via /api/enviar-agora ou reagendamento da web)
     */
    public void executarEnvioImediato() {
        log.info("Execucao manual iniciada");
        executarPipeline();
    }

    private void executarPipeline() {
        try {
            // Determina categorias e quantidade baseado nas preferencias
            String categoria = null;
            int quantidade = pageSize;
            String emailDestino = emailPadrao;
            String idioma = "pt";

            if (preferencias != null) {
                emailDestino = preferencias.getEmail();
                quantidade   = preferencias.getQuantidade();
                idioma       = preferencias.getIdioma();
                // Pega a primeira categoria selecionada (NewsAPI aceita uma por vez)
                if (preferencias.getCategorias() != null && !preferencias.getCategorias().isEmpty()) {
                    String cat = preferencias.getCategorias().get(0);
                    if (!cat.equals("all")) categoria = cat;
                }
            }

            log.info("PASSO 1/4: Buscando noticias... (categoria: {}, quantidade: {})", categoria, quantidade);
            List<NewsArticle> artigos = newsApiService.buscarNoticias(categoria, quantidade);

            if (artigos.isEmpty()) {
                log.warn("Nenhuma noticia encontrada. Abortando.");
                return;
            }
            log.info("{} noticias encontradas.", artigos.size());

            log.info("PASSO 2/4: Gerando resumos com IA (idioma: {})...", idioma);
            newsAIService.enriquecerNoticias(artigos, idioma);

            log.info("PASSO 3/4: Gerando introducao...");
            String introducao = newsAIService.gerarIntroducaoEmail(artigos.size(), idioma);

            log.info("PASSO 4/4: Enviando email para {}...", emailDestino);
            emailService.enviarNewsletterDiaria(artigos, introducao, emailDestino);

            log.info("PIPELINE CONCLUIDO COM SUCESSO!");

        } catch (Exception e) {
            log.error("FALHA NO PIPELINE: {}", e.getMessage(), e);
        }
    }
}
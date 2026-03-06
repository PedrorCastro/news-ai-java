package com.newsai.service;

import com.newsai.model.NewsArticle;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Value("${email.remetente.nome}")
    private String nomeRemetente;

    @Value("${email.destinatario}")
    private String destinatarioPadrao;

    /**
     * Envia o email para um destinatario especifico (usado pela pagina web)
     */
    public void enviarNewsletterDiaria(List<NewsArticle> artigos, String introducao, String destinatario) {
        log.info("Preparando email para: {}", destinatario);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remetente, nomeRemetente);
            helper.setTo(destinatario);
            helper.setSubject(gerarAssunto());
            helper.setText(montarHtml(artigos, introducao), true);
            mailSender.send(message);
            log.info("Email enviado com sucesso para {}!", destinatario);
        } catch (MessagingException e) {
            log.error("Falha ao enviar email: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado: {}", e.getMessage());
        }
    }

    /**
     * Envia para o destinatario padrao do application.properties
     */
    public void enviarNewsletterDiaria(List<NewsArticle> artigos, String introducao) {
        enviarNewsletterDiaria(artigos, introducao, destinatarioPadrao);
    }

    private String gerarAssunto() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        return "Suas Noticias Diarias - " + LocalDate.now().format(f);
    }

    private String montarHtml(List<NewsArticle> artigos, String introducao) {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("pt", "BR"));
        String dataHoje = LocalDate.now().format(f);

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang='pt-BR'><head><meta charset='UTF-8'>")
            .append("<style>")
            .append("body{font-family:Arial,sans-serif;background:#f0f4f8;margin:0;padding:0}")
            .append(".container{max-width:680px;margin:30px auto;background:#fff;border-radius:16px;overflow:hidden;box-shadow:0 4px 20px rgba(0,0,0,0.1)}")
            .append(".header{background:linear-gradient(135deg,#1a1a2e,#0f3460);color:white;padding:40px 30px;text-align:center}")
            .append(".header h1{margin:0;font-size:28px}")
            .append(".header .data{color:#a0c4ff;font-size:14px;margin-top:8px}")
            .append(".intro{background:#e8f4fd;padding:20px 30px;border-left:4px solid #0f3460;color:#333;font-size:15px;line-height:1.6}")
            .append(".content{padding:20px 30px}")
            .append(".article{border:1px solid #e8ecf0;border-radius:12px;margin-bottom:20px;overflow:hidden}")
            .append(".article-header{background:#f8fafc;padding:15px 20px;border-bottom:1px solid #e8ecf0}")
            .append(".article-source{color:#0f3460;font-size:12px;font-weight:700;text-transform:uppercase}")
            .append(".article-title{font-size:17px;font-weight:700;color:#1a1a2e;margin:6px 0 0;line-height:1.4}")
            .append(".article-body{padding:15px 20px}")
            .append(".article-desc{color:#555;font-size:14px;line-height:1.6;margin-bottom:12px}")
            .append(".ai-summary{background:linear-gradient(135deg,#e8f4fd,#f0e8fd);border-radius:8px;padding:12px 15px;margin-bottom:12px}")
            .append(".ai-label{font-size:11px;font-weight:700;color:#7c3aed;text-transform:uppercase;margin-bottom:5px}")
            .append(".ai-text{color:#333;font-size:14px;line-height:1.5}")
            .append(".read-more{display:inline-block;background:#0f3460;color:white;padding:8px 16px;border-radius:20px;text-decoration:none;font-size:13px;font-weight:600}")
            .append(".footer{background:#1a1a2e;color:#888;text-align:center;padding:20px;font-size:12px}")
            .append(".footer a{color:#a0c4ff}")
            .append("</style></head><body><div class='container'>")
            .append("<div class='header'><h1>Noticias Diarias</h1>")
            .append("<div class='data'>").append(dataHoje).append("</div></div>")
            .append("<div class='intro'>").append(introducao).append("</div>")
            .append("<div class='content'>");

        for (NewsArticle a : artigos) {
            String titulo  = a.getTitle()       != null ? a.getTitle()       : "Sem titulo";
            String desc    = a.getDescription() != null ? a.getDescription() : "";
            String fonte   = a.getSourceName()  != null ? a.getSourceName()  : "Desconhecido";
            String resumo  = a.getAiSummary()   != null ? a.getAiSummary()   : "";
            String url     = a.getUrl()         != null ? a.getUrl()         : "#";

            html.append("<div class='article'>")
                .append("<div class='article-header'>")
                .append("<div class='article-source'>").append(fonte).append("</div>")
                .append("<div class='article-title'>").append(titulo).append("</div>")
                .append("</div><div class='article-body'>");

            if (!desc.isEmpty())
                html.append("<p class='article-desc'>").append(desc).append("</p>");

            if (!resumo.isEmpty())
                html.append("<div class='ai-summary'><div class='ai-label'>Resumo IA</div>")
                    .append("<div class='ai-text'>").append(resumo).append("</div></div>");

            html.append("<a href='").append(url).append("' class='read-more' target='_blank'>Ler noticia completa</a>")
                .append("</div></div>");
        }

        html.append("</div>")
            .append("<div class='footer'>")
            .append("<p>Gerado pelo <strong>News AI Daily</strong></p>")
            .append("<p>Noticias por <a href='https://newsapi.org'>NewsAPI</a> | Resumos por <a href='https://anthropic.com'>Claude AI</a></p>")
            .append("</div></div></body></html>");

        return html.toString();
    }
}
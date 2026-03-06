package com.newsai.config;

import com.newsai.model.UserPreferences;
import com.newsai.scheduler.NewsScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class NewsController {

    private final NewsScheduler newsScheduler;
    private final TaskScheduler taskScheduler;

    /**
     * GET /api/health
     * Verifica se a aplicacao esta rodando.
     */


    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "News AI Daily",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * GET /api/enviar-agora
     * Dispara o envio IMEDIATAMENTE — apenas para testes manuais.
     * NAO e chamado pela pagina web.
     */
    @GetMapping("/enviar-agora")
    public ResponseEntity<Map<String, String>> enviarAgora() {
        log.info("Envio manual solicitado via /enviar-agora");
        new Thread(newsScheduler::executarEnvioImediato).start();
        return ResponseEntity.ok(Map.of(
                "mensagem", "Envio iniciado! Verifique seu email em alguns minutos.",
                "timestamp", LocalDateTime.now().toString()
        ));
    }

    /**
     * POST /api/configurar
     * Recebe as preferencias da pagina web e APENAS salva + reagenda.
     * NAO dispara o envio imediato — o email so chegara no horario escolhido.
     *
     * Body esperado:
     * {
     *   "email": "pedro@email.com",
     *   "hora": 8,
     *   "quantidade": 10,
     *   "idioma": "pt",
     *   "categorias": ["technology", "science"]
     * }
     */
    @PostMapping("/configurar")
    public ResponseEntity<Map<String, Object>> configurar(@RequestBody UserPreferences prefs) {
        log.info("=== NOVA CONFIGURACAO RECEBIDA ===");
        log.info("Email:      {}", prefs.getEmail());
        log.info("Hora:       {}:00", prefs.getHora());
        log.info("Quantidade: {}", prefs.getQuantidade());
        log.info("Idioma:     {}", prefs.getIdioma());
        log.info("Categorias: {}", prefs.getCategorias());

        // 1. Salva as preferencias no scheduler (sem disparar envio)
        newsScheduler.atualizarPreferencias(prefs);

        // 2. Reagenda o cron com o novo horario escolhido pelo usuario
        //    Formato: "0 0 HORA * * ?" = todo dia no horario escolhido
        String novoCron = String.format("0 0 %d * * ?", prefs.getHora());
        log.info("Reagendando cron para: {} (todo dia as {}:00)", novoCron, prefs.getHora());

        // schedule() registra uma nova tarefa recorrente com o cron dinamico
        // Ela VAI RODAR todo dia no horario — mas NAO agora
        taskScheduler.schedule(
            newsScheduler::executarEnvioImediato,
            new CronTrigger(novoCron)
        );

        log.info("Configuracao salva! Proximo envio sera as {}:00", prefs.getHora());

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Configuracao salva com sucesso! Voce recebera seu briefing todos os dias as "
                            + prefs.getHora() + ":00 no email " + prefs.getEmail(),
                "email", prefs.getEmail(),
                "proximoEnvio", "Hoje as " + prefs.getHora() + ":00",
                "categorias", prefs.getCategorias()
        ));
    }
}
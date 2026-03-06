package com.newsai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 🚀 NewsAiApplication - Ponto de entrada da aplicação
 *
 * @SpringBootApplication: anotação mágica que combina três anotações:
 *   - @Configuration: marca como fonte de configurações Spring
 *   - @EnableAutoConfiguration: Spring Boot configura tudo automaticamente
 *     (banco, email, web server, etc.) baseado nas dependências do pom.xml
 *   - @ComponentScan: Spring varre o pacote `com.newsai` e registra
 *     todos os @Service, @Component, @Controller, etc.
 *
 * @EnableScheduling: NECESSÁRIO para ativar o agendamento com @Scheduled.
 * Sem esta anotação, os métodos marcados com @Scheduled nunca executam!
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
public class NewsAiApplication {

    /**
     * main(): método Java padrão — porta de entrada de qualquer programa Java.
     * SpringApplication.run() inicializa:
     *   1. O servidor Tomcat embutido (porta 8080)
     *   2. O contexto do Spring (todos os beans/serviços)
     *   3. O agendador de tarefas
     */
    public static void main(String[] args) {
        log.info("╔══════════════════════════════════╗");
        log.info("║     🤖 NEWS AI DAILY v1.0.0      ║");
        log.info("║   Iniciando aplicação...          ║");
        log.info("╚══════════════════════════════════╝");

        SpringApplication.run(NewsAiApplication.class, args);

        log.info("✅ Aplicação iniciada com sucesso!");
        log.info("🌐 Acesse: http://localhost:8080/api/health");
        log.info("📧 Para teste manual: http://localhost:8080/api/configurar");
    }
}

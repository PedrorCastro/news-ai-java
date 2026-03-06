package com.newsai.model;

import lombok.Data;
import java.util.List;

/**
 * UserPreferences - Preferencias do usuario vindas da pagina web
 * O Spring converte automaticamente o JSON do POST para este objeto.
 */
@Data
public class UserPreferences {
    private String email;
    private int hora;
    private int quantidade;
    private String idioma;
    private List<String> categorias;
}
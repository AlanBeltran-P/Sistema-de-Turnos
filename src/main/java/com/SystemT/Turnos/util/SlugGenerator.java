package com.SystemT.Turnos.util;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugGenerator {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]+");
    private static final Pattern EDGES_DASHES = Pattern.compile("^-+|-+$");

    private SlugGenerator() {
    }

    public static String generar(String texto) {
        String sinEspacios = WHITESPACE.matcher(texto.trim()).replaceAll("-");
        String normalizado = Normalizer.normalize(sinEspacios, Normalizer.Form.NFD);
        String sinAcentos = normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        String slug = NON_LATIN.matcher(sinAcentos).replaceAll("");
        slug = EDGES_DASHES.matcher(slug).replaceAll("");
        return slug.toLowerCase();
    }
}
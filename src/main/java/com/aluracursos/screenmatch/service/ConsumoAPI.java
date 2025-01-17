package com.aluracursos.screenmatch.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumoAPI {
    public String obtenerDatos(String url) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-api-key", "6e25a63d")  // Sin espacio antes de la clave
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            System.err.println("Error de entrada/salida: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            System.err.println("La solicitud fue interrumpida: " + e.getMessage());
            throw new RuntimeException(e);
        }

        String json = response.body();
        return json;
    }
}

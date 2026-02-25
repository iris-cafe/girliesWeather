package org.bot.girliesweather.servicio;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class OllamaService {
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Envia los datos del clima a ollama para generar una respuesta personalizada.
     * @param nombreUsuario Nombre del usuario para saludarlo
     * @param datosClima Informacion tecnica obtenida de AEMET
     * @return Un mensaje con estilo, consejos de moda y emojis
     */
    public String consultarIA(String nombreUsuario, String datosClima) {
        try {
            String url = "http://localhost:11434/api/generate";

            //Para indicarle a ollama como debe responder
            String personalidad = "RESPONDE SIEMPRE EN ESPAÑOL. Eres 'GirlieBot', la bff de " + nombreUsuario + ". " +
                    "Eres súper trendy y divertida. " +
                    "Instrucciones: Máximo 2 frases cortas. La primera con el clima y la segunda con un consejo de moda. " +
                    "Usa muchos emojis. ✨💖";

            Map<String, Object> request = new HashMap<>();
            //El modelo llama3.2 porque es medianamente mas rapido
            request.put("model", "llama3.2:1b");
            request.put("prompt", personalidad + "\nSaluda a " + nombreUsuario + " y usa estos datos: " + datosClima);
            request.put("stream", false);

            Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
            return (String) response.get("response");
        } catch (Exception e) {
            return "La IA está descansando, sister. ✨";
        }
    }
}
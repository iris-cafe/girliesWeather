package org.bot.girliesweather.servicio;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Este servicio se encarga de la comunicacion con la API oficial de AEMET.
 * Actua como el radar meteorologico de GirlieBot para obtener los datos
 * reales antes de pasarselos a ollama.
 */

@Service
public class ClimaService {

    /**Cliente para realizar peticiones HTTP a servicios externos */
    private final RestTemplate restTemplate = new RestTemplate();

    /** API Key de AEMET OpenData configurada en application.properties */
    @Value("${aemet.api.key}")
    private String aemetKey;

    /**
     * Obtener clima, como su nombre indica, obtiene los datos meteorologicos actuales filtrando por ciudad.
     * @param ciudad El nombre de la ciudad que el usuario pone en Telegram.
     * @return Devuelve un fragmento de datos tecnicos (JSON) de la ciudad
     * o un mensaje de error si no se encuentran datos.
     */
    public String obtenerClima(String ciudad) {
        try {
            // Se solicita a AEMET la url temporal donde estan los datos
            String url = "https://opendata.aemet.es/opendata/api/observacion/convencional/todas?api_key=" + aemetKey;
            Map<String, Object> respuestaAemet = restTemplate.getForObject(url, Map.class);

            if (respuestaAemet != null && respuestaAemet.containsKey("datos")) {
                // AEMET responde con un JSON que contiene la URL final de los datos
                String urlDatosReales = (String) respuestaAemet.get("datos");

                // Despues se descarga el JSON gigante con todas las estaciones
                String datosBrutos = restTemplate.getForObject(urlDatosReales, String.class);

                // Con este filtro se busca solo el trozo de la ciudad (ej: "Madrid"),
                // es decir, filtramos de forma sencilla buscando el nombre en el texto
                int index = datosBrutos.toLowerCase().indexOf(ciudad.toLowerCase());

                if (index != -1) {
                    // Despues se coge un trozo de 300 caracteres desde donde aparece el nombre,
                    // asi se obtiene la temperatura, humedad y viento sin exceder el limite de la IA
                    return datosBrutos.substring(index, Math.min(index + 300, datosBrutos.length()));
                } else {
                    return "No encuentro datos exactos para " + ciudad + ", pero dile algo bonito igualmente.";
                }
            }
            //En caso de error, envia algo mas friendly, en vez de un error que no entienda el usuario
            return "No pude conectar con el radar de moda (AEMET).";

        } catch (Exception e) {
            // Log de error para el desarrollador en consola
            System.out.println("Error en AEMET: " + e.getMessage());
            return "Error técnico, sister: " + e.getMessage();
        }
    }
}
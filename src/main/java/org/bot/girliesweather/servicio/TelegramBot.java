package org.bot.girliesweather.servicio;

import org.bot.girliesweather.modelo.Mensaje;
import org.bot.girliesweather.modelo.Usuario;
import org.bot.girliesweather.repo.MensajeRepo;
import org.bot.girliesweather.repo.UsuarioRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase principal que gestiona la comunicacion con Telegram.
 * Escucha los mensajes, coordina los servicios y envia las respuestas.
 */

@Service
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired private UsuarioRepo usuarioRepo;
    @Autowired private MensajeRepo mensajeRepo;
    @Autowired private ClimaService climaService;
    @Autowired private OllamaService ollamaService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${bot.token}") private String botToken;
    @Value("${bot.name}") private String botName;


    @Override
    public String getBotUsername() { return botName; }

    @Override
    public String getBotToken() { return botToken; }

    /**
     * Metodo que se ejecuta cada vez que alguien escribe al bot.
     * Realiza el flujo completo de: Guardar usuario - Consultar Clima - Consultar IA - Responder
     */

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String textoUsuario = update.getMessage().getText();
            String nombre = update.getMessage().getFrom().getFirstName();

            // Aqui se registra o actualiza al usuario en la bbdd
            Usuario usuario = usuarioRepo.findById(chatId).orElse(new Usuario());
            usuario.setChatId(chatId);
            usuario.setNombre(nombre);
            usuario.setUltimoComando(textoUsuario);
            usuarioRepo.save(usuario);

            String respuestaFinal;

            if (textoUsuario.toLowerCase().startsWith("tiempo ")) {
                // Comando de clima: se extrae la ciudad y se consulta API + IA
                String ciudad = textoUsuario.substring(7);
                String datosClima = climaService.obtenerClima(ciudad);
                respuestaFinal = ollamaService.consultarIA(nombre, datosClima);

            } else if (textoUsuario.equalsIgnoreCase("/historial")) {
                // Comando historial: para recuperar los registros de PostgreSQL
                List<Mensaje> historial = mensajeRepo.findTop5ByUsuarioOrderByFechaDesc(usuario);
                respuestaFinal = construirMensajeHistorial(nombre, historial);

            } else if (textoUsuario.equalsIgnoreCase("/limpiar")) {
                // Comando limpiar: para borrar el historial del usuario
                mensajeRepo.deleteByUsuario(usuario);
                respuestaFinal = "¡Historial borrado, " + nombre + "! ✨ Tu cuenta está limpia bby.";

            } else {
                // Mensaje por defecto
                respuestaFinal = "¡Holis " + nombre + "! ✨ Mis comandos son:\n" +
                        "1) 'Tiempo [Ciudad]'\n" +
                        "2) /historial\n" +
                        "3) /limpiar";
            }

            guardarEnHistorial(textoUsuario, respuestaFinal, usuario);
            enviarTexto(chatId, respuestaFinal);
        }
    }

    private void guardarEnHistorial(String original, String respuesta, Usuario user) {
        Mensaje msg = new Mensaje();
        msg.setContenidoOriginal(original);
        msg.setRespuestaIA(respuesta);
        msg.setFecha(LocalDateTime.now());
        msg.setUsuario(user);
        mensajeRepo.save(msg);
    }

    private String construirMensajeHistorial(String nombre, List<Mensaje> historial) {
        if (historial.size() < 5) return "Hay poquitas consultas guardados aún, " + nombre + ". Preguntame cuando haya más girliepop ✨";
        StringBuilder sb = new StringBuilder("Tus últimas consultas, bby: 💅\n\n");
        for (Mensaje m : historial) {
            sb.append("📅 ").append(m.getFecha().getDayOfMonth()).append("/")
                    .append(m.getFecha().getMonthValue()).append(" - ")
                    .append(m.getContenidoOriginal()).append("\n");
        }
        return sb.toString();
    }

    //Envia un mensaje de texto de vuelta al usuario en Telegram.
    private void enviarTexto(long chatId, String texto) {
        SendMessage sm = SendMessage.builder().chatId(String.valueOf(chatId)).text(texto).build();
        try { execute(sm); } catch (Exception e) { e.printStackTrace(); }
    }
}
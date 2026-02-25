package org.bot.girliesweather.config;

import org.bot.girliesweather.servicio.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Esta configuracion es necesaria para registrar el bot en la plataforma de Telegram.
 * Define el Bean que gestiona la sesion del bot al iniciar la aplicacion Spring Boot.
 */

@Configuration
public class BotConfig {
    /**
     * Registra y arranca el bot de Telegram usando una sesion por defecto.
     * @param bot La instancia del servicio TelegramBot.
     * @return El API de TelegramBots listo para recibir mensajes.
     * @throws Exception por si hay algun error en el registro.
     */
    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot bot) throws Exception {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);

        return api;
    }
}
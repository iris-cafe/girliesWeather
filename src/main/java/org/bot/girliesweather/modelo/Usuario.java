package org.bot.girliesweather.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Esta entidad representa a una girlie (usuario) del bot.
 * Se guarda su ID de Telegram para saber con quien hablamos y sus preferencias.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
public class Usuario implements Serializable {

    @Id
    private Long chatId; // Id unico de la conversacion de Telegram
    private String nombre; // El nombre que el usuario tenga en el perfil
    private String ultimoComando; // Guarda lo último que el usuario pide
}
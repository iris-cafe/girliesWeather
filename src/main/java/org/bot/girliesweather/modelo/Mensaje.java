package org.bot.girliesweather.modelo;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entidad que representa el historial de mensajes en la base de datos.
 * Guarda tanto lo que el usuario pidio como la respuesta de ollama.
 */

@Entity
@Table(name = "mensajes")
@Data
public class Mensaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String contenidoOriginal; // Lo que el usuario escribio (ej: "Tiempo Madrid")

    @Column(columnDefinition = "TEXT")
    private String respuestaIA; // Lo que Ollama respondio

    private LocalDateTime fecha; // Momento exacto de la interaccion

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario; // La girlie propietaria de este mensaje
}

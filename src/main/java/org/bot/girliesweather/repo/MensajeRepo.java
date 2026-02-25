package org.bot.girliesweather.repo;

import org.bot.girliesweather.modelo.Mensaje;
import org.bot.girliesweather.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Repositorio para la entidad Mensaje.
 * Permite realizar consultas sobre el historial de conversaciones guardado.
 */
public interface MensajeRepo extends JpaRepository<Mensaje, Long> {
    /**
     * Recupera los ultimos 5 mensajes del usuario, ordenados por fecha descendente.
     * @param usuario El usuario del que queremos el historial.
     * @return Lista de los 5 mensajes mas recientes.
     */
    List<Mensaje> findTop5ByUsuarioOrderByFechaDesc(Usuario usuario);

    /**
     * Elimina todos los mensajes asociados a un usuario.
     * El @Transactional es necesario porque se estan modificando datos (vamos, un borrado).
     * @param usuario para saber el usuario que quiere limpiar su historial.
     */
    @Transactional
    void deleteByUsuario(Usuario usuario);
}
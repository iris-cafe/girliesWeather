package org.bot.girliesweather.repo;

import org.bot.girliesweather.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**
 * Repositorio para la entidad Usuario.
 * Gestiona el guardado y busqueda de los perfiles de Telegram en la base de datos.
 */
@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Long> {
    // Aqui se usan los metodos por defecto de JpaRepository
}
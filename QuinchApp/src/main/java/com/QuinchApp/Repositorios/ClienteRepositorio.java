package com.QuinchApp.Repositorios;

import com.QuinchApp.Entidades.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {

    @Query("SELECT c FROM Cliente c WHERE c.nombre = :nombre")
    public Cliente buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT c FROM Cliente c WHERE c.email = :email")
    public Cliente buscarPorEmail(@Param("email") String email);
    
    @Query("SELECT u FROM Usuario u WHERE u.nombreUsuario = :nombreUsuario")
    public Optional<Cliente> buscarPorNombreUsuario(@Param("nombreUsuario") String nombreUsuario);
    
    @Query("SELECT pr FROM Propiedad pr WHERE pr.id = :id")
    public Optional<Cliente> buscarPorId(@Param("id") int id);

}

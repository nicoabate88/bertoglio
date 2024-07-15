package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Articulo;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticuloRepositorio extends JpaRepository<Articulo, Long> {

    @Query("SELECT MAX(id) FROM Articulo")
    public Long ultimoArticulo();
    
    Optional<Articulo> findByNombre(String nombre);
    
    Optional<Articulo> findByCodigo(String codigo);

}

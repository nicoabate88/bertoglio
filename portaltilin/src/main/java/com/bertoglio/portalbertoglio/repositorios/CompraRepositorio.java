
package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Compra;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraRepositorio extends JpaRepository<Compra, Long>{
    
    @Query("SELECT MAX(id) FROM Compra")
    public Long ultimoCompra();
    
    @Query("SELECT c FROM Compra c WHERE proveedor_id = :id")
    public ArrayList<Compra> buscarCompraIdProveedor(@Param("id") Long id);
    
    @Query("SELECT c FROM Compra c WHERE c.estado != 'ELIMINADO'")
    public ArrayList<Compra> buscarCompras();
    


    
}

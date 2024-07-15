
package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Pago;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepositorio extends JpaRepository<Pago, Long>{
    
    @Query("SELECT p FROM Pago p WHERE proveedor_id = :id")
    public ArrayList<Pago> buscarPagoIdProveedor(@Param("id") Long id);
    
    @Query("SELECT MAX(id) FROM Pago")
    public Long ultimoPago();
    
     @Query(value = "SELECT * FROM pago p "
            + "INNER JOIN pago_valor pv ON p.id = pv.pago_id "
            + "INNER JOIN valor v ON pv.valor_id = v.id "
            + "WHERE v.id = :id", nativeQuery = true)
    public Pago buscarPagoValor(@Param("id") Long id);
    
    @Query("SELECT p FROM Pago p WHERE p.estado != 'ELIMINADO'")
    public ArrayList<Pago> buscarPagos();
    
}

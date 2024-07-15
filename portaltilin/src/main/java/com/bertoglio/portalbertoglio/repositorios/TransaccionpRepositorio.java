
package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Transaccionp;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionpRepositorio extends JpaRepository<Transaccionp, Long> {
    
    @Query("SELECT MAX(id) FROM Transaccionp")
    public Long ultimaTransaccion();
    
    @Query("SELECT t FROM Transaccionp t WHERE pago_id = :id")
    public Transaccionp buscarTransaccionIdPago(@Param("id") Long id);
    
    @Query("SELECT t FROM Transaccionp t WHERE compra_id = :id")
    public Transaccionp buscarTransaccionIdCompra(@Param("id") Long id);
    
    @Query(value = "SELECT * FROM transaccionp t "
            + "INNER JOIN cuentap_transaccion ct ON t.id = ct.transaccion_id "
            + "INNER JOIN cuentap c ON ct.cuentap_id = c.id "
            + "WHERE c.id = :id AND t.concepto != 'ELIMINADO'", nativeQuery = true)
    public ArrayList<Transaccionp> buscarTransaccionCuenta(@Param("id") Long id);
    
    @Query("SELECT t FROM Transaccionp t WHERE t.concepto != 'ELIMINADO'")
    public ArrayList<Transaccionp> buscarTransacciones();
    
}

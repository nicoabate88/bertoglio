package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Transaccion;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepositorio extends JpaRepository<Transaccion, Long> {

    @Query("SELECT MAX(id) FROM Transaccion")
    public Long ultimaTransaccion();

    @Query("SELECT t FROM Transaccion t WHERE recibo_id = :id")
    public Transaccion buscarTransaccionIdRecibo(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE servicio_id = :id")
    public Transaccion buscarTransaccionIdServicio(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE cliente_id = :id")
    public ArrayList<Transaccion> buscarTransaccionIdCliente(@Param("id") Long id);

    @Query("SELECT t FROM Transaccion t WHERE cuenta_id = :id")
    public ArrayList<Transaccion> buscarTransaccionIdCuenta(@Param("id") Long id);

    @Query(value = "SELECT * FROM transaccion t "
            + "INNER JOIN cuenta_transaccion ct ON t.id = ct.transaccion_id "
            + "INNER JOIN cuenta c ON ct.cuenta_id = c.id "
            + "WHERE c.id = :id AND t.concepto != 'ELIMINADO'", nativeQuery = true)
    public ArrayList<Transaccion> buscarTransaccionCuenta(@Param("id") Long id);
    
    @Query("SELECT t FROM Transaccion t WHERE t.concepto != 'ELIMINADO'")
    public ArrayList<Transaccion> buscarTransacciones();

}

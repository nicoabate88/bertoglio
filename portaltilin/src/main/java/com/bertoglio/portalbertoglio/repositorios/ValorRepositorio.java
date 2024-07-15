package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Valor;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ValorRepositorio extends JpaRepository<Valor, Long> {

    @Query("SELECT MAX(id) FROM Valor")
    public Long ultimoValor();
    
    @Query("SELECT v FROM Valor v WHERE v.estado = 'CARTERA'")
    public ArrayList<Valor> buscarValorCartera();
    
    @Query(value = "SELECT * FROM valor v "
            + "INNER JOIN recibo_valor rv ON v.id = rv.valor_id "
            + "INNER JOIN recibo r ON rv.recibo_id = r.id "
            + "WHERE r.id = :id", nativeQuery = true)
    public ArrayList<Valor> buscarValorRecibo(@Param("id") Long id);
    
        @Query(value = "SELECT * FROM valor v "
            + "INNER JOIN pago_valor pv ON v.id = pv.valor_id "
            + "INNER JOIN pago p ON pv.pago_id = p.id "
            + "WHERE p.id = :id", nativeQuery = true)
    public ArrayList<Valor> buscarValorPago(@Param("id") Long id);
    
        @Query(value = "SELECT * FROM valor v "
            + "INNER JOIN caja_valor cv ON v.id = cv.valor_id "
            + "INNER JOIN caja c ON cv.caja_id = c.id "
            + "WHERE c.id = :id AND v.estado != 'ELIMINADO'", nativeQuery = true)
    public ArrayList<Valor> buscarValorCaja(@Param("id") Long id);
    
        @Query(value = "SELECT * FROM valor v "
            + "INNER JOIN caja_valor cv ON v.id = cv.valor_id "
            + "INNER JOIN caja c ON cv.caja_id = c.id "
            + "WHERE c.id = :id AND v.estado = 'CARTERA'", nativeQuery = true)
    public ArrayList<Valor> buscarValorCarteraCaja(@Param("id") Long id);

}

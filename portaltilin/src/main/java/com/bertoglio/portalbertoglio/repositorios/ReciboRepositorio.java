package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Recibo;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReciboRepositorio extends JpaRepository<Recibo, Long> {

    @Query("SELECT MAX(id) FROM Recibo")
    public Long ultimoRecibo();
    
        @Query(value = "SELECT * FROM recibo r "
            + "INNER JOIN recibo_valor rv ON r.id = rv.recibo_id "
            + "INNER JOIN valor v ON rv.valor_id = v.id "
            + "WHERE v.id = :id", nativeQuery = true)
    public Recibo buscarReciboValor(@Param("id") Long id);
    
    @Query("SELECT r FROM Recibo r WHERE r.estado != 'ELIMINADO'")
    public ArrayList<Recibo> buscarRecibos();
    
    @Query("SELECT r FROM Recibo r WHERE cliente_id = :id")
    public ArrayList<Recibo> buscarReciboIdCliente(@Param("id") Long id);


}

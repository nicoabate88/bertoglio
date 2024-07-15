package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Detalle;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRepositorio extends JpaRepository<Detalle, Long> {

    @Query("SELECT MAX(id) FROM Detalle")
    public Long ultimoDetalle();

    @Query("SELECT d FROM Detalle d WHERE articulo_id = :id")
    public ArrayList<Detalle> buscarDetalleIdArticulo(@Param("id") Long id);
    
    @Query(value = "SELECT * FROM detalle d "
            + "INNER JOIN servicio_detalle sd ON d.id = sd.detalle_id "
            + "INNER JOIN servicio s ON sd.servicio_id = s.id "
            + "WHERE s.id = :id", nativeQuery = true)
    public ArrayList<Detalle> buscarDetalleServicio(@Param("id") Long id);
    
    @Query(value = "SELECT * FROM detalle d "
            + "INNER JOIN presupuesto_detalle pd ON d.id = pd.detalle_id "
            + "INNER JOIN presupuesto p ON pd.presupuesto_id = p.id "
            + "WHERE p.id = :id", nativeQuery = true)
    public ArrayList<Detalle> buscarDetallePresupuesto(@Param("id") Long id);
    
    @Query(value = "SELECT * FROM detalle d "
            + "INNER JOIN compra_detalle cd ON d.id = cd.detalle_id "
            + "INNER JOIN compra c ON cd.compra_id = c.id "
            + "WHERE c.id = :id", nativeQuery = true)
    public ArrayList<Detalle> buscarDetalleCompra(@Param("id") Long id);

}
  
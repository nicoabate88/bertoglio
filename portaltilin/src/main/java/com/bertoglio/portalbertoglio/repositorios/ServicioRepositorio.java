package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Servicio;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServicioRepositorio extends JpaRepository<Servicio, Long> {

    @Query("SELECT s FROM Servicio s WHERE cliente_id = :id AND s.estado != 'ELIMINADO'")
    public ArrayList<Servicio> buscarServicioIdCliente(@Param("id") Long id);

    @Query("SELECT s FROM Servicio s WHERE vehiculo_id =:id AND s.estado != 'ELIMINADO' ")
    public ArrayList<Servicio> buscarServicioIdVehiculo(@Param("id") Long id);

    @Query("SELECT MAX(id) FROM Servicio")
    public Long ultimoServicio();
    
    @Query("SELECT s FROM Servicio s WHERE s.estado != 'ELIMINADO'")
    public ArrayList<Servicio> buscarServicios();   

}

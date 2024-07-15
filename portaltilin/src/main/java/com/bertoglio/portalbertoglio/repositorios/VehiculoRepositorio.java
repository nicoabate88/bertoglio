package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Vehiculo;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VehiculoRepositorio extends JpaRepository<Vehiculo, Long> {

    @Query("SELECT v FROM Vehiculo v WHERE cliente_id = :id")
    public ArrayList<Vehiculo> buscarVehiculoIdCliente(@Param("id") Long id);

    @Query("SELECT MAX(id) FROM Vehiculo")
    public Long ultimoVehiculo();

}

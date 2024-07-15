package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Presupuesto;
import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PresupuestoRepositorio extends JpaRepository<Presupuesto, Long> {

    @Query("SELECT MAX(id) FROM Presupuesto")
    public Long ultimoServicio();

    @Query("SELECT p FROM Presupuesto p WHERE cliente_id = :id")
    public ArrayList<Presupuesto> buscarPresupuestoIdCliente(@Param("id") Long id);
    
     @Query("SELECT p FROM Presupuesto p WHERE p.estado = 'PRESUPUESTO'")
    public ArrayList<Presupuesto> buscarPresupuestos();

}

package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepositorio extends JpaRepository<Cuenta, Long> {

    @Query("SELECT id FROM Cuenta c WHERE cliente_id = :id")
    public Long buscarIdCuentaIdCliente(@Param("id") Long id);

    @Query("SELECT c FROM Cuenta c WHERE cliente_id = :id")
    public Cuenta buscarCuentaIdCliente(@Param("id") Long id);

}

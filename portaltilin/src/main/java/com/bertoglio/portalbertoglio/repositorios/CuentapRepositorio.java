
package com.bertoglio.portalbertoglio.repositorios;

import com.bertoglio.portalbertoglio.entidades.Cuentap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentapRepositorio extends JpaRepository<Cuentap, Long> {
    
    @Query("SELECT id FROM Cuentap c WHERE proveedor_id = :id")
    public Long buscarIdCuentaIdProveedor(@Param("id") Long id);
    
    @Query("SELECT c FROM Cuentap c WHERE proveedor_id = :id")
    public Cuentap buscarCuentaIdProveedor(@Param("id") Long id);
    
}

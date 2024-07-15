package com.bertoglio.portalbertoglio.entidades;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Cuentap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Proveedor proveedor;
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    private Double saldo;
    @OneToMany
    private List<Transaccionp> transaccion;

    public Cuentap() {
    }

    public Cuentap(Long id, Proveedor proveedor, Date fechaAlta, Double saldo, List<Transaccionp> transaccion) {
        this.id = id;
        this.proveedor = proveedor;
        this.fechaAlta = fechaAlta;
        this.saldo = saldo;
        this.transaccion = transaccion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public List<Transaccionp> getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(List<Transaccionp> transaccion) {
        this.transaccion = transaccion;
    }

}

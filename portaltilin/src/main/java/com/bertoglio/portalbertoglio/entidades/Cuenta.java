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
public class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Cliente cliente;
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;
    private Double saldo;
    @OneToMany
    private List<Transaccion> transaccion;

    public Cuenta() {
    }

    public Cuenta(Long id, Cliente cliente, Date fechaAlta, Double saldo, List<Transaccion> transaccion) {
        this.id = id;
        this.cliente = cliente;
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

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
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

    public List<Transaccion> getTransaccion() {
        return transaccion;
    }

    public void setTransaccion(List<Transaccion> transaccion) {
        this.transaccion = transaccion;
    }

    @Override
    public String toString() {
        return "Cuenta{" + "id=" + id + ", cliente=" + cliente + ", fechaAlta=" + fechaAlta + ", saldo=" + saldo + ", transaccion=" + transaccion + '}';
    }

}

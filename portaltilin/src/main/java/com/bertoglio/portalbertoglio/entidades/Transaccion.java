package com.bertoglio.portalbertoglio.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @OneToOne
    private Cliente cliente;
    private String observacion;
    private Double importe;
    private Double saldoAcumulado;
    private String concepto;
    @OneToOne
    private Servicio servicio;
    @OneToOne
    private Recibo recibo;

    public Transaccion() {
    }

    public Transaccion(Long id, Date fecha, Cliente cliente, String observacion, Double importe, Double saldoAcumulado, String concepto, Servicio servicio, Recibo recibo) {
        this.id = id;
        this.fecha = fecha;
        this.cliente = cliente;
        this.observacion = observacion;
        this.importe = importe;
        this.saldoAcumulado = saldoAcumulado;
        this.concepto = concepto;
        this.servicio = servicio;
        this.recibo = recibo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Double getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public void setSaldoAcumulado(Double saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public void setServicio(Servicio servicio) {
        this.servicio = servicio;
    }

    public Recibo getRecibo() {
        return recibo;
    }

    public void setRecibo(Recibo recibo) {
        this.recibo = recibo;
    }

    @Override
    public String toString() {
        return "Transaccion{" + "id=" + id + ", fecha=" + fecha + ", cliente=" + cliente + ", observacion=" + observacion + ", importe=" + importe + ", saldoAcumulado=" + saldoAcumulado + ", concepto=" + concepto + ", servicio=" + servicio + ", recibo=" + recibo + '}';
    }

}

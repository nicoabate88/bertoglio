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
public class Transaccionp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    @OneToOne
    private Proveedor proveedor;
    private String observacion;
    private Double importe;
    private Double saldoAcumulado;
    private String concepto;
    @OneToOne
    private Pago pago;
    @OneToOne
    private Compra compra;

    public Transaccionp() {
    }

    public Transaccionp(Long id, Date fecha, Proveedor proveedor, String observacion, Double importe, Double saldoAcumulado, String concepto, Pago pago, Compra compra) {
        this.id = id;
        this.fecha = fecha;
        this.proveedor = proveedor;
        this.observacion = observacion;
        this.importe = importe;
        this.saldoAcumulado = saldoAcumulado;
        this.concepto = concepto;
        this.pago = pago;
        this.compra = compra;
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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
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

    public Pago getPago() {
        return pago;
    }

    public void setPago(Pago pago) {
        this.pago = pago;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

}

package com.bertoglio.portalbertoglio.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Valor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long idSocio;
    private String nombre;
    private String tipoValor;
    private Double importe;
    private Integer numero;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String observacion;
    private Double saldoAcumulado;
    private String estado;

    public Valor() {
    }

    public Valor(Long id, Long idSocio, String nombre, String tipoValor, Double importe, Integer numero, Date fecha, String observacion, Double saldoAcumulado, String estado) {
        this.id = id;
        this.idSocio = idSocio;
        this.nombre = nombre;
        this.tipoValor = tipoValor;
        this.importe = importe;
        this.numero = numero;
        this.fecha = fecha;
        this.observacion = observacion;
        this.saldoAcumulado = saldoAcumulado;
        this.estado = estado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdSocio() {
        return idSocio;
    }

    public void setIdSocio(Long idSocio) {
        this.idSocio = idSocio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(String tipoValor) {
        this.tipoValor = tipoValor;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public Double getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public void setSaldoAcumulado(Double saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}

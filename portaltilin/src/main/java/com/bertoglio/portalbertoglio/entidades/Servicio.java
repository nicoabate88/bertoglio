package com.bertoglio.portalbertoglio.entidades;

import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Cliente cliente;
    @ManyToOne
    private Vehiculo vehiculo;
    @Temporal(TemporalType.DATE)
    private Date fecha;
    private String observacion;
    private Double total;
    private String estado;
    @OneToOne
    private Usuario usuario;
    @OneToMany
    private List<Detalle> detalle;

    public Servicio() {
    }

    public Servicio(Long id, Cliente cliente, Vehiculo vehiculo, Date fecha, String observacion, Double total, String estado, Usuario usuario, List<Detalle> detalle) {
        this.id = id;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.fecha = fecha;
        this.observacion = observacion;
        this.total = total;
        this.estado = estado;
        this.usuario = usuario;
        this.detalle = detalle;
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

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public void setVehiculo(Vehiculo vehiculo) {
        this.vehiculo = vehiculo;
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

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Detalle> getDetalle() {
        return detalle;
    }

    public void setDetalle(List<Detalle> detalle) {
        this.detalle = detalle;
    }

    
    
}

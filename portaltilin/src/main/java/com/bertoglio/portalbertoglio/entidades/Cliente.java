package com.bertoglio.portalbertoglio.entidades;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Long cuit;
    private String localidad;
    private String direccion;
    private Long telefono;
    private String email;
    @Temporal(TemporalType.DATE)
    private Date fechaAlta;

    public Cliente() {
    }

    public Cliente(Long id, String nombre, Long cuit, String localidad, String direccion, Long telefono, String email, Date fechaAlta) {
        this.id = id;
        this.nombre = nombre;
        this.cuit = cuit;
        this.localidad = localidad;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.fechaAlta = fechaAlta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getCuit() {
        return cuit;
    }

    public void setCuit(Long cuit) {
        this.cuit = cuit;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Long getTelefono() {
        return telefono;
    }

    public void setTelefono(Long telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    @Override
    public String toString() {
        return "Cliente{" + "id=" + id + ", nombre=" + nombre + ", cuit=" + cuit + ", localidad=" + localidad + ", direccion=" + direccion + ", telefono=" + telefono + ", email=" + email + ", fechaAlta=" + fechaAlta + '}';
    }

}

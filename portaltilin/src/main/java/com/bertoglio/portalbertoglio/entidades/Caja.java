package com.bertoglio.portalbertoglio.entidades;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Double saldo;
    private Double saldoAcumulado;
    @OneToMany
    private List<Valor> valor;

    public Caja() {
    }

    public Caja(Long id, String nombre, Double saldo, Double saldoAcumulado, List<Valor> valor) {
        this.id = id;
        this.nombre = nombre;
        this.saldo = saldo;
        this.saldoAcumulado = saldoAcumulado;
        this.valor = valor;
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

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public Double getSaldoAcumulado() {
        return saldoAcumulado;
    }

    public void setSaldoAcumulado(Double saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }

    public List<Valor> getValor() {
        return valor;
    }

    public void setValor(List<Valor> valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Caja{" + "id=" + id + ", nombre=" + nombre + ", saldo=" + saldo + ", saldoAcumulado=" + saldoAcumulado + ", valor=" + valor + '}';
    }

}

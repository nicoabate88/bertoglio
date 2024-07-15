package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Caja;
import com.bertoglio.portalbertoglio.entidades.Valor;
import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.repositorios.CajaRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ValorRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CajaServicio {

    @Autowired
    private CajaRepositorio cajaRepositorio;
    @Autowired
    private ValorRepositorio valorRepositorio;

    @Transactional
    public void crearCaja(String nombre) throws MiException {

        validarDatos(nombre);

        Caja caja = new Caja();
        String nombreMayusculas = nombre.toUpperCase();

        caja.setNombre(nombreMayusculas);
        caja.setSaldo(0.0);
        caja.setSaldoAcumulado(0.0);

        cajaRepositorio.save(caja);

    }

    public ArrayList<Caja> buscarCajas() {

        ArrayList<Caja> listaCajas = new ArrayList();

        listaCajas = (ArrayList<Caja>) cajaRepositorio.findAll();

        return listaCajas;
    }

    public Caja buscarCaja(Long id) {

        return cajaRepositorio.getById(id);

    }

    @Transactional
    public void agregarValorCaja(Long idValor) {

        Double saldo = 0.0;
        Long idCaja = idValor;  //asigno idValor porque es necesario declarar la variable afuera del if y tiene que estar inicializada

        Valor valor = new Valor();
        Optional<Valor> val = valorRepositorio.findById(idValor);
        if (val.isPresent()) {
            valor = val.get();
        }

        ArrayList<Caja> listaCajas = (ArrayList<Caja>) cajaRepositorio.findAll();
        for (Caja lista : listaCajas) {
            if (lista.getNombre().equalsIgnoreCase(valor.getTipoValor())) {
                idCaja = lista.getId();
            }
        }

        Caja caja = cajaRepositorio.getById(idCaja);

        List<Valor> valores = caja.getValor();
        valores.add(valor);
        caja.setValor(valores);

        for (Valor v : valores) {
            saldo = saldo + v.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;
        caja.setSaldo(saldoRed);

        cajaRepositorio.save(caja);

    }

    @Transactional
    public void quitarValorCaja(String cja, Long id) {

        Double saldo = 0.0;
        Long idCaja = id;
        int numeroInt = id.intValue();

        ArrayList<Caja> listaCajas = (ArrayList<Caja>) cajaRepositorio.findAll();
        for (Caja lista : listaCajas) {
            if (lista.getNombre().equalsIgnoreCase(cja)) {
                idCaja = lista.getId();
            }
        }

        Caja caja = cajaRepositorio.getById(idCaja);

        ArrayList<Valor> lista = valorRepositorio.buscarValorCaja(idCaja);
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() == numeroInt) {
                lista.remove(i);
            }
        }

        for (Valor v : lista) {
            saldo = saldo + v.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;
        caja.setSaldo(saldoRed);
        caja.setValor(lista);

        cajaRepositorio.save(caja);

    }

    public void validarDatos(String nombre) throws MiException {

        ArrayList<Caja> listaCajas = new ArrayList();

        listaCajas = buscarCajas();

        for (Caja lista : listaCajas) {
            if (lista.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El NOMBRE de la Caja ya está registrado");
            }
        }
    }

}

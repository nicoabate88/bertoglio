package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Caja;
import com.bertoglio.portalbertoglio.entidades.Valor;
import com.bertoglio.portalbertoglio.repositorios.ValorRepositorio;
import com.bertoglio.portalbertoglio.util.ValorComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValorServicio {

    @Autowired
    private ValorRepositorio valorRepositorio;
    @Autowired
    private CajaServicio cajaServicio;

    @Transactional
    public void crearValor(String tipoValor, String estado, Double importe, Integer numero, String fecha, String nombre, Long idSocio) throws ParseException {

        Valor valor = new Valor();
        Date fechaOrden = convertirFecha(fecha);

        valor.setTipoValor(tipoValor);
        valor.setEstado(estado);
        valor.setImporte(importe);
        valor.setNumero(numero);
        valor.setFecha(fechaOrden);
        valor.setObservacion(fecha);
        valor.setNombre(nombre);
        valor.setIdSocio(idSocio);

        valorRepositorio.save(valor);

        cajaServicio.agregarValorCaja(buscarUltimo());

    }

    public Long buscarUltimo() {

        return valorRepositorio.ultimoValor();
    }

    public Valor buscarValor(Long id) {

        return valorRepositorio.getById(id);

    }

    public ArrayList<Valor> buscarValores() {

        ArrayList<Valor> listaValores = (ArrayList<Valor>) valorRepositorio.findAll();

        return listaValores;
    }

    @Transactional
    public void modificarValor(Long id) { //llega idValor del cheque perteneciente al Recibo que cambía de CARTERA a GIRADO

        Valor valor = new Valor();
        Optional<Valor> val = valorRepositorio.findById(id);
        if (val.isPresent()) {
            valor = val.get();
        }

        valor.setEstado("GIRADO");

        valorRepositorio.save(valor);

    }

    @Transactional
    public void modificarEstadoValor(Long id) {

        Valor valor = new Valor();
        Optional<Valor> val = valorRepositorio.findById(id);
        if (val.isPresent()) {
            valor = val.get();
        }

        valor.setEstado("CARTERA");

        valorRepositorio.save(valor);

    }

    public void modificarValorPorEliminarPago(Long idPago) {

        ArrayList<Valor> listaValores = new ArrayList();

        listaValores = valorRepositorio.buscarValorPago(idPago);
        Valor valor = new Valor();

        for (Valor v : listaValores) {
            if (!v.getTipoValor().equalsIgnoreCase("CHEQUE")) {
                eliminarValor(v.getId());
            } else {
                modificarEstadoValor(v.getIdSocio());
                eliminarValor(v.getId());
            }
        }
    }

    public void modificarValorPorEliminarRecibo(Long idRecibo) {

        ArrayList<Valor> listaValores = new ArrayList();

        listaValores = valorRepositorio.buscarValorRecibo(idRecibo);

        for (Valor v : listaValores) {

            eliminarValor(v.getId());
        }
    }

    @Transactional
    public void eliminarValor(Long id) {

        Valor valor = new Valor();
        Optional<Valor> val = valorRepositorio.findById(id);
        if (val.isPresent()) {
            valor = val.get();
        }
        String caja = valor.getTipoValor();
        valor.setEstado("ELIMINADO");
        valor.setImporte(0.0);

        valorRepositorio.save(valor);
        cajaServicio.quitarValorCaja(caja, valor.getId());

    }

    public ArrayList<Valor> buscarValorCartera() {

        ArrayList<Valor> listaValores = valorRepositorio.buscarValorCartera();

        return listaValores;
    }

    public ArrayList<Valor> buscarValorIdCaja(Long idCaja) {

        Caja caja = cajaServicio.buscarCaja(idCaja);

        ArrayList<Valor> listaValores = valorRepositorio.buscarValorCaja(idCaja);

        Double saldoAcumulado = 0.0;

        for (Valor v : listaValores) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + v.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            v.setSaldoAcumulado(saldoAcumulado);
        }

        Collections.sort(listaValores, ValorComparador.ordenarIdDesc); //ordena con fecha descendente para presentar en la vista desde mas reciente a mas antiguo

        return listaValores;
    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}

package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Cuenta;
import com.bertoglio.portalbertoglio.entidades.Recibo;
import com.bertoglio.portalbertoglio.entidades.Servicio;
import com.bertoglio.portalbertoglio.entidades.Transaccion;
import com.bertoglio.portalbertoglio.repositorios.CuentaRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ReciboRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ServicioRepositorio;
import com.bertoglio.portalbertoglio.repositorios.TransaccionRepositorio;
import com.bertoglio.portalbertoglio.util.TransaccionComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransaccionServicio {

    @Autowired
    private TransaccionRepositorio transaccionRepositorio;
    @Autowired
    private ServicioRepositorio servicioRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private CuentaRepositorio cuentaRepositorio;
    @Autowired
    private ReciboRepositorio reciboRepositorio;

    @Transactional
    public void crearTransaccionServicio(Long idServicio) {

        Servicio servicio = new Servicio();
        Optional<Servicio> servi = servicioRepositorio.findById(idServicio);
        if (servi.isPresent()) {
            servicio = servi.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setCliente(servicio.getCliente());
        transaccion.setFecha(servicio.getFecha());
        transaccion.setConcepto("SERVICIO");
        transaccion.setObservacion("ORDEN DE SERVICIO N°" + servicio.getId());
        transaccion.setImporte(servicio.getTotal());
        transaccion.setServicio(servicio);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuenta(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionServicio(Long idServicio) {

        Servicio servicio = new Servicio();
        Optional<Servicio> servi = servicioRepositorio.findById(idServicio);
        if (servi.isPresent()) {
            servicio = servi.get();
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdServicio(idServicio);

        if (!servicio.getCliente().getNombre().equalsIgnoreCase(transaccion.getCliente().getNombre())) {//si lo que se modifico del servicio es cliente, entra en este if

            cuentaServicio.eliminarTransaccionCuenta(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionServicio(idServicio); //agrega transaccion en cuenta cliente modificado

        } else {     //Si lo que se modificó de servicio es fecha u observacion ingresa acá

            transaccion.setFecha(servicio.getFecha());
            transaccion.setImporte(servicio.getTotal());
            transaccionRepositorio.save(transaccion);

            cuentaServicio.modificarTransaccionCuenta(transaccion);

        }

    }

    @Transactional
    public void eliminarTransaccionServicio(Long idServicio) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdServicio(idServicio);

        cuentaServicio.eliminarTransaccionCuenta(transaccion);

    }

    @Transactional
    public void crearTransaccionRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Transaccion transaccion = new Transaccion();

        transaccion.setCliente(recibo.getCliente());
        transaccion.setFecha(recibo.getFecha());
        transaccion.setConcepto("RECIBO");
        transaccion.setObservacion("RECIBO N°" + recibo.getId());
        Double importeNegativo = recibo.getImporte() * -1;
        transaccion.setImporte(importeNegativo);
        transaccion.setRecibo(recibo);

        transaccionRepositorio.save(transaccion);

        cuentaServicio.agregarTransaccionCuenta(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionRecibo(Long idRecibo) {

        Recibo recibo = new Recibo();
        Optional<Recibo> rec = reciboRepositorio.findById(idRecibo);
        if (rec.isPresent()) {
            recibo = rec.get();
        }

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdRecibo(idRecibo);

        if (!recibo.getCliente().getNombre().equalsIgnoreCase(transaccion.getCliente().getNombre())) {    //si lo que se modifico en la transacion es cliente, entra en este if

            cuentaServicio.eliminarTransaccionCuenta(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionRecibo(idRecibo);   //agrega transaccion en cuenta cliente modificado

        } else {

            transaccion.setFecha(recibo.getFecha());
            // transaccion.setObservacion("RECIBO N°" + recibo.getId() + " / " + recibo.getObservacion());

            if (recibo.getImporte() > 0) {
                Double importeNegativo = recibo.getImporte() * -1;
                transaccion.setImporte(importeNegativo);
            } else {
                transaccion.setImporte(recibo.getImporte());
            }

            transaccionRepositorio.save(transaccion);

            cuentaServicio.modificarTransaccionCuenta(transaccion);

        }
    }

    @Transactional
    public void eliminarTransaccionRecibo(Long idRecibo) {

        Transaccion transaccion = transaccionRepositorio.buscarTransaccionIdRecibo(idRecibo);

        cuentaServicio.eliminarTransaccionCuenta(transaccion);

    }

    public ArrayList<Transaccion> buscarTransaccionIdCliente(Long idCliente) {

        ArrayList<Transaccion> listaTransacciones = transaccionRepositorio.buscarTransaccionIdCliente(idCliente);

        Collections.sort(listaTransacciones, TransaccionComparador.ordenarFechaDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaTransacciones;
    }

    public ArrayList<Transaccion> buscarTransaccionIdCuenta(Long idCuenta) {

        ArrayList<Transaccion> listaTransacciones = transaccionRepositorio.buscarTransaccionCuenta(idCuenta);
        
        Collections.sort(listaTransacciones, TransaccionComparador.ordenarFechaAcs);

        Double saldoAcumulado = 0.0;

        for (Transaccion t : listaTransacciones) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + t.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            t.setSaldoAcumulado(saldoAcumulado);
        }

       Collections.reverse(listaTransacciones);

        return listaTransacciones;
    }
    
    public Transaccion buscarTransaccion(Long id) {

        return transaccionRepositorio.getById(id);
    }

    public ArrayList<Transaccion> buscarTransacciones() {

        ArrayList<Transaccion> listaTransaccion = new ArrayList();

        listaTransaccion = transaccionRepositorio.buscarTransacciones();

        return listaTransaccion;
    }

    public Long buscarUltimo() {

        return transaccionRepositorio.ultimaTransaccion();

    }

}

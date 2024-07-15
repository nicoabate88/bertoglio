package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Compra;
import com.bertoglio.portalbertoglio.entidades.Cuentap;
import com.bertoglio.portalbertoglio.entidades.Pago;
import com.bertoglio.portalbertoglio.entidades.Transaccionp;
import com.bertoglio.portalbertoglio.repositorios.CompraRepositorio;
import com.bertoglio.portalbertoglio.repositorios.PagoRepositorio;
import com.bertoglio.portalbertoglio.repositorios.TransaccionpRepositorio;
import com.bertoglio.portalbertoglio.util.TransaccionpComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransaccionpServicio {

    @Autowired
    private TransaccionpRepositorio transaccionpRepositorio;
    @Autowired
    private PagoRepositorio pagoRepositorio;
    @Autowired
    private CuentapServicio cuentapServicio;
    @Autowired
    private CompraRepositorio compraRepositorio;

    @Transactional
    public void crearTransaccionPago(Long idPago) {

        Pago pago = new Pago();
        Optional<Pago> pgo = pagoRepositorio.findById(idPago);
        if (pgo.isPresent()) {
            pago = pgo.get();
        }

        Transaccionp transaccion = new Transaccionp();

        transaccion.setProveedor(pago.getProveedor());
        transaccion.setFecha(pago.getFecha());
        transaccion.setConcepto("PAGO");
        transaccion.setObservacion("ORDEN DE PAGO N°" + pago.getId());
        transaccion.setImporte(pago.getImporte());
        transaccion.setPago(pago);

        transaccionpRepositorio.save(transaccion);

        cuentapServicio.agregarTransaccionCuenta(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionPago(Long idPago) {

        Pago pago = new Pago();
        Optional<Pago> pgo = pagoRepositorio.findById(idPago);
        if (pgo.isPresent()) {
            pago = pgo.get();
        }

        Transaccionp transaccion = transaccionpRepositorio.buscarTransaccionIdPago(idPago);

        if (!pago.getProveedor().getNombre().equalsIgnoreCase(transaccion.getProveedor().getNombre())) {//si lo que se modifico en la transacion es cliente, entra en este if

            Long idProveedorElimina = transaccion.getProveedor().getId();

            cuentapServicio.eliminarTransaccionCuenta(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionPago(idPago); //agrega transaccion en cuenta cliente modificado

        } else {

            transaccion.setFecha(pago.getFecha());
            transaccion.setImporte(pago.getImporte());
            transaccionpRepositorio.save(transaccion);

            cuentapServicio.modificarTransaccionCuenta(transaccion);
        }
    }

    @Transactional
    public void eliminarTransaccionPago(Long idPago) {

        Transaccionp transaccion = transaccionpRepositorio.buscarTransaccionIdPago(idPago);

        transaccion.setConcepto("ELIMINADO");

        transaccionpRepositorio.save(transaccion);

        cuentapServicio.eliminarTransaccionCuenta(transaccion);

    }

    @Transactional
    public void crearTransaccionCompra(Long idCompra) {

        Compra compra = new Compra();
        Optional<Compra> com = compraRepositorio.findById(idCompra);
        if (com.isPresent()) {
            compra = com.get();
        }

        Transaccionp transaccion = new Transaccionp();

        transaccion.setProveedor(compra.getProveedor());
        transaccion.setFecha(compra.getFecha());
        transaccion.setConcepto("COMPRA");
        transaccion.setObservacion("COMPRA N°" + compra.getId());
        Double importeNegativo = compra.getImporte() * -1;
        transaccion.setImporte(importeNegativo);
        transaccion.setCompra(compra);

        transaccionpRepositorio.save(transaccion);

        cuentapServicio.agregarTransaccionCuenta(buscarUltimo());

    }

    @Transactional
    public void modificarTransaccionCompra(Long idCompra) {

        Compra compra = new Compra();
        Optional<Compra> com = compraRepositorio.findById(idCompra);
        if (com.isPresent()) {
            compra = com.get();
        }

        Transaccionp transaccion = transaccionpRepositorio.buscarTransaccionIdCompra(idCompra);

        if (!compra.getProveedor().getNombre().equalsIgnoreCase(transaccion.getProveedor().getNombre())) {//si lo que se modifico en la transacion es Proveedor, entra en este if

            Long idProveedorElimina = transaccion.getProveedor().getId();

            cuentapServicio.eliminarTransaccionCuenta(transaccion); //elimina transaccion en cuenta cliente modificado

            crearTransaccionCompra(idCompra); //agrega transaccion en cuenta cliente modificado

        } else {

            transaccion.setFecha(compra.getFecha());
            Double importeNegativo = compra.getImporte() * -1;
            transaccion.setImporte(importeNegativo);
            transaccionpRepositorio.save(transaccion);

            cuentapServicio.modificarTransaccionCuenta(transaccion);
        }
    }

    @Transactional
    public void eliminarTransaccionCompra(Long idCompra) {

        Transaccionp transaccion = transaccionpRepositorio.buscarTransaccionIdCompra(idCompra);

        transaccion.setConcepto("ELIMINADO");

        transaccionpRepositorio.save(transaccion);

        cuentapServicio.eliminarTransaccionCuenta(transaccion);

    }

    public Transaccionp buscarTransaccion(Long id) {

        return transaccionpRepositorio.getById(id);
    }

    public ArrayList<Transaccionp> buscarTransacciones() {

        ArrayList<Transaccionp> listaTransaccion = new ArrayList();

        listaTransaccion = transaccionpRepositorio.buscarTransacciones();

        return listaTransaccion;
    }

    public Long buscarUltimo() {

        return transaccionpRepositorio.ultimaTransaccion();

    }

    public ArrayList<Transaccionp> buscarTransaccionIdCuenta(Long idCuenta) {

        ArrayList<Transaccionp> listaTransacciones = transaccionpRepositorio.buscarTransaccionCuenta(idCuenta);
        
        Collections.sort(listaTransacciones, TransaccionpComparador.ordenarFechaAcs);

        Double saldoAcumulado = 0.0;

        for (Transaccionp t : listaTransacciones) {                 //for para obtener el saldo acumulado
            saldoAcumulado = saldoAcumulado + t.getImporte();
            saldoAcumulado = Math.round(saldoAcumulado * 100.0) / 100.0;  //redondeamos saldoAcumulado solo a 2 decimales
            t.setSaldoAcumulado(saldoAcumulado);
        }

        Collections.reverse(listaTransacciones);

        return listaTransacciones;
    }
    

}

package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Pago;
import com.bertoglio.portalbertoglio.entidades.Proveedor;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.entidades.Valor;
import com.bertoglio.portalbertoglio.repositorios.PagoRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ProveedorRepositorio;
import com.bertoglio.portalbertoglio.repositorios.UsuarioRepositorio;
import com.bertoglio.portalbertoglio.util.PagoComparador;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoServicio {

    @Autowired
    private PagoRepositorio pagoRepositorio;
    @Autowired
    private ProveedorRepositorio proveedorRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionpServicio transaccionpServicio;
    @Autowired
    private ValorServicio valorServicio;

    @Transactional
    public void crearPago(Long idProveedor, String observacion, Double importe, List<Valor> valores, Long idUsuario) throws ParseException {

        Proveedor proveedor = new Proveedor();
        Optional<Proveedor> prov = proveedorRepositorio.findById(idProveedor);
        if (prov.isPresent()) {
            proveedor = prov.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        Pago pago = new Pago();

        String obsMayusculas = observacion.toUpperCase();

        pago.setEstado("EJECUTADO");
        pago.setProveedor(proveedor);
        pago.setFecha(new Date());
        pago.setObservacion(obsMayusculas);
        pago.setImporte(importe);
        pago.setValor(valores);
        pago.setUsuario(usuario);

        pagoRepositorio.save(pago);

        transaccionpServicio.crearTransaccionPago(buscarUltimo());

    }

    @Transactional
    public void modificarPago(Long idPago, Long idProveedor, String observacion, Long idUsuario) throws ParseException { //modificar Proveedor u observacion de Recibo

        Pago pago = new Pago();
        Optional<Pago> pgo = pagoRepositorio.findById(idPago);
        if (pgo.isPresent()) {
            pago = pgo.get();
        }
        Proveedor proveedor = new Proveedor();
        Optional<Proveedor> prov = proveedorRepositorio.findById(idProveedor);
        if (prov.isPresent()) {
            proveedor = prov.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMayusculas = observacion.toUpperCase();

        pago.setProveedor(proveedor);
        pago.setObservacion(obsMayusculas);
        pago.setUsuario(usuario);

        pagoRepositorio.save(pago);

        transaccionpServicio.modificarTransaccionPago(idPago);

    }

    @Transactional
    public void modificarPagoV(Long idPago, List<Valor> valores, Long idUsuario) { //modificar Valores de Pago

        Pago pago = new Pago();
        Optional<Pago> pgo = pagoRepositorio.findById(idPago);
        if (pgo.isPresent()) {
            pago = pgo.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        Double total = 0.0;

        for (Valor valor : valores) {
            total = total + Math.abs(valor.getImporte());
        }

        double totalRedondeado = Math.round(total * 100.0) / 100.0;  //redondeamos total solo a 2 decimales

        pago.setImporte(totalRedondeado);
        pago.setValor(valores);
        pago.setUsuario(usuario);

        pagoRepositorio.save(pago);

        transaccionpServicio.modificarTransaccionPago(idPago);

    }

    public Pago buscarPago(Long idPago) {

        return pagoRepositorio.getById(idPago);

    }

    public Pago buscarPagoIdValor(Long idValor) {

        return pagoRepositorio.buscarPagoValor(idValor);

    }

    public ArrayList<Pago> buscarPagos() {

        ArrayList<Pago> listaPagos = new ArrayList();

        listaPagos = pagoRepositorio.buscarPagos();

        return listaPagos;
    }

    public Long buscarUltimo() {

        return pagoRepositorio.ultimoPago();
    }

    @Transactional
    public void eliminarPago(Long idPago) {

        Pago pago = new Pago();
        Optional<Pago> pgo = pagoRepositorio.findById(idPago);
        if (pgo.isPresent()) {
            pago = pgo.get();
        }

        pago.setEstado("ELIMINADO");
        pago.setImporte(0.0);

        pagoRepositorio.save(pago);
        transaccionpServicio.eliminarTransaccionPago(idPago);
        valorServicio.modificarValorPorEliminarPago(idPago);

    }

    public ArrayList<Pago> buscarPagosIdDesc() {

        ArrayList<Pago> listaPagos = buscarPagos();

        Collections.sort(listaPagos, PagoComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaPagos;

    }

    public ArrayList<Pago> buscarPagosNombreAsc() {

        ArrayList<Pago> listaPagos = buscarPagos();

        Collections.sort(listaPagos, PagoComparador.ordenarNombreAsc); //ordena de forma DESC los ID, de mayor a menor

        return listaPagos;

    }

    public ArrayList<Pago> buscarPagosImporteDesc() {

        ArrayList<Pago> listaPagos = buscarPagos();

        Collections.sort(listaPagos, PagoComparador.ordenarImporteDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaPagos;

    }

    public ArrayList<Pago> buscarPagosFechaDesc() {

        ArrayList<Pago> listaPagos = buscarPagos();

        Collections.sort(listaPagos, PagoComparador.ordenarFechaDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaPagos;

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}

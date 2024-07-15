package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Compra;
import com.bertoglio.portalbertoglio.entidades.Detalle;
import com.bertoglio.portalbertoglio.entidades.Proveedor;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.repositorios.CompraRepositorio;
import com.bertoglio.portalbertoglio.repositorios.DetalleRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ProveedorRepositorio;
import com.bertoglio.portalbertoglio.repositorios.UsuarioRepositorio;
import com.bertoglio.portalbertoglio.util.CompraComparador;
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
public class CompraServicio {

    @Autowired
    private CompraRepositorio compraRepositorio;
    @Autowired
    private ProveedorRepositorio proveedorRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private TransaccionpServicio transaccionpServicio;
    @Autowired
    private ArticuloServicio articuloServicio;
    @Autowired
    private DetalleRepositorio detalleRepositorio;
    @Autowired
    private DetalleServicio detalleServicio;

    @Transactional
    public void crearCompra(Long idProveedor, String fecha, String observacion, String tipoComprobante, Integer numeroComprobante, Double importe, List<Detalle> detalles, Long idUsuario) throws ParseException {

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

        Compra compra = new Compra();
        Date fechaOrden = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();

        compra.setProveedor(proveedor);
        compra.setFecha(fechaOrden);
        compra.setObservacion(obsMayusculas);
        compra.setTipoComprobante(tipoComprobante);
        compra.setNumeroComprobante(numeroComprobante);
        compra.setImporte(importe);
        compra.setEstado("EJECUTADO");
        compra.setDetalle(detalles);
        compra.setUsuario(usuario);

        if (!detalles.isEmpty()) {

            for (Detalle a : detalles) {
                articuloServicio.actualizarStockCompra(a.getArticulo().getId(), a.getPrecio(), a.getCantidad());

            }
        }

        compraRepositorio.save(compra);

        transaccionpServicio.crearTransaccionCompra(buscarUltimo());

    }

    @Transactional
    public void modificarCompra(Long idCompra, Long idProveedor, String fecha, String observacion, String tipoComprobante, Integer numeroComprobante, Double importe, Long idUsuario) throws ParseException {

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
        Compra compra = new Compra();
        Optional<Compra> com = compraRepositorio.findById(idCompra);
        if (com.isPresent()) {
            compra = com.get();
        }

        Date fechaOrden = convertirFecha(fecha);
        String obsMayusculas = observacion.toUpperCase();

        compra.setProveedor(proveedor);
        compra.setFecha(fechaOrden);
        compra.setObservacion(obsMayusculas);
        compra.setTipoComprobante(tipoComprobante);
        compra.setNumeroComprobante(numeroComprobante);
        compra.setImporte(importe);
        compra.setUsuario(usuario);

        compraRepositorio.save(compra);

        transaccionpServicio.modificarTransaccionCompra(idCompra);

    }

    @Transactional
    public void modificarArticuloCompra(Long idCompra, List<Detalle> detalles) {

        Compra compra = new Compra();
        Optional<Compra> com = compraRepositorio.findById(idCompra);
        if (com.isPresent()) {
            compra = com.get();
        }

        compra.setDetalle(detalles);
        compraRepositorio.save(compra);

    }

    @Transactional
    public void eliminarCompra(Long id) {

        Compra compra = new Compra();
        Optional<Compra> com = compraRepositorio.findById(id);
        if (com.isPresent()) {
            compra = com.get();
        }

        ArrayList<Detalle> detalles = detalleRepositorio.buscarDetalleCompra(id);
        for (Detalle d : detalles) {
            articuloServicio.stockArtResta(d);
            detalleServicio.modificarDetalle(d.getId());
        }

        compra.setEstado("ELIMINADO");
        compra.setImporte(0.0);

        compraRepositorio.save(compra);

        transaccionpServicio.eliminarTransaccionCompra(id);
    }

    public Compra buscarCompra(Long id) {

        return compraRepositorio.getById(id);

    }

    public ArrayList<Compra> bucarCompras() {

        ArrayList<Compra> listaCompras = new ArrayList();

        listaCompras = compraRepositorio.buscarCompras();

        return listaCompras;
    }

    public Long buscarUltimo() {

        Long idCompra = compraRepositorio.ultimoCompra();

        return idCompra;

    }

    public ArrayList<Compra> buscarCompraIdDesc() {

        ArrayList<Compra> listaCompras = bucarCompras();

        Collections.sort(listaCompras, CompraComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaCompras;

    }

    public ArrayList<Compra> buscarCompraNombreAsc() {

        ArrayList<Compra> listaCompras = bucarCompras();

        Collections.sort(listaCompras, CompraComparador.ordenarNombreAsc); //ordena de forma DESC los ID, de mayor a menor

        return listaCompras;

    }

    public ArrayList<Compra> buscarCompraTipoAsc() {

        ArrayList<Compra> listaCompras = bucarCompras();

        Collections.sort(listaCompras, CompraComparador.ordenarTipoAsc); //ordena de forma DESC los ID, de mayor a menor

        return listaCompras;

    }

    public ArrayList<Compra> buscarCompraNumeroAsc() {

        ArrayList<Compra> listaCompras = bucarCompras();

        Collections.sort(listaCompras, CompraComparador.ordenarNumAcs); //ordena de forma DESC los ID, de mayor a menor

        return listaCompras;

    }

    public ArrayList<Compra> buscarCompraImporteAsc() {

        ArrayList<Compra> listaCompras = bucarCompras();

        Collections.sort(listaCompras, CompraComparador.ordenarImporteAcs); //ordena de forma DESC los ID, de mayor a menor

        return listaCompras;

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}

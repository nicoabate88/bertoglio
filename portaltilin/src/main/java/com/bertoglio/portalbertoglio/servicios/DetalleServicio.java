package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Articulo;
import com.bertoglio.portalbertoglio.entidades.Detalle;
import com.bertoglio.portalbertoglio.repositorios.DetalleRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DetalleServicio {

    @Autowired
    private DetalleRepositorio detalleRepositorio;

    @Transactional
    public void crearDetalle(String nombre, String codigo, Double cantidad, Double precio, Double total, Articulo articulo, String concepto) {

        Detalle detalle = new Detalle();

        double totalRedondeado = Math.round(total * 100.0) / 100.0;  //redondeamos total solo a 2 decimales

        detalle.setNombre(nombre);
        detalle.setCodigo(codigo);
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setTotal(totalRedondeado);
        detalle.setArticulo(articulo);
        detalle.setConcepto(concepto);

        detalleRepositorio.save(detalle);

    }

    public Long buscarUltimo() {

        Long idDetalle = detalleRepositorio.ultimoDetalle();

        return idDetalle;

    }

    @Transactional
    public void eliminarDetalle(Long id) {

        detalleRepositorio.deleteById(id);

    }

    @Transactional
    public void modificarDetalle(Long id) {

        Detalle detalle = new Detalle();
        Optional<Detalle> det = detalleRepositorio.findById(id);
        if (det.isPresent()) {
            detalle = det.get();
        }

        detalle.setConcepto("ELIMINADO");
        detalle.setTotal(null);

        detalleRepositorio.save(detalle);

    }

    @Transactional
    public void modificarDetallePresupuesto(Long id) {

        Detalle detalle = new Detalle();
        Optional<Detalle> det = detalleRepositorio.findById(id);
        if (det.isPresent()) {
            detalle = det.get();
        }

        detalle.setConcepto("SERVICIO");

        detalleRepositorio.save(detalle);

    }

}

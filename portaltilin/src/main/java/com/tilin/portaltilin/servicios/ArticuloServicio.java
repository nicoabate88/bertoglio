package com.tilin.portaltilin.servicios;

import com.tilin.portaltilin.entidades.Articulo;
import com.tilin.portaltilin.entidades.Detalle;
import com.tilin.portaltilin.excepciones.MiException;
import com.tilin.portaltilin.repositorios.ArticuloRepositorio;
import com.tilin.portaltilin.repositorios.DetalleRepositorio;
import com.tilin.portaltilin.repositorios.ServicioRepositorio;
import com.tilin.portaltilin.util.ArticuloComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticuloServicio {

    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private ServicioRepositorio servicioRepositorio;
    @Autowired
    private DetalleRepositorio detalleRepositorio;

    @Transactional
    public void crearArticulo(String nombre, String codigo, Double precio, Double cantidad) throws MiException {

        validarDatos(nombre);
        
        Articulo articulo = new Articulo();

        String nombreMayusculas = nombre.toUpperCase();
        String codigoMayusculas = codigo.toUpperCase();

        articulo.setNombre(nombreMayusculas);
        articulo.setCodigo(codigoMayusculas);
        articulo.setPrecio(precio);
        articulo.setCantidad(cantidad);
        articulo.setFechaAlta(new Date());

        articuloRepositorio.save(articulo);

    }

    @Transactional
    public void crearArticuloCsv(ArrayList<Articulo> articulos) throws Exception {

        try {

            for (Articulo a : articulos) {

                     Optional<Articulo> p = articuloRepositorio.findByNombre(a.getNombre());   
                   
                    if (p.isPresent()) {                                  //Si el articulo está persistido, modifica el precio
                       Articulo existente = p.get();
                       existente.setPrecio(a.getPrecio());
                       articuloRepositorio.save(existente);
                       
                    } else {                                             //Si el articulo no esta persistido, lo persiste en BD
                        articuloRepositorio.save(a);
                    }
            }

        } catch (Exception e) {
            throw new Exception("La Lista a Importar contiene errores");
        }
    }
    
    @Transactional
    public void actualizarStockCompra(Long id, Double precio, Double cantidad) {

        Articulo articulo = new Articulo();

        Optional<Articulo> art = articuloRepositorio.findById(id);
        if (art.isPresent()) {
            articulo = art.get();
        }

        Double c = articulo.getCantidad()+cantidad;
        if(precio!=0.0){
        articulo.setPrecio(precio);
        }
        articulo.setCantidad(c);

        articuloRepositorio.save(articulo);

    }
    
    @Transactional
    public void stockArtResta(Detalle detalle){ //metodo para ajustar stock de articulos por factura modificada (articulo eliminado)
        
        Articulo articulo = new Articulo();
        Optional<Articulo> art = articuloRepositorio.findByNombre(detalle.getNombre());
        if(art.isPresent()){
            articulo = art.get();
        }
        
        Double c = detalle.getCantidad();
        articulo.setCantidad(articulo.getCantidad()-c);
        
        articuloRepositorio.save(articulo);
    }
    
    @Transactional
    public void stockArtSuma(Detalle detalle){ //metodo para ajustar stock de articulos por factura modificada (articulo eliminado)
        
        Articulo articulo = new Articulo();
        Optional<Articulo> art = articuloRepositorio.findByNombre(detalle.getNombre());
        if(art.isPresent()){
            articulo = art.get();
        }
        
        Double c = detalle.getCantidad();
        articulo.setCantidad(articulo.getCantidad()+c);
        
        articuloRepositorio.save(articulo);

    }

    public ArrayList<Articulo> buscarArticulos() {

        ArrayList<Articulo> listaArticulos = new ArrayList();

        listaArticulos = (ArrayList<Articulo>) articuloRepositorio.findAll();

        return listaArticulos;
    }

    public ArrayList<Articulo> buscarArticuloIdDesc() {

        ArrayList<Articulo> listaArticulos = buscarArticulos();

        Collections.sort(listaArticulos, ArticuloComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaArticulos;
    }

    public ArrayList<Articulo> buscarArticuloNombreAsc() {

        ArrayList<Articulo> listaArticulos = buscarArticulos();

        Collections.sort(listaArticulos, ArticuloComparador.ordenarNombreAsc); //ordena por nombre alfabetico

        return listaArticulos;
    }

    public ArrayList<Articulo> buscarArticuloPrecioAsc() {

        ArrayList<Articulo> listaArticulos = buscarArticulos();

        Collections.sort(listaArticulos, ArticuloComparador.ordenarPrecioAsc); //ordena por precio de menor a mayor

        return listaArticulos;
    }

    public ArrayList<Articulo> buscarArticuloCantidadAsc() {

        ArrayList<Articulo> listaArticulos = buscarArticulos();

        Collections.sort(listaArticulos, ArticuloComparador.ordenarCantidadAsc); //ordena por cantidad de menor a mayor

        return listaArticulos;
    }

    public Articulo buscarArticulo(Long id) {

        return articuloRepositorio.getById(id);
    }

    @Transactional
    public void modificarArticulo(Long id, String nombre, String codigo, Double precio, Double cantidad) {

        Articulo articulo = new Articulo();

        Optional<Articulo> art = articuloRepositorio.findById(id);
        if (art.isPresent()) {
            articulo = art.get();
        }

        String nombreMayusculas = nombre.toUpperCase();
        String codigoMayusculas = codigo.toUpperCase();

        articulo.setNombre(nombreMayusculas);
        articulo.setCodigo(codigoMayusculas);
        articulo.setPrecio(precio);
        articulo.setCantidad(cantidad);

        articuloRepositorio.save(articulo);

    }

    @Transactional
    public void eliminarArticulo(Long id) throws MiException {

        ArrayList<Detalle> listaDetalle = detalleRepositorio.buscarDetalleIdArticulo(id);

        if (listaDetalle == null || listaDetalle.isEmpty()) {

            articuloRepositorio.deleteById(id);

        } else {

            throw new MiException("El Artículo no puede ser eliminado, se ha usado para registrar Servicios");
        }
    }

    public Long buscarUltimo() {

        return articuloRepositorio.ultimoArticulo();
    }

    @Transactional
    public void actualizarPrecio(Double precio) {

        ArrayList<Articulo> listaArticulo = buscarArticulos();

        Articulo art = new Articulo();

        for (Articulo articulo : listaArticulo) {

            art.setId(articulo.getId());
            art.setNombre(articulo.getNombre());

            art.setPrecio(articulo.getPrecio() + ((articulo.getPrecio() * precio) / 100));

            articuloRepositorio.save(art);

        }
    }
    
    public void validarDatos(String nombre) throws MiException {

        ArrayList<Articulo> listaArticulo = new ArrayList();

        listaArticulo = buscarArticulos();

        for (Articulo a : listaArticulo) {
            if (a.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El nombre de ARTÍCULO ya está registrado");

            }

        }

    }
     
}

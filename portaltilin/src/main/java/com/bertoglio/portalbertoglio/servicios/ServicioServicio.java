package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Cliente;
import com.bertoglio.portalbertoglio.entidades.Detalle;
import com.bertoglio.portalbertoglio.entidades.Servicio;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.entidades.Vehiculo;
import com.bertoglio.portalbertoglio.repositorios.ArticuloRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ClienteRepositorio;
import com.bertoglio.portalbertoglio.repositorios.DetalleRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ServicioRepositorio;
import com.bertoglio.portalbertoglio.repositorios.UsuarioRepositorio;
import com.bertoglio.portalbertoglio.repositorios.VehiculoRepositorio;
import com.bertoglio.portalbertoglio.util.ServicioComparador;
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
public class ServicioServicio {

    @Autowired
    private ServicioRepositorio servicioRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private DetalleRepositorio detalleRepositorio;
    @Autowired
    private ArticuloServicio articuloServicio;

    @Transactional
    public void crearServicio(Long idCliente, Long idVehiculo, String fecha, String observacion, Double total, List<Detalle> detalles, Long idUsuario) throws ParseException {

        Servicio servicio = new Servicio();

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Vehiculo vehiculo = new Vehiculo();
        Optional<Vehiculo> veh = vehiculoRepositorio.findById(idVehiculo);
        if (veh.isPresent()) {
            vehiculo = veh.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMay = observacion.toUpperCase();
        Date fechaOrden = convertirFecha(fecha);
        double totalRedondeado = Math.round(total * 100.0) / 100.0;  //redondeamos total solo a 2 decimales

        servicio.setCliente(cliente);
        servicio.setVehiculo(vehiculo);
        servicio.setFecha(fechaOrden);
        servicio.setObservacion(obsMay);
        servicio.setTotal(totalRedondeado);
        servicio.setEstado("EJECUTADO");
        servicio.setUsuario(usuario);
        servicio.setDetalle(detalles);

        servicioRepositorio.save(servicio);

        for (Detalle d : detalles) {                           //for para enviar detalles para ajustar stock de articulo 
            articuloServicio.stockArtResta(d);
        }

        transaccionServicio.crearTransaccionServicio(buscarUltimo());

    }

    @Transactional
    public void modificarServicio(Long id, Long idCliente, Long idVehiculo, String fecha, String observacion, Double total, List<Detalle> detalles, Long idUsuario) throws ParseException {

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }
        Vehiculo vehiculo = new Vehiculo();
        Optional<Vehiculo> veh = vehiculoRepositorio.findById(idVehiculo);
        if (veh.isPresent()) {
            vehiculo = veh.get();
        }

        Servicio servicio = new Servicio();
        Optional<Servicio> servi = servicioRepositorio.findById(id);
        if (servi.isPresent()) {
            servicio = servi.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        String obsMay = observacion.toUpperCase();
        Date fechaOrden = convertirFecha(fecha);
        double totalRedondeado = Math.round(total * 100.0) / 100.0;  //redondeamos total solo a 2 decimales

        servicio.setCliente(cliente);
        servicio.setVehiculo(vehiculo);
        servicio.setFecha(fechaOrden);
        servicio.setObservacion(obsMay);
        servicio.setUsuario(usuario);
        servicio.setTotal(totalRedondeado);
        servicio.setDetalle(detalles);

        servicioRepositorio.save(servicio);

        transaccionServicio.modificarTransaccionServicio(id);

    }

    public ArrayList<Servicio> buscarServicios() {

        ArrayList<Servicio> listaServicios = new ArrayList();

        listaServicios = servicioRepositorio.buscarServicios();

        return listaServicios;

    }

    public ArrayList<Servicio> buscarServiciosIdDesc() {

        ArrayList<Servicio> listaServicios = buscarServicios();

        Collections.sort(listaServicios, ServicioComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaServicios;

    }

    public ArrayList<Servicio> buscarServiciosNombreAsc() {

        ArrayList<Servicio> listaServicios = buscarServicios();

        Collections.sort(listaServicios, ServicioComparador.ordenarNombreAsc); //ordena por nombre alfabetico los nombres de clientes

        return listaServicios;

    }

    public ArrayList<Servicio> buscarServiciosFechaDesc() {

        ArrayList<Servicio> listaServicios = buscarServicios();

        Collections.sort(listaServicios, ServicioComparador.ordenarFechaDesc); //ordena por nombre alfabetico los nombres de clientes

        return listaServicios;

    }

    public Servicio buscarServicio(Long id) {

        return servicioRepositorio.getById(id);
    }

    public ArrayList<Servicio> buscarServicioIdCliente(Long id) {

        ArrayList<Servicio> listaServicios = servicioRepositorio.buscarServicioIdCliente(id);

        Collections.sort(listaServicios, ServicioComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaServicios;
    }

    public ArrayList<Servicio> buscarServicioDominio(String dominio) {

        ArrayList<Servicio> listaServicio = (ArrayList<Servicio>) servicioRepositorio.findAll();
        ArrayList<Servicio> servicioDominio = new ArrayList();

        for (Servicio servicio : listaServicio) {
            if (servicio.getVehiculo().getDominio().equalsIgnoreCase(dominio)) {
                servicioDominio.add(servicio);
            }
        }
        return servicioDominio;
    }

    public ArrayList<Servicio> buscarServicioIdVehiculo(Long idVehiculo) {

        ArrayList<Servicio> listaServicios = servicioRepositorio.buscarServicioIdVehiculo(idVehiculo);

        Collections.sort(listaServicios, ServicioComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaServicios;

    }

    public Long buscarUltimo() {

        Long idServicio = servicioRepositorio.ultimoServicio();

        return idServicio;

    }

    @Transactional
    public void eliminarServicio(Long idServicio) {

        Servicio servicio = new Servicio();
        Optional<Servicio> servi = servicioRepositorio.findById(idServicio);
        if (servi.isPresent()) {
            servicio = servi.get();
        }

        ArrayList<Detalle> lista = detalleRepositorio.buscarDetalleServicio(idServicio);
        for (Detalle d : lista) {
            detalleServicio.modificarDetalle(d.getId());
        }

        transaccionServicio.eliminarTransaccionServicio(idServicio);

        servicio.setEstado("ELIMINADO");
        servicio.setTotal(0.0);
        servicio.setCliente(null);
        servicio.setDetalle(null);
        servicioRepositorio.save(servicio);

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}

package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Cliente;
import com.bertoglio.portalbertoglio.entidades.Detalle;
import com.bertoglio.portalbertoglio.entidades.Presupuesto;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.entidades.Vehiculo;
import com.bertoglio.portalbertoglio.repositorios.ClienteRepositorio;
import com.bertoglio.portalbertoglio.repositorios.DetalleRepositorio;
import com.bertoglio.portalbertoglio.repositorios.PresupuestoRepositorio;
import com.bertoglio.portalbertoglio.repositorios.UsuarioRepositorio;
import com.bertoglio.portalbertoglio.repositorios.VehiculoRepositorio;
import com.bertoglio.portalbertoglio.util.PresupuestoComparador;
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
public class PresupuestoServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;
    @Autowired
    private PresupuestoRepositorio presupuestoRepositorio;
    @Autowired
    private ServicioServicio servicioServicio;
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private DetalleRepositorio detalleRepositorio;
    @Autowired
    private DetalleServicio detalleServicio;

    @Transactional
    public void crearPresupuesto(Long idCliente, Long idVehiculo, String fecha, String observacion, Double total, List<Detalle> detalles, Long idUsuario) throws ParseException {

        Presupuesto presupuesto = new Presupuesto();

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

        Date fechaOrden = convertirFecha(fecha);
        double totalRedondeado = Math.round(total * 100.0) / 100.0;  //redondeamos total solo a 2 decimales

        presupuesto.setCliente(cliente);
        presupuesto.setVehiculo(vehiculo);
        presupuesto.setFecha(fechaOrden);
        presupuesto.setObservacion(observacion);
        presupuesto.setTotal(totalRedondeado);
        presupuesto.setEstado("PRESUPUESTO");
        presupuesto.setUsuario(usuario);
        presupuesto.setDetalle(detalles);

        presupuestoRepositorio.save(presupuesto);

    }

    public Presupuesto buscarPresupuesto(Long id) {

        return presupuestoRepositorio.getById(id);
    }

    public ArrayList<Presupuesto> buscarPresupuestos() {

        ArrayList<Presupuesto> listaPresupuestos = new ArrayList();

        listaPresupuestos = presupuestoRepositorio.buscarPresupuestos();

        return listaPresupuestos;

    }

    public ArrayList<Presupuesto> buscarPresupuestoIdDesc() {

        ArrayList<Presupuesto> listaPresupuestos = buscarPresupuestos();

        Collections.sort(listaPresupuestos, PresupuestoComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaPresupuestos;

    }

    public ArrayList<Presupuesto> buscarPresupuestoNombreAsc() {

        ArrayList<Presupuesto> listaPresupuestos = buscarPresupuestos();

        Collections.sort(listaPresupuestos, PresupuestoComparador.ordenarNombreAsc); //ordena de forma DESC los ID, de mayor a menor

        return listaPresupuestos;

    }

    public ArrayList<Presupuesto> buscarPresupuestoFechaDesc() {

        ArrayList<Presupuesto> listaPresupuestos = buscarPresupuestos();

        Collections.sort(listaPresupuestos, PresupuestoComparador.ordenarFechaDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaPresupuestos;

    }

    public Long buscarUltimo() {

        Long idServicio = presupuestoRepositorio.ultimoServicio();

        return idServicio;

    }

    @Transactional
    public void modificarPresupuesto(Long id, Long idCliente, Long idVehiculo, String fecha, String observacion, Double total, List<Detalle> detalles, Long idUsuario) throws ParseException {

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

        Presupuesto presupuesto = new Presupuesto();
        Optional<Presupuesto> presu = presupuestoRepositorio.findById(id);
        if (presu.isPresent()) {
            presupuesto = presu.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        Date fechaOrden = convertirFecha(fecha);
        double totalRedondeado = Math.round(total * 100.0) / 100.0;  //redondeamos total solo a 2 decimales

        presupuesto.setCliente(cliente);
        presupuesto.setVehiculo(vehiculo);
        presupuesto.setFecha(fechaOrden);
        presupuesto.setObservacion(observacion);
        presupuesto.setTotal(totalRedondeado);
        presupuesto.setDetalle(detalles);
        presupuesto.setUsuario(usuario);

        presupuestoRepositorio.save(presupuesto);

    }

    @Transactional
    public void modificarPresupuestoS(Long idPresupuesto, Long idUsuario) throws ParseException {

        Presupuesto presupuesto = new Presupuesto();
        Optional<Presupuesto> presu = presupuestoRepositorio.findById(idPresupuesto);
        if (presu.isPresent()) {
            presupuesto = presu.get();
        }

        Usuario usuario = new Usuario();
        Optional<Usuario> user = usuarioRepositorio.findById(idUsuario);
        if (user.isPresent()) {
            usuario = user.get();
        }

        presupuesto.setEstado("SERVICIO");
        presupuesto.setUsuario(usuario);

        presupuestoRepositorio.save(presupuesto);

        for (Detalle d : presupuesto.getDetalle()) {
            detalleServicio.modificarDetallePresupuesto(d.getId());
        }

        Date date = presupuesto.getFecha();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = dateFormat.format(date);

        Long idCliente = presupuesto.getCliente().getId();
        Long idVehiculo = presupuesto.getVehiculo().getId();
        String observacion = presupuesto.getObservacion();
        Double total = presupuesto.getTotal();
        ArrayList<Detalle> listaDetalle = new ArrayList();
        listaDetalle.addAll(presupuesto.getDetalle());

        servicioServicio.crearServicio(idCliente, idVehiculo, fecha, observacion, total, listaDetalle, idUsuario);

    }

    @Transactional
    public void eliminarPresupuesto(Long idPresupuesto) {

        Presupuesto presupuesto = new Presupuesto();
        Optional<Presupuesto> presu = presupuestoRepositorio.findById(idPresupuesto);
        if (presu.isPresent()) {
            presupuesto = presu.get();
        }

        ArrayList<Detalle> lista = detalleRepositorio.buscarDetallePresupuesto(idPresupuesto);
        for (Detalle d : lista) {
            detalleServicio.modificarDetalle(d.getId());
        }

        presupuesto.setEstado("ELIMINADO");
        presupuesto.setDetalle(null);
        presupuesto.setTotal(0.0);
        presupuesto.setCliente(null);

        presupuestoRepositorio.save(presupuesto);

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}

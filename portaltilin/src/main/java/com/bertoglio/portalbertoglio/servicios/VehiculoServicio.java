package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Cliente;
import com.bertoglio.portalbertoglio.entidades.Presupuesto;
import com.bertoglio.portalbertoglio.entidades.Servicio;
import com.bertoglio.portalbertoglio.entidades.Vehiculo;
import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.repositorios.ClienteRepositorio;
import com.bertoglio.portalbertoglio.repositorios.PresupuestoRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ServicioRepositorio;
import com.bertoglio.portalbertoglio.repositorios.VehiculoRepositorio;
import com.bertoglio.portalbertoglio.util.VehiculoComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VehiculoServicio {

    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private ServicioRepositorio servicioRepositorio;
    @Autowired
    private PresupuestoRepositorio presupuestoRepositorio;

    @Transactional
    public void crearVehiculo(String dominio, String marca, String modelo, Long anio, Long idCliente) throws MiException {

        validarDatos(dominio);

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Vehiculo vehiculo = new Vehiculo();

        String dominioMayusculas = dominio.toUpperCase();
        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();

        vehiculo.setDominio(dominioMayusculas);
        vehiculo.setMarca(marcaMayusculas);
        vehiculo.setModelo(modeloMayusculas);
        vehiculo.setAnio(anio);
        vehiculo.setCliente(cliente);
        vehiculo.setFechaAlta(new Date());

        vehiculoRepositorio.save(vehiculo);

    }

    public ArrayList<Vehiculo> buscarVehiculos() {

        ArrayList<Vehiculo> listaVehiculos = (ArrayList<Vehiculo>) vehiculoRepositorio.findAll();

        return listaVehiculos;
    }

    public ArrayList<Vehiculo> buscarVehiculosIdDesc() {

        ArrayList<Vehiculo> listaVehiculos = buscarVehiculos();

        Collections.sort(listaVehiculos, VehiculoComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaVehiculos;
    }

    public ArrayList<Vehiculo> buscarVehiculosClientesAsc() {

        ArrayList<Vehiculo> listaVehiculos = buscarVehiculos();

        Collections.sort(listaVehiculos, VehiculoComparador.ordenarNombreAsc); //ordena alfabeticamente por nombre de cliente

        return listaVehiculos;
    }

    public ArrayList<Vehiculo> buscarVehiculosDominioAsc() {

        ArrayList<Vehiculo> listaVehiculos = buscarVehiculos();

        Collections.sort(listaVehiculos, VehiculoComparador.ordenarDominioAsc); //ordena alfabeticamente por dominio

        return listaVehiculos;
    }

    public Vehiculo buscarVehiculo(Long id) {

        return vehiculoRepositorio.getOne(id);
    }

    public ArrayList<Vehiculo> buscarVehiculoIdCliente(Long idCliente) {

        ArrayList<Vehiculo> listaVehiculos = vehiculoRepositorio.buscarVehiculoIdCliente(idCliente);

        Collections.sort(listaVehiculos, VehiculoComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaVehiculos;

    }

    @Transactional
    public void modificarVehiculo(Long id, String dominio, String marca, String modelo, Long anio, Long idCliente) {

        Vehiculo vehiculo = new Vehiculo();

        Optional<Vehiculo> veh = vehiculoRepositorio.findById(id);

        if (veh.isPresent()) {
            vehiculo = veh.get();
        }

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        String dominioMayusculas = dominio.toUpperCase();
        String marcaMayusculas = marca.toUpperCase();
        String modeloMayusculas = modelo.toUpperCase();

        vehiculo.setDominio(dominioMayusculas);
        vehiculo.setMarca(marcaMayusculas);
        vehiculo.setModelo(modeloMayusculas);
        vehiculo.setAnio(anio);
        vehiculo.setCliente(cliente);

        vehiculoRepositorio.save(vehiculo);

    }

    @Transactional
    public void eliminarVehiculo(Long id) throws MiException {

        ArrayList<Servicio> listaServicio = servicioRepositorio.buscarServicioIdVehiculo(id);
        ArrayList<Presupuesto> listaPresupuesto = presupuestoRepositorio.buscarPresupuestoIdCliente(id);

        if (listaServicio.isEmpty() && listaPresupuesto.isEmpty()) {

            vehiculoRepositorio.deleteById(id);

        } else {

            throw new MiException("El Vehículo no puede ser eliminado, tiene Servicio o Presupuesto realizados");

        }

    }

    public Long buscarUltimo() {

        return vehiculoRepositorio.ultimoVehiculo();

    }

    public void validarDatos(String dominio) throws MiException {

        ArrayList<Vehiculo> listaVehiculo = new ArrayList();

        listaVehiculo = buscarVehiculos();

        for (Vehiculo lista : listaVehiculo) {
            if (lista.getDominio().equalsIgnoreCase(dominio)) {
                throw new MiException("El Dominio del Vehículo ya está registrado");

            }

        }

    }

}

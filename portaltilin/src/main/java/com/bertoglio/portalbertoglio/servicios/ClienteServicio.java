package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Cliente;
import com.bertoglio.portalbertoglio.entidades.Recibo;
import com.bertoglio.portalbertoglio.entidades.Vehiculo;
import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.repositorios.ClienteRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ReciboRepositorio;
import com.bertoglio.portalbertoglio.repositorios.VehiculoRepositorio;
import com.bertoglio.portalbertoglio.util.ClienteComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClienteServicio {

    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private VehiculoRepositorio vehiculoRepositorio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private ReciboRepositorio reciboRepositorio;

    @Transactional
    public void crearCliente(String nombre, Long cuit, String localidad, String direccion, Long telefono, String email) throws MiException {

        validarDatos(nombre);

        Cliente cliente = new Cliente();

        String nombreMayusculas = nombre.toUpperCase();
        String localidadMayusculas = localidad.toUpperCase();
        String direccionMayusculas = direccion.toUpperCase();

        cliente.setNombre(nombreMayusculas);
        cliente.setCuit(cuit);
        cliente.setLocalidad(localidadMayusculas);
        cliente.setDireccion(direccionMayusculas);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);
        cliente.setFechaAlta(new Date());

        clienteRepositorio.save(cliente);

        cuentaServicio.crearCuenta(buscarUltimo());

    }

    public Cliente buscarCliente(Long id) {

        return clienteRepositorio.getById(id);

    }

    public ArrayList<Cliente> bucarClientes() {

        ArrayList<Cliente> listaClientes = new ArrayList();

        listaClientes = (ArrayList<Cliente>) clienteRepositorio.findAll();

        return listaClientes;
    }

    public ArrayList<Cliente> buscarClientesIdDesc() {

        ArrayList<Cliente> listaClientes = bucarClientes();

        Collections.sort(listaClientes, ClienteComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaClientes;

    }

    public ArrayList<Cliente> buscarClientesNombreAsc() {

        ArrayList<Cliente> listaClientes = bucarClientes();

        Collections.sort(listaClientes, ClienteComparador.ordenarNombreAsc); //ordena por nombre alfabetico los nombres de clientes

        return listaClientes;

    }

    @Transactional
    public void modificarCliente(Long id, String nombre, Long cuit, String localidad, String direccion, Long telefono, String email) {

        Cliente cliente = new Cliente();

        Optional<Cliente> cte = clienteRepositorio.findById(id);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        String nombreMayusculas = nombre.toUpperCase();
        String localidadMayusculas = localidad.toUpperCase();
        String direccionMayusculas = direccion.toUpperCase();

        cliente.setNombre(nombreMayusculas);
        cliente.setCuit(cuit);
        cliente.setLocalidad(localidadMayusculas);
        cliente.setDireccion(direccionMayusculas);
        cliente.setTelefono(telefono);
        cliente.setEmail(email);

        clienteRepositorio.save(cliente);

    }

    @Transactional
    public void eliminarCliente(Long id) throws MiException {

        ArrayList<Vehiculo> listaVehiculo = vehiculoRepositorio.buscarVehiculoIdCliente(id);
        ArrayList<Recibo> listaRecibo = reciboRepositorio.buscarReciboIdCliente(id);

        if (listaVehiculo.isEmpty() && listaRecibo.isEmpty()) {

            clienteRepositorio.deleteById(id);

            cuentaServicio.eliminarCuenta(id);

        } else {

            throw new MiException("El Cliente no puede ser eliminado, tiene Servicio o Vehículo asociado");
        }

    }

    public Long buscarUltimo() {

        return clienteRepositorio.ultimoCliente();

    }

    public void validarDatos(String nombre) throws MiException {

        ArrayList<Cliente> listaClientes = new ArrayList();

        listaClientes = bucarClientes();

        for (Cliente lista : listaClientes) {
            if (lista.getNombre().equalsIgnoreCase(nombre)) {
                throw new MiException("El NOMBRE de Cliente ya está registrado");
            }
        }
    }

}

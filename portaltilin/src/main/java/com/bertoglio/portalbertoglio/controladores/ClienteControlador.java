package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.servicios.ClienteServicio;
import com.bertoglio.portalbertoglio.servicios.CuentaServicio;
import com.bertoglio.portalbertoglio.servicios.ServicioServicio;
import com.bertoglio.portalbertoglio.servicios.TransaccionServicio;
import com.bertoglio.portalbertoglio.servicios.VehiculoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cliente")
@PreAuthorize("hasRole('ROLE_admin')")
public class ClienteControlador {

    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private VehiculoServicio vehiculoServicio;
    @Autowired
    private ServicioServicio servicioServicio;
    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private TransaccionServicio transaccionServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {

        return "cliente_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam(required = false) Long cuit, @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String direccion, @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, ModelMap modelo) {

        try {

            clienteServicio.crearCliente(nombre, cuit, localidad, direccion, telefono, email);

            Long id = clienteServicio.buscarUltimo();

            modelo.put("cliente", clienteServicio.buscarCliente(id));
            modelo.put("exito", "Cliente REGISTRADO exitosamente");

            return "cliente_mostrar.html";

        } catch (MiException ex) {
            modelo.put("nombre", nombre);
            modelo.put("cuit", cuit);
            modelo.put("localidad", localidad);
            modelo.put("direccion", direccion);
            modelo.put("telefono", telefono);
            modelo.put("email", email);
            modelo.put("error", ex.getMessage());

            return "cliente_registrar.html";
        }
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());

        return "cliente_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdAsc(ModelMap modelo) {

        modelo.addAttribute("clientes", clienteServicio.bucarClientes());

        return "cliente_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());

        return "cliente_listar.html";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable Long id, ModelMap modelo) {

        modelo.addAttribute("cliente", clienteServicio.buscarCliente(id));
        modelo.put("cuenta", cuentaServicio.buscarCuentaIdCliente(id));

        return "cliente_detalle.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) throws MiException {

        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "cliente_modificar.html";
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam(required = false) Long cuit, @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String direccion, @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, ModelMap modelo) {

        clienteServicio.modificarCliente(id, nombre, cuit, localidad, direccion, telefono, email);

        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.put("exito", "Cliente MODIFICADO exitosamente");

        return "cliente_mostrar.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));

        return "cliente_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            clienteServicio.eliminarCliente(id);

            modelo.put("exito", "Cliente ELIMINADO exitosamente");
            modelo.addAttribute("clientes", clienteServicio.bucarClientes());

            return "cliente_listar.html";

        } catch (MiException ex) {

            modelo.put("cliente", clienteServicio.buscarCliente(id));
            modelo.put("error", ex.getMessage());

            return "cliente_eliminar.html";
        }
    }

    @GetMapping("/vehiculo/{id}")
    public String vehiculo(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculoIdCliente(id));

        return "cliente_vehiculo.html";
    }

    @GetMapping("/servicio/{id}")
    public String servicio(@PathVariable Long id, ModelMap modelo) {

        modelo.put("cliente", clienteServicio.buscarCliente(id));
        modelo.addAttribute("servicios", servicioServicio.buscarServicioIdCliente(id));

        return "cliente_servicio.html";
    }

    @GetMapping("/listarPdf")
    public String listarPdf(ModelMap modelo) {

        modelo.addAttribute("clientes", clienteServicio.bucarClientes());

        return "cliente_listarPdf.html";
    }
}

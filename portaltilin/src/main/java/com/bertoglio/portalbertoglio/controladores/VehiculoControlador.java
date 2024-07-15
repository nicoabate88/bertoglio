package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.servicios.ClienteServicio;
import com.bertoglio.portalbertoglio.servicios.ServicioServicio;
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
@RequestMapping("/vehiculo")
@PreAuthorize("hasRole('ROLE_admin')")
public class VehiculoControlador {

    @Autowired
    private VehiculoServicio vehiculoServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ServicioServicio servicioServicio;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());

        return "vehiculo_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String dominio, @RequestParam String marca,
            @RequestParam String modelo, @RequestParam(required = false) Long anio, @RequestParam Long idCliente, ModelMap model) {

        try {

            vehiculoServicio.crearVehiculo(dominio, marca, modelo, anio, idCliente);

            Long id = vehiculoServicio.buscarUltimo();
            model.put("vehiculo", vehiculoServicio.buscarVehiculo(id));
            model.put("exito", "Vehículo registrado exitosamente");

            return "vehiculo_mostrar.html";

        } catch (MiException ex) {
            model.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
            model.put("dominio", dominio);
            model.put("marca", marca);
            model.put("modelo", modelo);
            model.put("anio", anio);
            model.put("error", ex.getMessage());

            return "vehiculo_registrar.html";
        }
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("vehiculo", vehiculoServicio.buscarVehiculo(id));

        return "vehiculo_mostrar.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculosIdDesc());

        return "vehiculo_listar.html";

    }

    @GetMapping("/listarIdAsc")
    public String listarIdDesc(ModelMap modelo) {

        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculos());

        return "vehiculo_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculosClientesAsc());

        return "vehiculo_listar.html";
    }

    @GetMapping("/listarDominioAsc")
    public String listarDominioAsc(ModelMap modelo) {

        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculosDominioAsc());

        return "vehiculo_listar.html";
    }

    @GetMapping("/servicio/{id}")
    public String servicio(@PathVariable Long id, ModelMap modelo) {

        modelo.put("vehiculo", vehiculoServicio.buscarVehiculo(id));
        modelo.addAttribute("servicios", servicioServicio.buscarServicioIdVehiculo(id));

        return "vehiculo_servicio.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("vehiculo", vehiculoServicio.buscarVehiculo(id));
        modelo.addAttribute("clientes", clienteServicio.bucarClientes());

        return "vehiculo_modificar.html";
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String dominio, @RequestParam String marca,
            @RequestParam String modelo, @RequestParam(required = false) Long anio, @RequestParam Long idCliente, ModelMap model) {

        vehiculoServicio.modificarVehiculo(id, dominio, marca, modelo, anio, idCliente);

        model.put("vehiculo", vehiculoServicio.buscarVehiculo(id));
        model.put("exito", "Vehículo MODIFICADO exitosamente");

        return "vehiculo_mostrar.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("vehiculo", vehiculoServicio.buscarVehiculo(id));

        return "vehiculo_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {
            vehiculoServicio.eliminarVehiculo(id);

            modelo.put("exito", "Vehículo ELIMINADO exitosamente");
            modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculos());

            return "vehiculo_listar.html";

        } catch (MiException ex) {

            modelo.addAttribute("vehiculo", vehiculoServicio.buscarVehiculo(id));
            modelo.put("error", ex.getMessage());

            return "vehiculo_eliminar.html";
        }

    }

}

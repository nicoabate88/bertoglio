package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.servicios.ProveedorServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@RequestMapping("/proveedor")
@PreAuthorize("hasRole('ROLE_admin')")
public class ProveedorControlador {

    @Autowired
    private ProveedorServicio proveedorServicio;

    @GetMapping("/registrar")
    public String registrar() {

        return "proveedor_registrar.html";

    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam(required = false) Long cuit, @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String direccion, @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, ModelMap modelo) {

        try {

            proveedorServicio.crearProveedor(nombre, cuit, localidad, direccion, telefono, email);

            Long id = proveedorServicio.buscarUltimo();

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("exito", "Proveedor REGISTRADO exitosamente");

            return "proveedor_mostrar.html";

        } catch (MiException ex) {
            modelo.put("nombre", nombre);
            modelo.put("cuit", cuit);
            modelo.put("localidad", localidad);
            modelo.put("direccion", direccion);
            modelo.put("telefono", telefono);
            modelo.put("email", email);
            modelo.put("error", ex.getMessage());

            return "proveedor_registrar.html";
        }
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresIdDesc());

        return "proveedor_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdAsc(ModelMap modelo) {

        modelo.addAttribute("proveedores", proveedorServicio.bucarProveedores());

        return "proveedor_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresNombreAsc());

        return "proveedor_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));

        return "proveedor_mostrar.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) throws MiException {

        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));

        return "proveedor_modificar.html";
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam(required = false) Long cuit, @RequestParam(required = false) String localidad,
            @RequestParam(required = false) String direccion, @RequestParam(required = false) Long telefono, @RequestParam(required = false) String email, ModelMap modelo) {

        try {
            proveedorServicio.modificarProveedor(id, nombre, cuit, localidad, direccion, telefono, email);

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("exito", "Proveedor MODIFICADO exitosamente");

            return "proveedor_mostrar.html";

        } catch (MiException ex) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("error", ex.getMessage());

            return "proveedor_modificar.html";
        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("proveedor", proveedorServicio.buscarProveedor(id));

        return "proveedor_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            proveedorServicio.eliminarProveedor(id);

            modelo.put("exito", "Proveedor ELIMINADO exitosamente");
            modelo.addAttribute("proveedores", proveedorServicio.bucarProveedores());

            return "proveedor_listar.html";

        } catch (MiException ex) {

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("error", ex.getMessage());

            return "proveedor_eliminar.html";
        }
    }

}

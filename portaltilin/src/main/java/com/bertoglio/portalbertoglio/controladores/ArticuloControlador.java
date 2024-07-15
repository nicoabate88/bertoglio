package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.servicios.ArticuloServicio;
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
@RequestMapping("/articulo")
@PreAuthorize("hasRole('ROLE_admin')")
public class ArticuloControlador {

    @Autowired
    private ArticuloServicio articuloServicio;

    @GetMapping("/registrar")
    public String registrar() {

        return "articulo_registrar.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Double precio, @RequestParam(required = false) Double cantidad, ModelMap modelo) {

        try {

            articuloServicio.crearArticulo(nombre, codigo, precio, cantidad);

            Long id = articuloServicio.buscarUltimo();
            modelo.put("articulo", articuloServicio.buscarArticulo(id));
            modelo.put("exito", "Artículo REGISTRADO exitosamente");

            return "articulo_mostrar.html";

        } catch (MiException ex) {

            modelo.put("error", ex.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("codigo", codigo);

            return "articulo_registrar.html";
        }

    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("articulo", articuloServicio.buscarArticulo(id));

        return "articulo_mostrar.html";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("articulos", articuloServicio.buscarArticuloIdDesc());

        return "articulo_listar.html";
    }

    @GetMapping("/listarPdf")
    public String listarPdf(ModelMap modelo) {

        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());

        return "articulo_listarPdf.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdDesc(ModelMap modelo) {

        modelo.addAttribute("articulos", articuloServicio.buscarArticulos());

        return "articulo_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());

        return "articulo_listar.html";
    }

    @GetMapping("/listarPrecioAsc")
    public String listarPrecioAsc(ModelMap modelo) {

        modelo.addAttribute("articulos", articuloServicio.buscarArticuloPrecioAsc());

        return "articulo_listar.html";
    }

    @GetMapping("/listarCantidadAsc")
    public String listarCantidadAsc(ModelMap modelo) {

        modelo.addAttribute("articulos", articuloServicio.buscarArticuloCantidadAsc());

        return "articulo_listar.html";
    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("articulo", articuloServicio.buscarArticulo(id));

        return "articulo_modificar.html";
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam String nombre, @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Double precio, @RequestParam(required = false) Double cantidad, ModelMap modelo) {

        try {
            articuloServicio.modificarArticulo(id, nombre, codigo, precio, cantidad);
            modelo.addAttribute("articulo", articuloServicio.buscarArticulo(id));
            modelo.put("exito", "Artículo MODIFICADO exitosamente");

            return "articulo_mostrar.html";

        } catch (MiException ex) {

            modelo.put("articulo", articuloServicio.buscarArticulo(id));
            modelo.put("error", ex.getMessage());

            return "articulo_modificar.html";
        }

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("articulo", articuloServicio.buscarArticulo(id));

        return "articulo_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {

            articuloServicio.eliminarArticulo(id);

            modelo.addAttribute("articulos", articuloServicio.buscarArticuloIdDesc());
            modelo.put("exito", "Artículo ELIMINADO exitosamente");

            return "articulo_listar.html";

        } catch (MiException ex) {

            modelo.put("error", ex.getMessage());
            modelo.put("articulo", articuloServicio.buscarArticulo(id));

            return "articulo_eliminar.html";
        }

    }

}

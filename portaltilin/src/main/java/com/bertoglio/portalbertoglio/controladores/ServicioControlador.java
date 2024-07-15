package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Articulo;
import com.bertoglio.portalbertoglio.entidades.Detalle;
import com.bertoglio.portalbertoglio.entidades.Servicio;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.repositorios.ArticuloRepositorio;
import com.bertoglio.portalbertoglio.repositorios.DetalleRepositorio;
import com.bertoglio.portalbertoglio.servicios.ArticuloServicio;
import com.bertoglio.portalbertoglio.servicios.ClienteServicio;
import com.bertoglio.portalbertoglio.servicios.DetalleServicio;
import com.bertoglio.portalbertoglio.servicios.ServicioServicio;
import com.bertoglio.portalbertoglio.servicios.VehiculoServicio;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Optional;
import javax.servlet.http.HttpSession;
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
@RequestMapping("/servicio")
@PreAuthorize("hasRole('ROLE_admin')")
public class ServicioControlador {

    @Autowired
    private ServicioServicio servicioServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ArticuloServicio articuloServicio;
    @Autowired
    private VehiculoServicio vehiculoServicio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private DetalleRepositorio detalleRepositorio;

    ArrayList<Detalle> detalles = new ArrayList(); //ArrayList para crear los detalles que se guardaran en el servicio
    ArrayList<Detalle> detallesM = new ArrayList();  //ArrayList para modificar los detalles que se guardaran en la modificacion del servicio
    ArrayList<Detalle> listaPersistida = new ArrayList(); //ArrayList de detalles de articulos almacenado en la BD (proximo a agregar articulos)
    ArrayList<Detalle> sumar = new ArrayList();
    ArrayList<Detalle> restar = new ArrayList();

    Long auxBorrarArticulo;  //variable utilizada para pasar idServicio desde metodo modificaA a borrarArticuloA

    Long idCliente;
    Long idVehiculo;
    String fecha;
    String observacion;

    @GetMapping("/registrar")
    public String registrarCliente(ModelMap modelo) {

        detalles.clear();

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());

        return "servicio_registrarCliente.html";
    }

    @PostMapping("/registrarC")
    public String procesarOpcionSeleccionada(@RequestParam("cliente") Long cliente, ModelMap modelo) {

        idCliente = cliente;

        modelo.put("idCliente", idCliente);
        modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculoIdCliente(idCliente));
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());

        return "servicio_registrar.html";
    }

    @GetMapping("/registro")
    public String registro(HttpSession session, ModelMap modelo) throws ParseException {

        Double total = 0.0;
        for (Detalle detalle : detalles) {
            total = total + detalle.getTotal();
        }

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        servicioServicio.crearServicio(idCliente, idVehiculo, fecha, observacion, total, detalles, logueado.getId());

        Long id = servicioServicio.buscarUltimo();
        String totalMiles = convertirNumeroMiles(total);

        modelo.put("servicio", servicioServicio.buscarServicio(id));
        modelo.put("fecha", fecha);
        modelo.put("totalServicio", totalMiles);
        modelo.put("exito", "Servicio REGISTRADO exitosamente");

        return "servicio_registrado.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("servicios", servicioServicio.buscarServiciosIdDesc());

        return "servicio_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdDesc(ModelMap modelo) {

        modelo.addAttribute("servicios", servicioServicio.buscarServicios());

        return "servicio_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("servicios", servicioServicio.buscarServiciosNombreAsc());

        return "servicio_listar.html";
    }

    @GetMapping("/listarFechaDesc")
    public String listarFechaDesc(ModelMap modelo) {

        modelo.addAttribute("servicios", servicioServicio.buscarServiciosFechaDesc());

        return "servicio_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Servicio servicio = servicioServicio.buscarServicio(id);
        String totalMiles = convertirNumeroMiles(servicio.getTotal());

        modelo.put("servicio", servicio);
        modelo.put("totalServicio", totalMiles);

        return "servicio_mostrar.html";
    }

    @GetMapping("/mostrarPdf/{id}")
    public String mostrarPdf(@PathVariable Long id, ModelMap modelo) {

        Servicio servicio = servicioServicio.buscarServicio(id);
        String totalMiles = convertirNumeroMiles(servicio.getTotal());

        modelo.put("servicio", servicio);
        modelo.put("totalServicio", totalMiles);

        return "servicio_mostrarPdf.html";

    }

    @GetMapping("/vehiculoMostrar/{id}")
    public String vehiculoMostrar(@PathVariable Long id, ModelMap modelo) {

        Servicio servicio = servicioServicio.buscarServicio(id);
        String totalMiles = convertirNumeroMiles(servicio.getTotal());

        modelo.put("servicio", servicio);
        modelo.put("totalServicio", totalMiles);

        return "servicio_vehiculoMostrar.html";
    }

    @PostMapping("/addArticulo")
    public String addArticulo(@RequestParam Long cliente, @RequestParam Long vehiculo, @RequestParam String fechaA,
            @RequestParam String observacionA, @RequestParam Long idArticulo, @RequestParam Double cantidad, @RequestParam Double precio, ModelMap modelo) {

        idVehiculo = vehiculo;
        fecha = fechaA;
        observacion = observacionA;
        Double totalServicio = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio

        Detalle detalle = new Detalle();
        Articulo articulo = new Articulo();

        Optional<Articulo> articuloRepo = articuloRepositorio.findById(idArticulo);

        if (articuloRepo.isPresent()) {
            articulo = articuloRepo.get();
        }

        detalle.setNombre(articulo.getNombre());
        detalle.setCodigo(articulo.getCodigo());
        detalle.setCantidad(cantidad);
        if (precio != 0.0) {
            detalle.setPrecio(precio);
        } else {
            detalle.setPrecio(articulo.getPrecio());
        }

        double total = detalle.getPrecio() * cantidad;
        double totalRedondeado = Math.round(total * 100.0) / 100.0;
        detalle.setTotal(totalRedondeado);
        detalle.setArticulo(articulo);

        detalleServicio.crearDetalle(detalle.getNombre(), detalle.getCodigo(), detalle.getCantidad(), detalle.getPrecio(), detalle.getTotal(), detalle.getArticulo(), "SERVICIO");

        detalle.setId(detalleServicio.buscarUltimo());

        detalles.add(detalle);

        for (Detalle det : detalles) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalServicio = totalServicio + det.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        modelo.put("idCliente", idCliente);
        modelo.put("cliente", clienteServicio.buscarCliente(cliente));
        modelo.put("idVehiculo", idVehiculo);
        modelo.put("vehiculo", vehiculoServicio.buscarVehiculo(vehiculo));
        modelo.put("fecha", fecha);
        modelo.put("observacion", observacion);
        modelo.put("totalServicio", totalServicioMiles);

        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.addAttribute("detalle", detalles);

        return "servicio_agregarArticulo.html";
    }

    @GetMapping("/borrarArticulo/{id}")
    public String borrarArticulo(@PathVariable Long id, ModelMap modelo) {

        int numeroInt = id.intValue();  //convierto en int id de detalle que llega para buscarlo y eliminarlo del array
        Double totalServicio = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio

        for (int i = 0; i < detalles.size(); i++) {
            if (detalles.get(i).getId() == numeroInt) {
                detalles.remove(i);
            }
        }

        detalleServicio.eliminarDetalle(id);

        for (Detalle det : detalles) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio

            totalServicio = totalServicio + det.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        modelo.put("idCliente", idCliente);
        modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
        modelo.put("idVehiculo", idVehiculo);
        modelo.put("vehiculo", vehiculoServicio.buscarVehiculo(idVehiculo));
        modelo.put("fecha", fecha);
        modelo.put("observacion", observacion);
        modelo.put("totalServicio", totalServicioMiles);

        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.addAttribute("detalle", detalles);

        return "servicio_agregarArticulo.html";
    }

    @GetMapping("/modificar/{id}")  //metodo para modificar CABECERA de Servicio
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        detallesM.clear();

        Servicio servicio = servicioServicio.buscarServicio(id);
        String totalServicio = convertirNumeroMiles(servicio.getTotal());

        detallesM.addAll(servicio.getDetalle());

        modelo.put("servicio", servicio);
        modelo.put("totalServicio", totalServicio);
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculosDominioAsc());

        return "servicio_modificar.html";
    }

    @PostMapping("/modifica/{id}")  //metodo para guardar en BD modificar CABECERA de Servicio
    public String modifica(@RequestParam Long id, @RequestParam Long idCliente, @RequestParam Long idVehiculo, @RequestParam String fecha,
            @RequestParam String observacion, @RequestParam Double total, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        servicioServicio.modificarServicio(id, idCliente, idVehiculo, fecha, observacion, total, detallesM, logueado.getId());

        String totalServicio = convertirNumeroMiles(total);

        modelo.put("servicio", servicioServicio.buscarServicio(id));
        modelo.put("fecha", fecha);
        modelo.put("totalServicio", totalServicio);
        modelo.put("exito", "Servicio MODIFICADO exitosamente");

        return "servicio_registrado.html";
    }

    @GetMapping("/modificarA/{id}") //metodo para modificar ARTICULOS de Servicio
    public String modificarA(@PathVariable Long id, ModelMap modelo) {

        auxBorrarArticulo = id;  //utilizada para pasar idServicio a metodo borrarArticuloA
        Double totalServicio = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio

        listaPersistida.clear();
        sumar.clear();
        restar.clear();

        Servicio servicio = servicioServicio.buscarServicio(id);

        listaPersistida.addAll(servicio.getDetalle());

        for (Detalle det : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalServicio = totalServicio + det.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        modelo.put("servicio", servicio);
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.put("listaPersistida", listaPersistida);
        modelo.put("totalServicioM", totalServicioMiles);
        modelo.put("totalServicio", totalServicio);

        return "servicio_modificarA.html";

    }

    @GetMapping("/borrarArticuloA/{id}")  //metodo para BORRAR ARTICULO de Servicio
    public String borrarArticuloA(@PathVariable Long id, ModelMap modelo) {

        Detalle detalle = new Detalle();
        Optional<Detalle> det = detalleRepositorio.findById(id);
        if (det.isPresent()) {
            detalle = det.get();
        }

        sumar.add(detalle);
        detalleServicio.modificarDetalle(id);

        int numeroInt = id.intValue();  //convierto en int id de detalle que llega para buscarlo y eliminarlo del array
        Double totalServicio = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio

        for (int i = 0; i < listaPersistida.size(); i++) {
            if (listaPersistida.get(i).getId() == numeroInt) {
                listaPersistida.remove(i);
            }
        }
        for (Detalle d : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalServicio = totalServicio + d.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        modelo.put("servicio", servicioServicio.buscarServicio(auxBorrarArticulo));  //variable 
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.put("listaPersistida", listaPersistida);
        modelo.put("totalServicioM", totalServicioMiles);
        modelo.put("totalServicio", totalServicio);

        return "servicio_modificarA.html";
    }

    @PostMapping("/agregarArticuloA")
    public String agregarArticuloA(@RequestParam Long idServicio, @RequestParam Long idArticulo, @RequestParam Double cantidad, @RequestParam Double precio, ModelMap modelo) {

        Double totalServicio = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio
        Detalle detalle = new Detalle();
        Articulo articulo = new Articulo();

        Optional<Articulo> articuloRepo = articuloRepositorio.findById(idArticulo);
        if (articuloRepo.isPresent()) {
            articulo = articuloRepo.get();
        }

        detalle.setNombre(articulo.getNombre());
        detalle.setCodigo(articulo.getCodigo());
        detalle.setCantidad(cantidad);
        if (precio != 0.0) {
            detalle.setPrecio(precio);
        } else {
            detalle.setPrecio(articulo.getPrecio());
        }

        double total = detalle.getPrecio() * cantidad;
        double totalRedondeado = Math.round(total * 100.0) / 100.0;
        detalle.setTotal(totalRedondeado);
        detalle.setArticulo(articulo);

        detalleServicio.crearDetalle(detalle.getNombre(), detalle.getCodigo(), detalle.getCantidad(), detalle.getPrecio(), detalle.getTotal(), detalle.getArticulo(), "SERVICIO");
        detalle.setId(detalleServicio.buscarUltimo());

        restar.add(detalle);
        listaPersistida.add(detalle);

        for (Detalle det : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalServicio = totalServicio + det.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        modelo.put("servicio", servicioServicio.buscarServicio(idServicio));
        modelo.put("listaPersistida", listaPersistida);
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.put("totalServicioM", totalServicioMiles);
        modelo.put("totalServicio", totalServicio);

        return "servicio_modificarA.html";
    }

    @PostMapping("/modificaA/{id}")   //metodo para guardar en BD modificacion (agregado de articulo)
    public String modificaA(@RequestParam Long idServicio, @RequestParam Long idCliente, @RequestParam Long idVehiculo, @RequestParam String fecha,
            @RequestParam String observacion, @RequestParam Double total, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        servicioServicio.modificarServicio(idServicio, idCliente, idVehiculo, fecha, observacion, total, listaPersistida, logueado.getId());

        if (!restar.isEmpty()) {
            for (Detalle detalle : restar) {                    //paso a Articulo para restar de stock
                articuloServicio.stockArtResta(detalle);
            }
        }

        if (!sumar.isEmpty()) {
            for (Detalle detalle : sumar) {                    //paso a Articulo para sumar de stock
                articuloServicio.stockArtSuma(detalle);
                detalleServicio.modificarDetalle(detalle.getId());
            }
        }

        String totalServicio = convertirNumeroMiles(total);

        modelo.put("servicio", servicioServicio.buscarServicio(idServicio));
        modelo.put("totalServicio", totalServicio);
        modelo.put("fecha", fecha);
        modelo.put("exito", "Servicio MODIFICADO exitosamente");

        return "servicio_registrado.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        Servicio servicio = servicioServicio.buscarServicio(id);
        String totalServicio = convertirNumeroMiles(servicio.getTotal());

        modelo.put("servicio", servicio);
        modelo.put("totalServicio", totalServicio);

        return "servicio_eliminar.html";

    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        Servicio servicio = servicioServicio.buscarServicio(id);

        for (Detalle detalle : servicio.getDetalle()) {                    //paso a Articulo para sumar de stock
            articuloServicio.stockArtSuma(detalle);
            detalleServicio.modificarDetalle(detalle.getId());

        }

        servicioServicio.eliminarServicio(id);

        modelo.addAttribute("servicios", servicioServicio.buscarServiciosIdDesc());
        modelo.put("exito", "Servicio ELIMINADO exitosamente");

        return "servicio_listar.html";
    }

    @GetMapping("/vehiculo/{dominio}")
    public String servicio(@PathVariable String dominio, ModelMap modelo) {

        modelo.addAttribute("servicios", servicioServicio.buscarServicioDominio(dominio));

        return "servicio_dominio.html";
    }

    @GetMapping("/listarPdf")
    public String listaPdf(ModelMap modelo) {

        modelo.addAttribute("servicios", servicioServicio.buscarServicios());

        return "servicio_listarPdf";
    }

    public String convertirNumeroMiles(Double num) {    //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

}

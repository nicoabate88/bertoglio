package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Articulo;
import com.bertoglio.portalbertoglio.entidades.Detalle;
import com.bertoglio.portalbertoglio.entidades.Presupuesto;
import com.bertoglio.portalbertoglio.entidades.Servicio;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.repositorios.ArticuloRepositorio;
import com.bertoglio.portalbertoglio.servicios.ArticuloServicio;
import com.bertoglio.portalbertoglio.servicios.ClienteServicio;
import com.bertoglio.portalbertoglio.servicios.DetalleServicio;
import com.bertoglio.portalbertoglio.servicios.PresupuestoServicio;
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
@RequestMapping("/presupuesto")
@PreAuthorize("hasRole('ROLE_admin')")
public class PresupuestoControlador {

    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private VehiculoServicio vehiculoServicio;
    @Autowired
    private ArticuloServicio articuloServicio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;
    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private PresupuestoServicio presupuestoServicio;
    @Autowired
    private ServicioServicio servicioServicio;

    ArrayList<Detalle> detalles = new ArrayList();  //ArrayList para crear los detalles que se guardaran en el servicio
    ArrayList<Detalle> detallesM = new ArrayList();  //ArrayList para modificar los detalles que se guardaran en la modificacion del servicio
    ArrayList<Detalle> listaPersistida = new ArrayList(); //ArrayList de detalles de articulos almacenado en la BD (proximo a ModificarA agregar articulos)

    Long auxBorrarArticulo;  //variable utilizada para pasar idPresupuesto desde metodo modificaA a borrarArticuloA

    Long idCliente;
    Long idVehiculo;
    String fecha;
    String observacion;

    @GetMapping("/registrar")
    public String registrarCliente(ModelMap modelo) {

        detalles.clear();

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());

        return "presupuesto_registrarCliente.html";
    }

    @PostMapping("/registrarC")
    public String procesarOpcionSeleccionada(@RequestParam("cliente") Long cliente, ModelMap modelo) {

        idCliente = cliente;

        modelo.put("idCliente", idCliente);
        modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculoIdCliente(idCliente));
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());

        return "presupuesto_registrar.html";
    }

    @GetMapping("/registro")
    public String registro(HttpSession session, ModelMap modelo) throws ParseException {

        Double total = 0.0;
        for (Detalle detalle : detalles) {
            total = total + detalle.getTotal();
        }

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        presupuestoServicio.crearPresupuesto(idCliente, idVehiculo, fecha, observacion, total, detalles, logueado.getId());

        Long id = presupuestoServicio.buscarUltimo();
        String totalMiles = convertirNumeroMiles(total);

        modelo.put("presupuesto", presupuestoServicio.buscarPresupuesto(id));
        modelo.put("fecha", fecha);
        modelo.put("totalServicio", totalMiles);
        modelo.put("exito", "Presupuesto REGISTRADO exitosamente");

        return "presupuesto_registrado.html";

    }

    @PostMapping("/addArticulo")
    public String addArticulo(@RequestParam Long cliente, @RequestParam Long vehiculo, @RequestParam String fechaA,
            @RequestParam String observacionA, @RequestParam Long idArticulo, @RequestParam Double cantidad, @RequestParam Double precio, ModelMap modelo) {

        idCliente = cliente;
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

        detalleServicio.crearDetalle(detalle.getNombre(), detalle.getCodigo(), detalle.getCantidad(), detalle.getPrecio(), detalle.getTotal(), detalle.getArticulo(), "PRESUPUESTO");

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

        return "presupuesto_agregarArticulo.html";
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

        return "presupuesto_agregarArticulo.html";
    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("presupuestos", presupuestoServicio.buscarPresupuestoIdDesc());

        return "presupuesto_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdAsc(ModelMap modelo) {

        modelo.addAttribute("presupuestos", presupuestoServicio.buscarPresupuestos());

        return "presupuesto_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("presupuestos", presupuestoServicio.buscarPresupuestoNombreAsc());

        return "presupuesto_listar.html";
    }

    @GetMapping("/listarFechaDesc")
    public String listarFechaDesc(ModelMap modelo) {

        modelo.addAttribute("presupuestos", presupuestoServicio.buscarPresupuestoFechaDesc());

        return "presupuesto_listar.html";
    }

    @GetMapping("/modificarA/{id}") //metodo para modificar ARTICULOS de Servicio
    public String modificarA(@PathVariable Long id, ModelMap modelo) {

        auxBorrarArticulo = id;  //utilizada para pasar idServicio a metodo borrarArticuloA
        Double totalServicio = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio

        listaPersistida.clear();

        Presupuesto presupuesto = presupuestoServicio.buscarPresupuesto(id);

        listaPersistida.addAll(presupuesto.getDetalle());

        for (Detalle det : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalServicio = totalServicio + det.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        modelo.put("presupuesto", presupuesto);
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.put("listaPersistida", listaPersistida);
        modelo.put("totalServicioM", totalServicioMiles);
        modelo.put("totalServicio", totalServicio);

        return "presupuesto_modificarA.html";

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

        detalleServicio.crearDetalle(detalle.getNombre(), detalle.getCodigo(), detalle.getCantidad(), detalle.getPrecio(), detalle.getTotal(), detalle.getArticulo(), "PRESUPUESTO");
        detalle.setId(detalleServicio.buscarUltimo());

        listaPersistida.add(detalle);

        for (Detalle det : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalServicio = totalServicio + det.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        modelo.put("presupuesto", presupuestoServicio.buscarPresupuesto(idServicio));
        modelo.put("listaPersistida", listaPersistida);
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.put("totalServicioM", totalServicioMiles);
        modelo.put("totalServicio", totalServicio);

        return "presupuesto_modificarA.html";
    }

    @GetMapping("/borrarArticuloA/{id}")  //metodo para BORRAR ARTICULO de Servicio
    public String borrarArticuloA(@PathVariable Long id, ModelMap modelo) {

        int numeroInt = id.intValue();  //convierto en int id de detalle que llega para buscarlo y eliminarlo del array
        Double totalServicio = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio

        for (int i = 0; i < listaPersistida.size(); i++) {
            if (listaPersistida.get(i).getId() == numeroInt) {
                listaPersistida.remove(i);
            }
        }
        for (Detalle det : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalServicio = totalServicio + det.getTotal();
        }

        String totalServicioMiles = convertirNumeroMiles(totalServicio);

        detalleServicio.modificarDetalle(id);

        modelo.put("presupuesto", presupuestoServicio.buscarPresupuesto(auxBorrarArticulo));  //variable 
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.put("listaPersistida", listaPersistida);
        modelo.put("totalServicioM", totalServicioMiles);
        modelo.put("totalServicio", totalServicio);

        return "presupuesto_modificarA.html";
    }

    @PostMapping("/modificaA/{id}")   //metodo para guardar en BD modificacion (agregado de articulo)
    public String modificaA(@RequestParam Long idServicio, @RequestParam Long idCliente, @RequestParam Long idVehiculo, @RequestParam String fecha,
            @RequestParam String observacion, @RequestParam Double total, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        presupuestoServicio.modificarPresupuesto(idServicio, idCliente, idVehiculo, fecha, observacion, total, listaPersistida, logueado.getId());

        String totalServicio = convertirNumeroMiles(total);

        modelo.put("presupuesto", presupuestoServicio.buscarPresupuesto(idServicio));
        modelo.put("fecha", fecha);
        modelo.put("totalServicio", totalServicio);
        modelo.put("exito", "Presupuesto MODIFICADO exitosamente");

        return "presupuesto_registrado.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Presupuesto presupuesto = presupuestoServicio.buscarPresupuesto(id);
        String totalMiles = convertirNumeroMiles(presupuesto.getTotal());

        modelo.put("presupuesto", presupuesto);
        modelo.put("totalServicio", totalMiles);

        return "presupuesto_mostrar.html";
    }

    @GetMapping("/mostrarPdf/{id}")
    public String mostrarPdf(@PathVariable Long id, ModelMap modelo) {

        Presupuesto presupuesto = presupuestoServicio.buscarPresupuesto(id);
        String totalMiles = convertirNumeroMiles(presupuesto.getTotal());

        modelo.put("presupuesto", presupuesto);
        modelo.put("totalServicio", totalMiles);

        return "presupuesto_mostrarPdf.html";

    }

    @GetMapping("/modificar/{id}")  //metodo para modificar CABECERA de Servicio
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        detallesM.clear();

        Presupuesto presupuesto = presupuestoServicio.buscarPresupuesto(id);
        String totalServicio = convertirNumeroMiles(presupuesto.getTotal());

        detallesM.addAll(presupuesto.getDetalle());

        modelo.put("presupuesto", presupuesto);
        modelo.put("totalServicio", totalServicio);
        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
        modelo.addAttribute("vehiculos", vehiculoServicio.buscarVehiculosDominioAsc());

        return "presupuesto_modificar.html";
    }

    @PostMapping("/modifica/{id}")  //metodo para guardar en BD modificar CABECERA de Servicio
    public String modifica(@RequestParam Long id, @RequestParam Long idCliente, @RequestParam Long idVehiculo, @RequestParam String fecha,
            @RequestParam String observacion, @RequestParam Double total, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        presupuestoServicio.modificarPresupuesto(id, idCliente, idVehiculo, fecha, observacion, total, detallesM, logueado.getId());

        String totalServicio = convertirNumeroMiles(total);

        modelo.put("presupuesto", presupuestoServicio.buscarPresupuesto(id));
        modelo.put("fecha", fecha);
        modelo.put("totalServicio", totalServicio);
        modelo.put("exito", "Presupuesto MODIFICADO exitosamente");

        return "presupuesto_registrado.html";
    }

    @GetMapping("/modificarS/{id}")
    public String modificarS(@PathVariable Long id, ModelMap modelo) {

        Presupuesto presupuesto = presupuestoServicio.buscarPresupuesto(id);
        String totalServicio = convertirNumeroMiles(presupuesto.getTotal());

        modelo.put("presupuesto", presupuesto);
        modelo.put("totalServicio", totalServicio);

        return "presupuesto_modificarS.html";

    }

    @GetMapping("/modificaS/{id}")
    public String modificaS(@PathVariable Long id, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        presupuestoServicio.modificarPresupuestoS(id, logueado.getId());

        Long idServicio = servicioServicio.buscarUltimo();
        Servicio servicio = servicioServicio.buscarServicio(idServicio);
        String totalMiles = convertirNumeroMiles(servicio.getTotal());

        modelo.put("servicio", servicio);
        modelo.put("totalServicio", totalMiles);
        modelo.put("exito", "Servicio REGISTRADO exitosamente");

        return "servicio_registrado.html";

    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        Presupuesto presupuesto = presupuestoServicio.buscarPresupuesto(id);
        String totalServicio = convertirNumeroMiles(presupuesto.getTotal());

        modelo.put("presupuesto", presupuesto);
        modelo.put("totalServicio", totalServicio);

        return "presupuesto_eliminar.html";

    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        presupuestoServicio.eliminarPresupuesto(id);

        modelo.addAttribute("presupuestos", presupuestoServicio.buscarPresupuestos());
        modelo.put("exito", "Presupuesto ELIMINADO exitosamente");

        return "presupuesto_listar.html";
    }

    public String convertirNumeroMiles(Double num) {    //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

}

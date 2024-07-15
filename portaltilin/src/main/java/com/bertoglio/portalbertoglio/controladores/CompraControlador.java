package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Articulo;
import com.bertoglio.portalbertoglio.entidades.Compra;
import com.bertoglio.portalbertoglio.entidades.Detalle;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.repositorios.ArticuloRepositorio;
import com.bertoglio.portalbertoglio.servicios.ArticuloServicio;
import com.bertoglio.portalbertoglio.servicios.CompraServicio;
import com.bertoglio.portalbertoglio.servicios.DetalleServicio;
import com.bertoglio.portalbertoglio.servicios.ProveedorServicio;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
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
@RequestMapping("/compra")
@PreAuthorize("hasRole('ROLE_admin')")
public class CompraControlador {

    @Autowired
    private CompraServicio compraServicio;
    @Autowired
    private ProveedorServicio proveedorServicio;
    @Autowired
    private DetalleServicio detalleServicio;
    @Autowired
    private ArticuloServicio articuloServicio;
    @Autowired
    private ArticuloRepositorio articuloRepositorio;

    ArrayList<Detalle> detalles = new ArrayList();
    ArrayList<Detalle> listaPersistida = new ArrayList();
    Usuario logueado;
    Long idProveedor;
    String fecha;
    String tipoComprobante;
    Integer numeroComprobante;
    Double importe;
    String observacion;
    Long idCompraB;

    @GetMapping("/registrar")
    public String registrar(HttpSession session, ModelMap modelo) {

        logueado = (Usuario) session.getAttribute("usuariosession");

        detalles.clear();

        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresNombreAsc());
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());

        return "compra_registrar.html";
    }

    @PostMapping("/addArticulo")
    public String addArticulo(@RequestParam Long id, @RequestParam String fechaC, @RequestParam String tipo,
            @RequestParam Integer numero, @RequestParam Double total, @RequestParam(required = false) String obs,
            @RequestParam(required = false) Long idArticulo, @RequestParam(required = false) Double cantidad,
            @RequestParam(required = false) Double precio, ModelMap modelo) throws ParseException {

        idProveedor = id;
        fecha = fechaC;
        tipoComprobante = tipo;
        numeroComprobante = numero;
        importe = total;
        observacion = obs;

        if (idArticulo == null) {

            compraServicio.crearCompra(id, fechaC, obs, tipo, numero, total, detalles, logueado.getId());

            Long idCompra = compraServicio.buscarUltimo();

            String totalCompra = convertirNumeroMiles(total);

            modelo.put("compra", compraServicio.buscarCompra(idCompra));
            modelo.put("importe", totalCompra);
            modelo.put("fecha", fechaC);
            modelo.put("exito", "Compra REGISTRADA exitosamente");

            return "compra_registrado.html";

        } else {

            Articulo articulo = articuloServicio.buscarArticulo(idArticulo);
            Detalle detalle = new Detalle();
            detalle.setNombre(articulo.getNombre());
            detalle.setCodigo(articulo.getCodigo());
            detalle.setCantidad(cantidad);
            detalle.setPrecio(precio);
            detalle.setTotal(cantidad * precio);
            detalle.setArticulo(articulo);

            detalleServicio.crearDetalle(detalle.getNombre(), detalle.getCodigo(), detalle.getCantidad(), detalle.getPrecio(), detalle.getTotal(), detalle.getArticulo(), "COMPRA");
            detalle.setId(detalleServicio.buscarUltimo());

            detalles.add(detalle);

            String totalSuma = convertirNumeroMiles(total);

            modelo.put("proveedor", proveedorServicio.buscarProveedor(id));
            modelo.put("fecha", fechaC);
            modelo.put("importe", totalSuma);
            modelo.put("tipo", tipo);
            modelo.put("numero", numero);
            modelo.put("observacion", obs);
            modelo.put("total", total);
            modelo.addAttribute("lista", detalles);
            modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());

            return "compra_agregarArticulo.html";

        }
    }

    @GetMapping("/borrarArticulo/{id}")
    public String borrarArticulo(@PathVariable Long id, ModelMap modelo) {

        int numeroInt = id.intValue();  //convierto en int id de detalle que llega para buscarlo y eliminarlo del array

        for (int i = 0; i < detalles.size(); i++) {
            if (detalles.get(i).getId() == numeroInt) {
                detalles.remove(i);
            }
        }

        detalleServicio.eliminarDetalle(id);
        String totalSuma = convertirNumeroMiles(importe);

        modelo.put("proveedor", proveedorServicio.buscarProveedor(idProveedor));
        modelo.put("fecha", fecha);
        modelo.put("importe", totalSuma);
        modelo.put("tipo", tipoComprobante);
        modelo.put("numero", numeroComprobante);
        modelo.put("observacion", observacion);
        modelo.put("total", importe);
        modelo.addAttribute("lista", detalles);
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());

        return "compra_agregarArticulo.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("compras", compraServicio.buscarCompraIdDesc());

        return "compra_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdAsc(ModelMap modelo) {

        modelo.addAttribute("compras", compraServicio.bucarCompras());

        return "compra_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("compras", compraServicio.buscarCompraNombreAsc());

        return "compra_listar.html";
    }

    @GetMapping("/listarTipoAsc")
    public String listarTipoAsc(ModelMap modelo) {

        modelo.addAttribute("compras", compraServicio.buscarCompraTipoAsc());

        return "compra_listar.html";
    }

    @GetMapping("/listarNumeroAsc")
    public String listarNumeroAsc(ModelMap modelo) {

        modelo.addAttribute("compras", compraServicio.buscarCompraNumeroAsc());

        return "compra_listar.html";
    }

    @GetMapping("/listarImporteAsc")
    public String listarImporteAsc(ModelMap modelo) {

        modelo.addAttribute("compras", compraServicio.buscarCompraImporteAsc());

        return "compra_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Compra compra = compraServicio.buscarCompra(id);
        String total = convertirNumeroMiles(compra.getImporte());

        modelo.put("compra", compra);
        modelo.put("importe", total);

        return "compra_mostrar.html";
    }

    private String convertirNumeroMiles(Double num) {   //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

    @GetMapping("/modificar/{id}")
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        modelo.addAttribute("proveedores", proveedorServicio.bucarProveedores());
        modelo.put("compra", compraServicio.buscarCompra(id));

        return "compra_modificar.html";
    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam Long idProveedor, @RequestParam String tipoComprobante,
            @RequestParam String fecha, @RequestParam Integer numeroComprobante, @RequestParam Double importe,
            @RequestParam(required = false) String observacion, HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String total = convertirNumeroMiles(importe);

        compraServicio.modificarCompra(id, idProveedor, fecha, observacion, tipoComprobante, numeroComprobante, importe, logueado.getId());

        modelo.put("compra", compraServicio.buscarCompra(id));
        modelo.put("fecha", fecha);
        modelo.put("importe", total);
        modelo.put("exito", "Compra MODIFICADA exitosamente");

        return "compra_registrado.html";
    }

    @GetMapping("/modificarA/{id}")
    public String modificarA(@PathVariable Long id, ModelMap modelo) {

        idCompraB = id;     //idCompra para pasar a metodo borrarArticuloM en caso de ejecutarse
        listaPersistida.clear();
        Compra compra = compraServicio.buscarCompra(id);
        listaPersistida.addAll(compra.getDetalle());

        modelo.put("compra", compra);
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.addAttribute("detalles", listaPersistida);

        return "compra_modificarA.html";
    }

    @GetMapping("/modificaA/{id}")
    public String modificaA(@PathVariable Long id, ModelMap modelo) {

        compraServicio.modificarArticuloCompra(id, listaPersistida);

        Compra compra = compraServicio.buscarCompra(id);
        String total = convertirNumeroMiles(compra.getImporte());

        modelo.put("compra", compra);
        modelo.put("importe", total);
        modelo.put("exito", "Compra MODIFICADA exitosamente");

        return "compra_mostrar.html";
    }

    @PostMapping("/addArticuloM")
    public String agregarArticuloA(@RequestParam Long idCompra, @RequestParam Long idArticulo, @RequestParam Double cantidad, @RequestParam Double precio, ModelMap modelo) {

        Articulo articulo = articuloServicio.buscarArticulo(idArticulo);
        Detalle detalle = new Detalle();
        detalle.setNombre(articulo.getNombre());
        detalle.setCodigo(articulo.getCodigo());
        detalle.setCantidad(cantidad);
        detalle.setPrecio(precio);
        detalle.setTotal(cantidad * precio);
        detalle.setArticulo(articulo);

        detalleServicio.crearDetalle(detalle.getNombre(), detalle.getCodigo(), detalle.getCantidad(), detalle.getPrecio(), detalle.getTotal(), detalle.getArticulo(), "COMPRA");
        articuloServicio.actualizarStockCompra(idArticulo, precio, cantidad);
        detalle.setId(detalleServicio.buscarUltimo());

        listaPersistida.add(detalle);

        modelo.put("compra", compraServicio.buscarCompra(idCompra));
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.addAttribute("detalles", listaPersistida);

        return "compra_modificarA.html";
    }

    @GetMapping("/borrarArticuloM/{id}")
    public String borrarArticuloM(@PathVariable Long id, ModelMap modelo) {

        Detalle detalle = new Detalle();

        int numeroInt = id.intValue();  //convierto en int id de detalle que llega para buscarlo y eliminarlo del array

        for (int i = 0; i < listaPersistida.size(); i++) {
            if (listaPersistida.get(i).getId() == numeroInt) {
                detalle.setNombre(listaPersistida.get(i).getNombre());
                detalle.setCantidad(listaPersistida.get(i).getCantidad());
                detalle.setPrecio(listaPersistida.get(i).getPrecio());
                listaPersistida.remove(i);
            }
        }

        articuloServicio.stockArtResta(detalle);
        detalleServicio.modificarDetalle(id);

        modelo.put("compra", compraServicio.buscarCompra(idCompraB));
        modelo.addAttribute("articulos", articuloServicio.buscarArticuloNombreAsc());
        modelo.addAttribute("detalles", listaPersistida);

        return "compra_modificarA.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        Compra compra = compraServicio.buscarCompra(id);
        String total = convertirNumeroMiles(compra.getImporte());

        modelo.put("importe", total);
        modelo.put("compra", compraServicio.buscarCompra(id));

        return "compra_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        compraServicio.eliminarCompra(id);

        modelo.putIfAbsent("exito", "Compra ELIMINADA exitosamente");
        modelo.addAttribute("compras", compraServicio.buscarCompraIdDesc());

        return "compra_listar.html";
    }

}

package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Pago;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.entidades.Valor;
import com.bertoglio.portalbertoglio.servicios.CajaServicio;
import com.bertoglio.portalbertoglio.servicios.PagoServicio;
import com.bertoglio.portalbertoglio.servicios.ProveedorServicio;
import com.bertoglio.portalbertoglio.servicios.ValorServicio;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
@RequestMapping("/pago")
@PreAuthorize("hasRole('ROLE_admin')")
public class PagoControlador {

    @Autowired
    private PagoServicio pagoServicio;
    @Autowired
    private ProveedorServicio proveedorServicio;
    @Autowired
    private ValorServicio valorServicio;
    @Autowired
    private CajaServicio cajaServicio;

    Long idProveedorPago;
    String observacion;
    ArrayList<Valor> valores = new ArrayList();
    ArrayList<Valor> valoresM = new ArrayList();
    ArrayList<Valor> listaPersistida = new ArrayList();
    Long aux;
    int numero = 0;
    Long socio = (long) numero;

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo) {

        valores.clear();

        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresNombreAsc());
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.addAttribute("cheques", valorServicio.buscarValorCartera());

        return "pago_registrar.html";
    }

    @GetMapping("/registro")
    public String registro(HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Double totalPago = 0.0;

        for (Valor val : valores) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            totalPago = totalPago + val.getImporte();
        }

        pagoServicio.crearPago(idProveedorPago, observacion, totalPago, valores, logueado.getId());

        String total = convertirNumeroMiles(totalPago);

        Long id = pagoServicio.buscarUltimo();
        modelo.put("pago", pagoServicio.buscarPago(id));
        modelo.put("totalPago", total);
        modelo.addAttribute("valores", valores);
        modelo.put("exito", "Pago REGISTRADO exitosamente");

        return "pago_registrado.html";
    }

    @PostMapping("/agregarValor")
    public String agregarValor(@RequestParam Long idProveedor, @RequestParam(required = false) String observacionP,
            @RequestParam(required = false) String tipoValor, @RequestParam(required = false) Double importe, @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) String fechaP, @RequestParam(required = false) Long idValor, ModelMap modelo) throws ParseException {

        Double totalPago = 0.0;
        String nombre = "PAGO";
        idProveedorPago = idProveedor;
        observacion = observacionP;

        if (!tipoValor.equalsIgnoreCase("CHEQUE")) {

            valorServicio.crearValor(tipoValor, "PAGADO", importe * -1, numero, fechaP, nombre, socio);

            Long id = valorServicio.buscarUltimo();

            Valor valor = valorServicio.buscarValor(id);
            Double absoluto = Math.abs(valor.getImporte());
            valor.setImporte(absoluto);

            valores.add(valor);

        } else {

            Valor valor = valorServicio.buscarValor(idValor);

            valorServicio.crearValor(valor.getTipoValor(), "PAGADO", valor.getImporte() * -1, valor.getNumero(), valor.getObservacion(), nombre, idValor);
            valorServicio.modificarValor(idValor);

            Long id = valorServicio.buscarUltimo();

            Valor cheque = valorServicio.buscarValor(id);
            Double absoluto = Math.abs(cheque.getImporte());
            cheque.setImporte(absoluto);

            valores.add(cheque);

        }

        for (Valor val : valores) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            totalPago = totalPago + val.getImporte();
        }

        String totalPagoMiles = convertirNumeroMiles(totalPago);

        modelo.put("idProveedor", idProveedorPago);
        modelo.put("observacion", observacion);
        modelo.put("proveedor", proveedorServicio.buscarProveedor(idProveedor));
        modelo.put("totalPago", totalPagoMiles);
        modelo.addAttribute("valores", valores);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.addAttribute("cheques", valorServicio.buscarValorCartera());

        return "pago_agregarValor.html";

    }

    @GetMapping("/borrarValor/{id}")
    public String borrarValor(@PathVariable Long id, ModelMap modelo) {

        int numeroInt = id.intValue();  //convierto en int id de Valor que llega para buscarlo y eliminarlo del array
        Double totalPago = 0.0; //variable para ir almacenado total de valores pertenecientes a un mismo Pago

        for (int i = 0; i < valores.size(); i++) {
            if (valores.get(i).getId() == numeroInt) {
                valores.remove(i);
            }
        }
        Valor valor = valorServicio.buscarValor(id);

        if (!valor.getTipoValor().equalsIgnoreCase("CHEQUE")) {

            valorServicio.eliminarValor(id);

        } else {

            valorServicio.modificarEstadoValor(valor.getIdSocio());
            valorServicio.eliminarValor(id);
        }

        for (Valor val : valores) {    //bucle para sumar total de Valores para pasar a HTML como total de Pago: totalPago
            totalPago = totalPago + val.getImporte();
        }

        String totalPagoMiles = convertirNumeroMiles(totalPago);

        modelo.put("idProveedor", idProveedorPago);
        modelo.put("observacion", observacion);
        modelo.put("proveedor", proveedorServicio.buscarProveedor(idProveedorPago));
        modelo.put("totalPago", totalPagoMiles);
        modelo.addAttribute("valores", valores);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.addAttribute("cheques", valorServicio.buscarValorCartera());

        return "pago_agregarValor.html";
    }

    @GetMapping("/modificar/{id}")  //metodo para modificar CABECERA de Pago
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        valoresM.clear();

        Pago pago = pagoServicio.buscarPago(id);

        String totalPago = convertirNumeroMiles(pago.getImporte());

        valoresM.addAll(pago.getValor());

        modelo.put("pago", pago);
        modelo.put("totalPago", totalPago);
        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresNombreAsc());

        return "pago_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam Long idProveedor,
            @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        pagoServicio.modificarPago(id, idProveedor, observacion, logueado.getId());

        Pago pago = pagoServicio.buscarPago(id);

        String totalPago = convertirNumeroMiles(pago.getImporte());

        modelo.put("pago", pago);
        modelo.put("totalPago", totalPago);
        modelo.put("exito", "Pago MODIFICADO exitosamente");

        return "pago_mostrar.html";
    }

    @GetMapping("/modificarV/{id}") //metodo para modificar Valores de Pago
    public String modificarV(@PathVariable Long id, ModelMap modelo) {

        aux = id;  //utilizada para pasar idPago a metodo borrarValorV
        Double total = 0.0; //variable para ir almacenado total de valores pertenecientes a un mismo Pago

        listaPersistida.clear();

        Pago pago = pagoServicio.buscarPago(id);

        listaPersistida.addAll(pago.getValor());

        for (Valor val : listaPersistida) {    //bucle para sumar total de Valores para pasar a HTML como total de Pago: totalPago
            total = total + Math.abs(val.getImporte());
        }

        String totalPago = convertirNumeroMiles(total);

        modelo.put("pago", pago);
        modelo.put("totalPago", totalPago);
        modelo.addAttribute("valores", listaPersistida);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.addAttribute("cheques", valorServicio.buscarValorCartera());

        return "pago_modificarV.html";

    }

    @GetMapping("/modificaV/{id}")
    public String modificaV(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        pagoServicio.modificarPagoV(id, listaPersistida, logueado.getId());

        Double total = 0.0;

        for (Valor valor : listaPersistida) {
            total = total + Math.abs(valor.getImporte());
        }

        String totalPago = convertirNumeroMiles(total);

        modelo.put("pago", pagoServicio.buscarPago(id));
        modelo.put("totalPago", totalPago);
        modelo.put("exito", "Pago MODIFICADO exitosamente");

        return "pago_mostrar.html";
    }

    @PostMapping("/agregarValorV/{id}")
    public String agregarValorV(@RequestParam Long id, @RequestParam(required = false) String tipoValor,
            @RequestParam(required = false) Double importe, @RequestParam(required = false) Integer numero,
            @RequestParam(required = false) String fechaP, @RequestParam(required = false) Long idValor,
            ModelMap modelo) throws ParseException {

        Double total = 0.0;
        String nombre = "PAGO";

        if (!tipoValor.equalsIgnoreCase("CHEQUE")) {

            valorServicio.crearValor(tipoValor, "PAGADO", importe * -1, numero, fechaP, nombre, socio);

            Long idV = valorServicio.buscarUltimo();

            Valor valor = valorServicio.buscarValor(idV);

            listaPersistida.add(valor);

        } else {

            Valor valor = valorServicio.buscarValor(idValor);

            valorServicio.crearValor(valor.getTipoValor(), "PAGADO", valor.getImporte() * -1, valor.getNumero(), valor.getObservacion(), nombre, idValor);
            valorServicio.modificarValor(idValor);

            Long idV = valorServicio.buscarUltimo();

            Valor cheque = valorServicio.buscarValor(idV);

            listaPersistida.add(valor);

        }

        for (Valor val : listaPersistida) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            total = total + Math.abs(val.getImporte());
        }

        String totalPago = convertirNumeroMiles(total);

        modelo.put("pago", pagoServicio.buscarPago(id));
        modelo.put("totalPago", totalPago);
        modelo.addAttribute("valores", listaPersistida);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.addAttribute("cheques", valorServicio.buscarValorCartera());

        return "pago_modificarV.html";
    }

    @GetMapping("/borrarValorV/{id}")
    public String borrarValorV(@PathVariable Long id, ModelMap modelo) {

        int numeroInt = id.intValue();  //convierto en int id de Valor que llega para buscarlo y eliminarlo del array
        Double total = 0.0; //variable para ir almacenado total de Valores pertenecientes a un mismo Recibo

        for (int i = 0; i < listaPersistida.size(); i++) {
            if (listaPersistida.get(i).getId() == numeroInt) {
                listaPersistida.remove(i);
            }
        }

        Valor valor = valorServicio.buscarValor(id);

        if (!valor.getTipoValor().equalsIgnoreCase("CHEQUE")) {

            valorServicio.eliminarValor(id);

        } else {

            valorServicio.modificarEstadoValor(valor.getIdSocio());
            valorServicio.eliminarValor(id);
        }

        for (Valor val : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            total = total + Math.abs(val.getImporte());
        }

        String totalPago = convertirNumeroMiles(total);

        modelo.put("pago", pagoServicio.buscarPago(aux));
        modelo.put("totalPago", totalPago);
        modelo.addAttribute("valores", listaPersistida);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.addAttribute("cheques", valorServicio.buscarValorCartera());

        return "pago_modificarV.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("pagos", pagoServicio.buscarPagosIdDesc());

        return "pago_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdAsc(ModelMap modelo) {

        modelo.addAttribute("pagos", pagoServicio.buscarPagos());

        return "pago_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("pagos", pagoServicio.buscarPagosNombreAsc());

        return "pago_listar.html";
    }

    @GetMapping("/listarImporteDesc")
    public String listarImporteDesc(ModelMap modelo) {

        modelo.addAttribute("pagos", pagoServicio.buscarPagosImporteDesc());

        return "pago_listar.html";
    }

    @GetMapping("/listarFechaDesc")
    public String listarFechaDesc(ModelMap modelo) {

        modelo.addAttribute("pagos", pagoServicio.buscarPagosFechaDesc());

        return "pago_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Pago pago = pagoServicio.buscarPago(id);

        String total = convertirNumeroMiles(pago.getImporte());

        modelo.put("pago", pago);
        modelo.put("totalPago", total);

        return "pago_mostrar.html";
    }

    @GetMapping("/mostrarPdf/{id}")
    public String mostrarPdf(@PathVariable Long id, ModelMap modelo) {

        Pago pago = pagoServicio.buscarPago(id);

        String total = convertirNumeroMiles(pago.getImporte());

        modelo.put("pago", pago);
        modelo.put("total", total);

        return "pago_mostrarPdf.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("pago", pagoServicio.buscarPago(id));

        return "pago_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        pagoServicio.eliminarPago(id);

        modelo.addAttribute("pagos", pagoServicio.buscarPagos());
        modelo.put("exito", "Pago ELIMINADO exitosamente");

        return "pago_listar.html";

    }

    @GetMapping("/cancelar")
    public String cancela(ModelMap modelo) {

        for (Valor v : valores) {
            if (!v.getTipoValor().equalsIgnoreCase("CHEQUE")) {

                valorServicio.eliminarValor(v.getId());

            } else {

                valorServicio.modificarEstadoValor(v.getIdSocio());
                valorServicio.eliminarValor(v.getId());
            }
        }

        valores.clear();

        modelo.addAttribute("proveedores", proveedorServicio.buscarProveedoresNombreAsc());
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.addAttribute("cheques", valorServicio.buscarValorCartera());

        return "pago_registrar.html";
    }

    private String convertirNumeroMiles(Double num) {   //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
        return formato.parse(fecha);
    }

}

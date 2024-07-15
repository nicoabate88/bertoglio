package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Recibo;
import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.entidades.Valor;
import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.servicios.CajaServicio;
import com.bertoglio.portalbertoglio.servicios.ClienteServicio;
import com.bertoglio.portalbertoglio.servicios.ReciboServicio;
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
@RequestMapping("/recibo")
@PreAuthorize("hasRole('ROLE_admin')")
public class ReciboControlador {

    @Autowired
    private ReciboServicio reciboServicio;
    @Autowired
    private ClienteServicio clienteServicio;
    @Autowired
    private ValorServicio valorServicio;
    @Autowired
    private CajaServicio cajaServicio;

    Long idClienteRecibo;
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

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "recibo_registrar.html";
    }

    @GetMapping("/registro")
    public String registro(HttpSession session, ModelMap modelo) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        Double totalRecibo = 0.0;

        for (Valor val : valores) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            totalRecibo = totalRecibo + val.getImporte();
        }

        reciboServicio.crearRecibo(idClienteRecibo, observacion, totalRecibo, logueado.getId(), valores);

        String total = convertirNumeroMiles(totalRecibo);

        Long id = reciboServicio.buscarUltimo();
        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.put("totalRecibo", total);
        modelo.addAttribute("valores", valores);
        modelo.put("exito", "Recibo REGISTRADO exitosamente");

        return "recibo_registrado.html";
    }

    @PostMapping("/agregarValor")
    public String agregarValor(@RequestParam Long idCliente, @RequestParam(required = false) String observacionR,
            @RequestParam String tipoValor, @RequestParam Double importe, @RequestParam Integer numero,
            @RequestParam String fechaV, ModelMap modelo) throws ParseException {

        idClienteRecibo = idCliente;
        observacion = observacionR;
        Double totalRecibo = 0.0;
        String nombre = "RECIBO";
        String estado = "COBRADO";
        if (tipoValor.equalsIgnoreCase("CHEQUE")) {
            estado = "CARTERA";
        }

        valorServicio.crearValor(tipoValor, estado, importe, numero, fechaV, nombre, socio);

        Date fechaOrden = convertirFecha(fechaV);

        Valor valor = new Valor();

        valor.setId(valorServicio.buscarUltimo());
        valor.setTipoValor(tipoValor);
        valor.setImporte(importe);
        valor.setNumero(numero);
        valor.setFecha(fechaOrden);
        valor.setObservacion(fechaV);

        valores.add(valor);

        for (Valor val : valores) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            totalRecibo = totalRecibo + val.getImporte();
        }

        String totalReciboMiles = convertirNumeroMiles(totalRecibo);

        modelo.put("idCliente", idClienteRecibo);
        modelo.put("observacion", observacion);
        modelo.put("cliente", clienteServicio.buscarCliente(idCliente));
        modelo.put("totalRecibo", totalReciboMiles);
        modelo.addAttribute("valores", valores);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "recibo_agregarValor.html";

    }

    @GetMapping("/borrarValor/{id}")
    public String borrarValor(@PathVariable Long id, ModelMap modelo) {

        int numeroInt = id.intValue();  //convierto en int id de Valor que llega para buscarlo y eliminarlo del array
        Double totalRecibo = 0.0; //variable para ir almacenado total de detalles pertenecientes a un mismo servicio

        for (int i = 0; i < valores.size(); i++) {
            if (valores.get(i).getId() == numeroInt) {
                valores.remove(i);
            }
        }

        valorServicio.eliminarValor(id);

        for (Valor val : valores) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            totalRecibo = totalRecibo + val.getImporte();
        }

        String totalReciboMiles = convertirNumeroMiles(totalRecibo);

        modelo.put("idCliente", idClienteRecibo);
        modelo.put("observacion", observacion);
        modelo.put("cliente", clienteServicio.buscarCliente(idClienteRecibo));
        modelo.put("totalRecibo", totalReciboMiles);
        modelo.addAttribute("valores", valores);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "recibo_agregarValor.html";
    }

    @GetMapping("/modificar/{id}")  //metodo para modificar CABECERA de Recibo
    public String modificar(@PathVariable Long id, ModelMap modelo) {

        valoresM.clear();

        Recibo recibo = reciboServicio.buscarRecibo(id);
        String totalRecibo = convertirNumeroMiles(recibo.getImporte());

        valoresM.addAll(recibo.getValor());

        modelo.put("recibo", recibo);
        modelo.put("totalRecibo", totalRecibo);
        modelo.addAttribute("clientes", clienteServicio.bucarClientes());

        return "recibo_modificar.html";

    }

    @PostMapping("/modifica/{id}")
    public String modifica(@RequestParam Long id, @RequestParam Long idCliente,
            @RequestParam(required = false) String observacion, ModelMap modelo, HttpSession session) throws ParseException {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        reciboServicio.modificarRecibo(id, idCliente, observacion, logueado.getId());

        Recibo recibo = reciboServicio.buscarRecibo(id);
        String total = convertirNumeroMiles(recibo.getImporte());

        modelo.put("recibo", recibo);
        modelo.put("totalRecibo", total);
        modelo.put("exito", "Recibo MODIFICADO exitosamente");

        return "recibo_mostrar.html";
    }

    @GetMapping("/modificarV/{id}") //metodo para modificar Valores de Recibo
    public String modificarV(@PathVariable Long id, ModelMap modelo) {

        aux = id;  //utilizada para pasar idRecibo a metodo borrarValorV
        Double total = 0.0; //variable para ir almacenado total de valores pertenecientes a un mismo recibo

        listaPersistida.clear();

        Recibo recibo = reciboServicio.buscarRecibo(id);

        listaPersistida.addAll(recibo.getValor());

        for (Valor val : listaPersistida) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            total = total + val.getImporte();
        }

        String totalRecibo = convertirNumeroMiles(total);

        modelo.put("recibo", recibo);
        modelo.put("totalRecibo", totalRecibo);
        modelo.addAttribute("valores", listaPersistida);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "recibo_modificarV.html";

    }

    @GetMapping("/modificaV/{id}")
    public String modificaV(@PathVariable Long id, ModelMap modelo, HttpSession session) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");

        reciboServicio.modificarReciboV(id, listaPersistida, logueado.getId());

        Double total = 0.0;

        for (Valor valor : listaPersistida) {
            total = total + valor.getImporte();
        }
        String totalRecibo = convertirNumeroMiles(total);

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.put("totalRecibo", totalRecibo);
        modelo.put("exito", "Recibo MODIFICADO exitosamente");

        return "recibo_mostrar.html";
    }

    @PostMapping("/agregarValorV/{id}")
    public String agregarValorV(@RequestParam Long id, @RequestParam String tipoValor, @RequestParam Double importe,
            @RequestParam Integer numero, String fechaV, ModelMap modelo) throws ParseException {

        Double total = 0.0;
        String nombre = "RECIBO";

        valorServicio.crearValor(tipoValor, "COBRADO", importe, numero, fechaV, nombre, socio);

        Date fechaOrden = convertirFecha(fechaV);

        Valor valor = new Valor();

        valor.setId(valorServicio.buscarUltimo());
        valor.setTipoValor(tipoValor);
        valor.setImporte(importe);
        valor.setNumero(numero);
        valor.setFecha(fechaOrden);
        valor.setObservacion(fechaV);

        listaPersistida.add(valor);

        for (Valor val : listaPersistida) {    //bucle para sumar total de Valores para pasar a HTML como total de Recibo: totalRecibo
            total = total + val.getImporte();
        }

        String totalRecibo = convertirNumeroMiles(total);

        modelo.put("recibo", reciboServicio.buscarRecibo(id));
        modelo.put("totalRecibo", totalRecibo);
        modelo.addAttribute("valores", listaPersistida);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "recibo_modificarV.html";
    }

    @GetMapping("/borrarValorV/{id}")
    public String borrarValorV(@PathVariable Long id, ModelMap modelo) {

        int numeroInt = id.intValue();  //convierto en int id de Valor que llega para buscarlo y eliminarlo del array
        Double totalRecibo = 0.0; //variable para ir almacenado total de Valores pertenecientes a un mismo Recibo

        for (int i = 0; i < listaPersistida.size(); i++) {
            if (listaPersistida.get(i).getId() == numeroInt) {
                listaPersistida.remove(i);
            }
        }

        for (Valor val : listaPersistida) {    //bucle para sumar total de detalles para pasar a HTML como total de servicio: totalServicio
            totalRecibo = totalRecibo + val.getImporte();
        }

        String total = convertirNumeroMiles(totalRecibo);

        valorServicio.eliminarValor(id);

        modelo.put("recibo", reciboServicio.buscarRecibo(aux));
        modelo.put("totalRecibo", total);
        modelo.addAttribute("valores", listaPersistida);
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "recibo_modificarV.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("recibos", reciboServicio.buscarRecibosIdDesc());

        return "recibo_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdAsc(ModelMap modelo) {

        modelo.addAttribute("recibos", reciboServicio.buscarRecibos());

        return "recibo_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("recibos", reciboServicio.buscarRecibosNombreAsc());

        return "recibo_listar.html";
    }

    @GetMapping("/listarImporteDesc")
    public String listarImporteDesc(ModelMap modelo) {

        modelo.addAttribute("recibos", reciboServicio.buscarRecibosImporteDesc());

        return "recibo_listar.html";
    }

    @GetMapping("/listarFechaDesc")
    public String listarFechaDesc(ModelMap modelo) {

        modelo.addAttribute("recibos", reciboServicio.buscarRecibosFechaDesc());

        return "recibo_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Recibo recibo = reciboServicio.buscarRecibo(id);
        String total = convertirNumeroMiles(recibo.getImporte());

        modelo.put("recibo", recibo);
        modelo.put("totalRecibo", total);

        return "recibo_mostrar.html";
    }

    @GetMapping("/mostrarPdf/{id}")
    public String mostrarPdf(@PathVariable Long id, ModelMap modelo) {

        Recibo recibo = reciboServicio.buscarRecibo(id);
        String total = convertirNumeroMiles(recibo.getImporte());

        modelo.put("recibo", recibo);
        modelo.put("total", total);

        return "recibo_mostrarPdf.html";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, ModelMap modelo) {

        modelo.put("recibo", reciboServicio.buscarRecibo(id));

        return "recibo_eliminar.html";
    }

    @GetMapping("/elimina/{id}")
    public String elimina(@PathVariable Long id, ModelMap modelo) {

        try {
            reciboServicio.eliminarRecibo(id);

            modelo.addAttribute("recibos", reciboServicio.buscarRecibos());
            modelo.put("exito", "Recibo ELIMINADO exitosamente");

            return "recibo_listar.html";

        } catch (MiException ex) {

            modelo.put("recibo", reciboServicio.buscarRecibo(id));
            modelo.put("error", ex.getMessage());

            return "recibo_eliminar.html";
        }
    }

    @GetMapping("/cancelar")
    public String cancela(ModelMap modelo) {

        for (Valor v : valores) {
            valorServicio.eliminarValor(v.getId());
        }

        valores.clear();

        modelo.addAttribute("clientes", clienteServicio.buscarClientesNombreAsc());
        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "recibo_registrar.html";
    }

    private String convertirNumeroMiles(Double num) {   //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

    public Date convertirFecha(String fecha) throws ParseException { //convierte fecha String a fecha Date
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        return formato.parse(fecha);
    }

}

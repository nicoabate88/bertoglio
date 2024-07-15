package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Cuentap;
import com.bertoglio.portalbertoglio.entidades.Transaccionp;
import com.bertoglio.portalbertoglio.servicios.CompraServicio;
import com.bertoglio.portalbertoglio.servicios.CuentapServicio;
import com.bertoglio.portalbertoglio.servicios.PagoServicio;
import com.bertoglio.portalbertoglio.servicios.TransaccionpServicio;
import java.text.DecimalFormat;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cuentap")
@PreAuthorize("hasRole('ROLE_admin')")
public class CuentapControlador {

    @Autowired
    private CuentapServicio cuentapServicio;
    @Autowired
    private TransaccionpServicio transaccionpServicio;
    @Autowired
    private CompraServicio compraServicio;
    @Autowired
    private PagoServicio pagoServicio;

    String saldo;

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        saldo = "";
        Double total = 0.0;
        ArrayList<Cuentap> cuentas = cuentapServicio.buscarCuentas();
        for (Cuentap cuenta : cuentas) {
            total = total + cuenta.getSaldo();
        }

        saldo = convertirNumeroMiles(total);

        modelo.addAttribute("cuentas", cuentapServicio.buscarCuentasNombreAsc());
        modelo.put("saldo", saldo);

        return "cuentap_listar.html";
    }

    @GetMapping("/listarIdAsc")
    public String listarIdAsc(ModelMap modelo) {

        modelo.addAttribute("cuentas", cuentapServicio.buscarCuentas());
        modelo.put("saldo", saldo);

        return "cuentap_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("cuentas", cuentapServicio.buscarCuentasNombreAsc());
        modelo.put("saldo", saldo);

        return "cuentap_listar.html";
    }

    @GetMapping("/listarSaldoDesc")
    public String listarSaldoDesc(ModelMap modelo) {

        modelo.addAttribute("cuentas", cuentapServicio.buscarCuentasSaldoDesc());
        modelo.put("saldo", saldo);

        return "cuentap_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Cuentap cuenta = cuentapServicio.buscarCuenta(id);
        String total = convertirNumeroMiles(cuenta.getSaldo());

        modelo.put("cuenta", cuenta);
        modelo.put("total", total);
        modelo.addAttribute("transacciones", transaccionpServicio.buscarTransaccionIdCuenta(id));

        return "cuentap_mostrar.html";

    }

    @GetMapping("/mostrarPdf/{id}")
    public String mostrarPdf(@PathVariable Long id, ModelMap modelo) {

        Cuentap cuenta = cuentapServicio.buscarCuenta(id);
        String total = convertirNumeroMiles(cuenta.getSaldo());

        modelo.put("cuenta", cuenta);
        modelo.put("total", total);
        modelo.addAttribute("transacciones", transaccionpServicio.buscarTransaccionIdCuenta(id));

        return "cuentap_mostrarPdf.html";

    }

    @GetMapping("/mostrarTransaccion/{id}")
    public String mostrarTransaccion(@PathVariable Long id, ModelMap modelo) {

        Transaccionp transaccion = transaccionpServicio.buscarTransaccion(id);
        Double valorAbsoluto = Math.abs(transaccion.getImporte());
        String total = convertirNumeroMiles(valorAbsoluto);

        if (transaccion.getCompra() != null) {

            modelo.put("compra", compraServicio.buscarCompra(transaccion.getCompra().getId()));
            modelo.put("importe", total);

            return "transaccionp_mostrarCompra.html";

        } else {

            modelo.put("pago", pagoServicio.buscarPago(transaccion.getPago().getId()));
            modelo.put("totalPago", total);

            return "transaccionp_mostrarPago.html";
        }

    }

    public String convertirNumeroMiles(Double num) {    //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

}

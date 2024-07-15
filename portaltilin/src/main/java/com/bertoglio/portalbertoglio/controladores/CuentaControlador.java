package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Cuenta;
import com.bertoglio.portalbertoglio.entidades.Transaccion;
import com.bertoglio.portalbertoglio.servicios.CuentaServicio;
import com.bertoglio.portalbertoglio.servicios.ReciboServicio;
import com.bertoglio.portalbertoglio.servicios.ServicioServicio;
import com.bertoglio.portalbertoglio.servicios.TransaccionServicio;
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
@RequestMapping("/cuenta")
@PreAuthorize("hasRole('ROLE_admin')")
public class CuentaControlador {

    @Autowired
    private CuentaServicio cuentaServicio;
    @Autowired
    private TransaccionServicio transaccionServicio;
    @Autowired
    private ServicioServicio servicioServicio;
    @Autowired
    private ReciboServicio reciboServicio;

    String saldo;

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        saldo = "";
        Double total = 0.0;
        ArrayList<Cuenta> cuentas = cuentaServicio.buscarCuentas();
        for (Cuenta cuenta : cuentas) {
            total = total + cuenta.getSaldo();
        }

        saldo = convertirNumeroMiles(total);

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasNombreAsc());
        modelo.put("saldo", saldo);

        return "cuenta_listar.html";
    }

    @GetMapping("/listarIdDesc")
    public String listarIdDesc(ModelMap modelo) {

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasIdDesc());
        modelo.put("saldo", saldo);

        return "cuenta_listar.html";
    }

    @GetMapping("/listarNombreAsc")
    public String listarNombreAsc(ModelMap modelo) {

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasNombreAsc());
        modelo.put("saldo", saldo);

        return "cuenta_listar.html";
    }

    @GetMapping("/listarSaldoDesc")
    public String listarSaldoDesc(ModelMap modelo) {

        modelo.addAttribute("cuentas", cuentaServicio.buscarCuentasSaldoDesc());
        modelo.put("saldo", saldo);

        return "cuenta_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String total = convertirNumeroMiles(cuenta.getSaldo());

        modelo.put("cuenta", cuenta);
        modelo.put("total", total);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuenta(id));

        return "cuenta_mostrar.html";

    }

    @GetMapping("/mostrarPdf/{id}")
    public String mostrarPdf(@PathVariable Long id, ModelMap modelo) {

        Cuenta cuenta = cuentaServicio.buscarCuenta(id);
        String total = convertirNumeroMiles(cuenta.getSaldo());

        modelo.put("cuenta", cuenta);
        modelo.put("total", total);
        modelo.addAttribute("transacciones", transaccionServicio.buscarTransaccionIdCuenta(id));

        return "cuenta_mostrarPdf.html";

    }

    @GetMapping("/mostrarTransaccion/{id}")
    public String mostrarTransaccion(@PathVariable Long id, ModelMap modelo) {

        Transaccion transaccion = transaccionServicio.buscarTransaccion(id);
        Double valorAbsoluto = Math.abs(transaccion.getImporte());
        String total = convertirNumeroMiles(valorAbsoluto);

        if (transaccion.getServicio() != null) {

            modelo.put("servicio", servicioServicio.buscarServicio(transaccion.getServicio().getId()));
            modelo.put("totalServicio", total);

            return "transaccion_mostrarServicio.html";

        } else {

            modelo.put("recibo", reciboServicio.buscarRecibo(transaccion.getRecibo().getId()));
            modelo.put("totalRecibo", total);

            return "transaccion_mostrarRecibo.html";
        }

    }

    public String convertirNumeroMiles(Double num) {    //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

}

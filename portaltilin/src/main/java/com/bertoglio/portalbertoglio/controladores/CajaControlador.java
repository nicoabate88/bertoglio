package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Caja;
import com.bertoglio.portalbertoglio.entidades.Valor;
import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.servicios.CajaServicio;
import com.bertoglio.portalbertoglio.servicios.PagoServicio;
import com.bertoglio.portalbertoglio.servicios.ReciboServicio;
import com.bertoglio.portalbertoglio.servicios.ValorServicio;
import java.text.DecimalFormat;
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
@RequestMapping("/caja")
@PreAuthorize("hasRole('ROLE_admin')")
public class CajaControlador {

    @Autowired
    private CajaServicio cajaServicio;
    @Autowired
    private ValorServicio valorServicio;
    @Autowired
    private ReciboServicio reciboServicio;
    @Autowired
    private PagoServicio pagoServicio;

    @GetMapping("/registrar")
    public String registrar() {

        return "caja_registrar.html";
    }

    @PostMapping("/registro")
    public String registro(@RequestParam String nombre, ModelMap modelo) throws MiException {

        cajaServicio.crearCaja(nombre);

        modelo.addAttribute("cajas", cajaServicio.buscarCajas());
        modelo.put("exito", "Caja REGISTRADO exitosamente");

        return "caja_listar.html";

    }

    @GetMapping("/listar")
    public String listar(ModelMap modelo) {

        modelo.addAttribute("cajas", cajaServicio.buscarCajas());

        return "caja_listar.html";
    }

    @GetMapping("/mostrar/{id}")
    public String mostrar(@PathVariable Long id, ModelMap modelo) {

        Caja caja = cajaServicio.buscarCaja(id);
        String total = convertirNumeroMiles(caja.getSaldo());

        modelo.put("caja", caja);
        modelo.put("total", total);
        modelo.addAttribute("valores", valorServicio.buscarValorIdCaja(id));

        return "caja_mostrar.html";

    }

    @GetMapping("/mostrarCartera/{id}")
    public String mostrarCartera(@PathVariable Long id, ModelMap modelo) {

        Caja caja = cajaServicio.buscarCaja(id);
        String total = convertirNumeroMiles(caja.getSaldo());

        modelo.put("caja", caja);
        modelo.put("total", total);
        modelo.addAttribute("valores", valorServicio.buscarValorCartera());

        return "caja_mostrarCartera.html";

    }

    @GetMapping("/mostrarValor/{id}")
    public String mostrarTransaccion(@PathVariable Long id, ModelMap modelo) {

        Valor valor = valorServicio.buscarValor(id);

        if (valor.getNombre().equalsIgnoreCase("RECIBO")) {

            modelo.put("recibo", reciboServicio.buscarReciboIdValor(id));

            return "caja_mostrarRecibo.html";

        } else {

            modelo.put("pago", pagoServicio.buscarPagoIdValor(id));

            return "caja_mostrarPago.html";
        }

    }

    public String convertirNumeroMiles(Double num) {    //metodo que sirve para dar formato separador de miles a total

        DecimalFormat formato = new DecimalFormat("#,##0.00");
        String numeroFormateado = formato.format(num);

        return numeroFormateado;

    }

}

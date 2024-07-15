package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Cliente;
import com.bertoglio.portalbertoglio.entidades.Cuenta;
import com.bertoglio.portalbertoglio.entidades.Transaccion;
import com.bertoglio.portalbertoglio.repositorios.ClienteRepositorio;
import com.bertoglio.portalbertoglio.repositorios.CuentaRepositorio;
import com.bertoglio.portalbertoglio.repositorios.TransaccionRepositorio;
import com.bertoglio.portalbertoglio.util.CuentaComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentaServicio {

    @Autowired
    private CuentaRepositorio cuentaRepositorio;
    @Autowired
    private ClienteRepositorio clienteRepositorio;
    @Autowired
    private TransaccionRepositorio transaccionRepositorio;

    @Transactional
    public void crearCuenta(Long idCliente) {

        Cliente cliente = new Cliente();
        Optional<Cliente> cte = clienteRepositorio.findById(idCliente);
        if (cte.isPresent()) {
            cliente = cte.get();
        }

        Cuenta cuenta = new Cuenta();

        cuenta.setCliente(cliente);
        cuenta.setFechaAlta(new Date());
        cuenta.setSaldo(0.0);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void agregarTransaccionCuenta(Long idTransaccion) {

        Double saldo = 0.0;

        Transaccion transaccion = new Transaccion();
        Optional<Transaccion> tran = transaccionRepositorio.findById(idTransaccion);
        if (tran.isPresent()) {
            transaccion = tran.get();
        }

        Long idCliente = transaccion.getCliente().getId();
        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdCliente(idCliente);

        List<Transaccion> transacciones = cuenta.getTransaccion();
        transacciones.add(transaccion);
        cuenta.setTransaccion(transacciones);

        for (Transaccion t : transacciones) {
            saldo = saldo + t.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void modificarTransaccionCuenta(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idCliente = transaccion.getCliente().getId();
        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdCliente(idCliente);

        List<Transaccion> transacciones = cuenta.getTransaccion();
        for (Transaccion t : transacciones) {
            if (t.getId() == transaccion.getId()) {
                t.setFecha(transaccion.getFecha());
                t.setImporte(transaccion.getImporte());
            }
        }
        cuenta.setTransaccion(transacciones);

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void eliminarTransaccionCuenta(Transaccion transaccion) {

        Double saldo = 0.0;
        Long idCliente = transaccion.getCliente().getId();
        int numeroInt = transaccion.getId().intValue();

        transaccion.setConcepto("ELIMINADO");
        transaccion.setImporte(0.0);
        transaccion.setServicio(null);
        transaccion.setRecibo(null);
        transaccion.setCliente(null);
        transaccionRepositorio.save(transaccion);

        Cuenta cuenta = cuentaRepositorio.buscarCuentaIdCliente(idCliente);

        List<Transaccion> transacciones = cuenta.getTransaccion();
        for (int i = 0; i < transacciones.size(); i++) {
            if (transacciones.get(i).getId() == numeroInt) {
                transacciones.remove(i);
            }
        }

        for (Transaccion tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentaRepositorio.save(cuenta);

    }

    @Transactional
    public void eliminarCuenta(Long idCliente) {

        Long id = cuentaRepositorio.buscarIdCuentaIdCliente(idCliente);

        cuentaRepositorio.deleteById(id);

    }

    public Cuenta buscarCuenta(Long id) {

        return cuentaRepositorio.getById(id);
    }

    public ArrayList<Cuenta> buscarCuentas() {

        ArrayList<Cuenta> listaCuentas = new ArrayList();

        listaCuentas = (ArrayList<Cuenta>) cuentaRepositorio.findAll();

        return listaCuentas;
    }

    public Cuenta buscarCuentaIdCliente(Long idCliente) {

        return cuentaRepositorio.buscarCuentaIdCliente(idCliente);

    }

    public ArrayList<Cuenta> buscarCuentasIdDesc() {

        ArrayList<Cuenta> listaCuentas = buscarCuentas();

        Collections.sort(listaCuentas, CuentaComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaCuentas;

    }

    public ArrayList<Cuenta> buscarCuentasNombreAsc() {

        ArrayList<Cuenta> listaCuentas = buscarCuentas();

        Collections.sort(listaCuentas, CuentaComparador.ordenarNombreAsc); //ordena por nombre alfabetico los nombres de clientes

        return listaCuentas;

    }

    public ArrayList<Cuenta> buscarCuentasSaldoDesc() {

        ArrayList<Cuenta> listaCuentas = buscarCuentas();

        Collections.sort(listaCuentas, CuentaComparador.ordenarSaldoDesc); //ordena por nombre alfabetico los nombres de clientes

        return listaCuentas;

    }

}

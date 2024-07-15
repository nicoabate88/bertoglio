package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Cuentap;
import com.bertoglio.portalbertoglio.entidades.Proveedor;
import com.bertoglio.portalbertoglio.entidades.Transaccionp;
import com.bertoglio.portalbertoglio.repositorios.CuentapRepositorio;
import com.bertoglio.portalbertoglio.repositorios.ProveedorRepositorio;
import com.bertoglio.portalbertoglio.repositorios.TransaccionpRepositorio;
import com.bertoglio.portalbertoglio.util.CuentapComparador;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CuentapServicio {

    @Autowired
    private ProveedorRepositorio proveedorRepositorio;
    @Autowired
    private CuentapRepositorio cuentapRepositorio;
    @Autowired
    private TransaccionpRepositorio transaccionpRepositorio;

    @Transactional
    public void crearCuenta(Long idProveedor) {

        Proveedor proveedor = new Proveedor();
        Optional<Proveedor> prov = proveedorRepositorio.findById(idProveedor);
        if (prov.isPresent()) {
            proveedor = prov.get();
        }

        Cuentap cuentap = new Cuentap();

        cuentap.setProveedor(proveedor);
        cuentap.setFechaAlta(new Date());
        cuentap.setSaldo(0.0);

        cuentapRepositorio.save(cuentap);

    }

    @Transactional
    public void eliminarCuenta(Long idProveedor) {

        Long id = cuentapRepositorio.buscarIdCuentaIdProveedor(idProveedor);

        cuentapRepositorio.deleteById(id);

    }

    public Cuentap buscarCuenta(Long id) {

        return cuentapRepositorio.getById(id);
    }

    public ArrayList<Cuentap> buscarCuentas() {

        ArrayList<Cuentap> listaCuentas = new ArrayList();

        listaCuentas = (ArrayList<Cuentap>) cuentapRepositorio.findAll();

        return listaCuentas;
    }

    public ArrayList<Cuentap> buscarCuentasIdDesc() {

        ArrayList<Cuentap> listaCuentas = buscarCuentas();

        Collections.sort(listaCuentas, CuentapComparador.ordenarIdDesc); //ordena de forma DESC los ID, de mayor a menor

        return listaCuentas;

    }

    public ArrayList<Cuentap> buscarCuentasNombreAsc() {

        ArrayList<Cuentap> listaCuentas = buscarCuentas();

        Collections.sort(listaCuentas, CuentapComparador.ordenarNombreAsc); //ordena por nombre alfabetico los nombres de clientes

        return listaCuentas;

    }

    public ArrayList<Cuentap> buscarCuentasSaldoDesc() {

        ArrayList<Cuentap> listaCuentas = buscarCuentas();

        Collections.sort(listaCuentas, CuentapComparador.ordenarSaldoDesc); //ordena por nombre alfabetico los nombres de clientes

        return listaCuentas;

    }

    @Transactional
    public void agregarTransaccionCuenta(Long idTransaccion) {

        Double saldo = 0.0;

        Transaccionp transaccion = new Transaccionp();
        Optional<Transaccionp> tran = transaccionpRepositorio.findById(idTransaccion);
        if (tran.isPresent()) {
            transaccion = tran.get();
        }

        Long idProveedor = transaccion.getProveedor().getId();

        Cuentap cuenta = cuentapRepositorio.buscarCuentaIdProveedor(idProveedor);

        List<Transaccionp> transacciones = cuenta.getTransaccion();
        transacciones.add(transaccion);
        cuenta.setTransaccion(transacciones);

        for (Transaccionp t : transacciones) {
            saldo = saldo + t.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;
        cuenta.setSaldo(saldoRed);

        cuentapRepositorio.save(cuenta);

    }

    @Transactional
    public void modificarTransaccionCuenta(Transaccionp transaccion) {

        Double saldo = 0.0;
        Long idProveedor = transaccion.getProveedor().getId();
        Cuentap cuenta = cuentapRepositorio.buscarCuentaIdProveedor(idProveedor);

        List<Transaccionp> transacciones = cuenta.getTransaccion();
        for (Transaccionp t : transacciones) {
            if (t.getId() == transaccion.getId()) {
                t.setFecha(transaccion.getFecha());
                t.setImporte(transaccion.getImporte());
            }
        }
        cuenta.setTransaccion(transacciones);

        for (Transaccionp tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentapRepositorio.save(cuenta);

    }

    @Transactional
    public void eliminarTransaccionCuenta(Transaccionp transaccion) {

        Double saldo = 0.0;
        Long idProveedor = transaccion.getProveedor().getId();
        Cuentap cuenta = cuentapRepositorio.buscarCuentaIdProveedor(idProveedor);

        List<Transaccionp> transacciones = cuenta.getTransaccion();
        for (Transaccionp t : transacciones) {
            if (t.getId() == transaccion.getId()) {
                t.setConcepto("ELIMINADO");
                t.setImporte(0.0);
            }
        }
        cuenta.setTransaccion(transacciones);

        for (Transaccionp tr : transacciones) {
            saldo = saldo + tr.getImporte();
        }

        double saldoRed = Math.round(saldo * 100.0) / 100.0;

        cuenta.setSaldo(saldoRed);

        cuentapRepositorio.save(cuenta);

    }

}

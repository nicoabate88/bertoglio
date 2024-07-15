package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Cuenta;
import java.util.Comparator;

public class CuentaComparador {

    public static Comparator<Cuenta> ordenarNombreAsc = new Comparator<Cuenta>() {
        @Override
        public int compare(Cuenta c1, Cuenta c2) {
            return c1.getCliente().getNombre().compareTo(c2.getCliente().getNombre());
        }
    };

    public static Comparator<Cuenta> ordenarIdDesc = new Comparator<Cuenta>() {
        @Override
        public int compare(Cuenta c1, Cuenta c2) {
            return c1.getId().compareTo(c2.getId());
        }
    };

    public static Comparator<Cuenta> ordenarSaldoDesc = new Comparator<Cuenta>() {
        @Override
        public int compare(Cuenta c1, Cuenta c2) {
            return c2.getSaldo().compareTo(c1.getSaldo());
        }
    };

}

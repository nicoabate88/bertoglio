package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Cuentap;
import java.util.Comparator;

public class CuentapComparador {

    public static Comparator<Cuentap> ordenarNombreAsc = new Comparator<Cuentap>() {
        @Override
        public int compare(Cuentap c1, Cuentap c2) {
            return c1.getProveedor().getNombre().compareTo(c2.getProveedor().getNombre());
        }
    };

    public static Comparator<Cuentap> ordenarIdDesc = new Comparator<Cuentap>() {
        @Override
        public int compare(Cuentap c1, Cuentap c2) {
            return c2.getId().compareTo(c1.getId());
        }
    };

    public static Comparator<Cuentap> ordenarSaldoDesc = new Comparator<Cuentap>() {
        @Override
        public int compare(Cuentap c1, Cuentap c2) {
            return c2.getSaldo().compareTo(c1.getSaldo());
        }
    };

}

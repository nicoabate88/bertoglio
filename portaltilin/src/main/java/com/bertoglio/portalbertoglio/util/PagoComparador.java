package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Pago;
import java.util.Comparator;

public class PagoComparador {

    public static Comparator<Pago> ordenarNombreAsc = new Comparator<Pago>() {
        @Override
        public int compare(Pago p1, Pago p2) {
            return p1.getProveedor().getNombre().compareTo(p2.getProveedor().getNombre());
        }
    };

    public static Comparator<Pago> ordenarIdDesc = new Comparator<Pago>() {
        @Override
        public int compare(Pago p1, Pago p2) {
            return p2.getId().compareTo(p1.getId());
        }
    };

    public static Comparator<Pago> ordenarImporteDesc = new Comparator<Pago>() {
        @Override
        public int compare(Pago p1, Pago p2) {
            return p2.getImporte().compareTo(p1.getImporte());
        }
    };

    public static Comparator<Pago> ordenarFechaDesc = new Comparator<Pago>() {
        @Override
        public int compare(Pago p1, Pago p2) {
            return p2.getFecha().compareTo(p1.getFecha());
        }
    };

}

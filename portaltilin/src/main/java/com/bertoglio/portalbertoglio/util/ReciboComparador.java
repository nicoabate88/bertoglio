package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Recibo;
import java.util.Comparator;

public class ReciboComparador {

    public static Comparator<Recibo> ordenarNombreAsc = new Comparator<Recibo>() {
        @Override
        public int compare(Recibo r1, Recibo r2) {
            return r1.getCliente().getNombre().compareTo(r2.getCliente().getNombre());
        }
    };

    public static Comparator<Recibo> ordenarIdDesc = new Comparator<Recibo>() {
        @Override
        public int compare(Recibo r1, Recibo r2) {
            return r2.getId().compareTo(r1.getId());
        }
    };

    public static Comparator<Recibo> ordenarImporteDesc = new Comparator<Recibo>() {
        @Override
        public int compare(Recibo r1, Recibo r2) {
            return r2.getImporte().compareTo(r1.getImporte());
        }
    };

    public static Comparator<Recibo> ordenarFechaDesc = new Comparator<Recibo>() {
        @Override
        public int compare(Recibo r1, Recibo r2) {
            return r2.getFecha().compareTo(r1.getFecha());
        }
    };
}

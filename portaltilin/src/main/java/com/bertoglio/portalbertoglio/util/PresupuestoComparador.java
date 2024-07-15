package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Presupuesto;
import java.util.Comparator;

public class PresupuestoComparador {

    public static Comparator<Presupuesto> ordenarNombreAsc = new Comparator<Presupuesto>() {
        @Override
        public int compare(Presupuesto p1, Presupuesto p2) {
            return p1.getCliente().getNombre().compareTo(p2.getCliente().getNombre());
        }
    };

    public static Comparator<Presupuesto> ordenarIdDesc = new Comparator<Presupuesto>() {
        @Override
        public int compare(Presupuesto p1, Presupuesto p2) {
            return p2.getId().compareTo(p1.getId());
        }
    };

    public static Comparator<Presupuesto> ordenarFechaDesc = new Comparator<Presupuesto>() {
        @Override
        public int compare(Presupuesto p1, Presupuesto p2) {
            return p2.getFecha().compareTo(p1.getFecha());
        }
    };

}

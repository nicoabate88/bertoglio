package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Servicio;
import java.util.Comparator;

public class ServicioComparador {

    public static Comparator<Servicio> ordenarNombreAsc = new Comparator<Servicio>() {
        @Override
        public int compare(Servicio s1, Servicio s2) {
            return s1.getCliente().getNombre().compareTo(s2.getCliente().getNombre());
        }
    };

    public static Comparator<Servicio> ordenarIdDesc = new Comparator<Servicio>() {
        @Override
        public int compare(Servicio s1, Servicio s2) {
            return s2.getId().compareTo(s1.getId());
        }
    };

    public static Comparator<Servicio> ordenarFechaDesc = new Comparator<Servicio>() {
        @Override
        public int compare(Servicio s1, Servicio s2) {
            return s2.getFecha().compareTo(s1.getFecha());
        }
    };

}

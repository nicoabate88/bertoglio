package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Proveedor;
import java.util.Comparator;

public class ProveedorComparador {

    public static Comparator<Proveedor> ordenarNombreAsc = new Comparator<Proveedor>() {
        @Override
        public int compare(Proveedor p1, Proveedor p2) {
            return p1.getNombre().compareTo(p2.getNombre());
        }
    };

    public static Comparator<Proveedor> ordenarIdDesc = new Comparator<Proveedor>() {
        @Override
        public int compare(Proveedor p1, Proveedor p2) {
            return p2.getId().compareTo(p1.getId());
        }
    };

}

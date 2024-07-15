package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Articulo;
import java.util.Comparator;

public class ArticuloComparador {

    public static Comparator<Articulo> ordenarIdDesc = new Comparator<Articulo>() {
        @Override
        public int compare(Articulo a1, Articulo a2) {
            return a2.getId().compareTo(a1.getId());
        }
    };

    public static Comparator<Articulo> ordenarNombreAsc = new Comparator<Articulo>() {
        @Override
        public int compare(Articulo a1, Articulo a2) {
            return a1.getNombre().compareTo(a2.getNombre());
        }
    };

    public static Comparator<Articulo> ordenarPrecioAsc = new Comparator<Articulo>() {
        @Override
        public int compare(Articulo a1, Articulo a2) {
            return a1.getPrecio().compareTo(a2.getPrecio());
        }
    };

    public static Comparator<Articulo> ordenarCantidadAsc = new Comparator<Articulo>() {
        @Override
        public int compare(Articulo a1, Articulo a2) {
            return a1.getCantidad().compareTo(a2.getCantidad());
        }
    };
}

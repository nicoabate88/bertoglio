package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Vehiculo;
import java.util.Comparator;

public class VehiculoComparador {

    public static Comparator<Vehiculo> ordenarIdDesc = new Comparator<Vehiculo>() {
        @Override
        public int compare(Vehiculo v1, Vehiculo v2) {
            return v2.getId().compareTo(v1.getId());
        }
    };

    public static Comparator<Vehiculo> ordenarNombreAsc = new Comparator<Vehiculo>() {
        @Override
        public int compare(Vehiculo v1, Vehiculo v2) {
            return v1.getCliente().getNombre().compareTo(v2.getCliente().getNombre());
        }
    };

    public static Comparator<Vehiculo> ordenarDominioAsc = new Comparator<Vehiculo>() {
        @Override
        public int compare(Vehiculo v1, Vehiculo v2) {
            return v1.getDominio().compareTo(v2.getDominio());
        }
    };
}

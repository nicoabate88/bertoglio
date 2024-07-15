package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Cliente;
import java.util.Comparator;

public class ClienteComparador {

    public static Comparator<Cliente> ordenarNombreAsc = new Comparator<Cliente>() {
        @Override
        public int compare(Cliente c1, Cliente c2) {
            return c1.getNombre().compareTo(c2.getNombre());
        }
    };

    public static Comparator<Cliente> ordenarIdDesc = new Comparator<Cliente>() {
        @Override
        public int compare(Cliente c1, Cliente c2) {
            return c2.getId().compareTo(c1.getId());
        }
    };
    

}

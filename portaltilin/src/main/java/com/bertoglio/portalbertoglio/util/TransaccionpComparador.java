
package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Transaccionp;
import java.util.Comparator;


public class TransaccionpComparador {
    
        public static Comparator<Transaccionp> ordenarIdDesc = new Comparator<Transaccionp>() {
        @Override
        public int compare(Transaccionp t1, Transaccionp t2) {
            return t2.getId().compareTo(t1.getId());
        }
    };

    public static Comparator<Transaccionp> ordenarFechaDesc = new Comparator<Transaccionp>() {
        @Override
        public int compare(Transaccionp t1, Transaccionp t2) {
            return t2.getFecha().compareTo(t1.getFecha());
        }
    };

    public static Comparator<Transaccionp> ordenarFechaAcs = new Comparator<Transaccionp>() {
        @Override
        public int compare(Transaccionp t1, Transaccionp t2) {
            return t1.getFecha().compareTo(t2.getFecha());
        }
    };
    
}


package com.bertoglio.portalbertoglio.util;

import com.bertoglio.portalbertoglio.entidades.Compra;
import java.util.Comparator;


public class CompraComparador {
    
        public static Comparator<Compra> ordenarNombreAsc = new Comparator<Compra>() {
        @Override
        public int compare(Compra c1, Compra c2) {
            return c1.getProveedor().getNombre().compareTo(c2.getProveedor().getNombre());
        }
    };

    public static Comparator<Compra> ordenarIdDesc = new Comparator<Compra>() {
        @Override
        public int compare(Compra c1, Compra c2) {
            return c2.getId().compareTo(c1.getId());
        }
    };
    
     public static Comparator<Compra> ordenarTipoAsc = new Comparator<Compra>() {
        @Override
        public int compare(Compra c1, Compra c2) {
            return c1.getTipoComprobante().compareTo(c2.getTipoComprobante());
        }
    };
     
      public static Comparator<Compra> ordenarNumAcs = new Comparator<Compra>() {
        @Override
        public int compare(Compra c1, Compra c2) {
            return c1.getNumeroComprobante().compareTo(c2.getNumeroComprobante());
        }
    };
      
        public static Comparator<Compra> ordenarImporteAcs = new Comparator<Compra>() {
        @Override
        public int compare(Compra c1, Compra c2) {
            return c1.getImporte().compareTo(c2.getImporte());
        }
    };
    
    
    
}

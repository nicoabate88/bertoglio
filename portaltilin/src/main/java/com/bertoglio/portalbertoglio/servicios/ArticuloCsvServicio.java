package com.bertoglio.portalbertoglio.servicios;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import com.bertoglio.portalbertoglio.entidades.Articulo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ArticuloCsvServicio {

    @Autowired
    private ArticuloServicio articuloServicio;

    @Transactional
    public void importarArticulo(MultipartFile file) throws IOException, CsvValidationException, Exception {

        ArrayList<Articulo> articulos = new ArrayList();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            boolean firstLine = true;

            while ((line = reader.readNext()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Omitir la cabecera
                }
                /*
                 if (line.length < 3) {
                    continue; // Saltar líneas mal formateadas
                }
                 */
                String nombre = line[0];
                String codigo = line[1];
                Double precio = Double.parseDouble(line[2]);
                /*
                try {   Para Saltar líneas con un precio no válido
                    precio = Double.parseDouble(line[2]);
                } catch (NumberFormatException e) {
                    continue; 
                }
                 */
                Articulo articulo = new Articulo();
                String nombreM = nombre;
                String codigoM = codigo;

                articulo.setNombre(nombreM);
                articulo.setCodigo(codigoM);
                articulo.setPrecio(precio);
                articulo.setCantidad(0.0);
                articulo.setFechaAlta(new Date());

                articulos.add(articulo);
            }
        }

        articuloServicio.crearArticuloCsv(articulos);

    }

}

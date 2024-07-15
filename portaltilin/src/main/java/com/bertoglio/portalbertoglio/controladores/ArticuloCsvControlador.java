package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.servicios.ArticuloCsvServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.ui.ModelMap;

@Controller
@RequestMapping("/csv")
@PreAuthorize("hasRole('ROLE_admin')")
public class ArticuloCsvControlador {

    @Autowired
    private ArticuloCsvServicio articuloCsvServicio;

    @RequestMapping("/csvIndex")
    public String indexCsv() {

        return "articuloCsv_importar.html";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, ModelMap modelo) throws Exception {

        try {

            articuloCsvServicio.importarArticulo(file);

            return "articuloCsv_importado.html";

        } catch (Exception e) {

            modelo.put("error", "La Lista a Importar contiene errores ");

            return "articuloCsv_importar.html";
        }
    }
}       
              
    




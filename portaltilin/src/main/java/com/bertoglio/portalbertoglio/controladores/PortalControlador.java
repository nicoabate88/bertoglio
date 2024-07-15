package com.bertoglio.portalbertoglio.controladores;

import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.servicios.UsuarioServicio;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class PortalControlador {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @GetMapping("/")
    public String login(@RequestParam(required = false) String error, ModelMap modelo) {

        if (error != null) {
            modelo.put("error", "Usuario o Contraseña incorrecto");
        }

        return "login.html";
    }

    @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("/index")
    public String index(HttpSession session, ModelMap modelo) {

        Usuario logueado = (Usuario) session.getAttribute("usuariosession");
        String nombreMayusculas = logueado.getUsuario().toUpperCase();

        modelo.put("usuario", nombreMayusculas);

        return "index.html";
    }

    // @PreAuthorize("hasRole('ROLE_admin')")
    @GetMapping("/registrar")
    public String registrarUsuario() {

        return "usuario_registrar.html";
    }

    // @PreAuthorize("hasRole('ROLE_admin')")
    @PostMapping("/registro")
    public String registroUsuario(@RequestParam String nombre, @RequestParam String nombreUsuario,
            @RequestParam String password, @RequestParam String password2, ModelMap modelo) {

        try {

            usuarioServicio.crearUsuario(nombre, nombreUsuario, password, password2);

            Long id = usuarioServicio.buscarUltimoUsuario();

            modelo.put("usuario", usuarioServicio.buscarUsuario(id));
            modelo.put("exito", "Usuario REGISTRADO exitosamente");

            return "usuario_mostrar.html";

        } catch (MiException ex) {

            modelo.put("nombre", nombre);
            modelo.put("nombreUsuario", nombreUsuario);
            modelo.put("error", ex.getMessage());

            return "usuario_registrar.html";
        }
    }

}

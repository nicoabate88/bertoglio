package com.bertoglio.portalbertoglio.servicios;

import com.bertoglio.portalbertoglio.entidades.Usuario;
import com.bertoglio.portalbertoglio.excepciones.MiException;
import com.bertoglio.portalbertoglio.repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Transactional
    public void crearUsuario(String nombre, String nombreUsuario, String password, String password2) throws MiException {

        validarDatos(nombre, nombreUsuario, password, password2);

        Usuario user = new Usuario();

        user.setNombre(nombre);
        user.setUsuario(nombreUsuario);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setRol("admin");

        usuarioRepositorio.save(user);

    }

    public ArrayList<Usuario> buscarUsuarios() {

        ArrayList<Usuario> listaUsuarios = new ArrayList();

        listaUsuarios = (ArrayList<Usuario>) usuarioRepositorio.findAll();

        return listaUsuarios;

    }

    public Long buscarUltimoUsuario() {

        return usuarioRepositorio.ultimoUsuario();
    }

    public Usuario buscarUsuario(Long idUsuario) {

        return usuarioRepositorio.getById(idUsuario);
    }

    public void validarDatos(String nombre, String nombreUsuario, String password, String password2) throws MiException {

        if (!password.equals(password2)) {
            throw new MiException("Las contraseñas ingresadas deben ser iguales");
        }

        ArrayList<Usuario> listaUsuarios = new ArrayList();

        listaUsuarios = buscarUsuarios();

        for (Usuario lista : listaUsuarios) {

            if (lista.getUsuario().equals(nombreUsuario)) {
                throw new MiException("El nombre de Usuario ingresado no está disponible, por favor ingrese otro");
            }
        }

    }

    @Override
    public UserDetails loadUserByUsername(String nombreUsuario) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepositorio.buscarUsuarioPorUsuario(nombreUsuario);

        if (usuario != null) {

            List<GrantedAuthority> permisos = new ArrayList();

            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());

            permisos.add(p);

            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

            HttpSession session = attr.getRequest().getSession(true);

            session.setAttribute("usuariosession", usuario);

            return new User(usuario.getUsuario(), usuario.getPassword(), permisos);

        } else {
            return null;
        }

    }

}

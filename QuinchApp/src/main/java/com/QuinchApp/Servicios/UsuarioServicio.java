package com.QuinchApp.Servicios;

import com.QuinchApp.Entidades.Cliente;
import com.QuinchApp.Entidades.Imagen;
import com.QuinchApp.Entidades.Propietario;
import com.QuinchApp.Entidades.Usuario;
import com.QuinchApp.Enums.Rol;
import com.QuinchApp.Repositorios.ClienteRepositorio;
import com.QuinchApp.Repositorios.PropietarioRepositorio;
import com.QuinchApp.Repositorios.UsuarioRepositorio;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class UsuarioServicio implements UserDetailsService {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private ImagenServicio imagenServicio;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PropietarioRepositorio propRepo;
    @Autowired
    private ClienteRepositorio clienteRepo;

    @Transactional
    public void registrar(String nombre, String nombreUsuario, String email, String password, String password2, long telefono, MultipartFile archivo, String tipo) throws Exception {
        validar(nombre, nombreUsuario, email, password, telefono, archivo, password2);
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setTelefono(telefono);
        Date fechaAlta = new Date();
        usuario.setFechaAlta(fechaAlta);
        boolean activo = Boolean.TRUE;
        usuario.setActivo(activo);
        Imagen miImagen = imagenServicio.guardar(archivo);
        usuario.setFotoPerfil(miImagen);
        if (tipo.equalsIgnoreCase("cliente")) {
            usuario.setRol(Rol.CLIENTE);
            Cliente cliente = new Cliente(usuario.getId(), nombre, nombreUsuario, email, usuario.getPassword(), telefono, usuario.getRol(), usuario.getFotoPerfil(), usuario.getFechaAlta(), activo);
            System.out.println(cliente);
            clienteRepo.save(cliente);
        } else {
            usuario.setRol(Rol.PROPIETARIO);
            Propietario propietario = new Propietario(usuario.getId(), nombre, nombreUsuario, email, usuario.getPassword(), telefono, usuario.getRol(), usuario.getFotoPerfil(), usuario.getFechaAlta(), activo);
            System.out.println(propietario);
            propRepo.save(propietario);
        }
    }

    private void validarActualizar(String nombre, String nombreUsuario, String email, String password, long telefono, String password2) throws Exception {
        if (nombre == null || nombre.isEmpty()) {
            throw new Exception("El nombre no puede estar estar vacío");
        }
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            throw new Exception("El nombre de usuareio no puede estar estar vacío");
        }
        if (email == null || email.isEmpty()) {
            throw new Exception("El email no puede estar vacio");
        }
        if (password.isEmpty()) {
            throw new Exception("La contraseña no puede estar vacía");
        }
        if (password2.isEmpty()) {
            throw new Exception("Debe repetir la contraseña");
        }
        if (!password.equals(password2)) {
            throw new Exception("Las contraseñas ingresadas deben ser iguales");
        }
        if (telefono == 0L) {
            throw new Exception("El telefono no puede estar vacío");
        }
        
    }

    private void validar(String nombre, String nombreUsuario, String email, String password, long telefono, MultipartFile archivo, String password2) throws Exception {
        if (nombre == null || nombre.isEmpty()) {
            throw new Exception("El nombre no puede estar estar vacío");
        }
        if (nombreUsuario == null || nombreUsuario.isEmpty()) {
            throw new Exception("El nombre de usuareio no puede estar estar vacío");
        }
        if (email == null || email.isEmpty()) {
            throw new Exception("El email no puede estar vacio");
        }
        if (password.isEmpty()) {
            throw new Exception("La contraseña no puede estar vacía");
        }
        if (password2.isEmpty()) {
            throw new Exception("Debe repetir la contraseña");
        }
        if (!password.equals(password2)) {
            throw new Exception("Las contraseñas ingresadas deben ser iguales");
        }
        if (telefono == 0L) {
            throw new Exception("El telefono no puede estar vacío");
        }
        if (archivo == null) {
            throw new Exception("La imagen no puede estar vacía");
        }
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
        if (usuario != null) {
            throw new Exception("El email ya se encuentra registrado, pruebe con otro");
        }
    }

 @Transactional
public void actualizar(int id, String nombre, String nombreUsuario, String email, String password, String password2, long telefono, MultipartFile archivo, String tipo) throws Exception {
    validarActualizar(nombre, nombreUsuario, email, password, telefono,  password2);
    if (id < 0) {
        throw new Exception("Ingrese un id");
    }
    Optional<Usuario> respuesta = usuarioRepositorio.findById(id);
    if (respuesta.isPresent()) {
        Usuario usuario = respuesta.get();
        usuario.setNombre(nombre);
        usuario.setNombreUsuario(nombreUsuario);
        usuario.setEmail(email);
        usuario.setPassword(new BCryptPasswordEncoder().encode(password));
        usuario.setTelefono(telefono);
        
        Imagen miImagen = usuario.getFotoPerfil();
        if (archivo != null && !archivo.isEmpty()) {
            int idImagen = 0;
            if (miImagen != null) {
                idImagen = miImagen.getIdImagen();
            }
            miImagen = imagenServicio.actualizar(archivo, idImagen);
        }
        usuario.setFotoPerfil(miImagen);
        
        if (tipo.equalsIgnoreCase("cliente")) {
            Optional<Cliente> clienteOptional = clienteRepo.findById(id);
            if (clienteOptional.isPresent()) {
                Cliente cliente = clienteOptional.get();
                cliente.setNombre(nombre);
                cliente.setNombreUsuario(nombreUsuario);
                cliente.setEmail(email);
                cliente.setPassword(new BCryptPasswordEncoder().encode(password));
                cliente.setTelefono(telefono);
                cliente.setFotoPerfil(miImagen);
                clienteRepo.save(cliente);
            } else {
                usuario.setRol(Rol.CLIENTE);
                Cliente cliente = new Cliente(usuario.getId(), nombre, nombreUsuario, email, usuario.getPassword(), telefono, usuario.getRol(), usuario.getFotoPerfil(), usuario.getFechaAlta(), usuario.isActivo());
                cliente.setFotoPerfil(miImagen);
                clienteRepo.save(cliente);
            }
        } else {
            Optional<Propietario> propietarioOptional = propRepo.findById(id);
            if (propietarioOptional.isPresent()) {
                Propietario propietario = propietarioOptional.get();
                propietario.setNombre(nombre);
                propietario.setNombreUsuario(nombreUsuario);
                propietario.setEmail(email);
                propietario.setPassword(new BCryptPasswordEncoder().encode(password));
                propietario.setTelefono(telefono);
                propietario.setFotoPerfil(miImagen);
                propRepo.save(propietario);
            } else {
                usuario.setRol(Rol.PROPIETARIO);
                Propietario propietario = new Propietario(usuario.getId(), nombre, nombreUsuario, email, usuario.getPassword(), telefono, usuario.getRol(), usuario.getFotoPerfil(), usuario.getFechaAlta(), usuario.isActivo());
                propietario.setFotoPerfil(miImagen);
                propRepo.save(propietario);
            }
        }
    }
}

    @Transactional
    public List<Usuario> listarUsuarios(String palabraClave) {
        if (palabraClave != null) {
            List<Usuario> usuarios = usuarioRepositorio.findAll(palabraClave);
            return usuarios;
        } else {
            List<Usuario> usuarios = usuarioRepositorio.findAll();
            return usuarios;
        }
    }

    @Transactional
    public Usuario getOne(Integer id) {
        return usuarioRepositorio.getOne(id);
    }

    @Transactional
    public void borrar(Integer id) {
        usuarioRepositorio.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(email);
        if (usuario != null && usuario.isActivo()) { // Verificar si el usuario está activo
            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString());
            permisos.add(p);
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("usuariosession", usuario);
            return new User(usuario.getEmail(), usuario.getPassword(), permisos);
        } else {
            throw new UsernameNotFoundException("Usuario no encontrado o inactivo");
        }
    }

    public Usuario bajaAlta(Integer id) {
        Optional<Usuario> optinalUsuario = usuarioRepositorio.findById(id);
        Usuario usuario = new Usuario();
        if (optinalUsuario.isPresent()) {
            usuario = optinalUsuario.get();
            if (usuario.isActivo() == false) {
                usuario.setActivo(Boolean.TRUE);
                usuarioRepositorio.save(usuario);
            } else {
                usuario.setActivo(Boolean.FALSE);
                usuarioRepositorio.save(usuario);
            }
        }
        return usuario;
    }

    public Cliente buscarPorNombreUsuario(String nombreUsuario) {
        Usuario usuario = usuarioRepositorio.buscarPorEmail(nombreUsuario);

        if (usuario instanceof Cliente) {
            return (Cliente) usuario;
        } else {
            // si el usuario no es un Cliente, puedes lanzar una excepción o devolver null según tu necesidad
            throw new RuntimeException("El usuario no es un Cliente");
            // return null;
        }
    }

}

package org.example.ejemploservletweb.Controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.DAOUsuario;
import org.example.ejemploservletweb.Modelo.Libro;
import org.example.ejemploservletweb.Modelo.Usuario;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "usuarioServlet", value = "/usuarios-servlet")
public class UsuarioServlet extends HttpServlet {

    DAOUsuario daoUsuario;
    ObjectMapper conversorJson;

    public void init() {
        daoUsuario = new DAOUsuario();
        conversorJson = new ObjectMapper();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        conversorJson.registerModule(new JavaTimeModule());

        List<Usuario> listaUsuarios = daoUsuario.getAllUsuarios();
        System.out.println(listaUsuarios); //Simplemente por tener un seguimiento por consola pero realmente no es necesario

        String json_response = conversorJson.writeValueAsString(listaUsuarios);
        System.out.println(json_response); //Esto me imprime el json por consola, en este caso me va ha imprimir los objetos usuarios obtenidos de la listaUsuarios obtenida con el getAll(), el conversor de Json lo que hace es pasar las clases de java a formato JSON para utilizarlos en este formato por si es necesario.
        out.printf("La lista de todos los usuarios con sus respectivas propiedades:\n %s",
                json_response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        conversorJson.registerModule(new JavaTimeModule());

            String operacion = request.getParameter("operacion");
            String dni = request.getParameter("dni");
            String nombre = request.getParameter("nombre");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String tipo = request.getParameter("tipo");
        /*LocalDate penalizacionHasta = null;*/ //ACUERDATE DE MIRAR ESTO

        switch (operacion) {
            case "Insertar usuario":
                insertarUsuario(dni, nombre, email, password, tipo, out);
                break;
            case "Actualizar usuario":
                actualizarUsuario(dni, nombre, password, out);
                break;
            case "Eliminar usuario":
                eliminarUsuario(dni, out);
                break;
            case "Buscar usuario":
                buscarUsuario(email, out);
                break;
            default:
                out.println("Error, no has introducido una opción posible.");
                break;
        }
    }

    public void insertarUsuario(String dni, String nombre, String email, String password, String tipo, PrintWriter out) throws JsonProcessingException {
        Usuario usuario = new Usuario(dni, nombre, email, password, tipo.toUpperCase());

        if (daoUsuario.getUsuarioByEmail(email) == null) {
            if (usuario.getTipo().equalsIgnoreCase("A")) {
                usuario.setTipo("administrador");
            } else {
                usuario.setTipo("normal");
            }
            daoUsuario.addUsuario((Usuario) usuario);
            String json = conversorJson.writeValueAsString(usuario);
            out.println(json);
            out.printf("El usuario %s ha sido registrado correctamente. \n", usuario.getNombre());
            /*System.out.printf("El usuario %s ha sido registrado correctamente. \n", usuario.getNombre());*/
        } else {
            System.out.println("El usuario ya existe, prueba con otro correo electrónico.");
            out.printf("El usuario ya existe, prueba con otro correo electrónico.\n");
        }
    }

    public void actualizarUsuario(String dni, String nombre, String password, PrintWriter out) throws JsonProcessingException {
        Usuario usuario = daoUsuario.getUsuarioByDni(dni);
        if (usuario != null) {
            usuario.setNombre(nombre);
            usuario.setPassword(password);
            daoUsuario.updateUsuario(usuario);
            String json = conversorJson.writeValueAsString(usuario);
            out.println(json);
            out.printf("El usuario con DNI -> %s ha sido actualizado correctamente.\n", usuario.getDni());
        } else {
            out.printf("El usuario no puede ser nulo, intenta introducir correctamente tu DNI.\n");
        }
    }

    public void eliminarUsuario(String dni, PrintWriter out) throws JsonProcessingException {
        Usuario usuario = daoUsuario.getUsuarioByDni(dni);
        if (usuario != null) {
            daoUsuario.deleteUsuario(usuario);
            String json = conversorJson.writeValueAsString(usuario);
            out.println(json);
            out.printf("El usuario con DNI -> %s ha sido eliminado correctamente.\n", usuario.getDni());
        } else {
            out.printf("El usuario no puede ser nulo, intenta introducir correctamente tu DNI.\n");
        }
    }

    public void buscarUsuario(String email, PrintWriter out) throws JsonProcessingException {
        Usuario usuario = daoUsuario.getUsuarioByEmail(email);
        if (usuario != null) {
            daoUsuario.getUsuarioByEmail(email);
            String json = conversorJson.writeValueAsString(usuario);
            out.println(json);
            out.printf("%s. \n", usuario.toString());
        } else {
            out.printf("El usuario no existe, intenta introducir correctamente tu email.\n");
        }
    }
}

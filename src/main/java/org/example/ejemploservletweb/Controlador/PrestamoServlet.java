package org.example.ejemploservletweb.Controlador;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "prestamoServlet", value = "/prestamos-servlet")
public class PrestamoServlet extends HttpServlet {
    DAOGenerico<Prestamo, Integer> daoPrestamo;
    DAOEjemplar daoEjemplar;
    DAOUsuario daoUsuario;
    ObjectMapper conversorJson;

    public void init() {
        daoPrestamo = new DAOGenerico<>(Prestamo.class, Integer.class);
        daoEjemplar = new DAOEjemplar(Ejemplar.class, Integer.class);
        daoUsuario = new DAOUsuario();
        conversorJson = new ObjectMapper();
    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        conversorJson.registerModule(new JavaTimeModule());

        List<Prestamo> listaPrestamos = (List<Prestamo>) daoPrestamo.getAll();
        String json_response = conversorJson.writeValueAsString(listaPrestamos);
        out.printf("La lista de todos los préstamos es la siguiente: %s\n", json_response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        conversorJson.registerModule(new JavaTimeModule());

        String operacion = request.getParameter("operacion");
        Integer usuario_id = Integer.parseInt(request.getParameter("usuario_id"));
        Integer ejemplar_id = Integer.parseInt(request.getParameter("ejemplar_id"));
        Integer id_prestamo = Integer.parseInt(request.getParameter("id"));
        /*String fechaInicio = request.getParameter("fechaInicio");
        String fechaDevolucion = request.getParameter("password");*/

        switch (operacion) {
            case "Insertar prestamo":
                insertarPrestamo(usuario_id, ejemplar_id, out);
                break;
            case "Actualizar prestamo":
                actualizarPrestamo(id_prestamo, ejemplar_id, out);
                break;
            case "Eliminar prestamo":
                eliminarPrestamo(id_prestamo, out);
                break;
            case "Buscar prestamo":
                buscarPrestamo(id_prestamo, out);
                break;
            default:
                out.println("Error, no has introducido una opción posible.");
                break;
        }
    }

    public void insertarPrestamo(Integer usuario_id, Integer ejemplar_id, PrintWriter out) throws JsonProcessingException {
        Usuario usuario = (Usuario) daoUsuario.getUsuarioById(usuario_id);
        Ejemplar ejemplar = (Ejemplar) daoEjemplar.getById(ejemplar_id);
        if (usuario != null && ejemplar != null) {
            if (usuario.getPrestamos().size() < 3) {
                if (usuario.getPenalizacionHasta() == null) {
                    Prestamo prestamo = new Prestamo(usuario, ejemplar, LocalDate.now());
                    daoPrestamo.add(prestamo);
                    String json = conversorJson.writeValueAsString(prestamo);
                    out.println(json);
                    out.printf("El préstamo ha sido registrado correctamente, %s disfruta de ejemplar: %s.\n", usuario.getNombre(), ejemplar.getLibro().getTitulo());
                } else {
                    out.printf("El usuario con id: -> %s y con nombre: -> %s; no puede coger prestado ningún ejemplar, porque tiene una penalización hasta el día: %s.\n", usuario.getId(), usuario.getNombre());
                }
            } else {
                out.printf("El usuario con id: -> %s y con nombre: -> %s; no puede coger prestado ningún ejemplar, porque ya tiene el máximo de ejemplares prestados (3).\n", usuario.getId(), usuario.getNombre());
            }
        } else {
            out.printf("Usuario o ejemplar inexistentes en la base de datos, por favor introduzca correctamente los id. \n");
        }
    }

    public void actualizarPrestamo(Integer id_prestamo, Integer ejemplar_id, PrintWriter out) throws JsonProcessingException {
        Prestamo prestamo = (Prestamo) daoPrestamo.getById(id_prestamo);
        if (prestamo != null) {
            Ejemplar ejemplar = (Ejemplar) daoEjemplar.getById(ejemplar_id);
            prestamo.setEjemplar(ejemplar);
            prestamo.setFechaInicio(LocalDate.now());
            prestamo.setFechaDevolucion(LocalDate.now().plusDays(15));
            daoPrestamo.update(prestamo);
            String json = conversorJson.writeValueAsString(prestamo);
            out.println(json);
            out.printf("El préstamo ha sido actualizado correctamente, %s disfruta de ejemplar: %s.\n", prestamo.getUsuario().getNombre() , prestamo.getEjemplar().getLibro().getTitulo());
        } else {
            out.printf("No existe ningún préstamo asociado al id -> %s introducido.\n", id_prestamo);
        }
    }

    public void eliminarPrestamo(Integer id_prestamo, PrintWriter out) throws JsonProcessingException {
        Prestamo prestamo = (Prestamo) daoPrestamo.getById(id_prestamo);
        if (prestamo != null) {
            String json = conversorJson.writeValueAsString(prestamo);
            out.println(json);
            daoPrestamo.delete(prestamo);
            out.printf("Préstamo con id -> %s eliminado correctamente", id_prestamo);
        } else {
            out.printf("No existe ningún préstamo asociado al id -> %s introducido.\n", id_prestamo);
        }
    }

    public void buscarPrestamo(Integer id_prestamo, PrintWriter out) throws JsonProcessingException {
        Prestamo prestamo = (Prestamo) daoPrestamo.getById(id_prestamo);
        if (prestamo != null) {
            out.printf("Prestamo encontrado correctamente: \n%s\n", prestamo.toString());
            String json = conversorJson.writeValueAsString(prestamo);
            out.println(json);
        } else {
            out.printf("No existe ningún préstamo asociado al id -> %s introducido.\n", id_prestamo);
        }
    }

}

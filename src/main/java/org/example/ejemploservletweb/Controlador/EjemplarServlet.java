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
import java.util.List;

@WebServlet(name = "ejemplarServlet", value = "/ejemplares-servlet")
public class EjemplarServlet extends HttpServlet {
    DAOEjemplar daoEjemplar;
    ObjectMapper conversorJson;
    DAOGenerico<Libro, String> daolibro;

    @Override
    public void init() {
        daoEjemplar = new DAOEjemplar(Ejemplar.class, Integer.class);
        daolibro = new DAOGenerico<>(Libro.class,String.class);
        conversorJson = new ObjectMapper();
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        conversorJson.registerModule(new JavaTimeModule()); //Esto lo pongo por si acaso el Json tiene que trabajar con fechas para que puede hacer la conversion a Json sin ningún problema.

        List<Ejemplar> listaEjemplar = daoEjemplar.getAll();
        String json_response = conversorJson.writeValueAsString(listaEjemplar); // Para poder imprimir el objeto como una string pero con un valor json.
        out.println(json_response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        conversorJson.registerModule(new JavaTimeModule());

        String operacion = request.getParameter("operacion");
        String isbn = request.getParameter("isbn");
        String estado = request.getParameter("estado");
        Integer id = Integer.parseInt(request.getParameter("id"));

        switch (operacion) {
            case "Insertar ejemplar":
                insertarEjemplar(isbn, out);
                break;
            case "Actualizar ejemplar":
                actualizarEjemplar(isbn, estado, out);
                break;
            case "Eliminar ejemplar":
                eliminarEjemplar(id, out);
                break;
            case "Buscar ejemplar":
                buscarEjemplar(isbn, out);
                break;
            default:
                out.println("Error, no has introducido una opción posible.");
                break;
        }

    }

    public void insertarEjemplar(String isbn, PrintWriter out) throws JsonProcessingException {
        Libro libro = daolibro.getById(isbn);
        if (libro == null) {
            out.printf("Error, el libro de isbn -> %s, no existe y no se puede registrar un ejemplar de un libro que no existe.\n", isbn);
        } else {
            Ejemplar ejemplar = new Ejemplar(libro);
            daoEjemplar.add(ejemplar);
            String json = conversorJson.writeValueAsString(ejemplar);
            out.println(json);
            out.printf("El ejemplar de isbn -> %s y de título -> %s ha sido introducido correctamente\n", libro.getIsbn(), libro.getTitulo());
        }
    }
    public void actualizarEjemplar(String isbn, String estado, PrintWriter out) throws JsonProcessingException {
        Libro libro = daolibro.getById(isbn);
        if (libro == null) {
            out.printf("Error, el libro de isbn -> %s, no existe y no se puede actualizar un ejemplar de un libro que no existe.\n", isbn);
        } else {
            Ejemplar ejemplar = libro.getEjemplares().get(0);
            ejemplar.setEstado(estado);
            daoEjemplar.update(ejemplar);
            String json = conversorJson.writeValueAsString(ejemplar);
            out.println(json);
            out.printf("El ejemplar de id -> %s e isbn -> %s ha sido actualizado correctamente y ahora tiene un estado: %s\n", ejemplar.getId(), libro.getIsbn(), estado);
        }

    }
    public void eliminarEjemplar(Integer id, PrintWriter out) throws JsonProcessingException {
        Ejemplar ejemplar = (Ejemplar) daoEjemplar.getById(id);
        if (ejemplar == null) {
            out.printf("Error, el ejemplar de id -> %s, no existe y no se puede eliminar un ejemplar que no existe.\n", id);
        } else {
            String json = conversorJson.writeValueAsString(ejemplar);//HAY QUE IMPRIMIRLO ANTES DE BORRARLO PORQUE PARECE SER QUE CUANDO LO BORRAMOS DE LA BASE DE DATOS NO YO ES POSIBLE IMPRIMIRLO, PREGUNTAR A KAMEL POR QUE SUCEDE ESTO.
            out.println(json);
            daoEjemplar.delete(ejemplar);
            out.printf("El ejemplar de id -> %s e isbn -> %s ha sido eliminado correctamente.\n", ejemplar.getId(), ejemplar.getLibro().getIsbn());
        }
    }
    public void buscarEjemplar(String isbn, PrintWriter out) throws JsonProcessingException {
        List<Ejemplar> listaEjemplares = daoEjemplar.getListaEjemplaresByIsbn(isbn);
        if (listaEjemplares.isEmpty()) {
            out.printf("Error, el libro de isbn -> %s, no existe y no se puede encontrar ningún ejemplar de un libro que no existe.\n", isbn);
        } else {
            out.printf("La lista de ejemplares asociados al isbn -> %s:\n", isbn);
            String json = conversorJson.writeValueAsString(listaEjemplares);
            out.println(json);
        }
    }
}

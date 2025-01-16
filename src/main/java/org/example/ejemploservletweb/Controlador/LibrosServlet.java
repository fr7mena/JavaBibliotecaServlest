package org.example.ejemploservletweb.Controlador;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ejemploservletweb.Modelo.DAOGenerico;
import org.example.ejemploservletweb.Modelo.Libro;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "librosServlet", value = "/libros-servlet")
public class LibrosServlet extends HttpServlet {

    DAOGenerico<Libro, String> daolibro;
    ObjectMapper conversorJson;

    public void init(){ //Intersa inicialiizar aquí
        daolibro = new DAOGenerico<>(Libro.class,String.class);
        conversorJson = new ObjectMapper();
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        conversorJson.registerModule(new JavaTimeModule());

        String operacion = request.getParameter("operacion");
        String isbn = request.getParameter("isbn");
        String titulo = request.getParameter("titulo");
        String autor = request.getParameter("autor");

        switch (operacion) {
            case "Insertar libro":
                insertarLibro(isbn, titulo, autor, out);
                break;
            case "Actualizar libro":
                actualizarLibro(isbn, titulo, autor, out);
                break;
            case "Eliminar libro":
                eliminarLibro(isbn, out);
                break;
            case "Buscar libro":
                buscarLibro(isbn, out);
                break;
            default:
                out.println("Error, no has introducido una opción posible.");
                break;
        }
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException { //POR QUÉ ME HACE POR DEFECTO ESTE MÉTODOOOOOOO PREGUNTAR A KEMEL.
        response.setContentType("application/json"); //preguntar a kemel para qué se utiliza esto, PARECE SER QUE POR DEFECTO EL TIPO DE RESPUETA (que es lo que aqui se configura) SOLO SE TIENE QUE ESPECIFICAR CUANDO EL FORMATO ES DIFERENTE AL QUE POR DEFECTO ES, EL FORMATO POR DEFECTO ES HTML (text/html). POR CIERTO, ES UNA BUENA METODOLOGÍA ESPECIFICARLO SIEMPRE AUNQUE SE UTILICE EL FORMATO POR DEFECTO

        PrintWriter impresora = response.getWriter();
        ObjectMapper conversorJson = new ObjectMapper();
        conversorJson.registerModule(new JavaTimeModule());

        List<Libro> listaLibros  = daolibro.getAll();
        System.out.println("En java" + listaLibros);

        String json_response = conversorJson.writeValueAsString(listaLibros);
        System.out.println("En java json" + json_response);
        impresora.println(json_response);
    }

    public void destroy(){

    }

    public void insertarLibro(String isbn, String titulo, String autor, PrintWriter out) throws JsonProcessingException {
        Libro libro = new Libro(isbn, titulo, autor);
        daolibro.add(libro);
        String json = conversorJson.writeValueAsString(libro);
        out.println(json);
        out.printf("El libro con título -> %s, autor -> %s e isbn -> %s, ha sido insertado correctamente.\n", titulo, autor, isbn);
    }

    public void actualizarLibro(String isbn, String titulo, String autor, PrintWriter out) throws JsonProcessingException {
        Libro libro = daolibro.getById(isbn);
        libro.setTitulo(titulo);
        libro.setAutor(autor);
        daolibro.update(libro);
        String json = conversorJson.writeValueAsString(libro);
        out.println(json);
        out.printf("El libro con isbn -> %s, ha sido actualizado correctamente.\n", isbn);
    }

    public void eliminarLibro(String isbn, PrintWriter out) throws JsonProcessingException {
        Libro libro = daolibro.getById(isbn);
        daolibro.delete(libro);
        String json = conversorJson.writeValueAsString(libro);
        out.println(json);
        out.printf("El libro con título -> %s e isbn -> %s ha sido eliminado correctamente.\n", libro.getTitulo(), libro.getIsbn());
    }

    public void buscarLibro(String isbn, PrintWriter out) throws JsonProcessingException {
        Libro libro = daolibro.getById(isbn);
        String json = conversorJson.writeValueAsString(libro);
        out.println(json);
        out.printf("Libro encontrado correctamente.\n Libro: título -> %s, autor -> %s e isbn -> %s. \n", libro.getTitulo(), libro.getAutor(), isbn);
    }
}

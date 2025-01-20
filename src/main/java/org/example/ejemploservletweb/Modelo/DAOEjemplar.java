package org.example.ejemploservletweb.Modelo;

import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;

import java.util.List;

public class DAOEjemplar extends DAOGenerico {
    public DAOEjemplar(Class clase, Class claseID) {
        super(clase, claseID);
    }
    public List<Ejemplar> getListaEjemplaresByIsbn(String isbn) {
        List<Ejemplar> listaEjemplares = null;
        String sql = "SELECT * FROM ejemplar WHERE isbn = ?";
        Query query = em.createNativeQuery(sql, Ejemplar.class);
        query.setParameter(1, isbn);
        try {
            listaEjemplares = (List<Ejemplar>) query.getResultList();
            return listaEjemplares;
        } catch (NoResultException e) {
            e.printStackTrace();
            return listaEjemplares;
        }
    }
}

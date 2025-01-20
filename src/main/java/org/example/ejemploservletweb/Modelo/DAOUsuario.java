package org.example.ejemploservletweb.Modelo;

import jakarta.persistence.*;

import java.util.List;

public class DAOUsuario {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("unidad-biblioteca");
    EntityManager em = emf.createEntityManager();
    EntityTransaction tx = em.getTransaction();

    public DAOUsuario(){
    }

    //INSERT
        public boolean addUsuario(Usuario usuario){
        tx.begin();
        em.persist(usuario);
        tx.commit();
        return false;
    }

    //SELECT *
    public List<Usuario> getAllUsuarios(){
        return em.createQuery("SELECT u FROM Usuario u").getResultList();
    }

    //UPDATE
    public Usuario updateUsuario(Usuario usuario){
        tx.begin();
        usuario = em.merge(usuario);
        tx.commit();
        return usuario;
    }
    //DELETE WHERE usuario.id
    public boolean deleteUsuario(Usuario usuario){
        tx.begin();
        em.remove(usuario);
        tx.commit();
        return true;
    }

    public Usuario getUsuarioByEmail(String email) { //HE PUESTO ESTE DEL MÍO DEBERÍA FUNCIONAR
        Usuario usuario = null;
        String sql = "SELECT * FROM usuario WHERE email = ?";
        Query query = em.createNativeQuery(sql, Usuario.class);
        query.setParameter(1, email);
        try {
            usuario = (Usuario) query.getSingleResult();
            return usuario;
        } catch (NoResultException e) {
            return usuario;
        }
    }

    public Usuario getUsuarioById(int id){
        return em.find(Usuario.class, id);
    }

    public Usuario getUsuarioByDni(String dni){
        Query consulta = em.createQuery("SELECT u from Usuario u WHERE u.dni=:dni");
        consulta.setParameter("dni",dni);
        return (Usuario) consulta.getSingleResult();
    }

}

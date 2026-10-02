package com.tp.jpa.repository;

import com.tp.jpa.entity.Usuario;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class UsuarioRepository {

    private final EntityManager entityManager;

    public UsuarioRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void guardar(Usuario usuario) {
        entityManager.persist(usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(entityManager.find(Usuario.class, id));
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return entityManager.createQuery(
                        "SELECT u FROM Usuario u WHERE LOWER(u.email) = LOWER(:email)",
                        Usuario.class)
                .setParameter("email", email)
                .getResultStream()
                .findFirst();
    }

    public long contar() {
        return entityManager.createQuery("SELECT COUNT(u) FROM Usuario u", Long.class)
                .getSingleResult();
    }
}

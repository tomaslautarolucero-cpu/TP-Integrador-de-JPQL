package com.tp.jpa.repository;

import com.tp.jpa.entity.Categoria;
import jakarta.persistence.EntityManager;

public class CategoriaRepository {

    private final EntityManager entityManager;

    public CategoriaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void guardar(Categoria categoria) {
        entityManager.persist(categoria);
    }

    public long contar() {
        return entityManager.createQuery("SELECT COUNT(c) FROM Categoria c", Long.class)
                .getSingleResult();
    }
}

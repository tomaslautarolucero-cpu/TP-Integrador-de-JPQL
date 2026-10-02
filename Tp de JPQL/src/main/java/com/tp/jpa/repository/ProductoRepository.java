package com.tp.jpa.repository;

import com.tp.jpa.entity.Producto;
import jakarta.persistence.EntityManager;

import java.util.Optional;

public class ProductoRepository {

    private final EntityManager entityManager;

    public ProductoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void guardar(Producto producto) {
        entityManager.persist(producto);
    }

    public Optional<Producto> buscarPorCodigo(String codigo) {
        return entityManager.createQuery(
                        "SELECT p FROM Producto p WHERE p.codigo = :codigo",
                        Producto.class)
                .setParameter("codigo", codigo)
                .getResultStream()
                .findFirst();
    }

    public void eliminar(Producto producto) {
        entityManager.remove(producto);
    }

    public long contar() {
        return entityManager.createQuery("SELECT COUNT(p) FROM Producto p", Long.class)
                .getSingleResult();
    }
}

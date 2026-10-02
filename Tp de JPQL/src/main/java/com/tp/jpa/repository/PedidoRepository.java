package com.tp.jpa.repository;

import com.tp.jpa.entity.Pedido;
import jakarta.persistence.EntityManager;

public class PedidoRepository {

    private final EntityManager entityManager;

    public PedidoRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public long contar() {
        return entityManager.createQuery("SELECT COUNT(p) FROM Pedido p", Long.class)
                .getSingleResult();
    }

    public long contarDetalles() {
        return entityManager.createQuery("SELECT COUNT(d) FROM DetallePedido d", Long.class)
                .getSingleResult();
    }
}

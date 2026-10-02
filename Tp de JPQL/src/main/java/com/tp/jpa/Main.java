package com.tp.jpa;

import com.tp.jpa.service.TiendaService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("TiendaJPA");
        try {
            TiendaService tiendaService = new TiendaService(entityManagerFactory);
            tiendaService.ejecutarDemostracion();
        } finally {
            entityManagerFactory.close();
        }
    }
}

package com.tp.jpa;

import com.tp.jpa.entity.Producto;
import com.tp.jpa.entity.Usuario;
import com.tp.jpa.service.TiendaService;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TiendaServiceTest {

    private static EntityManagerFactory entityManagerFactory;
    private static TiendaService tiendaService;

    @BeforeAll
    static void configurar() {
        entityManagerFactory = Persistence.createEntityManagerFactory("TiendaJPATest");
        tiendaService = new TiendaService(entityManagerFactory);
    }

    @AfterAll
    static void cerrar() {
        entityManagerFactory.close();
    }

    @Test
    void ejecutaLasOperacionesCrudRequeridas() {
        tiendaService.ejecutarDemostracion();

        Usuario usuarioPorEmail = tiendaService.buscarUsuarioPorEmail("ana.garcia@example.com")
                .orElseThrow();
        Producto notebook = tiendaService.buscarProductoPorCodigo("PRD-001")
                .orElseThrow();
        Producto monitor = tiendaService.buscarProductoPorCodigo("PRD-002")
                .orElseThrow();
        TiendaService.Resumen resumen = tiendaService.obtenerResumen();

        assertAll(
                () -> assertEquals(2, resumen.usuarios()),
                () -> assertEquals(3, resumen.pedidos()),
                () -> assertEquals(6, resumen.detalles()),
                () -> assertEquals(3, resumen.categorias()),
                () -> assertEquals(9, resumen.productos()),
                () -> assertTrue(usuarioPorEmail.getId() > 0),
                () -> assertTrue(tiendaService.buscarUsuarioPorId(usuarioPorEmail.getId()).isPresent()),
                () -> assertEquals(new BigDecimal("1350.00"), notebook.getPrecio()),
                () -> assertEquals(20, notebook.getStock()),
                () -> assertEquals(new BigDecimal("475.00"), monitor.getPrecio()),
                () -> assertEquals(25, monitor.getStock()),
                () -> assertTrue(tiendaService.buscarProductoPorCodigo("PRD-010").isEmpty()));

        tiendaService.ejecutarDemostracion();

        assertAll(
                () -> assertEquals(resumen, tiendaService.obtenerResumen()),
                () -> assertEquals(new BigDecimal("1350.00"),
                        tiendaService.buscarProductoPorCodigo("PRD-001").orElseThrow().getPrecio()),
                () -> assertEquals(new BigDecimal("475.00"),
                        tiendaService.buscarProductoPorCodigo("PRD-002").orElseThrow().getPrecio()));
    }
}

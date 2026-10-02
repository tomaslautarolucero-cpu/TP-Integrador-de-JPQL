package com.tp.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String codigo;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private int stock;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    protected Producto() {
    }

    public Producto(String codigo, String nombre, String descripcion, BigDecimal precio, int stock,
                    Categoria categoria) {
        this.codigo = Objects.requireNonNull(codigo, "El código es obligatorio")
                .trim()
                .toUpperCase(Locale.ROOT);
        this.categoria = Objects.requireNonNull(categoria, "La categoría es obligatoria");
        actualizarDatos(nombre, descripcion, precio, stock);
        categoria.agregarProducto(this);
    }

    public void actualizarDatos(String nombre, String descripcion, BigDecimal precio, int stock) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio").trim();
        this.descripcion = Objects.requireNonNull(descripcion, "La descripción es obligatoria").trim();
        this.precio = Objects.requireNonNull(precio, "El precio es obligatorio");
        this.stock = stock;
        validar();
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = Objects.requireNonNull(categoria, "La categoría es obligatoria");
    }

    @PrePersist
    void registrarCreacion() {
        LocalDateTime ahora = LocalDateTime.now();
        fechaCreacion = ahora;
        fechaActualizacion = ahora;
        validar();
    }

    @PreUpdate
    void registrarActualizacion() {
        fechaActualizacion = LocalDateTime.now();
    }

    private void validar() {
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
        if (nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }
        if (precio.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    public Long getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
}

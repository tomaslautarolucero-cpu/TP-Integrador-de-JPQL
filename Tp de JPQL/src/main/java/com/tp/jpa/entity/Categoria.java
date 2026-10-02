package com.tp.jpa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "categorias")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String nombre;

    @OneToMany(mappedBy = "categoria")
    @OrderBy("id ASC")
    private List<Producto> productos = new ArrayList<>();

    protected Categoria() {
    }

    public Categoria(String nombre) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio").trim();
    }

    public void agregarProducto(Producto producto) {
        Objects.requireNonNull(producto, "El producto es obligatorio");
        productos.add(producto);
        producto.setCategoria(this);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public List<Producto> getProductos() {
        return List.copyOf(productos);
    }
}

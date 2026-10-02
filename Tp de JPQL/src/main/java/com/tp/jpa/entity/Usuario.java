package com.tp.jpa.entity;

import jakarta.persistence.CascadeType;
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
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<Pedido> pedidos = new ArrayList<>();

    protected Usuario() {
    }

    public Usuario(String nombre, String email) {
        this.nombre = Objects.requireNonNull(nombre, "El nombre es obligatorio").trim();
        this.email = Objects.requireNonNull(email, "El email es obligatorio")
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    public void agregarPedido(Pedido pedido) {
        Objects.requireNonNull(pedido, "El pedido es obligatorio");
        pedidos.add(pedido);
        pedido.setUsuario(this);
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public List<Pedido> getPedidos() {
        return List.copyOf(pedidos);
    }
}

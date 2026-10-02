package com.tp.jpa.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<DetallePedido> detalles = new ArrayList<>();

    protected Pedido() {
    }

    public Pedido(LocalDateTime fecha, EstadoPedido estado) {
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria");
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = Objects.requireNonNull(usuario, "El usuario es obligatorio");
    }

    public void agregarDetalle(Producto producto, int cantidad) {
        detalles.add(new DetallePedido(this, producto, cantidad, producto.getPrecio()));
    }

    public void cambiarEstado(EstadoPedido estado) {
        this.estado = Objects.requireNonNull(estado, "El estado es obligatorio");
    }

    public Long getId() {
        return id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public List<DetallePedido> getDetalles() {
        return List.copyOf(detalles);
    }
}

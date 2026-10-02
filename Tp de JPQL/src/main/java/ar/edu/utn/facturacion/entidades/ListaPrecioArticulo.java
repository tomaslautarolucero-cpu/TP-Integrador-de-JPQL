package ar.edu.utn.facturacion.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "listas_precio_articulo")
public class ListaPrecioArticulo extends AuditoriaApp {

    @ManyToOne
    @JoinColumn(nullable = false)
    private ListaPrecio listaPrecio;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Articulo articulo;

    @Column(nullable = false)
    private Double precioVenta;

    public ListaPrecioArticulo() {
    }

    public ListaPrecio getListaPrecio() {
        return listaPrecio;
    }

    public void setListaPrecio(ListaPrecio listaPrecio) {
        this.listaPrecio = listaPrecio;
    }

    public Articulo getArticulo() {
        return articulo;
    }

    public void setArticulo(Articulo articulo) {
        this.articulo = articulo;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(Double precioVenta) {
        this.precioVenta = precioVenta;
    }
}

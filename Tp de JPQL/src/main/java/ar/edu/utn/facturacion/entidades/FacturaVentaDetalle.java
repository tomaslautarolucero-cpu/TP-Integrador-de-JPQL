package ar.edu.utn.facturacion.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "facturas_venta_detalle")
public class FacturaVentaDetalle extends EntityId {

    @ManyToOne
    @JoinColumn(name = "factura_id", nullable = false)
    private FacturaVenta factura;

    @ManyToOne
    @JoinColumn(nullable = false)
    private ListaPrecioArticulo listaPrecioArticulo;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;

    @Column(nullable = false)
    private Double importeSubtotal;

    @Column(length = 200)
    private String descripcion;

    private Double porcentajeBonificacion;

    private Double importeNeto;

    private Double importeIva;

    public FacturaVentaDetalle() {
    }

    public FacturaVenta getFactura() {
        return factura;
    }

    public void setFactura(FacturaVenta factura) {
        this.factura = factura;
    }

    public ListaPrecioArticulo getListaPrecioArticulo() {
        return listaPrecioArticulo;
    }

    public void setListaPrecioArticulo(ListaPrecioArticulo listaPrecioArticulo) {
        this.listaPrecioArticulo = listaPrecioArticulo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public Double getImporteSubtotal() {
        return importeSubtotal;
    }

    public void setImporteSubtotal(Double importeSubtotal) {
        this.importeSubtotal = importeSubtotal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPorcentajeBonificacion() {
        return porcentajeBonificacion;
    }

    public void setPorcentajeBonificacion(Double porcentajeBonificacion) {
        this.porcentajeBonificacion = porcentajeBonificacion;
    }

    public Double getImporteNeto() {
        return importeNeto;
    }

    public void setImporteNeto(Double importeNeto) {
        this.importeNeto = importeNeto;
    }

    public Double getImporteIva() {
        return importeIva;
    }

    public void setImporteIva(Double importeIva) {
        this.importeIva = importeIva;
    }
}

package ar.edu.utn.facturacion.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "condiciones_iva")
public class CondicionIva extends AuditoriaApp {

    @Column(nullable = false, length = 10)
    private String codigoAfip;

    @Column(nullable = false, length = 100)
    private String denominacion;

    public CondicionIva() {
    }

    public String getCodigoAfip() {
        return codigoAfip;
    }

    public void setCodigoAfip(String codigoAfip) {
        this.codigoAfip = codigoAfip;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }
}

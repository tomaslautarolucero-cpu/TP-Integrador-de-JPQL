package ar.edu.utn.facturacion.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "domicilios")
public class Domicilio extends EntityId {

    @Column(length = 100)
    private String nombreCalle;

    @Column(length = 20)
    private String numeroCalle;

    public Domicilio() {
    }

    public String getNombreCalle() {
        return nombreCalle;
    }

    public void setNombreCalle(String nombreCalle) {
        this.nombreCalle = nombreCalle;
    }

    public String getNumeroCalle() {
        return numeroCalle;
    }

    public void setNumeroCalle(String numeroCalle) {
        this.numeroCalle = numeroCalle;
    }
}

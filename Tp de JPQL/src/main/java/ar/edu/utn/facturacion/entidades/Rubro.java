package ar.edu.utn.facturacion.entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "rubros")
public class Rubro extends AuditoriaApp {

    @Column(nullable = false, length = 100)
    private String denominacion;

    @Column(nullable = false, length = 50)
    private String codigo;

    public Rubro() {
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}

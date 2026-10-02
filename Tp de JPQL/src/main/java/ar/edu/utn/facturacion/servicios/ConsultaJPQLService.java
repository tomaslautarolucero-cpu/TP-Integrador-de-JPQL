package ar.edu.utn.facturacion.servicios;

import ar.edu.utn.facturacion.entidades.Articulo;
import ar.edu.utn.facturacion.entidades.Cliente;
import ar.edu.utn.facturacion.entidades.FacturaVenta;
import ar.edu.utn.facturacion.entidades.FacturaVentaDetalle;
import ar.edu.utn.facturacion.entidades.Marca;
import ar.edu.utn.facturacion.entidades.PuntoVenta;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;

import java.util.Date;
import java.util.List;

public class ConsultaJPQLService {

    private final EntityManager em;

    public ConsultaJPQLService(EntityManager em) {
        this.em = em;
    }

    public EntityManager getEm() {
        return em;
    }

    public List<FacturaVenta> consulta01_TodasLasFacturasDeVenta() {
        String jpql = "SELECT f FROM FacturaVenta f ORDER BY f.numero";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    public List<Tuple> consulta02_ProyeccionNumeroFechaYTotales() {
        String jpql = "SELECT f.numero, f.fechaEmision, f.importeTotal "
                + "FROM FacturaVenta f "
                + "ORDER BY f.numero";
        return em.createQuery(jpql, Tuple.class).getResultList();
    }

    public List<Articulo> consulta03_ArticulosDeUnRubro(String denominacionRubro) {
        String jpql = "SELECT a FROM Articulo a "
                + "WHERE a.rubro.denominacion = :denominacionRubro "
                + "ORDER BY a.denominacion";
        return em.createQuery(jpql, Articulo.class)
                .setParameter("denominacionRubro", denominacionRubro)
                .getResultList();
    }

    public List<FacturaVenta> consulta04_FacturasPorRangoDeFechas(Date desde, Date hasta) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.fechaEmision BETWEEN :desde AND :hasta "
                + "ORDER BY f.fechaEmision";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("desde", desde)
                .setParameter("hasta", hasta)
                .getResultList();
    }

    public List<FacturaVenta> consulta05_FacturasEmitidasNoAnuladasSobreImporte(String estado, Double importeMinimo) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.estado = :estado "
                + "AND f.importeTotal > :importeMinimo "
                + "AND f.fechaAnulacion IS NULL "
                + "ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("estado", estado)
                .setParameter("importeMinimo", importeMinimo)
                .getResultList();
    }

    public List<Cliente> consulta06_ClientesPorDenominacionOCuitCuil(String textoDenominacion, String prefijoCuitCuil) {
        String jpql = "SELECT c FROM Cliente c "
                + "WHERE LOWER(c.denominacion) LIKE LOWER(:textoDenominacion) "
                + "OR c.cuitCuil LIKE :prefijoCuitCuil "
                + "ORDER BY c.denominacion";
        return em.createQuery(jpql, Cliente.class)
                .setParameter("textoDenominacion", textoDenominacion)
                .setParameter("prefijoCuitCuil", prefijoCuitCuil)
                .getResultList();
    }

    public List<String> consulta07_EstadosDistintosDeFactura() {
        String jpql = "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC";
        return em.createQuery(jpql, String.class).getResultList();
    }

    public Object[] consulta08_TotalFacturaSumaImporteYPromedio(String estado) {
        String jpql = "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) "
                + "FROM FacturaVenta f "
                + "WHERE f.estado = :estado";
        return em.createQuery(jpql, Object[].class)
                .setParameter("estado", estado)
                .getSingleResult();
    }

    public List<PuntoVenta> consulta09_PuntosVentaPorListaDeNumeros(List<Integer> numeros) {
        String jpql = "SELECT p FROM PuntoVenta p "
                + "WHERE p.numero IN :numeros "
                + "ORDER BY p.numero";
        return em.createQuery(jpql, PuntoVenta.class)
                .setParameter("numeros", numeros)
                .getResultList();
    }

    public List<FacturaVenta> consulta10_FacturasCargadasPorUsuario(String nombreUsuario) {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.usuarioCarga.usuario = :nombreUsuario "
                + "ORDER BY f.fechaEmision";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("nombreUsuario", nombreUsuario)
                .getResultList();
    }

    public List<FacturaVentaDetalle> consulta11_DetallesDeFacturasDeUnPuntoVenta(Integer numeroPuntoVenta) {
        String jpql = "SELECT d FROM FacturaVentaDetalle d "
                + "JOIN d.factura f "
                + "JOIN f.puntoVenta p "
                + "WHERE p.numero = :numeroPuntoVenta "
                + "ORDER BY f.numero, d.descripcion";
        return em.createQuery(jpql, FacturaVentaDetalle.class)
                .setParameter("numeroPuntoVenta", numeroPuntoVenta)
                .getResultList();
    }

    public List<Tuple> consulta12_ArticulosConSuMarcaIncluyendoSinMarca() {
        String jpql = "SELECT a.denominacion, m.denominacion "
                + "FROM Articulo a "
                + "LEFT JOIN a.marca m "
                + "ORDER BY a.denominacion";
        return em.createQuery(jpql, Tuple.class).getResultList();
    }

    public List<FacturaVenta> consulta13_FacturasQueContienenArticulosDeUnaMarca(String denominacionMarca) {
        String jpql = "SELECT DISTINCT f FROM FacturaVenta f "
                + "JOIN f.detalles d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "WHERE m.denominacion = :denominacionMarca "
                + "ORDER BY f.numero";
        return em.createQuery(jpql, FacturaVenta.class)
                .setParameter("denominacionMarca", denominacionMarca)
                .getResultList();
    }

    public List<FacturaVenta> consulta14_FacturasPorSobreElPromedio() {
        String jpql = "SELECT f FROM FacturaVenta f "
                + "WHERE f.importeTotal > (SELECT AVG(f2.importeTotal) FROM FacturaVenta f2) "
                + "ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, FacturaVenta.class).getResultList();
    }

    public List<Tuple> consulta15_ResumenFacturadoPorPuntoVenta() {
        String jpql = "SELECT p.descripcion, COUNT(f), SUM(f.importeTotal) "
                + "FROM FacturaVenta f "
                + "JOIN f.puntoVenta p "
                + "GROUP BY p.descripcion "
                + "ORDER BY SUM(f.importeTotal) DESC";
        return em.createQuery(jpql, Tuple.class).getResultList();
    }

    public List<String> consulta16_UsuariosDeCargaConMasDeNFacturas(Long cantidadMinima) {
        String jpql = "SELECT u.usuario "
                + "FROM FacturaVenta f "
                + "JOIN f.usuarioCarga u "
                + "GROUP BY u.usuario "
                + "HAVING COUNT(f) > :cantidadMinima "
                + "ORDER BY u.usuario";
        return em.createQuery(jpql, String.class)
                .setParameter("cantidadMinima", cantidadMinima)
                .getResultList();
    }

    public List<Tuple> consulta17_UnidadesVendidasYSubtotalPorMarca() {
        String jpql = "SELECT m.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) "
                + "FROM FacturaVentaDetalle d "
                + "JOIN d.listaPrecioArticulo lpa "
                + "JOIN lpa.articulo a "
                + "JOIN a.marca m "
                + "GROUP BY m.denominacion "
                + "ORDER BY SUM(d.importeSubtotal) DESC";
        return em.createQuery(jpql, Tuple.class).getResultList();
    }

    public List<Marca> consulta18_MarcasQueFueronFacturadas() {
        String jpql = "SELECT m FROM Marca m "
                + "WHERE EXISTS ("
                + "    SELECT d.id FROM FacturaVentaDetalle d "
                + "    JOIN d.listaPrecioArticulo lpa "
                + "    JOIN lpa.articulo a "
                + "    WHERE a.marca = m"
                + ") "
                + "ORDER BY m.denominacion";
        return em.createQuery(jpql, Marca.class).getResultList();
    }

    public List<Articulo> consulta19_ArticulosQueNuncaFueronFacturados() {
        String jpql = "SELECT a FROM Articulo a "
                + "WHERE NOT EXISTS ("
                + "    SELECT d.id FROM FacturaVentaDetalle d "
                + "    JOIN d.listaPrecioArticulo lpa "
                + "    WHERE lpa.articulo = a"
                + ") "
                + "ORDER BY a.denominacion";
        return em.createQuery(jpql, Articulo.class).getResultList();
    }

    public List<Tuple> consulta20_ClasificacionDeFacturasPorValor(Double limiteAlto, Double limiteMedio) {
        String jpql = "SELECT f.numero, f.importeTotal, "
                + "CASE "
                + "    WHEN f.importeTotal > :limiteAlto THEN 'ALTO VALOR' "
                + "    WHEN f.importeTotal >= :limiteMedio THEN 'MEDIO VALOR' "
                + "    ELSE 'BAJO VALOR' "
                + "END "
                + "FROM FacturaVenta f "
                + "ORDER BY f.importeTotal DESC";
        return em.createQuery(jpql, Tuple.class)
                .setParameter("limiteAlto", limiteAlto)
                .setParameter("limiteMedio", limiteMedio)
                .getResultList();
    }
}

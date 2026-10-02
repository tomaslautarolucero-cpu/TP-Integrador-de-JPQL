package ar.edu.utn.facturacion;

import ar.edu.utn.facturacion.entidades.Articulo;
import ar.edu.utn.facturacion.entidades.Cliente;
import ar.edu.utn.facturacion.entidades.FacturaVenta;
import ar.edu.utn.facturacion.entidades.FacturaVentaDetalle;
import ar.edu.utn.facturacion.entidades.Marca;
import ar.edu.utn.facturacion.entidades.PuntoVenta;
import ar.edu.utn.facturacion.servicios.ConsultaJPQLService;
import ar.edu.utn.facturacion.servicios.DatosJPQL;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.Tuple;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainJPQL {

    private static final SimpleDateFormat FORMATO_FECHA = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String UNIDAD_PERSISTENCIA = "FacturacionPU";

    private static final String RUBRO_EJEMPLO = "Electronica";
    private static final String MARCA_EJEMPLO = "Sony";
    private static final String USUARIO_EJEMPLO = "admin";
    private static final String ESTADO_EMITIDA = "EMITIDA";
    private static final Double IMPORTE_MINIMO = 10000.0;
    private static final Double LIMITE_ALTO_VALOR = 50000.0;
    private static final Double LIMITE_MEDIO_VALOR = 10000.0;
    private static final Long CANTIDAD_MINIMA_FACTURAS = 5L;
    private static final List<Integer> NUMEROS_PUNTO_VENTA = Arrays.asList(1, 2, 5);
    private static final String TEXTO_DENOMINACION = "%acme%";
    private static final String PREFIJO_CUIT_CUIL = "20-%";
    private static final Integer NUMERO_PUNTO_VENTA = 1;
    private static final Date FECHA_DESDE = fecha(2026, 1, 1);
    private static final Date FECHA_HASTA = fecha(2026, 3, 31);

    public static void main(String[] args) {
        EntityManagerFactory emf = crearEntityManagerFactory();
        EntityManager em = emf.createEntityManager();

        ConsultaJPQLService servicio = new ConsultaJPQLService(em);

        try {
            titulo("TP GRUPAL 2 - CONSULTAS AVANZADAS CON JPQL");
            System.out.println("Unidad de persistencia: " + UNIDAD_PERSISTENCIA);
            System.out.println("Base de datos         : PostgreSQL");
            System.out.println();

            System.out.println("---------------------------------------------------------");
            System.out.println("CARGA DE DATOS DE PRUEBA");
            System.out.println("---------------------------------------------------------");
            DatosJPQL.sembrar(em);
            System.out.println();

            nivel1(servicio);
            nivel2(servicio);
            nivel3(servicio);
            nivel4(servicio);
            nivel5(servicio);

            System.out.println();
            System.out.println("=========================================================");
            System.out.println("FIN DE LAS 20 CONSULTAS - todas finalizaron sin error");
            System.out.println("=========================================================");
        } catch (RuntimeException e) {
            System.out.println();
            System.out.println("ERROR al ejecutar las consultas JPQL: " + e.getMessage());
            e.printStackTrace(System.out);
        } finally {
            em.close();
            emf.close();
        }
    }

    private static EntityManagerFactory crearEntityManagerFactory() {
        Map<String, Object> sobreescrituras = new HashMap<>();
        agregarSiEsta(sobreescrituras, "jakarta.persistence.jdbc.url", "db.url");
        agregarSiEsta(sobreescrituras, "jakarta.persistence.jdbc.user", "db.user");
        agregarSiEsta(sobreescrituras, "jakarta.persistence.jdbc.password", "db.password");
        return Persistence.createEntityManagerFactory(UNIDAD_PERSISTENCIA, sobreescrituras);
    }

    private static void agregarSiEsta(Map<String, Object> destino, String propiedad, String sistema) {
        String valor = System.getProperty(sistema);
        if (valor != null && !valor.isBlank()) {
            destino.put(propiedad, valor);
        }
    }

    private static void nivel1(ConsultaJPQLService servicio) {
        encabezado("NIVEL 1 - CONSULTAS BASICAS Y PROYECCIONES");

        List<FacturaVenta> todas = servicio.consulta01_TodasLasFacturasDeVenta();
        System.out.println("[01] Todas las facturas de venta: " + todas.size() + " resultado(s)");
        for (FacturaVenta f : todas) {
            System.out.println("     #" + f.getNumero() + " | " + formatear(f.getFechaEmision())
                    + " | " + f.getEstado() + " | $" + f.getImporteTotal());
        }
        System.out.println();

        List<Tuple> proyeccion = servicio.consulta02_ProyeccionNumeroFechaYTotales();
        System.out.println("[02] Proyeccion numero / fechaEmision / importeTotal: " + proyeccion.size() + " resultado(s)");
        System.out.println("     numero | fechaEmision | importeTotal");
        for (Tuple t : proyeccion) {
            System.out.println("     " + t.get(0) + " | " + formatear((Date) t.get(1)) + " | $" + t.get(2));
        }
        System.out.println();

        List<Articulo> porRubro = servicio.consulta03_ArticulosDeUnRubro(RUBRO_EJEMPLO);
        System.out.println("[03] Articulos del rubro '" + RUBRO_EJEMPLO + "': " + porRubro.size() + " resultado(s)");
        for (Articulo a : porRubro) {
            System.out.println("     " + a.getCodigo() + " | " + a.getDenominacion());
        }
        System.out.println();

        List<FacturaVenta> porRango = servicio.consulta04_FacturasPorRangoDeFechas(FECHA_DESDE, FECHA_HASTA);
        System.out.println("[04] Facturas emitidas entre " + formatear(FECHA_DESDE) + " y " + formatear(FECHA_HASTA)
                + ": " + porRango.size() + " resultado(s)");
        for (FacturaVenta f : porRango) {
            System.out.println("     #" + f.getNumero() + " | " + formatear(f.getFechaEmision())
                    + " | $" + f.getImporteTotal());
        }
        System.out.println();
    }

    private static void nivel2(ConsultaJPQLService servicio) {
        encabezado("NIVEL 2 - CONDICIONALES COMBINADOS, TEXTO Y AGREGACIONES");

        List<FacturaVenta> emitidas = servicio.consulta05_FacturasEmitidasNoAnuladasSobreImporte(
                ESTADO_EMITIDA, IMPORTE_MINIMO);
        System.out.println("[05] Facturas " + ESTADO_EMITIDA + " sin anular y sobre $" + IMPORTE_MINIMO
                + ": " + emitidas.size() + " resultado(s)");
        for (FacturaVenta f : emitidas) {
            System.out.println("     #" + f.getNumero() + " | $" + f.getImporteTotal()
                    + " | fechaAnulacion=" + f.getFechaAnulacion());
        }
        System.out.println();

        List<Cliente> clientes = servicio.consulta06_ClientesPorDenominacionOCuitCuil(
                TEXTO_DENOMINACION, PREFIJO_CUIT_CUIL);
        System.out.println("[06] Clientes con denominacion que contiene '" + TEXTO_DENOMINACION
                + "' o cuitCuil que empieza con '" + PREFIJO_CUIT_CUIL + "': " + clientes.size() + " resultado(s)");
        for (Cliente c : clientes) {
            System.out.println("     " + c.getDenominacion() + " | CUIT/CUIL: " + c.getCuitCuil());
        }
        System.out.println();

        List<String> estados = servicio.consulta07_EstadosDistintosDeFactura();
        System.out.println("[07] Estados distintos registrados: " + estados.size() + " resultado(s) -> " + estados);
        System.out.println();

        Object[] totales = servicio.consulta08_TotalFacturaSumaImporteYPromedio(ESTADO_EMITIDA);
        System.out.println("[08] Agregacion sobre facturas " + ESTADO_EMITIDA + " (count, sum, avg) en un solo objeto:");
        System.out.println("     cantidad total  = " + totales[0]);
        System.out.println("     suma importes   = " + totales[1]);
        System.out.println("     importe promedio= " + totales[2]);
        System.out.println();

        List<PuntoVenta> puntosVenta = servicio.consulta09_PuntosVentaPorListaDeNumeros(NUMEROS_PUNTO_VENTA);
        System.out.println("[09] Puntos de venta cuyo numero esta en " + NUMEROS_PUNTO_VENTA
                + ": " + puntosVenta.size() + " resultado(s)");
        for (PuntoVenta p : puntosVenta) {
            System.out.println("     Nro " + p.getNumero() + " | " + p.getDescripcion());
        }
        System.out.println();
    }

    private static void nivel3(ConsultaJPQLService servicio) {
        encabezado("NIVEL 3 - NAVEGACION DE ENTIDADES, JOINS Y SUBCONSULTAS");

        List<FacturaVenta> porUsuario = servicio.consulta10_FacturasCargadasPorUsuario(USUARIO_EJEMPLO);
        System.out.println("[10] Facturas cargadas por el usuario '" + USUARIO_EJEMPLO + "': "
                + porUsuario.size() + " resultado(s)");
        for (FacturaVenta f : porUsuario) {
            System.out.println("     #" + f.getNumero() + " | " + formatear(f.getFechaEmision())
                    + " | $" + f.getImporteTotal());
        }
        System.out.println();

        List<FacturaVentaDetalle> detalles = servicio.consulta11_DetallesDeFacturasDeUnPuntoVenta(
                NUMERO_PUNTO_VENTA);
        System.out.println("[11] Detalles de factura del punto de venta Nro " + NUMERO_PUNTO_VENTA + ": "
                + detalles.size() + " resultado(s)");
        for (FacturaVentaDetalle d : detalles) {
            System.out.println("     factura #" + d.getFactura().getNumero()
                    + " | cantidad=" + d.getCantidad()
                    + " | " + d.getDescripcion()
                    + " | subtotal=$" + d.getImporteSubtotal());
        }
        System.out.println();

        List<Tuple> articulosConMarca = servicio.consulta12_ArticulosConSuMarcaIncluyendoSinMarca();
        System.out.println("[12] Articulos con su marca (LEFT JOIN, incluye los que no tienen): "
                + articulosConMarca.size() + " resultado(s)");
        System.out.println("     articulo | marca");
        for (Tuple t : articulosConMarca) {
            System.out.println("     " + t.get(0) + " | " + (t.get(1) == null ? "(sin marca)" : t.get(1)));
        }
        System.out.println();

        List<FacturaVenta> porMarca = servicio.consulta13_FacturasQueContienenArticulosDeUnaMarca(MARCA_EJEMPLO);
        System.out.println("[13] Facturas que contienen articulos de la marca '" + MARCA_EJEMPLO + "': "
                + porMarca.size() + " resultado(s)");
        for (FacturaVenta f : porMarca) {
            System.out.println("     #" + f.getNumero() + " | $" + f.getImporteTotal());
        }
        System.out.println();

        List<FacturaVenta> sobrePromedio = servicio.consulta14_FacturasPorSobreElPromedio();
        System.out.println("[14] Facturas con importeTotal estrictamente mayor al promedio general: "
                + sobrePromedio.size() + " resultado(s)");
        for (FacturaVenta f : sobrePromedio) {
            System.out.println("     #" + f.getNumero() + " | $" + f.getImporteTotal());
        }
        System.out.println();
    }

    private static void nivel4(ConsultaJPQLService servicio) {
        encabezado("NIVEL 4 - AGRUPAMIENTO (GROUP BY) Y FILTROS DE GRUPO (HAVING)");

        List<Tuple> porPuntoVenta = servicio.consulta15_ResumenFacturadoPorPuntoVenta();
        System.out.println("[15] Resumen por punto de venta (GROUP BY): " + porPuntoVenta.size() + " fila(s)");
        System.out.println("     punto de venta | cantidad de facturas | suma facturada");
        for (Tuple t : porPuntoVenta) {
            System.out.println("     " + t.get(0) + " | " + t.get(1) + " | $" + t.get(2));
        }
        System.out.println();

        List<String> usuarios = servicio.consulta16_UsuariosDeCargaConMasDeNFacturas(
                CANTIDAD_MINIMA_FACTURAS);
        System.out.println("[16] Usuarios de carga con mas de " + CANTIDAD_MINIMA_FACTURAS
                + " facturas (HAVING): " + usuarios.size() + " resultado(s) -> " + usuarios);
        System.out.println();

        List<Tuple> porMarca = servicio.consulta17_UnidadesVendidasYSubtotalPorMarca();
        System.out.println("[17] Unidades vendidas y subtotal por marca (GROUP BY): " + porMarca.size() + " fila(s)");
        System.out.println("     marca | unidades vendidas | subtotal acumulado");
        for (Tuple t : porMarca) {
            System.out.println("     " + t.get(0) + " | " + t.get(1) + " | $" + t.get(2));
        }
        System.out.println();
    }

    private static void nivel5(ConsultaJPQLService servicio) {
        encabezado("NIVEL 5 - SUBCONSULTAS CORRELACIONADAS, EXISTS Y EXPRESIONES CONDICIONALES");

        List<Marca> marcasFacturadas = servicio.consulta18_MarcasQueFueronFacturadas();
        System.out.println("[18] Marcas con al menos un articulo facturado (EXISTS): "
                + marcasFacturadas.size() + " resultado(s)");
        for (Marca m : marcasFacturadas) {
            System.out.println("     " + m.getCodigo() + " | " + m.getDenominacion());
        }
        System.out.println();

        List<Articulo> nuncaFacturados = servicio.consulta19_ArticulosQueNuncaFueronFacturados();
        System.out.println("[19] Articulos que nunca aparecen en un detalle de factura (NOT EXISTS): "
                + nuncaFacturados.size() + " resultado(s)");
        for (Articulo a : nuncaFacturados) {
            System.out.println("     " + a.getCodigo() + " | " + a.getDenominacion());
        }
        System.out.println();

        List<Tuple> clasificadas = servicio.consulta20_ClasificacionDeFacturasPorValor(
                LIMITE_ALTO_VALOR, LIMITE_MEDIO_VALOR);
        System.out.println("[20] Facturas clasificadas por valor (CASE): " + clasificadas.size() + " resultado(s)");
        System.out.println("     numero | importeTotal | Categoria");
        for (Tuple t : clasificadas) {
            System.out.println("     " + t.get(0) + " | $" + t.get(1) + " | " + t.get(2));
        }
        System.out.println();
    }

    private static Date fecha(int anio, int mes, int dia) {
        return Date.from(LocalDate.of(anio, mes, dia).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static String formatear(Date fecha) {
        return fecha == null ? "(nula)" : FORMATO_FECHA.format(fecha);
    }

    private static void titulo(String texto) {
        System.out.println("=========================================================");
        System.out.println(texto);
        System.out.println("=========================================================");
    }

    private static void encabezado(String texto) {
        System.out.println("---------------------------------------------------------");
        System.out.println(texto);
        System.out.println("---------------------------------------------------------");
    }
}

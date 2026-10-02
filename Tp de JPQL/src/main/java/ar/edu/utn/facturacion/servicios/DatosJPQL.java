package ar.edu.utn.facturacion.servicios;

import ar.edu.utn.facturacion.entidades.Articulo;
import ar.edu.utn.facturacion.entidades.Cliente;
import ar.edu.utn.facturacion.entidades.CondicionIva;
import ar.edu.utn.facturacion.entidades.Contacto;
import ar.edu.utn.facturacion.entidades.Domicilio;
import ar.edu.utn.facturacion.entidades.FacturaVenta;
import ar.edu.utn.facturacion.entidades.FacturaVentaDetalle;
import ar.edu.utn.facturacion.entidades.ListaPrecio;
import ar.edu.utn.facturacion.entidades.ListaPrecioArticulo;
import ar.edu.utn.facturacion.entidades.Marca;
import ar.edu.utn.facturacion.entidades.PuntoVenta;
import ar.edu.utn.facturacion.entidades.Rubro;
import ar.edu.utn.facturacion.entidades.TipoMoneda;
import ar.edu.utn.facturacion.entidades.Usuario;

import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class DatosJPQL {

    private static final double IVA = 21.0;
    private static final String ESTADO_EMITIDA = "EMITIDA";
    private static final String ESTADO_ANULADA = "ANULADA";

    public static void sembrar(EntityManager em) {
        if (!Boolean.parseBoolean(System.getProperty("jpql.sembrar", "true"))) {
            System.out.println("     Sembrado omitido por la propiedad jpql.sembrar=false.");
            return;
        }

        em.getTransaction().begin();
        try {
            purgar(em);

            Usuario admin = usuario(em, "admin", "Laura", "Gomez");
            Usuario martin = usuario(em, "mgomez", "Martin", "Gomez");
            Usuario rita = usuario(em, "rprincipi", "Rita", "Principi");

            PuntoVenta pvPalermo = puntoVenta(em, 1, "Sucursal Palermo", admin);
            PuntoVenta pvBelgrano = puntoVenta(em, 2, "Sucursal Belgrano", admin);

            CondicionIva ivaResponsable = condicionIva(em, "01", "IVA Responsable Inscripto", admin);
            CondicionIva ivaConsumidor = condicionIva(em, "05", "Consumidor Final", admin);
            TipoMoneda pesos = tipoMoneda(em, admin);
            ListaPrecio listaPrecio = listaPrecio(em, admin);

            Rubro electronica = rubro(em, "R-ELC", "Electronica", admin);
            Rubro almacen = rubro(em, "R-ALM", "Almacen", admin);

            Marca sony = marca(em, "M-SNY", "Sony", admin);
            Marca logitech = marca(em, "M-LOG", "Logitech", admin);
            Marca generica = marca(em, "M-GEN", "Generica", admin);

            Articulo notebook = articulo(em, "ART-001", "Notebook 14\"", electronica, sony, admin);
            Articulo mouse = articulo(em, "ART-002", "Mouse inalambrico", electronica, logitech, admin);
            Articulo teclado = articulo(em, "ART-003", "Teclado mecanico", electronica, logitech, admin);
            Articulo harina = articulo(em, "ART-004", "Harina 000", almacen, generica, admin);
            Articulo arroz = articulo(em, "ART-005", "Arroz 5kg", almacen, generica, admin);
            Articulo boletin = articulo(em, "ART-006", "Boletin sin marca", almacen, null, admin);
            articulo(em, "ART-007", "Auriculares premium", electronica, sony, admin);

            ListaPrecioArticulo lpaNotebook = listaPrecioArticulo(em, listaPrecio, notebook, 150000.0, admin);
            ListaPrecioArticulo lpaMouse = listaPrecioArticulo(em, listaPrecio, mouse, 25000.0, admin);
            ListaPrecioArticulo lpaTeclado = listaPrecioArticulo(em, listaPrecio, teclado, 45000.0, admin);
            ListaPrecioArticulo lpaHarina = listaPrecioArticulo(em, listaPrecio, harina, 3000.0, admin);
            ListaPrecioArticulo lpaArroz = listaPrecioArticulo(em, listaPrecio, arroz, 5000.0, admin);
            ListaPrecioArticulo lpaBoletin = listaPrecioArticulo(em, listaPrecio, boletin, 1500.0, admin);

            Cliente acme = cliente(em, "30123456789", "ACME S.A.", "compras@acme.com.ar", admin);
            Cliente sur = cliente(em, "20-12345678-9", "Distribuidora del Sur", "ventas@delsur.com.ar", admin);
            Cliente bruno = cliente(em, "20-98765432-1", "Bruno Rossi", "bruno.rossi@mail.com", admin);
            Cliente carla = cliente(em, "27-11111111-4", "Carla Mendez", "carla.mendez@mail.com", admin);

            factura(em, pesos, 1, LocalDate.of(2026, 1, 5), ESTADO_EMITIDA, null,
                    acme, pvPalermo, ivaResponsable, admin,
                    new LineaFactura(lpaHarina, 1, "Harina 000"));

            factura(em, pesos, 2, LocalDate.of(2026, 1, 10), ESTADO_EMITIDA, null,
                    acme, pvPalermo, ivaResponsable, admin,
                    new LineaFactura(lpaMouse, 1, "Mouse inalambrico"));

            factura(em, pesos, 3, LocalDate.of(2026, 1, 15), ESTADO_EMITIDA, null,
                    sur, pvPalermo, ivaResponsable, admin,
                    new LineaFactura(lpaNotebook, 1, "Notebook 14\""));

            factura(em, pesos, 4, LocalDate.of(2026, 1, 20), ESTADO_EMITIDA, null,
                    bruno, pvPalermo, ivaConsumidor, admin,
                    new LineaFactura(lpaTeclado, 1, "Teclado mecanico"));

            factura(em, pesos, 5, LocalDate.of(2026, 1, 25), ESTADO_EMITIDA, null,
                    acme, pvPalermo, ivaResponsable, admin,
                    new LineaFactura(lpaHarina, 5, "Harina 000"));

            factura(em, pesos, 6, LocalDate.of(2026, 2, 2), ESTADO_EMITIDA, null,
                    carla, pvPalermo, ivaConsumidor, admin,
                    new LineaFactura(lpaBoletin, 1, "Boletin sin marca"));

            factura(em, pesos, 7, LocalDate.of(2026, 2, 10), ESTADO_EMITIDA, null,
                    acme, pvBelgrano, ivaResponsable, admin,
                    new LineaFactura(lpaMouse, 3, "Mouse inalambrico"));

            factura(em, pesos, 8, LocalDate.of(2026, 2, 15), ESTADO_EMITIDA, null,
                    bruno, pvBelgrano, ivaConsumidor, martin,
                    new LineaFactura(lpaMouse, 1, "Mouse inalambrico"),
                    new LineaFactura(lpaBoletin, 1, "Boletin sin marca"));

            factura(em, pesos, 9, LocalDate.of(2026, 3, 1), ESTADO_ANULADA, LocalDate.of(2026, 3, 4),
                    sur, pvBelgrano, ivaResponsable, rita,
                    new LineaFactura(lpaNotebook, 1, "Notebook 14\""));

            factura(em, pesos, 10, LocalDate.of(2026, 3, 5), ESTADO_EMITIDA, null,
                    carla, pvBelgrano, ivaConsumidor, rita,
                    new LineaFactura(lpaArroz, 1, "Arroz 5kg"));

            em.getTransaction().commit();

            System.out.println("     Base reinicializada con el juego de datos del TP:");
            System.out.println("       3 usuarios | 2 puntos de venta | 2 rubros | 3 marcas");
            System.out.println("       7 articulos (ART-006 sin marca, ART-007 nunca facturado)");
            System.out.println("       4 clientes | 10 facturas (9 EMITIDA + 1 ANULADA)");
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    private static void purgar(EntityManager em) {
        String[] entidades = {
                "FacturaVentaDetalle",
                "FacturaVenta",
                "ListaPrecioArticulo",
                "Articulo",
                "ListaPrecio",
                "Rubro",
                "Marca",
                "Cliente",
                "Contacto",
                "Domicilio",
                "PuntoVenta",
                "CondicionIva",
                "TipoMoneda",
                "Usuario"
        };

        for (String entidad : entidades) {
            em.createQuery("DELETE FROM " + entidad + " e").executeUpdate();
        }
        em.flush();
        em.clear();
    }

    private record LineaFactura(ListaPrecioArticulo listaPrecioArticulo, int cantidad, String descripcion) {
    }

    private static FacturaVenta factura(EntityManager em, TipoMoneda tipoMoneda, long numero,
                                        LocalDate fechaEmision, String estado, LocalDate fechaAnulacion,
                                        Cliente cliente, PuntoVenta puntoVenta, CondicionIva condicionIva,
                                        Usuario usuario, LineaFactura... lineas) {
        FacturaVenta factura = new FacturaVenta();
        factura.setNumero(numero);
        factura.setFechaEmision(aFecha(fechaEmision));
        factura.setEstado(estado);
        factura.setCae(String.format("750%011d", numero));
        factura.setResultadoAfip("A");
        factura.setObservaciones("Factura de prueba - TP JPA JPQL");
        factura.setCliente(cliente);
        factura.setPuntoVenta(puntoVenta);
        factura.setCondicionIva(condicionIva);
        factura.setTipoMoneda(tipoMoneda);
        factura.setUsuarioCarga(usuario);
        factura.setUsuarioModificacion(usuario);

        if (fechaAnulacion != null) {
            factura.setFechaAnulacion(aFecha(fechaAnulacion));
            factura.setResultadoAfip("R");
            factura.setMotivoRechazo("Anulada a pedido del cliente");
        }

        double total = 0;
        for (LineaFactura linea : lineas) {
            double unitario = linea.listaPrecioArticulo().getPrecioVenta();
            double subtotal = redondear(unitario * linea.cantidad());
            double iva = redondear(subtotal * IVA / 100.0);

            FacturaVentaDetalle detalle = new FacturaVentaDetalle();
            detalle.setListaPrecioArticulo(linea.listaPrecioArticulo());
            detalle.setCantidad(linea.cantidad());
            detalle.setPrecioUnitario(unitario);
            detalle.setImporteSubtotal(subtotal);
            detalle.setImporteNeto(subtotal);
            detalle.setImporteIva(iva);
            detalle.setPorcentajeBonificacion(0.0);
            detalle.setDescripcion(linea.descripcion());

            factura.agregarDetalle(detalle);
            total += subtotal + iva;
        }

        factura.setImporteTotal(redondear(total));
        factura.setImporteCobrado(ESTADO_EMITIDA.equals(estado) ? redondear(total) : 0.0);
        factura.setImporteSaldo(redondear(factura.getImporteTotal() - factura.getImporteCobrado()));

        em.persist(factura);
        return factura;
    }

    private static Usuario usuario(EntityManager em, String usuario, String nombre, String apellido) {
        Usuario entity = new Usuario();
        entity.setUsuario(usuario);
        entity.setClave(usuario + "123");
        entity.setNombre(nombre);
        entity.setApellido(apellido);
        em.persist(entity);
        return entity;
    }

    private static PuntoVenta puntoVenta(EntityManager em, int numero, String descripcion, Usuario usuario) {
        PuntoVenta entity = new PuntoVenta();
        entity.setNumero(numero);
        entity.setDescripcion(descripcion);
        entity.setTipoEmision("A");
        entity.setDomicilioComercial("Av. Corrientes " + (1000 + numero));
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static CondicionIva condicionIva(EntityManager em, String codigo, String denominacion, Usuario usuario) {
        CondicionIva entity = new CondicionIva();
        entity.setCodigoAfip(codigo);
        entity.setDenominacion(denominacion);
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static TipoMoneda tipoMoneda(EntityManager em, Usuario usuario) {
        TipoMoneda entity = new TipoMoneda();
        entity.setCodigoAfip("PES");
        entity.setDenominacion("Pesos Argentinos");
        entity.setSimbolo("$");
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static ListaPrecio listaPrecio(EntityManager em, Usuario usuario) {
        ListaPrecio entity = new ListaPrecio();
        entity.setCodigo("LP-2026-01");
        entity.setDenominacion("Lista Mayorista 2026");
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static Rubro rubro(EntityManager em, String codigo, String denominacion, Usuario usuario) {
        Rubro entity = new Rubro();
        entity.setCodigo(codigo);
        entity.setDenominacion(denominacion);
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static Marca marca(EntityManager em, String codigo, String denominacion, Usuario usuario) {
        Marca entity = new Marca();
        entity.setCodigo(codigo);
        entity.setDenominacion(denominacion);
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static Articulo articulo(EntityManager em, String codigo, String denominacion, Rubro rubro,
                                     Marca marca, Usuario usuario) {
        Articulo entity = new Articulo();
        entity.setCodigo(codigo);
        entity.setDenominacion(denominacion);
        entity.setRubro(rubro);
        entity.setMarca(marca);
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static ListaPrecioArticulo listaPrecioArticulo(EntityManager em, ListaPrecio listaPrecio,
                                                           Articulo articulo, double precioVenta, Usuario usuario) {
        ListaPrecioArticulo entity = new ListaPrecioArticulo();
        entity.setListaPrecio(listaPrecio);
        entity.setArticulo(articulo);
        entity.setPrecioVenta(precioVenta);
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static Cliente cliente(EntityManager em, String cuitCuil, String denominacion,
                                   String email, Usuario usuario) {
        Contacto contacto = new Contacto();
        contacto.setEmail(email);
        contacto.setTelefono("011-4321-0000");
        contacto.setCelular("11-5555-1111");
        em.persist(contacto);

        Domicilio domicilio = new Domicilio();
        domicilio.setNombreCalle("Av. Corrientes");
        domicilio.setNumeroCalle("1234");
        em.persist(domicilio);

        Cliente entity = new Cliente();
        entity.setCuitCuil(cuitCuil);
        entity.setDenominacion(denominacion);
        entity.setContacto(contacto);
        entity.setDomicilio(domicilio);
        entity.setUsuarioCarga(usuario);
        entity.setUsuarioModificacion(usuario);
        em.persist(entity);
        return entity;
    }

    private static Date aFecha(LocalDate fecha) {
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    private static double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }
}

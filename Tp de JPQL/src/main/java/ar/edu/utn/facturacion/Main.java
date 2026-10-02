package ar.edu.utn.facturacion;

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
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Date;
import java.util.List;

public class Main {

    private static final double IVA = 21.0;

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FacturacionPU");
        EntityManager em = emf.createEntityManager();

        try {
            em.getTransaction().begin();

            Usuario usuario = crearUsuario(em);
            Cliente cliente = crearCliente(em, usuario);
            PuntoVenta puntoVenta = crearPuntoVenta(em, usuario);
            CondicionIva condicionIva = crearCondicionIva(em, usuario);
            TipoMoneda tipoMoneda = crearTipoMoneda(em, usuario);
            Marca marca = crearMarca(em, usuario);
            Rubro rubro = crearRubro(em, usuario);
            Articulo articuloUno = crearArticulo(em, usuario, rubro, marca, "ART-001", "Notebook 14\"");
            Articulo articuloDos = crearArticulo(em, usuario, rubro, marca, "ART-002", "Mouse inalambrico");
            ListaPrecio listaPrecio = crearListaPrecio(em, usuario);
            ListaPrecioArticulo lpaUno = crearListaPrecioArticulo(em, usuario, listaPrecio, articuloUno, 150000.0);
            ListaPrecioArticulo lpaDos = crearListaPrecioArticulo(em, usuario, listaPrecio, articuloDos, 25000.0);

            FacturaVentaDetalle detalleUno = crearDetalle(lpaUno, 2, "Notebook 14\"");
            FacturaVentaDetalle detalleDos = crearDetalle(lpaDos, 5, "Mouse inalambrico");

            FacturaVenta facturaVenta = new FacturaVenta();
            facturaVenta.setNumero(1L);
            facturaVenta.setFechaEmision(new Date());
            facturaVenta.setCliente(cliente);
            facturaVenta.setPuntoVenta(puntoVenta);
            facturaVenta.setCondicionIva(condicionIva);
            facturaVenta.setTipoMoneda(tipoMoneda);
            facturaVenta.setEstado("EMITIDA");
            facturaVenta.setCae("75012345678901");
            facturaVenta.setObservaciones("Factura de prueba - TP JPA Hibernate");
            facturaVenta.setUsuarioCarga(usuario);
            facturaVenta.setUsuarioModificacion(usuario);
            facturaVenta.setImporteTotal(detalleUno.getImporteNeto() + detalleUno.getImporteIva()
                    + detalleDos.getImporteNeto() + detalleDos.getImporteIva());
            facturaVenta.setImporteCobrado(facturaVenta.getImporteTotal());
            facturaVenta.setImporteSaldo(0.0);

            facturaVenta.agregarDetalle(detalleUno);
            facturaVenta.agregarDetalle(detalleDos);

            em.persist(facturaVenta);

            em.getTransaction().commit();

            System.out.println(">>> Transaccion confirmada. Cabecera factura id=" + facturaVenta.getId());
            System.out.println(">>> fechaAlta (seteada por @PrePersist) = " + facturaVenta.getFechaAlta());
            System.out.println(">>> fechaModificacion (@PrePersist)     = " + facturaVenta.getFechaModificacion());

            verificarCascade(em, facturaVenta.getId());
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }

    private static Usuario crearUsuario(EntityManager em) {
        Usuario usuario = new Usuario();
        usuario.setUsuario("admin");
        usuario.setClave("admin123");
        usuario.setNombre("Laura");
        usuario.setApellido("Gomez");
        em.persist(usuario);
        return usuario;
    }

    private static Cliente crearCliente(EntityManager em, Usuario usuario) {
        Contacto contacto = new Contacto();
        contacto.setEmail("compras@acme.com.ar");
        contacto.setTelefono("011-4321-0000");
        contacto.setCelular("11-5555-1111");
        em.persist(contacto);

        Domicilio domicilio = new Domicilio();
        domicilio.setNombreCalle("Av. Corrientes");
        domicilio.setNumeroCalle("1234");
        em.persist(domicilio);

        Cliente cliente = new Cliente();
        cliente.setCuitCuil("30123456789");
        cliente.setDenominacion("ACME S.A.");
        cliente.setContacto(contacto);
        cliente.setDomicilio(domicilio);
        cliente.setUsuarioCarga(usuario);
        cliente.setUsuarioModificacion(usuario);
        em.persist(cliente);
        return cliente;
    }

    private static PuntoVenta crearPuntoVenta(EntityManager em, Usuario usuario) {
        PuntoVenta puntoVenta = new PuntoVenta();
        puntoVenta.setNumero(1);
        puntoVenta.setDescripcion("Sucursal Palermo");
        puntoVenta.setTipoEmision("A");
        puntoVenta.setDomicilioComercial("Av. Corrientes 1234, CABA");
        puntoVenta.setUsuarioCarga(usuario);
        puntoVenta.setUsuarioModificacion(usuario);
        em.persist(puntoVenta);
        return puntoVenta;
    }

    private static CondicionIva crearCondicionIva(EntityManager em, Usuario usuario) {
        CondicionIva condicionIva = new CondicionIva();
        condicionIva.setCodigoAfip("01");
        condicionIva.setDenominacion("IVA Responsable Inscripto");
        condicionIva.setUsuarioCarga(usuario);
        condicionIva.setUsuarioModificacion(usuario);
        em.persist(condicionIva);
        return condicionIva;
    }

    private static TipoMoneda crearTipoMoneda(EntityManager em, Usuario usuario) {
        TipoMoneda tipoMoneda = new TipoMoneda();
        tipoMoneda.setCodigoAfip("PES");
        tipoMoneda.setDenominacion("Pesos Argentinos");
        tipoMoneda.setSimbolo("$");
        tipoMoneda.setUsuarioCarga(usuario);
        tipoMoneda.setUsuarioModificacion(usuario);
        em.persist(tipoMoneda);
        return tipoMoneda;
    }

    private static Marca crearMarca(EntityManager em, Usuario usuario) {
        Marca marca = new Marca();
        marca.setCodigo("M-GEN");
        marca.setDenominacion("Generica");
        marca.setUsuarioCarga(usuario);
        marca.setUsuarioModificacion(usuario);
        em.persist(marca);
        return marca;
    }

    private static Rubro crearRubro(EntityManager em, Usuario usuario) {
        Rubro rubro = new Rubro();
        rubro.setCodigo("R-TEC");
        rubro.setDenominacion("Tecnologia");
        rubro.setUsuarioCarga(usuario);
        rubro.setUsuarioModificacion(usuario);
        em.persist(rubro);
        return rubro;
    }

    private static Articulo crearArticulo(EntityManager em, Usuario usuario, Rubro rubro, Marca marca,
                                           String codigo, String denominacion) {
        Articulo articulo = new Articulo();
        articulo.setCodigo(codigo);
        articulo.setDenominacion(denominacion);
        articulo.setRubro(rubro);
        articulo.setMarca(marca);
        articulo.setUsuarioCarga(usuario);
        articulo.setUsuarioModificacion(usuario);
        em.persist(articulo);
        return articulo;
    }

    private static ListaPrecio crearListaPrecio(EntityManager em, Usuario usuario) {
        ListaPrecio listaPrecio = new ListaPrecio();
        listaPrecio.setCodigo("LP-2026-01");
        listaPrecio.setDenominacion("Lista Mayorista 2026");
        listaPrecio.setUsuarioCarga(usuario);
        listaPrecio.setUsuarioModificacion(usuario);
        em.persist(listaPrecio);
        return listaPrecio;
    }

    private static ListaPrecioArticulo crearListaPrecioArticulo(EntityManager em, Usuario usuario,
                                                                ListaPrecio listaPrecio, Articulo articulo,
                                                                double precioVenta) {
        ListaPrecioArticulo lpa = new ListaPrecioArticulo();
        lpa.setListaPrecio(listaPrecio);
        lpa.setArticulo(articulo);
        lpa.setPrecioVenta(precioVenta);
        lpa.setUsuarioCarga(usuario);
        lpa.setUsuarioModificacion(usuario);
        em.persist(lpa);
        return lpa;
    }

    private static FacturaVentaDetalle crearDetalle(ListaPrecioArticulo lpa, int cantidad, String descripcion) {
        double unitario = lpa.getPrecioVenta();
        double subtotal = unitario * cantidad;
        double neto = subtotal;
        double ivaMonto = neto * IVA / 100.0;

        FacturaVentaDetalle detalle = new FacturaVentaDetalle();
        detalle.setListaPrecioArticulo(lpa);
        detalle.setCantidad(cantidad);
        detalle.setPrecioUnitario(unitario);
        detalle.setImporteSubtotal(subtotal);
        detalle.setDescripcion(descripcion);
        detalle.setPorcentajeBonificacion(0.0);
        detalle.setImporteNeto(neto);
        detalle.setImporteIva(ivaMonto);
        return detalle;
    }

    private static void verificarCascade(EntityManager em, Long facturaId) {
        em.clear();

        FacturaVenta desdeBase = em.find(FacturaVenta.class, facturaId);

        System.out.println();
        System.out.println("=== VERIFICACION DE PERSISTENCIA EN H2 (lectura desde la base) ===");
        System.out.println("Facturas de venta   : " + em.createQuery("SELECT COUNT(f) FROM FacturaVenta f", Long.class).getSingleResult());
        System.out.println("Detalles de factura : " + em.createQuery("SELECT COUNT(d) FROM FacturaVentaDetalle d", Long.class).getSingleResult());
        System.out.println();

        if (desdeBase == null) {
            System.out.println("ERROR: la cabecera no se encontro en la base.");
            return;
        }

        System.out.println("Factura cargada por id=" + desdeBase.getId()
                + " numero=" + desdeBase.getNumero()
                + " estado=" + desdeBase.getEstado()
                + " total=" + desdeBase.getImporteTotal());
        System.out.println("Cliente             : " + desdeBase.getCliente().getDenominacion());
        System.out.println("Punto de venta      : " + desdeBase.getPuntoVenta().getDescripcion());
        System.out.println("Condicion IVA       : " + desdeBase.getCondicionIva().getDenominacion());
        System.out.println("Moneda              : " + desdeBase.getTipoMoneda().getDenominacion());
        System.out.println();

        List<FacturaVentaDetalle> detalles = desdeBase.getDetalles();
        System.out.println("Detalles encontrados en la base (sin em.persist explicito): " + detalles.size());
        for (FacturaVentaDetalle detalle : detalles) {
            System.out.println("  - id=" + detalle.getId()
                    + " | cantidad=" + detalle.getCantidad()
                    + " | unitario=" + detalle.getPrecioUnitario()
                    + " | neto=" + detalle.getImporteNeto()
                    + " | iva=" + detalle.getImporteIva()
                    + " | " + detalle.getDescripcion());
        }

        System.out.println();
        System.out.println("EXITO: el unico em.persist(facturaVenta) guardo la cabecera y sus "
                + detalles.size() + " detalles en cascada (CascadeType.ALL).");
    }
}

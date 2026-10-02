package com.tp.jpa.service;

import com.tp.jpa.entity.Categoria;
import com.tp.jpa.entity.EstadoPedido;
import com.tp.jpa.entity.Pedido;
import com.tp.jpa.entity.Producto;
import com.tp.jpa.entity.Usuario;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.PedidoRepository;
import com.tp.jpa.repository.ProductoRepository;
import com.tp.jpa.repository.UsuarioRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class TiendaService {

    private final EntityManagerFactory entityManagerFactory;

    public TiendaService(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = Objects.requireNonNull(
                entityManagerFactory,
                "El EntityManagerFactory es obligatorio");
    }

    public void ejecutarDemostracion() {
        System.out.println("=== DEMOSTRACIÓN JPA ===");
        boolean requiereOperacionesIniciales = prepararDatosIniciales();
        if (requiereOperacionesIniciales) {
            actualizarDosProductos();
            borrarProductoNoUtilizado();
        } else {
            System.out.println("La actualización y el borrado ya fueron realizados en una ejecución anterior.");
        }
        demostrarBusquedaDeUsuarios();
        mostrarResumen();
    }

    public Optional<Usuario> buscarUsuarioPorId(Long id) {
        Objects.requireNonNull(id, "El id es obligatorio");
        return ejecutarEnTransaccion(em -> new UsuarioRepository(em).buscarPorId(id));
    }

    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        Objects.requireNonNull(email, "El email es obligatorio");
        return ejecutarEnTransaccion(em -> new UsuarioRepository(em).buscarPorEmail(email));
    }

    public Optional<Producto> buscarProductoPorCodigo(String codigo) {
        Objects.requireNonNull(codigo, "El código es obligatorio");
        return ejecutarEnTransaccion(em -> new ProductoRepository(em).buscarPorCodigo(codigo));
    }

    public Resumen obtenerResumen() {
        return ejecutarEnTransaccion(em -> {
            UsuarioRepository usuarios = new UsuarioRepository(em);
            PedidoRepository pedidos = new PedidoRepository(em);
            CategoriaRepository categorias = new CategoriaRepository(em);
            ProductoRepository productos = new ProductoRepository(em);
            return new Resumen(
                    usuarios.contar(),
                    pedidos.contar(),
                    pedidos.contarDetalles(),
                    categorias.contar(),
                    productos.contar());
        });
    }

    private boolean prepararDatosIniciales() {
        Resumen resumenInicial = obtenerResumen();
        boolean baseVacia = resumenInicial.usuarios() == 0
                && resumenInicial.pedidos() == 0
                && resumenInicial.categorias() == 0
                && resumenInicial.productos() == 0;

        if (baseVacia) {
            ejecutarEnTransaccion(entityManager -> {
                persistirDatosIniciales(entityManager);
                return null;
            });
            System.out.println("Datos iniciales persistidos: 2 usuarios, 3 pedidos, 6 detalles, "
                    + "3 categorías y 10 productos.");
            return true;
        }

        if (buscarProductoPorCodigo("PRD-010").isEmpty()) {
            System.out.println("La demostración ya fue ejecutada sobre esta base de datos.");
            return false;
        }

        System.out.println("La base contiene los datos iniciales; se continuará la demostración.");
        return true;
    }

    private void persistirDatosIniciales(EntityManager entityManager) {
        CategoriaRepository categoriaRepository = new CategoriaRepository(entityManager);
        ProductoRepository productoRepository = new ProductoRepository(entityManager);
        UsuarioRepository usuarioRepository = new UsuarioRepository(entityManager);

        Categoria tecnologia = new Categoria("Tecnología");
        Categoria hogar = new Categoria("Hogar");
        Categoria deportes = new Categoria("Deportes");
        categoriaRepository.guardar(tecnologia);
        categoriaRepository.guardar(hogar);
        categoriaRepository.guardar(deportes);

        List<Producto> productos = List.of(
                new Producto("PRD-001", "Notebook Pro", "Laptop de 14 pulgadas con 16 GB de RAM",
                        new BigDecimal("1299.00"), 15, tecnologia),
                new Producto("PRD-002", "Monitor 4K", "Monitor UHD de 27 pulgadas",
                        new BigDecimal("449.90"), 20, tecnologia),
                new Producto("PRD-003", "Teclado mecánico", "Teclado RGB switches mecánicos",
                        new BigDecimal("89.90"), 35, tecnologia),
                new Producto("PRD-004", "Audífonos inalámbricos", "Audífonos Bluetooth con cancelación de ruido",
                        new BigDecimal("159.50"), 25, tecnologia),
                new Producto("PRD-005", "Cafetera compacta", "Cafetera para seis tazas",
                        new BigDecimal("79.90"), 18, hogar),
                new Producto("PRD-006", "Organizador modular", "Organizador de escritorio multipropósito",
                        new BigDecimal("24.50"), 40, hogar),
                new Producto("PRD-007", "Lámpara de escritorio", "Lámpara LED regulable",
                        new BigDecimal("45.00"), 22, hogar),
                new Producto("PRD-008", "Bicicleta urbana", "Bicicleta de ciudad con frame de aluminio",
                        new BigDecimal("749.00"), 8, deportes),
                new Producto("PRD-009", "Pelota deportiva", "Pelota de entrenamiento de alta durabilidad",
                        new BigDecimal("29.90"), 50, deportes),
                new Producto("PRD-010", "Set de pesas", "Set de mancuernas ajustables",
                        new BigDecimal("119.00"), 12, deportes));
        productos.forEach(productoRepository::guardar);

        Usuario ana = new Usuario("Ana García", "ana.garcia@example.com");
        Pedido primerPedido = new Pedido(LocalDateTime.now().minusDays(3), EstadoPedido.PAGADO);
        primerPedido.agregarDetalle(productos.get(0), 2);
        primerPedido.agregarDetalle(productos.get(1), 1);
        ana.agregarPedido(primerPedido);
        usuarioRepository.guardar(ana);

        Usuario luis = new Usuario("Luis Martínez", "luis.martinez@example.com");
        Pedido segundoPedido = new Pedido(LocalDateTime.now().minusDays(2), EstadoPedido.ENVIADO);
        segundoPedido.agregarDetalle(productos.get(2), 1);
        segundoPedido.agregarDetalle(productos.get(3), 2);
        luis.agregarPedido(segundoPedido);

        Pedido tercerPedido = new Pedido(LocalDateTime.now().minusDays(1), EstadoPedido.PENDIENTE);
        tercerPedido.agregarDetalle(productos.get(4), 1);
        tercerPedido.agregarDetalle(productos.get(5), 2);
        luis.agregarPedido(tercerPedido);
        usuarioRepository.guardar(luis);
    }

    private void actualizarDosProductos() {
        ejecutarEnTransaccion(entityManager -> {
            ProductoRepository repository = new ProductoRepository(entityManager);
            Producto notebook = obtenerProducto(repository.buscarPorCodigo("PRD-001"), "PRD-001");
            Producto monitor = obtenerProducto(repository.buscarPorCodigo("PRD-002"), "PRD-002");

            BigDecimal precioAnteriorNotebook = notebook.getPrecio();
            BigDecimal precioAnteriorMonitor = monitor.getPrecio();
            notebook.actualizarDatos(
                    notebook.getNombre(),
                    notebook.getDescripcion(),
                    new BigDecimal("1350.00"),
                    20);
            monitor.actualizarDatos(
                    monitor.getNombre(),
                    monitor.getDescripcion(),
                    new BigDecimal("475.00"),
                    25);

            System.out.printf("Producto PRD-001 actualizado: %.2f -> %.2f; stock: %d%n",
                    precioAnteriorNotebook, notebook.getPrecio(), notebook.getStock());
            System.out.printf("Producto PRD-002 actualizado: %.2f -> %.2f; stock: %d%n",
                    precioAnteriorMonitor, monitor.getPrecio(), monitor.getStock());
            return null;
        });
    }

    private void demostrarBusquedaDeUsuarios() {
        Optional<Usuario> resultadoPorEmail = buscarUsuarioPorEmail("ana.garcia@example.com");
        if (resultadoPorEmail.isEmpty()) {
            throw new IllegalStateException("No se encontró el usuario de prueba por email");
        }

        Usuario usuarioReferencia = resultadoPorEmail.get();
        Optional<Usuario> resultadoPorId = buscarUsuarioPorId(usuarioReferencia.getId());
        if (resultadoPorId.isEmpty()) {
            throw new IllegalStateException("No se encontró el usuario de prueba por id");
        }

        Usuario usuarioPorId = resultadoPorId.get();
        System.out.printf("Usuario encontrado por ID %d: %s (%s)%n",
                usuarioPorId.getId(), usuarioPorId.getNombre(), usuarioPorId.getEmail());
        System.out.printf("Usuario encontrado por email: %s (%s)%n",
                usuarioReferencia.getNombre(), usuarioReferencia.getEmail());
    }

    private void borrarProductoNoUtilizado() {
        ejecutarEnTransaccion(entityManager -> {
            ProductoRepository repository = new ProductoRepository(entityManager);
            Optional<Producto> producto = repository.buscarPorCodigo("PRD-010");
            if (producto.isPresent()) {
                Producto eliminado = producto.get();
                repository.eliminar(eliminado);
                System.out.printf("Producto eliminado: %s - %s%n",
                        eliminado.getCodigo(), eliminado.getNombre());
            } else {
                System.out.println("El producto de prueba PRD-010 ya fue eliminado.");
            }
            return null;
        });
    }

    private void mostrarResumen() {
        Resumen resumen = obtenerResumen();
        System.out.println("=== RESUMEN FINAL ===");
        System.out.printf("Usuarios: %d%n", resumen.usuarios());
        System.out.printf("Pedidos: %d%n", resumen.pedidos());
        System.out.printf("Detalles de pedido: %d%n", resumen.detalles());
        System.out.printf("Categorías: %d%n", resumen.categorias());
        System.out.printf("Productos: %d%n", resumen.productos());
    }

    private Producto obtenerProducto(Optional<Producto> producto, String codigo) {
        return producto.orElseThrow(() ->
                new IllegalStateException("No existe el producto de prueba " + codigo));
    }

    private <T> T ejecutarEnTransaccion(Function<EntityManager, T> operacion) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            T resultado = operacion.apply(entityManager);
            transaction.commit();
            return resultado;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                try {
                    transaction.rollback();
                } catch (RuntimeException rollbackException) {
                    exception.addSuppressed(rollbackException);
                }
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public record Resumen(
            long usuarios,
            long pedidos,
            long detalles,
            long categorias,
            long productos) {
    }
}

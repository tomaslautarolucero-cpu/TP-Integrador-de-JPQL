# Trabajo práctico: modelo de dominio persistente con JPA

> Nota de organización: este repositorio incluye dos trabajos relacionados, pero el proyecto principal y activo es el de facturación/JPQL bajo el paquete `ar.edu.utn.facturacion`. El paquete `com.tp.jpa` corresponde a un ejercicio complementario y no al TP principal.

## Objetivo

Implementar un modelo de dominio de una tienda utilizing Jakarta Persistence API, Hibernate y una base de datos PostgreSQL.

## Dominio implementado

- **Usuario**: 'id', 'nombre', 'email' y relación uno-a-muchos con `Pedido`.
- **Pedido**: 'id', 'fecha', 'estado' y relaciones con `Usuario` y `DetallePedido`.
- **DetallePedido**: 'id', 'cantidad' y 'precioUnitario'; representa la relación entre un pedido y un producto.
- **Categoría**: 'id', 'nombre' y relación uno-a-muchos con `Producto`.
- **Producto**: 'id', 'codigo', 'nombre', 'descripcion', 'precio', 'stock', fechas de creación y actualización, y relación con `Categoria`.

Las entidades utilizan anotaciones de JPA como `@Entity`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToOne`, `@Column`, `@JoinColumn`, `@Enumerated`, `@PrePersist` y `@PreUpdate`.

## Operaciones demostradas

1. Persistencia de 2 usuarios.
2. Persistencia de 3 pedidos, cada uno con 2 detalles.
3. Persistencia de 3 categorías.
4. Persistencia de 10 productos.
5. Actualización de los productos `PRD-001` y `PRD-002`.
6. Búsqueda de un usuario por id.
7. Búsqueda de un usuario por email mediante JPQL.
8. Borrado del producto `PRD-010`, que no participa en ningún pedido.

La aplicación evita repetir las operaciones de actualización y borrado cuando ya se ejecutaron sobre la misma base.

## Requisitos

- JDK 17 o superior.
- Apache Maven 3.9 o superior.

## Compilación y pruebas

```powershell
mvn clean test
```

La prueba de integración utiliza una base PostgreSQL dedicada (`facturacion_test_db`) y no modifica la base de datos de la aplicación (`facturacion_db`).

## Ejecución

```powershell
mvn clean compile exec:java
```

También puede ejecutarse `main` desde la clase `com.tp.jpa.Main` en NetBeans o IntelliJ IDEA.

La base de datos de la demostración se crea en:

```text
data/jpa_db.mv.db
```

Al finalizar, la aplicación muestra 2 usuarios, 3 pedidos, 6 detalles de pedido, 3 categorías y 9 productos, porque uno de los 10 productos iniciales fue eliminado.

## Estructura principal

```text
src/main/java/com/tp/jpa/
├── Main.java
├── entity/
│   ├── Usuario.java
│   ├── Pedido.java
│   ├── DetallePedido.java
│   ├── Categoria.java
│   ├── Producto.java
│   └── EstadoPedido.java
├── repository/
│   ├── UsuarioRepository.java
│   ├── PedidoRepository.java
│   ├── CategoriaRepository.java
│   └── ProductoRepository.java
└── service/
    └── TiendaService.java

src/main/resources/META-INF/
└── persistence.xml
```

## Conclusiones

- **Persistencia de objetos en base de datos:** se comprendió el uso de `EntityManager.persist()` para transformar entidades en objetos persistentes y la importancia de confirmar cada operación dentro de una transacción.
- **Ciclo de vida de una entidad:** se comprendieron los estados transitorio, persistente/administrado, removido y desvinculado. Las operaciones de búsqueda, actualización y borrado muestran las transiciones del ciclo de vida administradas por Hibernate.
- **Operaciones CRUD:** se implementaron y probaron las operaciones de creación, lectura, actualización y eliminación, utilizando JPQL parametrizado para la búsqueda de usuarios por email.

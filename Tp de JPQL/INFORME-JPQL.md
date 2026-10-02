# Trabajo Práctico Grupal: Consultas Avanzadas con JPQL

## Objetivo

Consultar la base de datos relacional orientada a objetos mediante JPQL, aplicando filtros,
agregaciones, funciones de agrupamiento, combinaciones entre tablas (JOINs) y subconsultas
correlacionadas.

## Stack

- JDK 17
- Jakarta Persistence API 3.1
- Hibernate ORM 6.6.18.Final
- PostgreSQL 42.7.4 (base `facturacion_db`, unidad de persistencia `FacturacionPU`)
- Apache Maven

## Archivos del TP

```text
src/main/java/ar/edu/utn/facturacion/
├── MainJPQL.java                  Orquestador: ejecuta y muestra las 20 consultas
├── entidades/                     Modelo de dominio (14 entidades)
└── servicios/
    ├── ConsultaJPQLService.java   Las 20 consultas JPQL
    └── DatosJPQL.java             Juego de datos de prueba
```

## Instrucciones generales y su cumplimiento

| Instrucción | Cumplimiento |
|---|---|
| 1. Usar `em.createQuery(jpql, ClaseDestino.class)` | Todas las consultas se ejecutan con `em.createQuery(jpql, ClaseDestino.class)`: entidades (`FacturaVenta.class`, `Articulo.class`, `Cliente.class`, `PuntoVenta.class`, `Marca.class`, `FacturaVentaDetalle.class`, `String.class`) y proyecciones (`Tuple.class`, `Object[].class`). |
| 2. Parámetros siempre nombrados (`:parametro`) | Todas las consultas con filtros usan parámetros nombrados y `setParameter(...)`. No hay concatenación de valores en el texto JPQL. |
| 3. Usar nombres de entidades y atributos Java, no de tablas | Todos los FROM/SELECT/WHERE referencian entidades (`FacturaVenta`, `FacturaVentaDetalle`, `Articulo`, `Marca`, `PuntoVenta`, `Cliente`, `Usuario`, `Rubro`, `ListaPrecioArticulo`) y atributos Java. |

## Cómo ejecutar

```powershell
mvn clean compile exec:java@tp2-consultas-jpql
```

La salida completa de la última ejecución está en `docs/salida-consultas-jpql.txt`.

Al iniciar, `DatosJPQL` reinicia las tablas del modelo y carga un juego de datos conocido y
reproducible (3 usuarios, 2 puntos de venta, 2 rubros, 3 marcas, 7 artículos, 4 clientes y
10 facturas), de modo que los resultados no dependen de lo que haya quedado de ejecuciones
anteriores. Si se quiere conservar la base tal como está, se ejecuta con
`-Djpql.sembrar=false`.

Los datos están diseñados para que los filtros tengan casos interesantes: un artículo sin
marca (`ART-006`), un artículo nunca facturado (`ART-007`), una factura anulada, dos puntos
de venta y tres usuarios de carga con distintos volumes.

---

# Nivel 1: Consultas Básicas y Proyecciones

## Consulta 1 — Entidades completas

**Consigna:** obtener la lista completa de todas las facturas de venta registradas.

```java
String jpql = "SELECT f FROM FacturaVenta f ORDER BY f.numero";
return em.createQuery(jpql, FacturaVenta.class).getResultList();
```

Resultado: 10 facturas (9 `EMITIDA` + 1 `ANULADA`).

## Consulta 2 — Proyección de atributos específicos

**Consigna:** seleccionar únicamente el número de factura, la fecha de emisión y el importe total.

```java
String jpql = "SELECT f.numero, f.fechaEmision, f.importeTotal "
        + "FROM FacturaVenta f "
        + "ORDER BY f.numero";
return em.createQuery(jpql, Tuple.class).getResultList();
```

Proyección de tres atributos heterogéneos, por eso el destino es `Tuple.class`.

## Consulta 3 — Filtrado por igualdad (WHERE)

**Consigna:** obtener todos los artículos que pertenecen a un rubro con una denominación específica.

```java
String jpql = "SELECT a FROM Articulo a "
        + "WHERE a.rubro.denominacion = :denominacionRubro "
        + "ORDER BY a.denominacion";
return em.createQuery(jpql, Articulo.class)
        .setParameter("denominacionRubro", denominacionRubro)
        .getResultList();
```

Parámetro usado: `denominacionRubro = "Electronica"` → 4 artículos.

## Consulta 4 — Filtrado por rango de fechas (BETWEEN)

**Consigna:** listar las facturas emitidas dentro de un rango de fechas.

```java
String jpql = "SELECT f FROM FacturaVenta f "
        + "WHERE f.fechaEmision BETWEEN :desde AND :hasta "
        + "ORDER BY f.fechaEmision";
return em.createQuery(jpql, FacturaVenta.class)
        .setParameter("desde", desde)
        .setParameter("hasta", hasta)
        .getResultList();
```

Parámetros usados: `2026-01-01` y `2026-03-31` → 10 facturas.

---

# Nivel 2: Condicionales Combinados, Operadores de Texto y Agregaciones Básicas

## Consulta 5 — Condicionales complejos y verificación de nulos

**Consigna:** facturas en estado `EMITIDA`, con importe total superior a $10.000 y no anuladas.

```java
String jpql = "SELECT f FROM FacturaVenta f "
        + "WHERE f.estado = :estado "
        + "AND f.importeTotal > :importeMinimo "
        + "AND f.fechaAnulacion IS NULL "
        + "ORDER BY f.importeTotal DESC";
return em.createQuery(jpql, FacturaVenta.class)
        .setParameter("estado", estado)
        .setParameter("importeMinimo", importeMinimo)
        .getResultList();
```

Combina `AND` e `IS NULL` con dos parámetros nombrados. El operador `OR` de esta misma familia
de consignas se aplica en la consulta 6. Parámetros: `EMITIDA` y `10000.0` → 6 facturas.

## Consulta 6 — Búsqueda por patrón de texto (LIKE y LOWER)

**Consigna:** clientes cuya denominación contenga un texto parcial, sin importar mayúsculas y
minúsculas, o cuyo CUIT/CUIL comience con `20-`.

```java
String jpql = "SELECT c FROM Cliente c "
        + "WHERE LOWER(c.denominacion) LIKE LOWER(:textoDenominacion) "
        + "OR c.cuitCuil LIKE :prefijoCuitCuil "
        + "ORDER BY c.denominacion";
return em.createQuery(jpql, Cliente.class)
        .setParameter("textoDenominacion", textoDenominacion)
        .setParameter("prefijoCuitCuil", prefijoCuitCuil)
        .getResultList();
```

`LOWER` sobre el atributo y sobre el parámetro hace la comparación insensible a mayúsculas.
Parámetros: `%acme%` y `20-%` → 3 clientes.

## Consulta 7 — Valores distintos y ordenamiento

**Consigna:** obtener sin duplicados todos los estados registrados, ordenados alfabéticamente.

```java
String jpql = "SELECT DISTINCT f.estado FROM FacturaVenta f ORDER BY f.estado ASC";
return em.createQuery(jpql, String.class).getResultList();
```

Resultado: `[ANULADA, EMITIDA]`.

## Consulta 8 — Funciones de agregación simples (COUNT, SUM, AVG)

**Consigna:** cantidad total de facturas emitidas, suma de importes e importe promedio devueltos
en un solo objeto/arreglo.

```java
String jpql = "SELECT COUNT(f), SUM(f.importeTotal), AVG(f.importeTotal) "
        + "FROM FacturaVenta f "
        + "WHERE f.estado = :estado";
return em.createQuery(jpql, Object[].class)
        .setParameter("estado", estado)
        .getSingleResult();
```

`getSingleResult()` sobre una proyección de tres valores devuelve un `Object[]` con las tres
agregaciones. Resultado: `9` facturas, `$418660.0` de suma, `$46517.78` de promedio.

## Consulta 9 — Operador de inclusión (IN)

**Consigna:** puntos de venta cuyo número coincida con una lista de enteros.

```java
String jpql = "SELECT p FROM PuntoVenta p "
        + "WHERE p.numero IN :numeros "
        + "ORDER BY p.numero";
return em.createQuery(jpql, PuntoVenta.class)
        .setParameter("numeros", numeros)
        .getResultList();
```

Parámetro: `List.of(1, 2, 5)` → 2 puntos de venta (el 5 no existe en la base).

---

# Nivel 3: Navegación de Entidades, JOINs y Subconsultas Simples

## Consulta 10 — Navegación implícita por relaciones (path expressions)

**Consigna:** consultar las facturas creadas por un usuario en particular navegando por
`usuarioCarga.usuario`.

```java
String jpql = "SELECT f FROM FacturaVenta f "
        + "WHERE f.usuarioCarga.usuario = :nombreUsuario "
        + "ORDER BY f.fechaEmision";
return em.createQuery(jpql, FacturaVenta.class)
        .setParameter("nombreUsuario", nombreUsuario)
        .getResultList();
```

No hay `JOIN` en el JPQL: la navegación `f.usuarioCarga.usuario` es resuelta por el proveedor.
Parámetro: `"admin"` → 7 facturas.

## Consulta 11 — INNER JOIN explícita

**Consigna:** obtener los detalles de factura correspondientes a facturas emitidas por un
punto de venta determinado.

```java
String jpql = "SELECT d FROM FacturaVentaDetalle d "
        + "JOIN d.factura f "
        + "JOIN f.puntoVenta p "
        + "WHERE p.numero = :numeroPuntoVenta "
        + "ORDER BY f.numero, d.descripcion";
return em.createQuery(jpql, FacturaVentaDetalle.class)
        .setParameter("numeroPuntoVenta", numeroPuntoVenta)
        .getResultList();
```

Dos `JOIN` internos encadenados. Parámetro: `1` → 6 detalles.

## Consulta 12 — LEFT JOIN (inclusión de nulos)

**Consigna:** listar la denominación de los artículos junto con la de su marca, incluyendo los
que no tienen marca asignada.

```java
String jpql = "SELECT a.denominacion, m.denominacion "
        + "FROM Articulo a "
        + "LEFT JOIN a.marca m "
        + "ORDER BY a.denominacion";
return em.createQuery(jpql, Tuple.class).getResultList();
```

`LEFT JOIN` conserva las filas con `marca = NULL`: `Boletin sin marca | (sin marca)` → 7 filas.

## Consulta 13 — Navegación multinivel con JOINs combinados

**Consigna:** obtener las facturas que contengan al menos un detalle de artículo perteneciente a
una marca específica.

```java
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
```

Cuatro niveles de navegación. `DISTINCT` evita repetir la factura cuando más de un detalle
cumple el filtro. Parámetro: `"Sony"` → 2 facturas.

## Consulta 14 — Subconsulta en cláusula WHERE

**Consigna:** listar las facturas cuyo `importeTotal` sea mayor al promedio de todas las facturas.

```java
String jpql = "SELECT f FROM FacturaVenta f "
        + "WHERE f.importeTotal > (SELECT AVG(f2.importeTotal) FROM FacturaVenta f2) "
        + "ORDER BY f.importeTotal DESC";
return em.createQuery(jpql, FacturaVenta.class).getResultList();
```

Subconsulta escalar de agregación en el `WHERE` → 3 facturas.

---

# Nivel 4: Agrupamiento (GROUP BY) y Filtros de Grupo (HAVING)

## Consulta 15 — Agrupamiento básico

**Consigna:** descripción del punto de venta, cantidad de facturas emitidas por cada uno y suma
total facturada.

```java
String jpql = "SELECT p.descripcion, COUNT(f), SUM(f.importeTotal) "
        + "FROM FacturaVenta f "
        + "JOIN f.puntoVenta p "
        + "GROUP BY p.descripcion "
        + "ORDER BY SUM(f.importeTotal) DESC";
return em.createQuery(jpql, Tuple.class).getResultList();
```

Resultado:

| Punto de venta | Facturas | Suma facturada |
|---|---|---|
| Sucursal Belgrano | 4 | $310365.0 |
| Sucursal Palermo | 6 | $289795.0 |

## Consulta 16 — Agrupamiento con condicional de grupo (HAVING)

**Consigna:** usuarios de carga que hayan registrado más de 5 facturas.

```java
String jpql = "SELECT u.usuario "
        + "FROM FacturaVenta f "
        + "JOIN f.usuarioCarga u "
        + "GROUP BY u.usuario "
        + "HAVING COUNT(f) > :cantidadMinima "
        + "ORDER BY u.usuario";
return em.createQuery(jpql, String.class)
        .setParameter("cantidadMinima", cantidadMinima)
        .getResultList();
```

El umbral también es parámetro nombrado. Parámetro: `5` → `[admin]`.

## Consulta 17 — Agrupamiento y agregación sobre entidades relacionadas

**Consigna:** denominación de cada marca, cantidad total de unidades vendidas y subtotal
acumulado.

```java
String jpql = "SELECT m.denominacion, SUM(d.cantidad), SUM(d.importeSubtotal) "
        + "FROM FacturaVentaDetalle d "
        + "JOIN d.listaPrecioArticulo lpa "
        + "JOIN lpa.articulo a "
        + "JOIN a.marca m "
        + "GROUP BY m.denominacion "
        + "ORDER BY SUM(d.importeSubtotal) DESC";
return em.createQuery(jpql, Tuple.class).getResultList();
```

Resultado:

| Marca | Unidades vendidas | Subtotal acumulado |
|---|---|---|
| Sony | 2 | $300000.0 |
| Logitech | 6 | $170000.0 |
| Generica | 7 | $23000.0 |

---

# Nivel 5: Subconsultas Correlacionadas, EXISTS, NOT EXISTS y CASE WHEN

## Consulta 18 — Subconsulta correlacionada con EXISTS

**Consigna:** marcas que tienen al menos un artículo que haya sido facturado.

```java
String jpql = "SELECT m FROM Marca m "
        + "WHERE EXISTS ("
        + "    SELECT d.id FROM FacturaVentaDetalle d "
        + "    JOIN d.listaPrecioArticulo lpa "
        + "    JOIN lpa.articulo a "
        + "    WHERE a.marca = m"
        + ") "
        + "ORDER BY m.denominacion";
return em.createQuery(jpql, Marca.class).getResultList();
```

La condición `a.marca = m` correlaciona la subconsulta con la marca de la consulta externa →
3 marcas.

## Consulta 19 — Subconsulta correlacionada con NOT EXISTS

**Consigna:** artículos que nunca fueron incluidos en ningún detalle de factura.

```java
String jpql = "SELECT a FROM Articulo a "
        + "WHERE NOT EXISTS ("
        + "    SELECT d.id FROM FacturaVentaDetalle d "
        + "    JOIN d.listaPrecioArticulo lpa "
        + "    WHERE lpa.articulo = a"
        + ") "
        + "ORDER BY a.denominacion";
return em.createQuery(jpql, Articulo.class).getResultList();
```

La correlación es `lpa.articulo = a` → 1 artículo (`ART-007 | Auriculares premium`).

## Consulta 20 — Proyección condicional (CASE WHEN)

**Consigna:** listar número, importe total y una columna calculada `Categoría` que clasifique la
factura como `ALTO VALOR`, `MEDIO VALOR` o `BAJO VALOR`, ordenado de mayor a menor importe.

```java
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
```

Los umbrales son parámetros nombrados: `50000.0` y `10000.0`.

| numero | importeTotal | Categoria |
|---|---|---|
| 9 | $181500.0 | ALTO VALOR |
| 3 | $181500.0 | ALTO VALOR |
| 7 | $90750.0 | ALTO VALOR |
| 4 | $54450.0 | ALTO VALOR |
| 8 | $32065.0 | MEDIO VALOR |
| 2 | $30250.0 | MEDIO VALOR |
| 5 | $18150.0 | MEDIO VALOR |
| 10 | $6050.0 | BAJO VALOR |
| 1 | $3630.0 | BAJO VALOR |
| 6 | $1815.0 | BAJO VALOR |

---

## Resumen de cobertura

| Nivel | Consultas | JPQL aplicado |
|---|---|---|
| Nivel 1 | 1 a 4 | Entidad completa, proyección, `WHERE`, `BETWEEN` |
| Nivel 2 | 5 a 9 | `AND`, `OR`, `IS NULL`, `LOWER`, `LIKE`, `DISTINCT`, `ORDER BY`, `COUNT`, `SUM`, `AVG`, `IN` |
| Nivel 3 | 10 a 14 | Path expressions, `INNER JOIN`, `LEFT JOIN`, joins multinivel, subconsulta en `WHERE` |
| Nivel 4 | 15 a 17 | `GROUP BY`, `HAVING`, agregación sobre entidades relacionadas |
| Nivel 5 | 18 a 20 | `EXISTS`, `NOT EXISTS` correlacionados, `CASE WHEN` |

Las 20 consultas se ejecutan en una sola corrida y finalizan sin error.
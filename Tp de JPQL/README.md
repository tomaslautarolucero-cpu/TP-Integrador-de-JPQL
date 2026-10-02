# TP JPA - Facturación + JPQL

Este repositorio incluye dos trabajos relacionados, pero con objetivos distintos:

## Proyecto principal (TP activo)

La parte que corresponde al trabajo práctico de facturación y JPQL es la que está dentro del paquete:

- `ar.edu.utn.facturacion`

Incluye:

- `Main.java`
- `MainJPQL.java`
- `entidades/`
- `servicios/`

Esta es la parte que se ejecuta con la unidad de persistencia `FacturacionPU` y la que se usa para las consultas avanzadas con JPQL.

Documentación del trabajo:

- `INFORME-JPQL.md`: las 20 consultas JPQL pedidas, con su código y sus resultados.
- `docs/salida-consultas-jpql.txt`: salida completa de la última ejecución.

## Trabajo extra / ejercicio paralelo

También hay un ejercicio adicional de tienda dentro del paquete:

- `com.tp.jpa`

Eso corresponde a un ejercicio de práctica independiente, y no forma parte del TP principal de facturación. Se mantiene separado para no mezclar dos modelos distintos en el mismo proyecto.

## Orden recomendado

- Mantener como trabajo principal: `ar.edu.utn.facturacion`
- Tratar `com.tp.jpa` como ejercicio complementario o legado
- Usar este README como referencia para no confundir ambos proyectos

## Comandos útiles

```bash
mvn test
mvn compile
mvn exec:java
```

Para las 20 consultas del TP de JPQL:

```bash
mvn clean compile exec:java@tp2-consultas-jpql
```

`MainJPQL` reinicia las tablas del modelo y carga el juego de datos de prueba antes de
consultar, así que los resultados son siempre los mismos. Para no tocar la base:
`-Djpql.sembrar=false`.

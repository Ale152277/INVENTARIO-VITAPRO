# Decisiones del proyecto

Revisado el 16/11/2026. Si una decisión cambia, se registra el motivo y se vincula el Issue o PR correspondiente.

## Alcance y empresas

VitaEquip es una herramienta interna para trabajadores autorizados de VitaPro. Los equipos que se registran actualmente se asocian a VitaPro, independientemente de su marca o fabricante.

Se conserva la tabla `empresas` para permitir más adelante el seguimiento de equipos en otras organizaciones. No se implementarán cuentas independientes para esas empresas en esta etapa.

## Aplicación y servicios

Se mantiene Android nativo con Kotlin, XML y MVVM. La app consulta Supabase directamente; no existe un backend Spring Boot para este proyecto.

Supabase gestiona autenticación, PostgreSQL y Storage. Las restricciones por rol deben comprobarse en RLS, no solo mediante botones ocultos.

## Usuarios

Se mantienen los roles ADMIN y LECTOR. Solo los trabajadores autorizados de VitaPro ingresan a la aplicación. Por ahora, el lector consulta equipos y reporta incidencias; el administrador gestiona los registros.

## Códigos QR

El QR generado contiene el ID del equipo. La aplicación lo resuelve después de autenticar al usuario y muestra la ficha correspondiente. La impresión de etiquetas y la consulta completa todavía deben verificarse.

## Mantenimientos

Se registrará cada intervención por separado, con fecha, equipo y trabajador responsable. Las fechas de último y próximo mantenimiento del equipo seguirán sirviendo como resumen.

## Sin conexión

El objetivo es consultar equipos previamente sincronizados y guardar incidencias para enviarlas cuando vuelva internet. Room y WorkManager son la estrategia prevista; la sincronización y los conflictos aún están por diseñarse.

## Notificaciones

El estado de lectura se gestionará por usuario. La relación entre destinatarios y notificaciones se definirá antes de implementar alertas automáticas.

## Trabajo en GitHub

Cada cambio debe partir de un Issue, desarrollarse en una rama específica y revisarse mediante Pull Request. Se utilizará `Ale` como rama de integración temporal hasta estabilizar el trabajo acumulado y trasladarlo a `main`.

Los documentos se mantienen breves. Los detalles de implementación y las pruebas quedan en Issues y PR.

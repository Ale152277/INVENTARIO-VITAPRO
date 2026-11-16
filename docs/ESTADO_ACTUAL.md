# Estado actual de VitaEquip

Revisión: 16/11/2026  
Referencia: rama `Ale`, commit `04f752f0`.

Este resumen se basa en la revisión del código. No implica que todos los flujos se hayan probado en un dispositivo.

## Disponible en el código

- Login con Supabase y roles ADMIN/LECTOR.
- Dashboard, listado de equipos y filtros por marca y área.
- Formulario de alta y edición de equipos.
- Historial de cambios de estado y ubicación.
- Registro de bancos de baterías.
- Generación de QR con el ID del equipo y lectura mediante ZXing.
- Consulta de datos e historial, con opción de imprimir un PDF.

## Requiere revisión

- [#6](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/6): asignación de empresa al crear equipos.
- [#3](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/3): recuperación de sesión y rol.
- [#2](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/2): guardado seguro de bancos de baterías.
- [#5](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/5): políticas RLS y permisos.
- [#9](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/9): ficha completa al escanear QR.

## Pendiente

- [#4](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/4): historial de mantenimientos y responsables.
- [#10](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/10): consulta de equipos sin conexión.
- [#7](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/7): incidencias offline y sincronización.
- [#8](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues/8): lectura individual y automatización de notificaciones.

Las clases locales de Room todavía son estructuras vacías. Las pantallas de reportes y notificaciones aún no están terminadas. Las pruebas del repositorio son las que incluye el proyecto Android de ejemplo; faltan pruebas funcionales propias.

## Próximo trabajo

Estabilizar registro, sesión y permisos antes de ampliar las funciones. La prioridad operativa es consultar equipos y registrar incidencias durante la parada de planta, incluso cuando no haya señal.

Los Issues contienen el detalle y las pruebas de cada tarea; este archivo solo registra el panorama general.

# VitaEquip

Aplicación Android de uso interno para el inventario y seguimiento de equipos eléctricos atendidos por VitaPro. Permite registrar equipos, consultar sus datos mediante códigos QR y revisar cambios de estado y ubicación.

Por ahora, los equipos registrados pertenecen al inventario de VitaPro. La estructura contempla otras empresas si más adelante se amplía el alcance. La marca o el fabricante de un equipo no determina su empresa.

## Tecnologías

- Kotlin y layouts XML.
- Arquitectura MVVM con ViewModel y LiveData.
- Supabase: autenticación, PostgreSQL y almacenamiento de archivos.
- ZXing para códigos QR.
- Room previsto para consultas sin conexión.

## Preparar el proyecto

1. Clonar el repositorio y abrirlo en Android Studio.
2. Sincronizar las dependencias de Gradle.
3. Configurar `SUPABASE_URL` y `SUPABASE_KEY` en el archivo local `local.properties`.
4. Ejecutar la aplicación en un dispositivo Android o emulador.

La clave incluida en una aplicación móvil debe ser una clave publicable de Supabase, nunca una `service_role` ni otra clave secreta. Los permisos de acceso deben validarse mediante RLS. No subir `local.properties` al repositorio.

## Seguimiento

Las tareas y errores se registran en [Issues](https://github.com/Ale152277/INVENTARIO-VITAPRO/issues). Cada cambio debe desarrollarse en una rama y pasar por un Pull Request.

El [estado actual](docs/ESTADO_ACTUAL.md) resume lo que existe y lo que falta. Las [decisiones técnicas](docs/DECISIONES.md) explican los criterios que seguimos.

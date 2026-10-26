# TV Control Center (prototipo)

Prototipo Android para Sony Google TV / Android TV 10–12 (API 29–32).

## Funciones incluidas
- Interfaz TV navegable con control remoto.
- Cambiar el color de fondo de la propia app.
- Abrir el selector de imágenes (la imagen todavía no se aplica como fondo).
- Atajos a ajustes de privacidad, aplicaciones y red.
- Listado de apps que exponen una actividad de inicio y acceso a su pantalla de información.

## Límites
- No cambia el fondo del launcher de Google TV.
- No elimina anuncios de la pantalla principal ni dentro de apps.
- No desinstala automáticamente apps protegidas o preinstaladas.
- La visibilidad de apps está sujeta a las reglas de Android.
- Los ajustes disponibles pueden variar según modelo y firmware Sony.

## Compilar sin computadora local usando GitHub Actions
1. Crea una cuenta/inicia sesión en GitHub desde el navegador de tu teléfono.
2. Crea un repositorio nuevo, por ejemplo `TVControlCenter`.
3. Sube el contenido de este proyecto conservando la estructura de carpetas.
4. Abre la pestaña **Actions** y ejecuta **Build Android APK** (o espera a que termine el flujo al subir archivos).
5. Cuando termine correctamente, abre la ejecución y descarga el artefacto `TVControlCenter-debug`.
6. Descomprime el ZIP del artefacto en un dispositivo que permita hacerlo y sube `app-debug.apk` a un alojamiento de archivos HTTPS confiable.
7. En Downloader, abre la URL directa del APK, descárgalo e instálalo. Autoriza a Downloader para instalar apps desconocidas si el sistema lo solicita.

La compilación debug sirve para pruebas. Para distribuirla a largo plazo, genera una compilación release firmada con una clave propia y guárdala de forma segura.

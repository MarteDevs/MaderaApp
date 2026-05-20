# 📦 RELEASES - Madera Poltand App

Guía de distribución y descarga de versiones compiladas de Madera Poltand.

## Últimas Versiones

### v1.0 (Compilación Inicial)

**Fecha:** Mayo 2026

Archivos disponibles:
- **app-release.apk** - APK Firmado (12.64 MB)
  - Descarga directa para instalación manual
  - Compatible con Android 8.0 (API 26) y superior
  - Usar para distribución directa o en tiendas alternativas

- **app-release.aab** - Android App Bundle (12.29 MB)
  - Para Google Play Store
  - Optimizado por Google Play para cada dispositivo

## 🔗 Descargar Archivos

### Opción 1: Desde Releases de GitHub

Los archivos compilados están disponibles en:
- [GitHub Releases](../../releases)

### Opción 2: Compilar Localmente

```bash
# Clonar repositorio
git clone <url-repositorio>
cd MaderaApp

# Compilar release
./gradlew assembleRelease

# Archivo generado
app/build/outputs/apk/release/app-release.apk
```

## 📱 Instalación

### Windows/Mac/Linux (desde APK)

1. Conectar dispositivo Android
2. Ejecutar:
   ```bash
   adb install app/build/outputs/apk/release/app-release.apk
   ```

### Android (instalación manual)

1. Descargar `app-release.apk`
2. Habilitar instalación desde fuentes desconocidas en Ajustes > Seguridad
3. Abrir el archivo APK con el gestor de archivos
4. Tocar "Instalar"

### Google Play Store

El archivo `app-release.aab` se usa exclusivamente en Google Play Console.

## ✅ Verificación

Verificar que la aplicación esté firmada:

```bash
jarsigner -verify app-release.apk
```

O usando apksigner:

```bash
apksigner verify app-release.apk
```

## 📋 Información de la Versión

| Propiedad | Valor |
|-----------|-------|
| Nombre | Madera Poltand |
| Versión | 1.0 |
| Código de Versión | 1 |
| Package ID | com.mars.madereraapp |
| Android Mínimo | API 26 (Android 8.0) |
| Android Objetivo | API 35 (Android 15) |
| Estado | Producción |

## 🔐 Verificación de Integridad

### Hash SHA-256

Para verificar la integridad de los archivos descargados:

```bash
# En Windows
certUtil -hashfile app-release.apk SHA256

# En Mac/Linux
sha256sum app-release.apk
```

## 🐛 Problemas de Instalación

### "Aplicación no instalada"
- Verificar que el dispositivo cumpla con Android 8.0 mínimo
- Liberar espacio en el dispositivo (al menos 50 MB)

### "No se puede instalar desde fuente desconocida"
- Habilitar en Ajustes > Seguridad > Fuentes desconocidas
- O en Ajustes > Privacidad > Aplicaciones especiales > Instalar apps desconocidas

### "Certificado no válido"
- Contactar al equipo de desarrollo
- Descargar nuevamente el archivo

## 📞 Soporte

Para problemas:
1. Revisar [README.md](README.md)
2. Revisar [BUILD_SUMMARY.md](BUILD_SUMMARY.md)
3. Contactar al equipo de desarrollo de Madera Poltand

## 📚 Documentación

- [README.md](README.md) - Documentación principal
- [BUILD_SUMMARY.md](BUILD_SUMMARY.md) - Detalles de compilación
- [INSTALL_GUIDE.md](INSTALL_GUIDE.md) - Guía de instalación (si aplica)

---

**Última actualización:** Mayo 2026

# Madera Poltand

Una aplicación Android moderna para la gestión de inventario y operaciones de Madera Poltand.

## 📱 Características

- Interfaz moderna con Jetpack Compose
- Arquitectura escalable con Hilt para inyección de dependencias
- Soporte para Android 8.0 (API 26) y superior
- Diseño responsivo para múltiples dispositivos

## 🛠️ Requisitos Previos

- Android Studio Arctic Fox o superior
- Java 11 o superior
- Gradle 8.0 o superior
- Android SDK 35
- Git

## 🚀 Instalación y Compilación

### 1. Clonar el repositorio

```bash
git clone <tu-repositorio>
cd MaderaApp
```

### 2. Configurar dependencias

```bash
./gradlew build
```

### 3. Ejecutar la aplicación en desarrollo

Para compilar y ejecutar en emulador o dispositivo:

```bash
./gradlew installDebug
```

### 4. Compilar versión de producción

La aplicación viene preconfigurada con firma para producción. Para generar el APK/AAB firmado:

```bash
# Generar APK release
./gradlew assembleRelease

# Generar Android App Bundle (AAB) para Play Store
./gradlew bundleRelease
```

Los archivos generados estarán ubicados en:
- `app/build/outputs/apk/release/` (APK)
- `app/build/outputs/bundle/release/` (AAB)

## 📋 Estructura del Proyecto

```
MaderaApp/
├── app/                          # Módulo principal de la aplicación
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/             # Código Kotlin/Java
│   │   │   ├── res/              # Recursos (layouts, strings, colores, etc.)
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                 # Tests unitarios
│   │   └── androidTest/          # Tests instrumentados
│   ├── build.gradle.kts          # Configuración del módulo
│   ├── proguard-rules.pro        # Reglas de ofuscación
│   └── release.keystore          # Certificado de firma
├── gradle/                       # Configuración de gradle
├── build.gradle.kts              # Configuración root
├── settings.gradle.kts           # Configuración de módulos
└── gradle.properties             # Propiedades de gradle

```

## 🔧 Configuración

### Versión de la Aplicación

Editar en `app/build.gradle.kts`:
```kotlin
versionCode = 1        // Incrementar para cada release
versionName = "1.0"    // Versión pública
```

### Certificado de Firma

La aplicación está configurada con un certificado de firma para producción en `app/release.keystore`.

> **⚠️ Importante**: Este archivo no debe ser compartido públicamente. Se recomienda mantenerlo en un lugar seguro.

## 🧪 Tests

### Ejecutar tests unitarios
```bash
./gradlew test
```

### Ejecutar tests instrumentados
```bash
./gradlew connectedAndroidTest
```

### Ejecutar todos los tests
```bash
./gradlew testDebug connectedAndroidTest
```

## 🔍 Verificar la Compilación

Para verificar que todo esté configurado correctamente:

```bash
./gradlew check
```

## 📦 Distribución

### Para Google Play Store

1. Generar AAB (Android App Bundle):
   ```bash
   ./gradlew bundleRelease
   ```

2. El archivo estará en: `app/build/outputs/bundle/release/app-release.aab`

3. Subir a Google Play Console

### Para Distribución Directa

1. Generar APK:
   ```bash
   ./gradlew assembleRelease
   ```

2. El archivo estará en: `app/build/outputs/apk/release/app-release.apk`

3. Distribuir el APK según sea necesario

## 🐛 Solución de Problemas

### Error: "release.keystore no encontrado"
Verificar que el archivo `app/release.keystore` existe en el directorio correcto.

### Error: "SDK version not found"
Instalar el SDK 35 desde Android Studio:
- Abirir Android Studio > Settings > SDK Manager
- Instalar Android SDK 35

### La compilación falla por dependencias
Limpiar y reconstruir:
```bash
./gradlew clean build
```

## 📝 Licencia

Este proyecto es propietario de Madera Poltand.

## 👥 Contacto

Para preguntas o soporte, contactar al equipo de desarrollo de Madera Poltand.

## 📈 Versión Actual

- **Versión**: 1.0
- **Código de Versión**: 1
- **Android Mínimo**: API 26 (Android 8.0)
- **Android Objetivo**: API 35 (Android 15)

---

**Última actualización**: Enero 2026

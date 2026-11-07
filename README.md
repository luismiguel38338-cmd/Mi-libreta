# 📓 Mi Libreta - Libreta de Deudas para Colmados y Pequeños Negocios

**Mi Libreta** es una aplicación nativa de Android desarrollada en **Kotlin** y **Jetpack Compose**, diseñada para dueños de colmados, abarrotes, tiendas de barrio y vendedores independientes que necesitan llevar un control estricto, ordenado y rápido de lo que sus clientes deben (fiados) y los abonos que realizan.

---

## 🌟 Características Principales

- **100% Offline y Privada**: Funciona completamente sin internet, sin servidores externos, sin cuentas ni registros en la nube. Todos los datos se quedan exclusivamente en el teléfono del comerciante.
- **Moneda Local (Pesos Dominicanos)**: Formato monetario estricto `"RD$ 1,250.00"`.
- **Cálculo Reactivo de Saldos**: El saldo de cada cliente se calcula matemáticamente como la suma de sus deudas menos la suma de sus abonos en tiempo real usando SQLite/Room y `Flow`.
- **Control de Crédito y Promesas de Pago**:
  - Límite de crédito opcional por cliente con barra de uso y alertas visuales al sobrepasarlo.
  - Fechas acordadas de pago (ej. quincena, fin de mes) con indicadores de vencimiento (*"Vence hoy"*, *"Venció hace 3 días"*).
  - Filtros rápidos en la lista: *Todos*, *Con deuda*, *Al día*, *Vencidos* y *Cerca del límite*.
- **Recordatorios por WhatsApp**:
  - Abre WhatsApp con mensajes automáticos personalizados: *"Hola [Cliente], te recuerdo desde [Negocio] que tienes un saldo pendiente de RD$ X. ¡Gracias!"*.
  - Atajos de llamada telefónica directa desde la lista.
- **Comprobantes y Recibos Digitales**:
  - Generación de comprobantes individuales de fiado y recibos de abono listos para compartir o imprimir.
  - Generación de estados de cuenta completos con historial de movimientos.
- **Panel de Estadísticas del Negocio**:
  - Capital total en la calle (cuentas por cobrar).
  - Fiado este mes vs. Cobrado este mes.
  - Movimientos del día.
  - Tasa de recuperación de cartera y Top 5 deudores.
- **Copia de Seguridad y Exportación**:
  - Exportación de la base de datos completa a archivo JSON para respaldar en Google Drive o WhatsApp.
  - Restauración segura de copias de seguridad.
  - Exportación de cartera de deudas a formato tabular compatible con Excel (CSV).
- **Protección Legal y de Privacidad**:
  - Política de Privacidad integrada en la app.
  - Términos de Servicio y Descargo de Responsabilidad (Disclaimer) para proteger al comerciante.
  - Cláusula de validez mercantil en todos los comprobantes compartidos.

---

## 🛠️ Stack Tecnológico

- **Lenguaje**: Kotlin 2.2+
- **Interfaz de Usuario**: Jetpack Compose & Material Design 3 (M3)
- **Persistencia Local**: Room Database 2.7.0 con **KSP** (Kotlin Symbol Processing)
- **Arquitectura**: MVVM (Model - View - ViewModel) con `StateFlow` y `collectAsStateWithLifecycle`
- **Navegación**: Navigation Compose con rutas estructuradas
- **Pruebas**: JUnit 4 y pruebas unitarias de formateo, cálculos y entidades

---

## 🚀 Cómo Abrir y Compilar el Proyecto

### Requisitos:
- **Android Studio**: Ladybug, Koala, Jellyfish o superior.
- **JDK**: Java 17 o 21 (incluido en Android Studio).
- **Android SDK**: `minSdk 24`, `targetSdk 36`, `compileSdk 36`.

### Pasos:
1. Clona este repositorio o abre la carpeta en Android Studio:
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   ```
2. En Android Studio: `File > Open...` y selecciona la carpeta del proyecto.
3. Espera que Gradle sincronice las dependencias automáticamente.
4. Para compilar desde la terminal:
   ```bash
   ./gradlew assembleDebug
   ```
5. Para ejecutar las pruebas unitarias:
   ```bash
   ./gradlew testDebugUnitTest
   ```

El archivo APK generado se ubicará en: `app/build/outputs/apk/debug/app-debug.apk`.

---

## ⚖️ Aviso Legal y Exención de Responsabilidad

Esta aplicación es una herramienta de asistencia administrativa y control interno contable. No constituye una entidad bancaria ni presta servicios financieros regulados. La custodia de los datos reside exclusivamente en el dispositivo del usuario.

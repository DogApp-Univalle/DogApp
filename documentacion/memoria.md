# Memoria de construcción — DogApp

Bitácora técnica del proyecto. Aquí se registra **qué se construyó, qué se cambió
respecto al material base y qué queda pendiente**, para que cualquier integrante
pueda retomar el trabajo sin adivinar.

- Curso: Desarrollo de Aplicaciones para Dispositivos Móviles
- Proyecto: DogApp — Agenda de citas para mascotas (Miniproyecto 1, Sprint 1)
- Repositorio: https://github.com/DogApp-Univalle/DogApp
- Última actualización: **2026-11-12 18:51**

> Este archivo es de trabajo, no reemplaza al enunciado oficial
> (`documentacion/HU_miniproyectoDogApp_Pro1.pdf`). Si hay conflicto, gana el PDF.

---

## 1. Punto de partida

El equipo arrancó a partir del proyecto de la clase 8 (`clase8_univalle-main`,
inventario), que trae la misma arquitectura que pide la rúbrica: MVVM,
Repository, Room con KSP, cuatro Fragments y `nav_graph.xml`.

**Decisión:** se copió ese esqueleto dentro de este repositorio y se transformó a
DogApp. El proyecto original de la clase 8 **no se modificó** (sigue intacto en su
carpeta). Usar material del curso no es copia; copiar el repositorio de otro
equipo sí lo es.

---

## 2. Cambios respecto al esqueleto

| # | Qué tenía el esqueleto | Qué tiene DogApp | Motivo |
|---|------------------------|------------------|--------|
| 1 | `com.example.clase8` | `com.univalle.dogapp` | Paquete propio del proyecto |
| 2 | `dataBinding = true` | `viewBinding = true` | Solo se necesita binding de vistas, no expresiones en XML |
| 3 | DAO devolvía `MutableList<Inventory>` | DAO devuelve `LiveData<List<CitaEntity>>` | Sin LiveData, el Home no se refresca al guardar/editar/borrar (trampa clásica) |
| 4 | Solo `getListInventory()` | `observarTodas()`, `observarPorId()`, `buscarPorId()`, CRUD | Detalle y Editar necesitan leer una cita concreta |
| 5 | `InventoryRepository(context)` construía la base | `CitaRepository(dao, api)` | El Repository no debe conocer `Context`; ahora lo arma `ServiceLocator` |
| 6 | Retrofit, Glide, Lottie y Biometric ausentes | Agregados y usados | Ítems de la rúbrica (API REST) y HU 1.0 |
| 7 | Navigation 2.3.5 | Navigation 2.7.6 | Versión compatible con `compileSdk 34` |
| 8 | Room 2.5.2 / corrutinas 1.6.4 / livedata 2.3.1 | Room 2.6.1 / corrutinas 1.7.3 / lifecycle 2.7.0 | Versiones consistentes entre sí |
| 9 | ViewModel `AndroidViewModel` con `context` | ViewModels con `ViewModelProvider.Factory` y sin `Context` | Regla de arquitectura: ningún ViewModel conoce `Context` |
| 10 | Modelo = entidad Room | `Cita` (dominio) + `CitaEntity` (Room) con mapeo | Separa datos de presentación |
| 11 | 4 Fragments (Home, Add, Details, Edit) | 5 Fragments (+ Login) | Faltaba HU 1.0 |
| 12 | Basura: `.idea/`, `app/release/app-release.aab`, `jacoco.gradle`, tests de ejemplo | Eliminado del repo | Repositorio limpio para entregar |

### Archivos eliminados del esqueleto

- `app/src/main/java/com/example/clase8/**` (reemplazado por el paquete nuevo)
- `app/src/androidTest/`, `app/src/test/` (tests de ejemplo de la clase)
- `app/release/app-release.aab`, `jacoco.gradle`
- Layouts y drawables del inventario (`fragment_*_item.xml`, `item_inventory.xml`,
  `imagen.jpg`, `bg_redondo.xml`, `boton.xml`)
- `res/values-night/` (la app fija tema claro para respetar el diseño de los pantallazos)

---

## 3. Qué se construyó

### 3.1 Capa de datos (Room)

- `CitaEntity` → tabla `citas`: `id` (autogenerado = turno `#n`), `nombreMascota`,
  `raza`, `propietario`, `telefono` (String, para no perder ceros a la izquierda),
  `sintoma`, `imagenUrl`, `fechaCreacion`.
- `CitaDao`: `insertar` (devuelve el id), `actualizar`, `eliminar`,
  `observarTodas(): LiveData<List<CitaEntity>>`, `observarPorId(id): LiveData<CitaEntity?>`,
  `buscarPorId(id)` (lectura puntual para la edición).
- `DogAppDatabase`: singleton con `Room.databaseBuilder`, archivo `dogapp.db`.

### 3.2 API REST (Retrofit)

- `DogApiService`: `api/breeds/list/all`, `api/breed/{raza}/images/random`,
  `api/breeds/image/random`.
- `RetrofitClient`: un solo `Retrofit` por proceso, Gson y logging básico.
- `CitaRepository.razas()`: una sola llamada por pantalla; si falla la red
  devuelve lista vacía y la app sigue funcionando.
- `CitaRepository.fotoDeRaza(raza)`: plan A `/breed/{raza}/images/random`,
  plan B `/breeds/image/random`, plan C `null` → marcador local.

### 3.3 MVVM + Repository

- `ServiceLocator`: construye base de datos, servicio y repository una sola vez.
- ViewModels: `LoginViewModel`, `HomeViewModel`, `FormCitaViewModel` (compartido
  por HU 3.0 y HU 5.0), `DetalleViewModel`. Todos sin `Context`.
- `HomeViewModel.citas` es directamente el `LiveData` del DAO → refresco automático.

### 3.4 Diseño (RA-2)

- Tokens en `res/values/colors.xml`: `dogapp_pink #F05861`, `dogapp_dark #2E2E33`,
  `dogapp_pink_disabled #F5A9AD`, `dogapp_field_focus #6D3FCB`,
  `dogapp_edit_blue #2B2B6B`, `ink`, `hint_gray`, `line_gray`.
- Medidas en `dimens.xml`: radios 16/24/28/48 dp, avatar 52 dp, solape 32 dp.
- `Theme.DogApp` (`Material3.Light.NoActionBar`) con barra de estado oscura.
- Fuente externa de la marca: `res/font/dogapp_brand.ttf` (**Lilita One**,
  descargada de Google Fonts, licencia OFL) — cumple el criterio "fuentes externas
  que imiten ese estilo".
- `bg_header_home.xml`: esquina inferior izquierda curva de 48 dp.
- `bg_toolbar_curved.xml`: esquinas inferiores curvas de 28 dp.
- `Widget.DogApp.TextInputLayout`: borde gris 1 dp y morado 2 dp con foco.
- `ShapeAppearanceOverlay.DogApp.Circle`: avatar circular.
- Icono propio `dogApp`: capas adaptativas generadas a partir de la cabeza de perro
  (`mipmap-anydpi-v26/ic_launcher.xml` + PNG por densidad). Ya no es el icono por
  defecto de Android Studio.

### 3.5 Pantallas

| HU | Estado | Notas |
|----|--------|-------|
| 1.0 Login | Implementada | Fondo oscuro sin toolbar, perro con transparencia, marca con fuente externa, huella Lottie (`assets/fingerprint.json`), `BiometricPrompt` con título/subtítulo/Cancelar, `popUpTo` para no volver al login |
| 2.0 Home | Implementada | Header con esquina curva, marca, RecyclerView con avatar circular, nombre, síntoma y turno `#n`; FAB rosado; atrás → `finishAffinity()` |
| 3.0 Nueva cita | Implementada | 4 campos validados, autocompletado de razas por prefijo (umbral 2), dropdown de síntomas quemado, botón que se habilita solo y pasa a blanco bold, mensaje "Selecciona un síntoma" |
| 4.0 Detalle | Implementada | Tarjeta de foto **superpuesta** sobre la tarjeta de datos (margen superior negativo + elevación mayor), turno, raza bold, síntoma, divisor rosado, propietario y teléfono; FAB eliminar (rosado, izquierda) y editar (azul oscuro, derecha) |
| 5.0 Editar | Implementada | Mismo formulario, precargado desde el argumento de navegación; al cambiar la raza se regenera la foto |

### 3.6 Navegación

`nav_graph.xml` con 5 destinos, `startDestination = loginFragment` y las acciones:
`action_login_to_home` (popUpTo login inclusive),
`action_home_to_nueva`, `action_home_to_detalle` (arg `citaId`),
`action_nueva_to_home` (popUpTo home),
`action_detalle_to_home`, `action_detalle_to_editar` (arg `citaId`),
`action_editar_to_home` (popUpTo home).

No se usó Safe Args: el `citaId` viaja con `Bundle`. Es válido y evita un plugin
más; si el equipo quiere, Safe Args se agrega después sin cambiar la navegación.

---

## 4. Trampas de la rúbrica que se corrigieron

1. **DAO con `List` en vez de `LiveData`** → ahora `LiveData`, el Home se refresca solo.
2. **Lógica en el Fragment** → la validación y el guardado viven en el ViewModel y el Repository.
3. **Cinco Activities** → una sola `MainActivity` + `NavHostFragment`.
4. **Back del Home volvía al login** → `finishAffinity()` + `popUpTo` al entrar al Home.
5. **Botón que arranca habilitado o nunca se habilita** → estado calculado con `doAfterTextChanged`.
6. **Botón habilitado sin cambiar a blanco bold** → `setTextColor` + `Typeface.BOLD`.
7. **Falta "Selecciona un síntoma"** → se valida el índice 0 del dropdown.
8. **Foto aleatoria aunque se eligió una raza** → `/breed/{raza}/images/random` y
   regeneración al editar la raza.
9. **Teléfono como número** → se guarda `String` con `inputType="number"` y `maxLength=10`.
10. **Tarjetas del detalle separadas** → solape con margen negativo y elevación.
11. **Icono por defecto de Android Studio** → icono propio generado.
12. **App que se cae sin internet** → `runCatching` en la API y marcador local.

---

## 5. Entorno de compilación (léelo antes de abrir el proyecto)

Se compiló desde línea de comandos con:

- **JDK 17** (`/usr/lib/jvm/java-17-openjdk-amd64`). El JDK 25 del sistema **no**
  sirve para Gradle 8.2; Android Studio trae su propio JBR y no hay problema.
- Android SDK con **platform `android-34`** (se instaló porque solo estaban 33, 35 y 37).
- `local.properties` con `sdk.dir` (está en `.gitignore`, cada integrante lo genera).

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./gradlew assembleDebug
```

Resultado: **BUILD SUCCESSFUL** (APK en `app/build/outputs/apk/debug/app-debug.apk`).

---

## 6. Estado verificado

| Verificación | Estado |
|--------------|--------|
| `assembleDebug` compila | ✅ |
| Room genera el DAO (KSP) | ✅ |
| Los 5 Fragments están en el grafo | ✅ |
| Icono propio generado | ✅ |
| Lottie de huella en `assets` | ✅ |
| Fuente externa en `res/font` | ✅ |
| Login con biometría (huella simulada) | ✅ navega al Home |
| Crear cita + autocompletado | ✅ "pu" → Pug, Puggle (filtro por prefijo) |
| Botón deshabilitado / habilitado blanco bold | ✅ |
| "Selecciona un síntoma" | ✅ |
| La cita aparece en el Home sin refrescar a mano | ✅ |
| Detalle con tarjetas superpuestas y turno | ✅ |
| Editar precargado + foto regenerada al cambiar raza | ✅ Pug → Akita cambió la foto |
| Eliminar y volver al Home | ✅ |
| Atrás en el Home → escritorio | ✅ |
| Modo avión: guarda la cita con marcador | ✅ sin cierre inesperado |

### 6.1 Hallazgos de la prueba en emulador (y sus correcciones)

Probar en el emulador destapó cuatro cosas que no se ven compilando:

| Hallazgo | Corrección aplicada |
|----------|--------------------|
| La animación Lottie no se dibujaba en el emulador | Se forzó `app:lottie_renderMode="software"` en el `LottieAnimationView` |
| Las tarjetas salían rosadas en vez de blancas | `elevationOverlayEnabled=false` en `Theme.DogApp` (Material 3 teñía con el `colorPrimary`) |
| El turno `#n` del Detalle quedaba tapado por el solape de tarjetas | `paddingTop=44dp` dentro de la tarjeta de datos |
| La imagen del perro mostraba cuello y pecho, no "solo la cabeza" | Recorte de la cabeza (`perro_cabeza.png`) con desvanecido en el borde |
| El desplegable de razas/síntomas salía rosado | `android:popupBackground="@color/white"` en los dos autocompletados |

### 6.2 Capturas (en `documentacion/capturas/`)

`01_login.png` · `02_home.png` · `03_nueva_cita.png` · `04_detalle.png` ·
`05_editar_cita.png` · `06_autocompletado.png` · `07_mensaje_sin_sintoma.png` ·
`08_sin_internet.png`

> El diálogo biométrico no se puede capturar: es una ventana segura del sistema
> (`FLAG_SECURE`), por eso la captura sale en negro. En la sustentación se muestra
> en vivo.

---

## 7. Pendientes (por prioridad)

1. **Completar el README**: nombres de los integrantes y enlace a Jira.
2. **Jira**: épica + 5 historias con criterios, subtareas y responsables; mover
   tarjetas durante el sprint (evidencia del RA-3).
3. **Ramas por HU y Pull Requests** en GitHub, con commits de todos los integrantes.
4. **PDF de entrega** `equipoX.pdf` con nombres, apellidos y enlaces.
5. **Tag `v1.0-sprint1`** y congelar el repositorio antes del 25 de noviembre, 6:30 p.m.
6. Opcional: confirmación antes de eliminar, Safe Args, pantalla de "sin conexión".

---

## 8. Registro cronológico

### 2026-11-12 — Fase 0 y Fase 1

- Se analizó la documentación (enunciado de HU, especificación técnica, diapositivas de la clase 9).
- Se encontró y reutilizó el esqueleto de la clase 8 sin modificarlo.
- Se creó la estructura del proyecto en este repositorio y se renombró el paquete.
- Se reescribió la capa de datos (Room + Retrofit + Repository + ServiceLocator).
- Se construyeron las 5 pantallas, el sistema de diseño, la fuente externa y el icono.
- Se corrigieron las trampas de la rúbrica listadas en la sección 4.
- Primer build exitoso.
- Repositorio Git inicializado con `origin = git@github.com:DogApp-Univalle/DogApp.git`.
- Prueba completa en emulador (API 37): huella enrolada con `adb -e emu finger touch 1`,
  recorrido login → home → nueva → home → detalle → editar → home → detalle → borrar,
  y prueba en modo avión.
- Se corrigieron los hallazgos de la sección 6.1.
- Se guardaron las capturas en `documentacion/capturas/`.

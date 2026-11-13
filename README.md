# DogApp — Agenda de citas para mascotas

Miniproyecto 1 · Sprint 1 · Desarrollo de Aplicaciones para Dispositivos Móviles
Universidad del Valle — Escuela de Ingeniería de Sistemas y Computación

Aplicación Android nativa (Kotlin) para que el administrador de una veterinaria
registre y gestione las citas de las mascotas que debe atender.

## Integrantes

- _Medina Hernán Cod. 2369271_
- _Santos Sebastian Cod. 2160870_
- _Trujillo Nicolas Cod. 2360268_
- _Chicaiza Andres Cod. 2560684_
- _Agudelo Juan Cod. 2360154_

## Pantallas (una sola Activity + cinco Fragments)

| HU | Pantalla | Fragment |
|----|----------|----------|
| 1.0 | Login con biometría | `LoginFragment` |
| 2.0 | Home administrador de citas | `HomeFragment` |
| 3.0 | Nueva cita | `NuevaCitaFragment` |
| 4.0 | Detalle de la cita | `DetalleFragment` |
| 5.0 | Editar cita | `EditarFragment` |

## Capturas

| Login (biometría) | Home | Nueva cita |
|---|---|---|
| ![Login](documentacion/capturas/01_login.png) | ![Home](documentacion/capturas/02_home.png) | ![Nueva cita](documentacion/capturas/03_nueva_cita.png) |

| Detalle | Editar cita | Autocompletado |
|---|---|---|
| ![Detalle](documentacion/capturas/04_detalle.png) | ![Editar](documentacion/capturas/05_editar_cita.png) | ![Autocompletado](documentacion/capturas/06_autocompletado.png) |

## Arquitectura MVVM + Repository

```
com.univalle.dogapp
├── data/
│   ├── local/     CitaEntity · CitaDao · DogAppDatabase   (Room / SQLite)
│   └── remote/    DogApiService · BreedsResponse · ImageResponse · RetrofitClient
├── repository/    CitaRepository
├── model/         Cita
├── viewmodel/     LoginViewModel · HomeViewModel · FormCitaViewModel · DetalleViewModel
├── view/          MainActivity · login/ · home/ · nueva/ · detalle/ · editar/ · form/
├── di/            ServiceLocator (inyección sin frameworks)
└── utils/         Constants · BiometricHelper
```

Reglas que se respetan:

- Ningún Fragment toca Room ni Retrofit: todo pasa por el ViewModel y el Repository.
- Ningún ViewModel recibe `Context`.
- Toda la UI se actualiza observando `LiveData`; las operaciones de datos son `suspend`.
- `observarTodas()` del DAO devuelve `LiveData<List<CitaEntity>>`, por eso el Home se
  refresca solo al insertar, editar o eliminar.

## Tecnologías

Kotlin · MVVM + Repository · Room 2.6.1 (KSP) · Retrofit 2.9 + Gson ·
Navigation 2.7.6 · Glide 4.16 · Lottie 6.3 · androidx.biometric 1.1 ·
ViewBinding · Corrutinas.

## Cómo compilar

```bash
# Requiere JDK 17 y el Android SDK con el platform 34
./gradlew assembleDebug
```

Abrir el proyecto en Android Studio y ejecutar en un dispositivo o emulador con
**huella enrolada** (en el emulador: `adb -e emu finger touch 1`).

## API REST consumida

| Uso | Endpoint |
|-----|----------|
| Lista de razas (autocompletado) | `https://dog.ceo/api/breeds/list/all` |
| Foto de una raza concreta | `https://dog.ceo/api/breed/{raza}/images/random` |
| Respaldo (raza no encontrada o sin red) | `https://dog.ceo/api/breeds/image/random` |

### Decisión técnica sobre la foto de la raza

El enunciado pide que la imagen corresponda a la raza
elegida ("si la raza fue Akita, la imagen debe mostrar un Akita"). Por eso:

1. Se usa `/breed/{raza}/images/random` para cumplir el requisito funcional.
2. Se deja `/breeds/image/random` como respaldo cuando la raza no existe en la API.
3. Si no hay red, la cita se guarda igual y la vista muestra un marcador local.

## Persistencia

Room con la tabla `citas`: `id` (turno `#n`, autogenerado), `nombreMascota`,
`raza`, `propietario`, `telefono` (String, para no perder ceros), `sintoma`,
`imagenUrl` y `fechaCreacion`.

## Gestión del proyecto

- Jira: [Proyecto DOG · DogApp](https://hernanchho.atlassian.net/jira/software/projects/DOG/boards/1/backlog)
- Repositorio: https://github.com/DogApp-Univalle/DogApp
- Bitácora de construcción: [documentacion/memoria.md](documentacion/memoria.md)
- Guía del tablero y backlog importable: [documentacion/jira/DogApp-backlog.md](documentacion/jira/DogApp-backlog.md)

## Flujo de ramas

```
main                    ← lo entregable, se congela el 25 de noviembre
└── develop             ← integración del sprint (aquí se trabaja)
    ├── feature/hu1-login
    ├── feature/hu2-home
    ├── feature/hu3-nueva
    ├── feature/hu4-detalle
    └── feature/hu5-editar
```

Cada historia se trabaja en su rama y se integra con un **Pull Request hacia `develop`**
revisado por otro integrante. No se commitea directamente en `main`.

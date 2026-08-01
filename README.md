# Sistema de Turnos

API REST para gestión de turnos de profesionales independientes (peluqueros, kinesiólogos, abogados, nutricionistas, etc.). Permite a cada profesional configurar sus servicios y disponibilidad, mientras que sus clientes reservan turnos desde un link público, sin necesidad de crear una cuenta.

## El problema que resuelve

Muchos profesionales independientes coordinan turnos por WhatsApp, lo que genera superposición de horarios, tiempo perdido en la coordinación, y ausentismo por falta de recordatorios. Este sistema centraliza la gestión de agenda y expone un flujo de reserva público simple para el cliente final.

## Stack técnico

- **Java 21** + **Spring Boot 4.1**
- **Spring Data JPA** + **MySQL 8.4**
- **Flyway** — versionado de schema de base de datos
- **Spring Security** + **JWT** (jjwt) — autenticación stateless
- **MapStruct** — mapeo entidad ↔ DTO
- **Docker** + **Docker Compose** — despliegue containerizado
- **springdoc-openapi (Swagger)** — documentación interactiva de la API

## Características principales

- 🔐 **Autenticación JWT** — solo los profesionales tienen cuenta; los clientes reservan sin login
- 📅 **Motor de disponibilidad** — calcula slots libres cruzando disponibilidad recurrente, excepciones puntuales (vacaciones, bloqueos) y turnos ya reservados
- 🔒 **Control de concurrencia** — previene el doble booking combinando lock pesimista a nivel de transacción y un constraint único a nivel de base de datos
- 🎫 **Reserva sin cuenta** — el cliente reserva desde un link público (`/reservar/{slug}`) y recibe un token de cancelación propio, sin exponer sus datos ni necesitar credenciales
- 📊 **Agenda del profesional** — visualización y gestión de turnos por rango de fechas, con cambio de estado (completado / ausente)

## Modelo de datos

![DER del sistema](docs/der-sistema-turnos.png)

*(el diagrama editable en formato draw.io está en `docs/der-sistema-turnos.drawio`)*

## Requisitos funcionales

### Gestión del profesional
- Registro e inicio de sesión (JWT)
- CRUD de servicios ofrecidos (nombre, duración, precio)
- Configuración de disponibilidad recurrente semanal
- Definición de excepciones puntuales (vacaciones, bloqueos parciales)

### Reserva pública (sin login)
- Consulta de servicios de un profesional por su slug público
- Consulta de horarios disponibles para un servicio y fecha
- Reserva de turno indicando nombre, teléfono y (opcional) email
- Prevención de doble booking mediante control de concurrencia

### Gestión de turnos
- Visualización de agenda por rango de fechas
- Cancelación de turno mediante token propio (cliente) o desde el panel (profesional)
- Cambio de estado del turno (completado / ausente)

## Cómo levantar el proyecto

### Requisitos previos
- [Docker](https://www.docker.com/products/docker-desktop/) y Docker Compose

### Pasos

1. Cloná el repositorio:
   ```bash
   git clone https://github.com/tu-usuario/sistema-de-turnos.git
   cd sistema-de-turnos
   ```

2. Creá un archivo `.env` en la raíz del proyecto, basado en `.env.example`:
   ```
   MYSQL_ROOT_PASSWORD=elegí_una_contraseña
   JWT_SECRET=generá_una_clave_aleatoria_de_al_menos_32_caracteres
   JWT_EXPIRATION=86400000
   ```

   Podés generar un `JWT_SECRET` seguro con:
   ```bash
   openssl rand -hex 32
   ```

3. Levantá los contenedores:
   ```bash
   docker compose up --build
   ```

4. La API va a estar disponible en `http://localhost:8080`, y la documentación interactiva en `http://localhost:8080/swagger-ui.html`.

## Documentación de la API

Con el proyecto corriendo, toda la documentación interactiva (endpoints, esquemas de request/response, y la posibilidad de probar cada ruta) está disponible en:

```
http://localhost:8080/swagger-ui.html
```

Para los endpoints privados, usá el botón **Authorize** en Swagger UI pegando el JWT obtenido desde `POST /api/auth/login` (con el prefijo `Bearer` ya lo agrega Swagger automáticamente).

## Endpoints principales

| Método | Ruta | Descripción | Auth |
|---|---|---|---|
| POST | `/api/auth/register` | Registro de un profesional | No |
| POST | `/api/auth/login` | Login, devuelve JWT | No |
| GET/POST/PUT/DELETE | `/api/servicios` | CRUD de servicios propios | Sí |
| GET/POST/PUT/DELETE | `/api/disponibilidad` | CRUD de disponibilidad propia | Sí |
| GET/POST/DELETE | `/api/excepciones` | CRUD de excepciones propias | Sí |
| GET | `/api/turnos/agenda` | Agenda del profesional por rango de fechas | Sí |
| PATCH | `/api/turnos/{id}/estado` | Cambiar estado de un turno | Sí |
| GET | `/api/public/profesionales/{slug}/servicios` | Servicios de un profesional | No |
| GET | `/api/public/profesionales/{slug}/disponibilidad` | Horarios disponibles | No |
| POST | `/api/public/profesionales/{slug}/turnos` | Reservar un turno | No |
| DELETE | `/api/public/turnos/{cancelacionToken}` | Cancelar un turno | No |

## Decisiones de diseño destacadas

- **Doble ID por entidad pública** (`id` interno autoincremental + `publicId` UUID expuesto): evita exponer IDs secuenciales adivinables en la API pública, sin sacrificar performance de joins internos.
- **Token de cancelación separado del ID del turno**: el cliente recibe un UUID de un solo propósito para cancelar su turno, distinto del identificador público del recurso — así un link compartido por error no otorga más permisos de los necesarios.
- **Control de concurrencia en dos capas**: lock pesimista (`SELECT ... FOR UPDATE`) a nivel de transacción para el camino feliz, más un constraint único a nivel de base de datos como garantía última, incluso si la lógica de aplicación fallara.
- **Migraciones versionadas con Flyway** en lugar de `ddl-auto: update`, para tener control explícito y reproducible sobre los cambios de schema.

## Roadmap / mejoras futuras

- [ ] Recordatorios automáticos por email antes del turno
- [ ] Refresh tokens para sesiones más largas sin re-loguear
- [ ] Panel de métricas (turnos por semana, tasa de ausentismo)

## Autor

Alan — [GitHub](https://github.com/tu-usuario)

Proyecto desarrollado como parte de un portafolio backend, con foco en autenticación JWT, control de concurrencia, y diseño de APIs REST con separación clara entre recursos públicos y privados.
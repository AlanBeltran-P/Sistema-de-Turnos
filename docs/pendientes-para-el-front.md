# Pendientes del back para el front

El front ([Sistema-Turnos-Frontend](https://github.com/AlanBeltran-P/Sistema-Turnos-Frontend)) hoy funciona con datos de ejemplo.
Esto es lo que le falta al back para que funcione con datos reales. Los nombres de los DTOs son los que ya usa el front.

## 1. Base de datos (Flyway)

### `V6__crear_tabla_cliente.sql`

| Columna | Tipo | Notas |
|---|---|---|
| `id` | BIGINT PK autoincremental | interno |
| `public_id` | UUID | el que se expone, igual que en Profesional |
| `nombre` | VARCHAR | obligatorio |
| `email` | VARCHAR | obligatorio, **UNIQUE** |
| `telefono` | VARCHAR | obligatorio |
| `password` | VARCHAR | hash con BCrypt, igual que el profesional |

### `V7__agregar_cliente_a_turno.sql`

- `turno.cliente_id`: BIGINT **nullable**, FK a `cliente.id`.
- Queda en `null` cuando se reserva sin cuenta, así la reserva pública sigue funcionando igual que hoy.

## 2. Seguridad

- El JWT tiene que llevar el **rol** (`PROFESIONAL` o `CLIENTE`). Hoy `UserDetailsServiceImpl` busca solo profesionales por email, así que hay que poder distinguir de qué tabla viene cada usuario.
- En `SecurityConfig`:
  - `/api/cliente/auth/**` → `permitAll()`
  - `/api/cliente/**` → `hasRole("CLIENTE")`
  - `/api/servicios/**`, `/api/disponibilidad/**`, `/api/excepciones/**` y `/api/turnos/**` → `hasRole("PROFESIONAL")`. Hoy alcanza con estar autenticado, y con dos tipos de usuario un cliente podría entrar ahí.

## 3. Endpoints nuevos

| Método | Ruta | Recibe | Devuelve | Errores |
|---|---|---|---|---|
| POST | `/api/cliente/auth/register` | `ClienteRegisterRequest` | `201` + `ClienteResponse` | `409` si el email ya existe, `400` si falla la validación |
| POST | `/api/cliente/auth/login` | `AuthRequest` (el que ya existe) | `AuthResponse { token }` | `401` si el email o la contraseña no coinciden |
| GET | `/api/cliente/me` | token de CLIENTE | `ClienteResponse` | `401` |
| GET | `/api/cliente/turnos` | token de CLIENTE | `List<TurnoResponse>` ordenada por `fechaHoraInicio` ascendente | `401` |
| GET | `/api/public/profesionales/{slug}` | slug | `ProfesionalPublicoResponse` | `404` si no existe |

### DTOs nuevos

```java
public record ClienteRegisterRequest(
        @NotBlank String nombre,
        @NotBlank @Email String email,
        @NotBlank String telefono,
        @NotBlank @Size(min = 8) String password
) {}

public record ClienteResponse(UUID publicId, String nombre, String email, String telefono) {}

public record ProfesionalPublicoResponse(String nombre, String slug) {}
```

### Detalles

- `GET /api/cliente/turnos` reutiliza el `TurnoResponse` que ya existe, que incluye `cancelacionToken`. Está bien mandarlo, porque el turno es del propio cliente. Así el front cancela con el `DELETE /api/public/turnos/{cancelacionToken}` que ya existe, sin endpoint nuevo.
- `GET /api/public/profesionales/{slug}` no devuelve email ni teléfono, igual que el resto de lo público.

## 4. Cambio en un endpoint existente

`POST /api/public/profesionales/{slug}/turnos` (reservar): el contrato no cambia.

- Si el pedido trae un `Authorization: Bearer <token>` de un CLIENTE, se guarda `cliente_id` en el turno.
- Si no trae token, `cliente_id` queda en `null`, como ahora.
- La ruta sigue en `permitAll()`. Hay que revisar que `JwtAuthenticationFilter` lea el token cuando viene, aunque la ruta sea pública.

## 5. Lo que cambia en el front al conectar

- En el mock, `ClienteAuthService.login()` devuelve el cliente directamente. Con el back real va a recibir el `token`, guardarlo y después llamar a `GET /api/cliente/me` para traer nombre, email y teléfono.
- Las pantallas no cambian: el cambio queda dentro de `core/services`, como el resto.

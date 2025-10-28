#  Permission Service

Microservicio encargado de gestionar los permisos sobre snippets (`OWNER` y `SHARED`).

---

##  Levantar el proyecto

### Con Docker

```bash
docker compose -f docker-compose.dev.yml up --build
```

La app quedará en http://localhost:8083.

### Sin Docker

```bash
export PORT=8083
export DB_HOST=localhost
export DB_PORT=5436
export DB_NAME=permission_db
export DB_USER=postgres
export DB_PASSWORD=postgres
./gradlew bootRun
```

### Health Check

```bash
curl http://localhost:8083/actuator/health
# {"status":"UP"}
```

---

## ⚙️ Variables de entorno

| Variable | Valor |
|----------|-------|
| PORT | 8083 |
| DB_HOST | localhost |
| DB_PORT | 5436 |
| DB_NAME | permission_db |
| DB_USER | postgres |
| DB_PASSWORD | postgres |

---

##  Endpoints principales

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/permissions` | Crea un permiso (OWNER o SHARED) |
| DELETE | `/api/permissions/{snippetId}/{userId}` | Elimina un permiso |
| GET | `/api/permissions/{snippetId}/author` | Obtiene el autor (OWNER) de un snippet |
| GET | `/api/permissions/user/{userId}` | Lista los permisos de un usuario (opcional `?type=`) |

### Ejemplos rápidos

#### Crear permiso

```bash
curl -X POST http://localhost:8083/api/permissions \
  -H "Content-Type: application/json" \
  -d '{"snippetId":"11111111-1111-1111-1111-111111111111","userId":"22222222-2222-2222-2222-222222222222","type":"OWNER"}'
```

#### Obtener autor

```bash
curl http://localhost:8083/api/permissions/11111111-1111-1111-1111-111111111111/author
```

#### Listar permisos

```bash
curl http://localhost:8083/api/permissions/user/22222222-2222-2222-2222-222222222222
```

#### Eliminar permiso

```bash
curl -X DELETE http://localhost:8083/api/permissions/11111111-1111-1111-1111-111111111111/22222222-2222-2222-2222-222222222222
```

---

##  Test

```bash
./gradlew test
```

---

##  Docker comandos útiles

```bash
docker compose down -v        # eliminar contenedores y volumenes
docker compose ps             # ver estado
docker logs permission-db     # logs de la base
```

---

##  Migraciones Flyway

**Ubicación:** `src/main/resources/db/migration`

**Ejemplo:**

```sql
CREATE TABLE permissions (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  snippet_id UUID NOT NULL,
  user_id UUID NOT NULL,
  type VARCHAR(12) NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW(),
  CONSTRAINT uq_permissions_snippet_user UNIQUE (snippet_id, user_id)
);
```
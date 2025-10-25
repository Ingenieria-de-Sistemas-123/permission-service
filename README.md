# permission-service

Microservicio para permisos de snippets.

## Endpoints (a completar)
- POST /api/permissions
- DELETE /api/permissions/{snippetId}/{userId}
- GET /api/permissions/{snippetId}/author
- GET /api/permissions/user/{userId}?type=OWNER|SHARED

## Dev local
```bash
# levantar con postgres
docker compose -f docker-compose.dev.yml up --build

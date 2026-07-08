# SistemaGuiasDespacho

Backend Spring Boot para el **Sistema de Gestión de Pedidos y Generación de Guías de Despacho**.
Actividad Sumativa — Semana 6 — Desarrollo Cloud Native (CDY2204) — Duoc UC.

## Stack

- Java 17 + Spring Boot 3.2.5
- Spring Security + OAuth2 Resource Server (validación de JWT emitidos por **Azure AD B2C**)
- Spring Data JPA + MySQL
- **openhtmltopdf** para generar el PDF de cada guía
- **AWS S3 SDK v2** para almacenar los PDFs
- Docker / docker-compose

## Endpoints

| # | Método | Ruta | Rol requerido |
|---|--------|------|----------------|
| 1 | POST | `/api/guias` | ROLE_OPERADOR |
| 2 | POST | `/api/guias/{id}/generar-pdf` | ROLE_OPERADOR |
| 3 | GET | `/api/guias/{id}/descargar` | ROLE_OPERADOR o ROLE_DESCARGA_GUIAS |
| 4 | PUT | `/api/guias/{id}` | ROLE_OPERADOR |
| 5 | DELETE | `/api/guias/{id}` | ROLE_OPERADOR |
| 6 | GET | `/api/guias?transportista=&fecha=` | ROLE_OPERADOR |
| — | GET | `/api/public/ping` | público (healthcheck) |

## Cómo correrlo localmente (sin Docker)

1. Instala Java 17, Maven y MySQL.
2. Crea la base de datos: `CREATE DATABASE guias_despacho_db;`
3. Completa las variables de Azure B2C y AWS (ver `.env.example`) como variables de entorno, o edítalas directo en `application.yml`.
4. `mvn spring-boot:run`

## Cómo correrlo con Docker (recomendado)

```bash
cp .env.example .env
# Edita .env con tus datos reales de Azure B2C y AWS Academy

docker-compose up --build
```

La app queda disponible en `http://localhost:8080`.

## Configuración pendiente (la completas en las siguientes fases)

Este proyecto ya está listo en código, pero para que funcione de punta a punta necesitas:

1. **Azure AD B2C**: crear el tenant, el user flow, el custom role claim, y reemplazar `AZURE_B2C_ISSUER_URI`, `AZURE_B2C_JWKS_URI` y `AZURE_B2C_ROLE_CLAIM`.
2. **AWS S3**: crear el bucket y reemplazar `AWS_S3_BUCKET`, `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN` (credenciales de AWS Academy).
3. **AWS API Gateway**: registrar estos mismos endpoints y configurar el authorizer con Azure B2C.
4. **EC2**: desplegar la imagen Docker y actualizar las URLs de prueba.

## Notas de seguridad

Los roles no se validan por nombre de usuario ni hardcodeados: se leen del **custom claim** que Azure AD B2C agrega al JWT (`extension_<appId>_Role`). `AzureB2CRoleConverter` transforma ese claim en un `ROLE_...` de Spring Security, que es lo que usan los `@PreAuthorize` de cada endpoint.

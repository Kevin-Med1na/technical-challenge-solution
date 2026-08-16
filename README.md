# Technical Challenge — Ingeniero de Software Jr

## Descripción general

Servicio backend REST que permite gestionar productos y extraer información de productos
desde [Automation Exercise](https://automationexercise.com) mediante scraping HTML asíncrono.

El sistema diferencia dos flujos:

- **Gestión manual**: CRUD completo de productos vía API REST.
- **Extracción automática**: solicitud de extracción de múltiples productos que se
  procesa en segundo plano, con seguimiento de estado en tiempo real.

## Tecnologías utilizadas

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 | Lenguaje principal |
| Spring Boot | 4.1.0 | Framework de aplicación |
| Spring Data JPA | — | Persistencia y repositorios |
| PostgreSQL | 16 | Base de datos relacional |
| Jsoup | 1.18.1 | Scraping HTML |
| Springdoc OpenAPI | 3.1.0 | Documentación API |
| Docker / Docker Compose | — | Empaque y ejecución |

## Por qué estas tecnologías

**Java + Spring Boot**: es el stack con el que más experiencia tengo y el que me permite
tomar decisiones técnicas fundamentadas. Spring Boot reduce la configuración inicial y
provee todo lo necesario para una API REST productiva.

**PostgreSQL**: base de datos relacional robusta, apropiada para los datos estructurados
del sistema. El modelo relacional permite mantener la integridad entre jobs, ítems y productos.

**Jsoup**: librería liviana y directa para parsear HTML en Java. Sin dependencias complejas
y con una API de selectores CSS familiar. Adecuada para scraping de páginas HTML estáticas
como las de Automation Exercise.

**Arquitectura MVC**: patrón con el que me encuentro más familiarizado. Permite una
separación clara entre controladores, servicios y acceso a datos.

## Cómo ejecutar la aplicación

### Opción 1 — Docker Compose (recomendado)

Requiere tener Docker instalado.

```bash
docker compose up --build
```

La aplicación estará disponible en `http://localhost:8080`.

### Opción 2 — Ejecución local

Requiere Java 21 y Maven instalados. La base de datos PostgreSQL debe estar corriendo
localmente en el puerto `5432` con la base de datos `technical_challenge`.

```bash
# Levantar solo la base de datos
docker compose up postgres -d

# Compilar y ejecutar la aplicación
mvn spring-boot:run
```

## Endpoints disponibles

La documentación completa de la API está disponible en: http://localhost:8080/swagger-ui.html

### Productos

POST /products Crear producto manualmente  
GET /products Listar todos los productos  
GET /products/{id} Consultar producto por ID  
PATCH /products/{id} Actualizar producto parcialmente  
DELETE /products/{id} Eliminar producto  


### Extracciones

POST /extractions Solicitar extracción de productos  
GET /extractions/{id} Consultar estado del trabajo  
GET /extractions/{id}/products Consultar productos obtenidos del trabajo  


### Ejemplo de extracción

**Request:**
```http
POST /extractions
Content-Type: application/json

{
  "productIds": [1, 2, 3]
}
```

**Response (202 Accepted):**
```json
{
  "id": "uuid-del-trabajo",
  "status": "PENDING",
  "total": 5,
  "processed": 0,
  "successful": 0,
  "failed": 0
}
```

## Estrategia de procesamiento asíncrono

Al recibir `POST /extractions` el sistema:

1. Crea el `ExtractionJob` en estado `PENDING`.
2. Crea un `ExtractionItem` por cada ID solicitado.
3. Retorna `202 Accepted` inmediatamente con el ID del trabajo.
4. Despacha el procesamiento a un `ThreadPoolTaskExecutor` mediante `@Async`.

El procesador asíncrono marca el job como `PROCESSING` y recorre cada ítem
secuencialmente. Por cada ítem: hace scraping con Jsoup, persiste el producto
y actualiza los contadores del job en tiempo real. Si un ítem falla, registra
el error y continúa con el siguiente sin interrumpir el trabajo completo.

El pool de hilos está configurado con un máximo de 3 hilos para no saturar el
sitio externo con solicitudes simultáneas ilimitadas.

Al finalizar, el job queda en uno de tres estados:

- `COMPLETED`: todos los productos fueron extraídos exitosamente.
- `COMPLETED_WITH_ERRORS`: al menos uno falló y al menos uno fue exitoso.
- `FAILED`: todos los productos fallaron.

## Decisiones técnicas y trade-offs

**Un hilo por job, no un hilo por ítem**: dentro de cada trabajo los productos
se procesan secuencialmente. Esto simplifica el manejo de estado (sin concurrencia
sobre los contadores del job) y es suficiente para los volúmenes esperados.

**Upsert por externalId**: si se solicita la extracción de un producto ya existente,
el sistema actualiza sus datos en lugar de crear un duplicado. Esto permite mantener
la información actualizada sin generar inconsistencias.

**Contadores explícitos en el job**: `total`, `processed`, `successful` y `failed`
se almacenan como campos en lugar de calcularlos con queries. Esto hace que el
endpoint de estado sea una lectura directa sin cálculos adicionales.

**Tipos String para campos del producto**: campos como `price` o `availability`
se almacenan como texto tal como vienen del HTML. Normalizar `price` a `BigDecimal`
implicaría lógica de parsing que puede fallar ante variaciones del formato del sitio.

## Aspectos que mejoraría con más tiempo

- **Reintentos automáticos**: ante fallas de red o timeouts, reintentar la extracción
  de un ítem con backoff exponencial antes de marcarlo como fallido.
- **Cancelación de trabajos**: endpoint para cancelar un job en curso.
- **Pruebas automatizadas**: tests de integración para los endpoints y tests unitarios
  para el servicio de scraping con HTML mockeado.
- **Paginación**: el endpoint `GET /products` debería paginar resultados para
  manejar volúmenes grandes.
- **Manejo de jobs huérfanos**: al iniciar la aplicación, marcar como `FAILED`
  cualquier job que quedó en estado `PROCESSING` (por ejemplo ante un reinicio
  inesperado del servidor).

## Herramientas de inteligencia artificial

Se utilizó Claude (Anthropic) como asistente durante el desarrollo para validar
decisiones de diseño, revisar compatibilidad de dependencias entre Spring Boot 4.x
y Springdoc, y verificar los selectores CSS del sitio de scraping.
Todo el código fue revisado, comprendido y ajustado manualmente.

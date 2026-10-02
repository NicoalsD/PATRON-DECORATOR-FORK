# Nueva función: perfil térmico y comparación de destinos

La función nueva tiene dos partes que se usan juntas en el cotizador:

1. **Perfil térmico de la carga** (Abstract Factory): el usuario indica si la carga viaja refrigerada, congelada o a ambiente controlado. El empaque y el sensor se ajustan a esa familia.
2. **Comparar destinos** (Prototype): con un clic, el mismo envío se cotiza hacia todas las demás ciudades para ver qué destino cuesta más y cuánto tarda.

Los escenarios de ejemplo ahora vienen del registro de prototipos, y la cadena de decoradores la arma el Builder. Dónde está cada patrón: [PATRONES_CREACIONALES.md](PATRONES_CREACIONALES.md).

## Uso en la interfaz

1. En **Configura la carga**, elige un escenario o configura el envío.
2. En **Perfil térmico de la carga**, elige *Refrigerado · 2–8 °C*, *Congelado · −25 a −15 °C* o *Ambiente controlado · 15–25 °C*. Las tarifas de *Empaque térmico* y *Registro de temperatura* cambian al instante.
3. Pulsa **Calcular cotización**. El desglose y las capacidades muestran el empaque y el sensor del perfil.
4. Pulsa **Comparar destinos** (columna *Tu envío compuesto*). Aparece una lista con cada ciudad, su plazo y su total. La ciudad elegida se marca como *(actual)*.

Al cambiar cualquier dato, la comparación se oculta hasta volver a pedirla. La descarga JSON conserva el perfil elegido.

## API

### GET `/api/profiles`

```json
[{"id":"refrigerated","name":"Refrigerado","temperatureRange":"2–8 °C","packagingRate":"$24.000 + $1.800/kg","sensorRate":"$12.000 por envío"}, ...]
```

### `profile` en POST `/api/quotes` y GET `/api/quotes/export`

Campo opcional: `refrigerated` (por defecto), `frozen` o `ambient`. La respuesta incluye `profile`. Un perfil desconocido retorna 400.

### GET `/api/templates`

Escenarios de ejemplo, entregados como copias del registro de prototipos:

```json
[{"id":"vaccines","name":"Vacunas en tránsito","origin":"Bogotá","destination":"Medellín","weightKg":2,"declaredValue":1500000,"profile":"refrigerated","services":["cold","monitor"]}, ...]
```

### POST `/api/quotes/compare`

Recibe el mismo cuerpo que `/api/quotes` y devuelve una fila por cada ciudad distinta del origen:

```json
{"origin":"Bogotá","destination":"Medellín","weightKg":2,"declaredValue":1500000,"services":["cold","monitor"]}
```

```json
[
  {"destination":"Medellín","total":76000,"deliveryHours":48,"current":true},
  {"destination":"Cali","total":76000,"deliveryHours":48,"current":false},
  {"destination":"Barranquilla","total":90000,"deliveryHours":72,"current":false}
]
```

Los datos inválidos (por ejemplo, origen igual a destino) retornan 400 con `message`.

## Ejemplos reproducibles

Envío de 2 kg Bogotá → Medellín con empaque y registro:

| Perfil | Base | Empaque | Sensor | Total |
|---|---|---|---|---|
| `refrigerated` | 36.400 | 27.600 | 12.000 | **76.000** |
| `frozen` | 36.400 | 45.000 | 20.000 | **101.400** |
| `ambient` | 36.400 | 10.200 | 7.000 | **53.600** |

## Código relacionado

| Parte | Archivo |
|---|---|
| Perfiles (fábricas y productos) | `src/main/java/com/celsius/domain/profile/` |
| Plantillas y registro (Prototype) | `src/main/java/com/celsius/domain/prototype/` |
| Comparación y escenarios | `QuoteService.compareDestinations()`, `QuoteService.templates()` |
| Endpoints | `web/QuoteController.java` |
| Interfaz | `templates/index.html` (selector de perfil, escenarios, botón *Comparar destinos*), `static/js/app.js` |
| Pruebas | `CargoProfileFactoryTest`, `ShipmentTemplateTest`, `ShipmentBuilderTest` |

# Celsius — Taller Patrón Decorator

Aplicación funcional en **Java 21 + Spring Boot 4.1.1 + Thymeleaf** que aplica Decorator a un caso de logística farmacéutica: cotizar un envío con servicios opcionales de cadena de frío.

![Vista de Celsius](docs/images/vista-escritorio.jpg)

## Contexto y análisis del enunciado

La captura del taller pide seleccionar un caso de la vida real, aplicar el patrón Decorator y entregar un frontend. Este proyecto responde con un cotizador que calcula en Java, permite combinar y reordenar servicios y muestra el objeto compuesto que produce cada resultado.

El caso tiene una complejidad deliberada: costos fijos, costos por peso, seguro porcentual con mínimo, prioridades dependientes del orden, plazos y validaciones. El frontend fue diseñado con la skill **Impeccable** bajo la dirección de un manual técnico con láminas superpuestas.

Las tarifas, plazos y capacidades son datos sintéticos para enseñar el patrón. El sistema implementa una cotización; las operaciones físicas de refrigeración, sensores, seguros y entregas se representan en el modelo.

## Ejecutar

Requisitos: JDK 21 o posterior y Maven 3.6.3 o posterior. La primera compilación necesita acceso a Maven Central. La tipografía está incluida y el frontend no requiere Node ni internet una vez descargadas las dependencias Java.

Desde esta carpeta:

```sh
mvn clean package
java -jar target/decorator-lab-1.0.0.jar
```

Abre **http://localhost:8080**. También puedes usar:

```sh
./scripts/run.sh
```

En macOS, `scripts/run.command` permite iniciar mediante doble clic. El script utiliza el JAR existente; después de cambiar fuentes, vuelve a ejecutar `mvn package` para actualizarlo. Para desarrollo con compilación actualizada:

```sh
mvn spring-boot:run
```

Para usar otro puerto:

```sh
PORT=8081 ./scripts/run.sh
```

El servidor escucha en `127.0.0.1`. Detén la aplicación con `Ctrl+C` en la terminal que la ejecuta.

## Recorrido de uso

1. Selecciona un escenario o introduce ciudades, peso y valor declarado.
2. Marca los servicios que necesita la carga. Se añaden a la cadena en el orden elegido.
3. Usa las flechas para cambiar qué decorador envuelve a cuál.
4. Pulsa **Calcular cotización**. Java devuelve desglose, total, capacidades, plazo y expresión de composición.
5. Compara el costo base y el costo añadido. Descarga el resultado en JSON o imprime la cotización.

Los cambios pendientes se señalan y desactivan descarga e impresión hasta recalcular. La interfaz incluye estados de carga, error, envío sin decoradores, navegación por teclado, movimiento reducido y adaptación a móvil.

## Dónde está Decorator

| Rol | Implementación | Responsabilidad |
|---|---|---|
| Component | `Shipment` | Define `Quote quote()` para bases y envolturas. |
| Concrete Component | `StandardShipment` | Cotiza el transporte interurbano. |
| Decorator | `ShipmentDecorator` | Implementa `Shipment`, conserva `Shipment wrapped` y delega `quote()` en él por defecto. |
| Concrete Decorators | Cinco clases en `domain/decorator` | Delegan al componente interior y enriquecen su cotización. |
| Cliente/composición | `QuoteService` | Construye la cadena según la selección; no calcula recargos. |

Ejemplo de código real:

```java
Shipment shipment = new StandardShipment(context);
shipment = new ColdChainDecorator(shipment, context.weightKg());
shipment = new TemperatureMonitorDecorator(shipment);
Quote result = shipment.quote();
```

Cada decorador obtiene la cotización interior con `super.quote()` (que delega en `wrapped.quote()`) y devuelve una nueva `Quote`. No altera el componente envuelto. `Quote` y sus listas son inmutables. El mismo contrato funciona sin decoradores, con uno o con varios.

## Reglas del simulador

Todos los importes se redondean a pesos COP con `HALF_UP`, usando `BigDecimal`.

| Servicio | Cálculo sintético | Efecto adicional |
|---|---|---|
| Transporte base | $28.000 + $4.200/kg; ruta con Barranquilla: $42.000 + $4.200/kg | 48 h; con Barranquilla: 72 h. |
| Empaque refrigerado | $24.000 + $1.800/kg | Añade capacidad de empaque térmico de ejemplo. |
| Registro de temperatura | $12.000 | Añade capacidad de registro durante el trayecto. |
| Cadena de custodia | $18.000 | Añade documentación y sello de integridad. |
| Seguro | 1,2 % del valor declarado; mínimo $8.000 | Añade cobertura ilustrativa. |
| Prioridad | 20 % del subtotal del objeto interior | Reduce 12 h, con piso de 24 h. |

El orden importa cuando hay Prioridad: su base de cálculo incluye únicamente las capas interiores. No existe una jerarquía de subclases para cada combinación.

### Ejemplo reproducible

Bogotá → Medellín, 2 kg, valor declarado $1.500.000:

- Solo transporte: **$36.400 / 48 h**.
- Refrigeración + monitoreo: **$76.000 / 48 h**.
- Seguro y después Prioridad: **$65.280 / 36 h**.
- Prioridad y después Seguro: **$61.680 / 36 h**.

El primer servicio de la lista es el más cercano a la base. El último es el decorador exterior.

## Organización

```text
PATRON DECORATOR/
├── README.md                  Contexto, ejecución y mapa del patrón
├── PRODUCT.md                 Contexto de producto para futuras ediciones
├── DESIGN.md                  Sistema visual implementado
├── pom.xml                    Proyecto Maven y dependencias
├── docs/
│   ├── CASO_ESTUDIO.md         Problema, alcance y justificación
│   ├── ARQUITECTURA.md         Flujo, responsabilidades y API
│   ├── GUIA_EXPOSICION.md      Guion y demostraciones para el taller
│   └── VALIDACION.md           Evidencia de compilación y comprobaciones
├── scripts/                   Inicio en terminal y macOS
├── src/main/java/com/celsius/
│   ├── domain/                Contrato, base y valores inmutables
│   │   └── decorator/         Decorador abstracto y cinco concretos
│   ├── application/           Catálogo, DTOs y composición
│   └── web/                   Controladores MVC/REST y errores
├── src/main/resources/
│   ├── templates/             Vista Thymeleaf
│   ├── static/                CSS, JavaScript, SVG y fuente con licencia
│   └── application.properties Configuración local
├── src/test/java/             Pruebas del dominio
└── .impeccable/                Dirección, tokens y revisión visual
```

`target/` contiene el JAR y los reportes generados por Maven. No es una carpeta de código fuente.

## API y pruebas

`GET /api/services` devuelve el catálogo. `POST /api/quotes` calcula una cotización. `GET /api/quotes/export` descarga el resultado como JSON desde Java. Consulta el contrato y un ejemplo en [docs/ARQUITECTURA.md](docs/ARQUITECTURA.md).

```sh
mvn test
```

Las seis pruebas cubren costos base, acumulación, seguro mínimo, efectos del orden, validaciones e inmutabilidad. Los detalles y comprobaciones del frontend están en [docs/VALIDACION.md](docs/VALIDACION.md).

## Extender el caso

Para incorporar un servicio nuevo, crea una clase que extienda `ShipmentDecorator`, sobrescriba `quote()` partiendo de `super.quote()` y devuelva una cotización enriquecida. Registra su metadata en `QuoteService.CATALOG` y su constructor en la composición de `QuoteService.calculate()`. La vista Thymeleaf genera las opciones desde ese catálogo. Si requiere parámetros adicionales, amplía el contexto/DTO y el formulario.

Esta versión permite cada decorador una vez por envío, usa cuatro ciudades y no persiste cotizaciones en un servidor. La descarga JSON es la forma de conservar el resultado. Puedes sustituir las tarifas sintéticas por un catálogo real e introducir persistencia sin cambiar el contrato `Shipment`.

Referencias del stack: [Spring Boot](https://spring.io/projects/spring-boot/), [requisitos de Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html) y [documentación de Thymeleaf](https://www.thymeleaf.org/documentation). La fuente Barlow Condensed se distribuye con su licencia SIL OFL en `static/fonts/OFL.txt`.

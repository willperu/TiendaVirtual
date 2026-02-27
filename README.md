🛒 TiendaVirtual — Sistema de Ventas Concurrentes
📌 Descripción

TiendaVirtual es una aplicación backend desarrollada en Java que simula el funcionamiento
de una tienda online con múltiples clientes comprando simultáneamente.
El proyecto fue diseñado para demostrar habilidades en programación orientada a objetos,
concurrencia, arquitectura por capas y control de versiones.

🎯 Objetivos del proyecto

- Simular compras concurrentes

- Controlar stock en tiempo real

- Evitar condiciones de carrera

- Implementar lógica de negocio desacoplada

- Aplicar buenas prácticas de arquitectura


⚙️ Tecnologías utilizadas

- Java

- Spring Initializr (estructura base del proyecto)

- Apache Ant (compilación y empaquetado)

- NetBeans (IDE de desarrollo)

- Git (control de versiones)

- GitHub (repositorio remoto)


🧠 Conceptos aplicados

- Programación orientada a objetos (POO)

- Programación concurrente (Threads)

- Arquitectura en capas

- Encapsulamiento

- Separación de responsabilidades

- Manejo de colecciones

- Simulación de transacciones


🧱 Arquitectura del proyecto

El sistema está organizado en capas:

- DAO → Acceso a datos
- Servicios → Lógica de negocio
- Modelos → Entidades
- Utils → Utilidades
- Test → Simulaciones concurrentes


🧪 Simulación concurrente

El proyecto incluye un módulo de pruebas que simula múltiples clientes intentando
comprar el mismo producto al mismo tiempo.

Esto permite validar:

- control de stock

- sincronización

- integridad de datos

- comportamiento bajo carga



📂 Estructura del repositorio
TiendaVirtual/
 ├── src/
 ├── test/
 ├── nbproject/
 ├── manifest.mf
 └── .gitignore


▶️ Cómo ejecutar el proyecto

Desde consola:

ant clean jar
java -jar dist/TiendaVirtual.jar
📊 Resultados esperados



Durante la simulación:

algunos clientes logran comprar

otros fallan por falta de stock

el sistema mantiene consistencia



👨‍💻 Autor

Proyecto desarrollado por Willperu como parte de portafolio profesional backend.


🤖 Nota

Este proyecto fue diseñado, estructurado y optimizado con apoyo de inteligencia artificial
como asistente técnico, siendo el desarrollo, comprensión e implementación realizados por el autor.



🚀 Próximas mejoras

- API REST

- Base de datos real

- Autenticación de usuarios

- Panel administrativo

- Pruebas unitarias automatizadas

- Dockerización



⭐ Proyecto educativo con fines demostrativos para portafolio profesional.

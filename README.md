# sistema-inscripcion-cursos
Sistema de inscripción a cursos de capacitación con API REST y cliente web en Java y Spring Boot.

## Ejecutar la API y la aplicación web

### Requisitos
- JDK 21.
- Acceso a Internet en la primera ejecución para descargar dependencias Maven.
- Los proyectos incluyen Maven Wrapper; no es necesario instalar Maven.

### 1. Iniciar cursos-api

Desde la raíz del repositorio, abre una terminal Git Bash:

```bash
cd cursos-api
./mvnw spring-boot:run -Dspring-boot.run.profiles=demo
```

La API estará disponible en:
http://localhost:8080/api/cursos

El perfil demo utiliza H2 en memoria y no requiere MySQL.
Los datos se eliminan al detener la API.

### 2. Iniciar cursos-web

Mantén la API ejecutándose y abre otra terminal desde la raíz:

```bash
cd cursos-web
./mvnw spring-boot:run
```

Abre:
http://localhost:8081/cursos

### Ejecución desde IntelliJ IDEA

1. Abrir cursos-api y cursos-web como proyectos Maven.
2. Seleccionar JDK 21 en ambos proyectos.
3. Ejecutar CursosApiApplication con el perfil demo.
4. Ejecutar CursosWebApplication.
5. Abrir http://localhost:8081/cursos.

### Configuración de la web

En cursos-web/src/main/resources/application.properties:

```properties
server.port=8081
cursos.api.url=http://localhost:8080
```

La aplicación web consume la API mediante HTTP.
La conexión a la base de datos se gestiona en cursos-api.

### Comprobación básica

1. Abrir el listado de cursos.
2. Seleccionar Registrar curso.
3. Completar el formulario y guardar.
4. Verificar el mensaje de éxito y el curso en el listado.

Si la API no está disponible, la web muestra un mensaje de error.

## Ejecutar con MySQL

La API permite utilizar MySQL para conservar los datos entre reinicios.
El perfil `demo` continúa disponible con H2 en memoria.

### Preparar la base de datos

Con MySQL ejecutándose en localhost:3306:

```sql
CREATE DATABASE IF NOT EXISTS cursos_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

JPA crea o actualiza las tablas al iniciar la API con el perfil `mysql`,
según las entidades y la configuración `ddl-auto=update`.

### Configurar IntelliJ IDEA

En Run → Edit Configurations → CursosApiApplication:

- Active profiles: `mysql`.
- Environment variables:
  - `DB_USERNAME`: usuario de MySQL.
  - `DB_PASSWORD`: contraseña de ese usuario.

Las credenciales se configuran localmente; no deben escribirse en
archivos versionados.

### Iniciar las aplicaciones

1. Mantener MySQL ejecutándose.
2. Ejecutar CursosApiApplication en el puerto 8080.
3. Ejecutar CursosWebApplication en el puerto 8081.
4. Abrir http://localhost:8081/.

### Persistencia

Con el perfil `mysql`, los datos permanecen después de reiniciar la API.
Los datos de H2 del perfil `demo` son independientes y no se transfieren
automáticamente a MySQL.

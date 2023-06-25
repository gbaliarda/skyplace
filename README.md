# Skyplace

Skyplace es un Marketplace de NFTs.

La aplicación consiste en un proyecto maven que se ocupa de buildear tanto el frontend (SPA, NextJS) como el backend (API REST, Spring + Jersey), en un único WAR que luego puede ser deployado a un servidor como Tomcat.

La interfaz web fue testeada en los navegadores Chrome y Firefox en sus últimas versiones.

## Estructura del backend

El backend esta estructurado en un modelo de N-capas, compuesto de los siguientes módulos:
- webapp: implementa las rutas de la API REST, haciendo uso de la capa de `services`.
- services: se encarga de ejecutar la lógica de negocio, conectando los controladores presentes en la capa de `webapp` con la capa de `persistence`.
- persistence: se encarga de manejar el acceso a los datos, conectando la base de datos (PostgreSQL) con la aplicación.
- interfaces: contiene los contratos para las implementaciones tanto de la capa de `services` como de `persistence`.
- models: contiene las entidades que se usan en la aplicación (ORM-based entities for JPA).

Las rutas de la API se encuentran detras de `/api`, y emplean un mecanismo de autorización basado en JWT, donde cada usuario tiene tanto un `accessToken` como un `refreshToken`.
- Cuando el `accessToken` expira, se pide uno nuevo haciendo uso del `refreshToken`.
- Cuando el `refreshToken` expira, el usuario debe solicitar un par nuevo de tokens.

Para generar el par de JWTs, se emplea un mecanismo de `Authorization: Basic`.

## Frontend

El frontend consiste en una SPA hecha con NextJS + Typescript, haciendo uso de la funcionalidad de *static export* del framework.

El mismo es accesible en la ruta `/` de la aplicación.

A continuación se listan los posibles comandos a ejecutar en el frontend, los cuales seran accesibles al navegar a la carpeta `/spa`.

- Correr en modo desarrollo:
```
npm run dev
```

- Generar los archivos estáticos que luego son copiados al módulo de `webapp` del backend:
```
npm run build
```

- Correr los tests:
```
npm run test
```

Los tokens JWT son almacenados en `localStorage` si el usuario desea mantener sesión iniciada, o en `sessionStorage` si desea que sus credenciales desaparezcan al cerrar la sesión de su navegador.


### Credenciales de usuarios para los distintos niveles de acceso

#### Admin:
- Email: gbaliarda@itba.edu.ar
- Contraseña: gbaliarda

#### User
- Email: pescudeiro@itba.edu.ar
- Contraseña: pescudeiro

Los **admins** pueden moderar el contenido publicado, es decir, borrar NFTs pertenecientes a cualquier usuario. Este rol debe asignarse directamente desde la base de datos.

Los **users** son los usuarios regulares que pueden crearse desde el registro dentro de la aplicación, pueden crear, publicar, comprar otros productos y realizar reseñas a otros usuarios.

### Transacciones en Goerli para probar (no se puede usar más de 1 vez la misma)
De pescudeiro@itba.edu.ar a gbaliarda@itba.edu.ar

- 0xd53b38bafe933fb80bfd48c9634e20b292e65edd243ad5761e5f220d7f3f619e (0.05 ETH)
- 0x450d3ddd35b63b10265e6131f6c93646403e6449239f66d7e0856613e181cd6f (0.0001 ETH)
- 0xf9b12f9efebab546ccc915eee40c68588b50d0c51f37c19742ad0259bc4e65fd (0.007 ETH)

De gbaliarda@itba.edu.ar a pescudeiro@itba.edu.ar

- 0x01817cb422cef1730a6a7a915f3d0a1128d8b73ab76689f6557988f467013986 (0.05 ETH)
- 0xa4242648d192e4a578cdc02f8a238c72ca0cfaecd2bfee0f2b7493ddcbc3692a (0.1 ETH)
- 0xf1be6bba1076e3c990b4550c4fa05d72a6797f038417649b789b58f9ece37497 (0.0002 ETH)

### Autores

- Gonzalo Baliarda - 61490
- Ezequiel Perez - 61475
- Nicolas Birsa - 61482
- Valentin Ye Li - 61011 
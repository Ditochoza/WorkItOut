# Changelog

## [v1.2.0 Beta 3] [Unreleased]  - 28/04/2022
### Nuevo
- Añadido ScrollView en inicio de sesión y regitro.

### Arreglos
- Arreglado error en crear rutina por la que se perdían datos al elegir ejercicios.
- Arreglado error por el que no se actualizaba la lista de videos de ejercicio al modificarla

## [v1.2.0 Beta 2] [Unreleased]  - 28/04/2022
### Nuevo
- Al intentar guardar un entrenamiento sin conexión no se permite y te muestra un mensaje.

### Arreglos
- Arreglado API de Youtube para que cuando no haya conexión permita obtener el resto de datos de Firebase.
- Se han arreglado los vídeos para que cuando no hay conexión aparezca un mensaje y el link.

## [v1.2.0 Beta 1] [Unreleased]  - 28/04/2022
### Nuevo
- Traducción al español
- Añadido SplashScreen para las versiones que lo soportan (Con animación a partir de API 31).
- Notificación de inicio de entrenamiento.
- Notificación de registros de entrenamiento guardados.
- PopUp al salir rutina para que se avise de que se van a perder los registros introducidos.

### Arreglos
- Arreglo en introducir registros de entrenamiento: Al medir con Reps a veces salía -1.
- Orden de registros de ejercicios al obtenerlos de la tabla de ejercicios generales.
- Añadido SavedStateHandle a todas las variables de estado.

### Cambios
- Ahora no se pueden editar ejercicios dentro de un entrenamiento.

## [v1.0.0 Beta 1] [Unreleased]  - 27/04/2022
### Nuevo
- Nueva pantalla para ver el historial de los registros del usuario en cada ejercicio.

### Arreglos
- Los registros obtenidos de la base de datos ahora se ordenan por fecha descendente.

### Cambios
- El nombre de rutinas y ejercicios en sus respectivas pantallas de info ahora aparece en el subtítulo del Toolbar.

## [v0.9.0] [Unreleased]  - 27/04/2022
### Nuevo
- El usuario puede iniciar un entrenamiento y guardar nuevos valores.
- Subida de entrenamiento registrado a la base de datos.
- Botón a información de ejercicio dentro de cada ejercicio en un entrenamiento.

### Arreglos
- Arreglado error por el que en algunas situaciones el orden de las rutinas programadas no era el correcto.

### Cambios
- Lista de músculos de ejercicio de la app actualizados (Grupos musculares).

## [v0.8.9] [Unreleased]  - 24/04/2022
### Nuevo
- Selección de sets y reps en selección de ejercicios de rutinas.
- Opción de usar reps (No para medir) en un ejercicio al crearlo.
- Opción de elegir el método de medida de un ejercicio al crearlo.
- Texto de sets y/o reps de ejercicios dentro de rutinas.

### Arreglos
- Padding arreglado en pantallas de creación/modificación de ejercicio/rutina
- Arreglo en chips de músculos en lista de rutinas en pantalla de inicio.

## [v0.8.8] [Unreleased]  - 24/04/2022
### Nuevo
- Selección de ejercicios en las rutinas.
- Animación de navegación a creación/modificación de ejercicio/rutina.
- Lista de ejercicios en vista de rutina y creación/modificación de rutina.

### Eliminado
- Botón de programar y desprogramar rutina en vista de rutina.

### Arreglos
- No aparecía el botón de guardar al crear/modificar ejercicio.
- Márgenes unificados en los fragmentos.

### Cambios
- Diseño de tarjeta de "Programado..." o "Programar" en vista de rutina modificado.

## [v0.8.5] [Unreleased]  - 23/04/2022
### Nuevo
- Videos recomendados en cada ejercicio.

## [v0.8] [Unreleased]  - 22/04/2022
### Nuevo
- Pantalla con listado de ejercicios (De app y de usuario).
- Pantalla con listado de rutinas del usuario.
- Pantalla de visualización de ejercicio.
- Pantalla de visualización de rutina.
- Menú de navegación lateral.
- Pantalla de ajustes:
-- Dar de baja usuario.
-- Cambiar contraseña de usuario.

## [v0.7] [Unreleased]  - 17/04/2022
### Nuevo
- Persistencia de datos con Firebase (Firestore, Storage y Firebase Auth).
- Pantalla de inicio de sesión.
- Pantalla de registro de usuario.
- Pantalla "Home" con rutinas programadas del usuario.
- Pantalla de creación/modificación de ejercicios (Creados por el usuario).
- Pantalla de creación/modificación de rutinas.
- Subida de ejercicios/rutinas a Firebase.
- Botón de cierre de sesión de usuario.

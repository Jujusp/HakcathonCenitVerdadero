# Cénit
Cénit es una plataforma educativa para dispositivos móviles con el modelo de sitios como Coursera o Udemy, en el cuál las personas se pueden inscribir a diferentes cursos para aprender lo que deseen. Utiliza la tecnología de Sceenic para permitir la realización de las clases y ofrece herramientas que pueden usar los estudiantes dentro de ésta.

## Requerimientos
- Android Studio
- Python3
- Postman
## Iniciar la aplicación

### 1. Ejecutar el servidor de autenticación
Abrir la línea de comandos, e ir a la carpeta "authserver".

Ejecutar: "python -m venv venv" (crea un ambiente virtual para la aplicación python)

(Windows) Ejecutar: venv\Scripts\pip install -r requirements.txt

(Linux) Ejecutar: venv/bin/pip install -r requirements.txt

Crear base de datos sqlite usando el comando: "sqlite3 < init_db.sql" (en caso de que no exista)

Iniciar el servidor ejecutando los siguientes comandos:

(Windows) set FLASK_APP=auth

(Windows) flask run

(Linux) FLASK_APP=auth venv/bin/flask run

### 2. Registrar usuarios en la base de datos de SQLite
Abrir Postman y crear una nueva petición (request) de tipo POST.

En el URL de la petición, usar la que fue dada en el servidor de autenticación (usualmente http://127.0.0.1:5000). Añadir "/register" al final.

En la ventana Headers, añadir una fila que tenga de llave (key) "Content-Type" y en valor(value) "application/json".

En body, seleccionar "raw" y de tipo JSON.

En el contenido, añadir: {"login":"ejemplo","password":"ejemplo"}
(Reemplazando la palabra 'ejemplo' por las credenciales que se deseen)

Enviar la petición. Debería aparecer correcta tanto en Postman como en la Linea de Comandos donde se esta ejecutando el servidor de autenticación.

### 3. Ejecutar la aplicación en Android Studio

Correr la aplicación con el emulador deseado, asegúrese de que el SDK sea 24 como mínimo:

En la pantalla de inicio de sesión, se colocan los siguientes datos:

- URL: http://10.0.2.2:5000 (Localhost para el emulador)
- Login: ejemplo (o el que se haya registrado en Postman)
- Password: ejemplo (o el que se haya registrado en Postman)
- Display Name: Como desee aparecer en una videollamada.

 Oprimir iniciar sesión. Desde ahí, ya podrá unirse a la clase que desee o usar los mensajes de texto.

## Ejecución del Chat:

Para ejecutar el chat de la aplicación es necesario abrir dos emuladores.

Luego se introduce de nombre "Luis" en uno de los emuladores, mientras en el otro "Pipe Rubio" (debido a que aún se carece de un register). Con ello ambas pantallas estarán conectadas al mismo servidor.

Ahora se va a poder escribir en el chat sin inconveniente alguno y será refrescado automáticamente.

Los mensajes serán guardados en una base de datos conectada al mismo servidor.

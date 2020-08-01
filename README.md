# Cénit
Cénit es una plataforma educativa para dispositivos móviles con el modelo de sitios como Coursera o Udemy, en el cuál las personas se pueden inscribir a diferentes cursos para aprender lo que deseen. Utiliza la tecnología de Sceenic para permitir la realización de las clases y ofrece herramientas que pueden usar los estudiantes dentro de ésta.

## Requerimientos
- Android Studio
- Python3
- Postman
## Iniciar la aplicación

### 1. Correr el servidor de autenticación
Abrir la línea de comandos, e ir a la carpeta "authserver".

Ejecutar: "python -m venv venv" (crea un ambiente virtual para la aplicación python)

(Windows) Ejecutar: venv\Scripts\pip install -r requirements.txt

(Linux) Ejecutar: venv/bin/pip install -r requirements.txt

Crear base de datos sqlite usando el comando: "sqlite3 < init_db.sql" (en caso de que no exista)

Iniciar el servidor ejecutando los siguientes comandos:

(Windows) set FLASK_APP=auth

(Windows) flask run

(Linux) FLASK_APP=auth venv/bin/flask run

### 2. Registrar usuarios en la base de datos
Abrir Postman y crear una nueva petición (request) de tipo POST.

En el URL de la petición, usar la que fue dada en el servidor de autenticación (usualmente http://127.0.0.1:5000). Añadir "/register" al final.

En la ventana Headers, añadir uno que tenga en Key "Content-Type" y en Value "application/json".

En body, seleccionar "raw" y de tipo JSON.

En el contenido, añadir: {"login":"ejemplo","password":"ejemplo"}
(Reemplazando la palabra ejemplo por las credenciales que se deseen)

Enviar la petición. Debería aparecer correcta tanto en Postman como en la Linea de Comandos donde se esta corriendo el servidor de autenticación.

### 3. Correr la aplicación en Android Studio

Correr la aplicación con el emulador

En la pantalla de inicio de sesión, se colocan los siguientes datos:

- URL: http://10.0.2.2:5000
- Login: ejemplo (o el que se haya registrado en Postman)
- Password: ejemplo (o el que se haya registrado en Postman)
- Session ID: El número que se desee.
- Display Name: El que se desee poner. Será un apodo en la aplicación.

 Oprimir iniciar sesión. Desde ahí, ya podrá unirse a la clase que desee o usar los mensajes de texto.

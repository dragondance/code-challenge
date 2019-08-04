Introducción
---------------
Creamos un microservicio rest para hacer operaciones sobre transacciones bancarias, para ello hemos utilizado 
lo siguiente:

- Spring Boot
- Lombok
- Docker
- Cucumber
- Gherkin
- Fabric8

Estructura
---------------
El proyecto contiene la siguiente división de packages dentro de 'com.code.challenge':

- **api:** Contiene la clase `TransactionController` que es la encargada de definir los diferentes Endpoints
y la lógica de nuestra aplicación.

- **entities:** Contiene las entidades de nuestra aplicación que se usarán para guardar
la información en base de datos, en nuestro caso tendremos dos: <br>
&nbsp;&nbsp; `Account` <br>
&nbsp;&nbsp; `Transaction`

- **models:** Contiene aquellas clases que utilizaremos para guardar información temporal en nuestra aplicación.

- **repository:** Contiene interfaces para el acceso a datos a base de datos.

- **util:** Clases que contienen métodos que pueden ser reutilizables por toda la aplicación.

Dentro de nuestro proyecto también se encuentra la carpeta resources, destacando estos dos elementos:

- **application.properties**: Se encuentra nuestra configuración de la base de datos.
- **data.sql**: Script para inicializar valores en las tablas de base de datos.

Documentación Servicios Rest
-----------------------------
Nuestro micro servicio contiene 3 Endpoint:
- create
- search
- status

Para realizar la implementación de estos microservicios se han seguido las buenas praccticas de rest.

###  Métodos Post

##### Create
`/create`: Comprueba que las operaciones sobre la cuenta son válidas y de ser así crea la transacción. Como parámtro de entrada
debe recibir un objeto en formato JSON de la siguiente forma:

``` json
{
    "reference": "12345A",
    "account_iban": "ES6220389439049641732642",
    "date": "2019-08-04T10:05:42.000Z",
    "ammount": 6.38,
    "fee": 3.18,
    "description": "Restaurant payment"
 }
```

`reference` (Optional): Si no se envia el sistema generará uno nuevo
`account_iban` (Obligatorio): Número de cuenta
`date`: "La fecha en la que se realiza la transacción" (Hemos asumido que el cliente que consume el servicio siempre envia una)
`ammount` (Obligatorio): Cantidad de la transacción.
`fee`: la tasa de la transacción que se restará la la cantidad.
`Descripcion` (Opcional): "Descripción de la transacción"
### Métodos Get

##### Search
`/search`: Devuelve la lista de las transacciones de la cuenta que se está buscando. Por **queryParams** admite el 
número de cuenta y la ordenación que se quiere. Un ejemplo de uso sería el siguiente:

`/serach?iban="ES6220389439049641732642"&order="asc"`

Si no se recibe ningún parámetro se devuelve el listado de todas las transacciones. Comentar que habría que implementar 
un sistema de seguridad para que devolviera las transacciones del usuario que está haciendo la petición.

----------------

##### Status
`/status/{reference}/{channel}`: Develve el estado de una transacción a partir de la referencia y el canal.

Los tipos de canales permitodos son los siguientes:

``` text
CLIENT
ATM
INTERNAL
```

Ejecución de las pruebas (ATDD)
-----------------------------------
Para ejecutar las pruebas hemos usado Cucumber con Gherkin. No hemos querido mockear el proceso a las llamadas 
del api rest por lo que hemos levantado la aplicación con Docker y realizamos la pruebas sobre la aplicación ya levantada.
Con esto nos cercioramos que las todas las pruebas se han ejecutado probando nuestra aplicación real.

Hay que ejecutar estos pasos para poder ejecutar este proceso:

`mvn clean install`

`docker build -t transaction-service .`

`docker run -p 8080:8080 transaction-service`

Levantando el servicio en `localhost:8080`

Luego desde nuestro IDE ejecutamos el plan de pruebas.

#### Automatización de pruebas con Docker + Maven (Fabric8)

El proceso anterior se ha querido automatizar usando [Fabric8](http://dmp.fabric8.io/). Con este plugin conseguimos que al 
hacer el `mvn clean install` compile y levante automaticamente el docker en la fase de maven **pre-integration-test** 
y en la fase *post-integration-test* para el servicio de docker.

````text
<plugin>
    <groupId>io.fabric8</groupId>
    <artifactId>docker-maven-plugin</artifactId>
    <version>0.30.0</version>

    <configuration>
        <images>
            <!-- A single's image configuration -->
            <image>
                ...
            </image>
        </images>
    </configuration>

    <!-- Connect start/stop to pre- and
         post-integration-test phase, respectively if you want to start
         your docker containers during integration tests -->
    <executions>
        <execution>
            <id>start</id>
            <phase>pre-integration-test</phase>
            <goals>
                <!-- "build" should be used to create the images with the
                     artifact -->
                <goal>build</goal>
                <goal>start</goal>
            </goals>
        </execution>
        <execution>
            <id>stop</id>
            <phase>post-integration-test</phase>
            <goals>
                <goal>stop</goal>
            </goals>
        </execution>
    </executions>
</plugin>
````

**Nota:** Hemos configurado Fabric8 para que levante y para el proceso de docker automaticamente, pero no está lanzando las 
pruebas de integración. Por tiempo no hemos podido seguir investigando sobre la configuración.

Errores conocidos
------------------
Con la nueva versión de Docker para windows 2.1.0.0 puede que salga un error:

`engine_linux: The system cannot find the file specified.`

Esto se soluciona reseteando la aplicación Docker for windows.

Al ejecutar el comando `Docker run` o `maven clean install` puede ser que al pararlo se queden los puertos a la escucha
para solucionarlo ejecutamos lo siguiente:

`docker stop $(docker ps -aq)`

`docker rm $(docker ps -aq)`

### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW 2025-2
### Ejercicio – programación concurrente, condiciones de carrera y sincronización de hilos. EJERCICIO INDIVIDUAL O EN PAREJAS.

## Integrantes:
### Juan David Martínez Mendez
### Santiago Gualdrón Rincón
## Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?

El consumo alto de CPU se debe a los bucles infinitos que están dentro de las clases **Consumer** y **Producer**; los cuales tienen ciclos "While(true)" los cuales no tienen condicionales que paren los hilos si llegan a los casos bordes, de esta forma se incrementa el consumo de CPU, agregando elementos sin pausas y eliminando elementos de la cola sin notificar a los demas hilos, incrementando mayormente la memoria del computador.
<img width="1332" height="889" alt="image" src="https://github.com/user-attachments/assets/077c1776-7dd3-41b6-936a-4362691a1a17" />

2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.

La forma inicial para reducir el consumo de la CPU, fue implementar en ambos el metodo try para que los hilos duerman (sleep) a 1 segundo, y mejorar el condicional inicial en **Consumer** para que, mientras no hayan datos en la cola, este hilo se quede esperando hasta que les notifiquen que ya hay datos nuevamente, todo esto en regiones criticas que fueron acopladas por synchronized().

<img width="839" height="535" alt="image" src="https://github.com/user-attachments/assets/bec47b51-bc47-4b1d-b2fb-f8c684976e5e" />

<img width="1332" height="830" alt="image" src="https://github.com/user-attachments/assets/3131738e-9e4c-42c3-87ef-42216df19f1d" />

3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.
Ahora, para lograr que el productor genere mas que el consumidor, se implemento en ambos, que el tiempo que duerme el hilo de **Consumer** sea mayor (1.5 seg) al de **Producer** (1.0 seg); esto enviandole un parametro donde el usuario escoge cuanto desea que sea el maximo para la cola, en este caso siendo 1000. Por ultimo se respeta colocandole de forma parecida ciclo while() (pero con condicional el limite superior) del **Consumer**; donde el hilo esperaba a que le avisaran cuando se hayan eliminado elementos de la cola para seguir con el trabajo

<img width="954" height="536" alt="image" src="https://github.com/user-attachments/assets/1535d433-afb6-4cdd-b623-375be168ce18" />

<img width="1332" height="828" alt="image" src="https://github.com/user-attachments/assets/bd403e35-b735-40e4-8983-df858f4ceaad" />

##### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.

Para desarrollar esta solución:
* Utilizamos una lista sincronizada para todos los hilos, esto para asegurar que al ir encontrando las IPs maliciosas, estas se almacenen en un mismo objeto. Este objeto se utilizo como lock de sincronización dentro del método "run".

<img width="699" height="572" alt="image" src="https://github.com/user-attachments/assets/7b7e34e0-65ed-4de4-892f-c18e2d5b2596" />

* Utilizamos el mismo "BLACK_LIST_ALARM_COUNT" dentro de la clase Thread para realizar la verificación y así detener el programa por medio del método "enoughOcurrences".
Asegurando que la solución esta correctamente implementada, podemos evidenciar que el tiempo de ejecución en comparación con la primera versión es considerablemente menor

<img width="840" height="396" alt="image" src="https://github.com/user-attachments/assets/da26d655-cf83-47a5-986b-17afcada5abc" />

##### Parte III. – Avance para el martes, antes de clase.

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.

Para N jugadores, sabiendo que "DEFAULT_IMMORTAL_HEALTH = 100" se puede considerar que esta invariante siempre debe ser: 
	numOfImmortals = N * 100

3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.

No, el invariante no cumple lo que se supone debe obtener como resultado estatico; esto por la condicion carrera, la cual hace que varien los datos de una forma aleatoria.

<img width="788" height="291" alt="image" src="https://github.com/user-attachments/assets/46c19767-66cd-4161-8cff-26fa61d0eb3b" />
<img width="788" height="298" alt="image" src="https://github.com/user-attachments/assets/af858d5f-747f-46ea-9915-a06a0e9392b0" />
<img width="790" height="294" alt="image" src="https://github.com/user-attachments/assets/6058da32-4ffb-40b5-a73e-8677722c30b0" />

4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.

<img width="795" height="292" alt="image" src="https://github.com/user-attachments/assets/84b99b1c-6432-4f75-b321-8ff7711cc60a" />
<img width="798" height="250" alt="image" src="https://github.com/user-attachments/assets/4255345f-6f2a-4078-b326-5b8e129574a8" />


5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.

No, aun no cumple el invariante, porque existen los casos donde 2 hilos ataquen al mismo hilo al mismo tiempo, si esto pasa existe la posibilidad de que aun no funcione bien la invariante, es un caso de condicion de carrera que hay que acotar para que sirva el invariante.
<img width="786" height="304" alt="image" src="https://github.com/user-attachments/assets/0ad87238-8c84-42b0-be92-7cf769d793f2" />

6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```

<img width="952" height="293" alt="image" src="https://github.com/user-attachments/assets/95067ff6-3cd6-4ae5-9fe5-8863d67ce6d5" />

7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.

El programa se detiene porque se genera deadlock, al no tener un orden de como se bloquean los hilos, estos pueden bloquear 2 diferentes al mismo tiempo que, despues serán necesarios para continuar y estaran bloqueados.

8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).

La estrategia es utilizar el nombre de los hilos (que son numeros) y organizar el bloqueo de tal forma que siempre se bloquee primero el menor, de esta forma no se podrá generar deadlock.

<img width="1028" height="476" alt="image" src="https://github.com/user-attachments/assets/f560409d-ac0b-4cc3-b923-3f49a468c991" />
<img width="1165" height="651" alt="image" src="https://github.com/user-attachments/assets/4b1e542f-6272-4c48-bc99-b3168b67ed90" />

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.

Caso de 100:

<img width="631" height="553" alt="image" src="https://github.com/user-attachments/assets/3421dbb4-1c64-40a5-b2bb-a1274d7495c4" />

Caso de 1000:

<img width="914" height="668" alt="image" src="https://github.com/user-attachments/assets/07d960e1-abc8-4d2b-8ce5-0583d148488d" />

Caso de 10000:

<img width="630" height="730" alt="image" src="https://github.com/user-attachments/assets/13906229-31b3-4303-b6d7-3705348db06c" />

Podemos evidenciar que el invariante se sigue cumpliendo

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.

Se puede crear una condición de carrera sobre la lista de inmortales, si un hilo elimina a un inmortal muerto mientras otro hilo busca de manera aleatoria dentro de la lista de inmortales se pueden producir inconsistencias.

Podemos ver que se generan inconsistencias de este tipo

<img width="498" height="162" alt="image" src="https://github.com/user-attachments/assets/ebefad79-16f2-4d04-9f8d-29bc0d539006" />

Por lo que la eliminación de inmortales muertos de la lista compartida genera condiciones de carrera, por lo que inmortales terminan peleando con inmortales muertos o tomando indices que no existen, resultando con una simulación inconsistente.

* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.

 Para lograr la implementación necesitamos que se verifiquen que los hilos ya eliminados no esten dentro de la lista y que además evitemos el caso donde algún hilo alcance a consultar ese hilo eliminado antes de ser realmente eliminado, por lo que realizamos la siguiente implementación:

<img width="737" height="763" alt="image" src="https://github.com/user-attachments/assets/81496f88-ca9b-4f3b-bbd4-64d9bf1edada" />

<img width="891" height="598" alt="image" src="https://github.com/user-attachments/assets/323685bf-8d7f-41b1-b32d-cfdff07b4577" />

donde hacemos verificaciones constantes de si los hilos siguen vivos y así poder tomar acciones.

11. Para finalizar, implemente la opción STOP.

<img width="460" height="252" alt="image" src="https://github.com/user-attachments/assets/01d65b04-7618-4504-b511-845d0269d75c" />


<!--
### Criterios de evaluación

1. Parte I.
	* Funcional: La simulación de producción/consumidor se ejecuta eficientemente (sin esperas activas).

2. Parte II. (Retomando el laboratorio 1)
	* Se modificó el ejercicio anterior para que los hilos llevaran conjuntamente (compartido) el número de ocurrencias encontradas, y se finalizaran y retornaran el valor en cuanto dicho número de ocurrencias fuera el esperado.
	* Se garantiza que no se den condiciones de carrera modificando el acceso concurrente al valor compartido (número de ocurrencias).


2. Parte III.
	* Diseño:
		- Coordinación de hilos:
			* Para pausar la pelea, se debe lograr que el hilo principal induzca a los otros a que se suspendan a sí mismos. Se debe también tener en cuenta que sólo se debe mostrar la sumatoria de los puntos de vida cuando se asegure que todos los hilos han sido suspendidos.
			* Si para lo anterior se recorre a todo el conjunto de hilos para ver su estado, se evalúa como R, por ser muy ineficiente.
			* Si para lo anterior los hilos manipulan un contador concurrentemente, pero lo hacen sin tener en cuenta que el incremento de un contador no es una operación atómica -es decir, que puede causar una condición de carrera- , se evalúa como R. En este caso se debería sincronizar el acceso, o usar tipos atómicos como AtomicInteger).

		- Consistencia ante la concurrencia
			* Para garantizar la consistencia en la pelea entre dos inmortales, se debe sincronizar el acceso a cualquier otra pelea que involucre a uno, al otro, o a los dos simultáneamente:
			* En los bloques anidados de sincronización requeridos para lo anterior, se debe garantizar que si los mismos locks son usados en dos peleas simultánemante, éstos será usados en el mismo orden para evitar deadlocks.
			* En caso de sincronizar el acceso a la pelea con un LOCK común, se evaluará como M, pues esto hace secuencial todas las peleas.
			* La lista de inmortales debe reducirse en la medida que éstos mueran, pero esta operación debe realizarse SIN sincronización, sino haciendo uso de una colección concurrente (no bloqueante).

	

	* Funcionalidad:
		* Se cumple con el invariante al usar la aplicación con 10, 100 o 1000 hilos.
		* La aplicación puede reanudar y finalizar(stop) su ejecución.
		
		-->

<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.

# Chytrý dům založený na multiagentových systémech

## požadavky
* java JRE nebo JDK ve verzi 11 a novější

## spuštění
* Spustitelné jar soubory se nachází ve složce spustitelne_soubory.
* Jako první je nutné spustit pomoci příkazové řádky Middleware.jar příkazem:
```
$ java -jar Middleware.jar
```
* Jestliže úspěšně běži middleware, lze tak spustit další agenty nacházející se společně ve složce.
## Základní
### Jade
```
$ java -jar RadiatorAgent.jar localhost
$ java -jar TemperatureAgent.jar localhost
$ java -jar WindowAgent.jar localhost
$ java -jar GuiAgent.jar localhost
```
### FIoT
```
$ java -jar RadiatorAgent.jar localhost
$ java -jar TemperatureAgent.jar localhost
$ java -jar WindowAgent.jar localhost
$ java -jar GuiAgent.jar
```
## Komplexní
### Jade
```
$ java -jar FireAgent.jar
$ java -jar LightAgent.jar
$ java -jar GuiAgent.jar
$ java -jar MotionAgent.jar
```
### FIoT
```
$ java -jar FireDevice.jar
$ java -jar LightDevice.jar
$ java -jar GuiDevice.jar
$ java -jar MotionDevice.jar
```
## Upozornění
* Nelze spouštět stejného agenta vícekrát.
* Nelze spouštět více agentových systémů najednou.
* během spouštění agenta u Základních příkladů lze místo localhost zadat IP adresu middleware (funguje v rámci lokální sítě mezi více PC).
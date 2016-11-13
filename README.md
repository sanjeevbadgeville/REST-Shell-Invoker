# REST api using Scala, Akka, Spray that invokes a shell script

# Inspired by Akka HTTP microservice - https://github.com/theiterators/akka-http-microservice

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.png)](https://heroku.com/deploy)

## Install SBT

http://www.scala-sbt.org/download.html

## Build jar

```
$ sbt
> assembly
```

When deploying microservice as a .jar file, one can overwrite the configuration values when running the jar.

````
java -jar ./target/scala-2.11/shell-invoker-assembly-1.0.jar
```

## Usage

Start services with sbt:

```
$ sbt
> ~re-start
```

With the service up, you can start sending HTTP requests:

```
$ curl http://localhost:9000/kickoff/param1/param2/param3/param4
./scripts/invoker.sh param1 param2 param3 param4
```

### Testing

Execute tests using `test` command:

```
$ sbt
> test
```


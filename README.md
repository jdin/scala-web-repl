# Scala Web REPL

Client/Server Application that imitates Scala REPL in a browser

![Screenshot](screenshot.gif)

## How to build

```shell script
sbt universal:packageBin
```

## How to start

Development mode:
```shell script
sbt run
```

Production mode:
```shell script
sbt stage
./target/universal/stage/bin/scala-web-repl 
```

## License

MIT

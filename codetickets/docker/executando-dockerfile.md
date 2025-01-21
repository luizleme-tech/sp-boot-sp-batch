## Remover o container

```bash
    docker rm -f mysql_codetickets
```


## Constuir a imagem

```bash
    docker build -t mysql_codetickets .
```

## Rodar o container

```bash
    docker run -d --name mysql_codetickets -p 3306:3306 mysql_codetickets
```

## Acessando o Mysql

```bash
  mysql -u myuser -p
```

```bash
    show databases;
```

```bash
    use cotickets-db;
```

```bash
    show tables;
```
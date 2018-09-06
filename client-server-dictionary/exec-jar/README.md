# Distributed Dictionary README

## Server Setup

In shell, run following code:

```
$ java -jar server.jar <PORT>
```

Please be sure to put the **Dictionaries** directory in the same directory as *server.jar*.

A CML UI is applied for administration setup

```
1. Add new administrator

2. Remove administrator

3. Terminate the server
```

## Client Setup

Press client.jar icon in GUI or in shell:

```
$ java -jar client.jar [remotehost port]
```

Server socket configuration could be applied in GUI.

## Dictionary

[wordset-dictionary](https://github.com/wordset/wordset-dictionary)
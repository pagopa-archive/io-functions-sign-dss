# Digital Signature Service (io-sign-dss)
Services to hash a PDF document, sign it via a signed hash and build the final PADES PDF.

## Technology Stack
- Java 11
- [DSS](https://github.com/esig/dss)

## Develop Locally 💻

### Install package

For the first time run

```sh
mvn clean install
```

### Lint and prettier

```sh
mvn prettier:write
```

```sh
mvn checkstyle:check
```


### Run server locally

```sh
mvn azure-functions:run
```

### Prerequisites
- git
- maven
- jdk
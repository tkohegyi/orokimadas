# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

Örökimádás (Perpetual Adoration) — a Java/Spring web application that schedules and coordinates round-the-clock chapel adoration shifts for a parish in Vác, Hungary. Production runs at http://orokimadas.magyar.website. Domain vocabulary used throughout the code (controllers, tables) is Hungarian: *adorátor/adorator* = a person signed up for a weekly hour of adoration, *koordinátor/coordinator* = person managing a shift/timeslot, *napszak* = a period of the day.

## Build & test

Gradle multi-module build, Java 11 source/target (CI builds with JDK 17).

```
./gradlew build                                   # compile + checkstyle + test + jacoco, all modules
./gradlew build release -P buildNumber=<n>         # also assembles the release/ zip (shadow jar + config + keystore + excel templates)
./gradlew test                                     # run all tests
./gradlew :adoration-application:adoration-engine:test --tests "website.magyar.adoration.bootstrap.AdorationBootstrapTest"   # single test class in a given module
./gradlew :adoration-application:adoration-engine:run -PconfigFilePath=/path/to/adoration.conf.properties   # run the app locally (defaults to adoration-engine/adoration.conf.properties)
./gradlew sonarqube                                 # SonarCloud analysis (needs SONAR_TOKEN), as run in CI
```

Checkstyle failures do not break the build (`checkstyle.ignoreFailures = true`); config is at `config/checkstyle/checkstyle.xml`. Test coverage is collected with JaCoCo per module and reported to SonarCloud.

CI (`.github/workflows/main.yml`) runs on push/PR to `main`: `./gradlew build release sonarqube`.

## Module architecture

Gradle project `adoration` (`settings.gradle`) with subprojects under `adoration-application/modules/`, layered bottom-to-top:

- **adoration-core** — cross-cutting basics: configuration helpers, shared exceptions, generic helper utilities. No dependency on the other modules.
- **adoration-database** — Hibernate/PostgreSQL persistence layer. Entity classes live in `.../database/tables` (`Person`, `Coordinator`, `Link`, `Social`, `AuditTrail`), business/service logic in `.../database/business` and `.../database/service`. `SessionFactoryHelper` owns the Hibernate `SessionFactory` lifecycle (`hibernate.cfg.xml`). Depends on adoration-core.
- **adoration-webapp** — the actual web tier: Spring MVC controllers (`.../web/controller`, e.g. `AdoratorsController`, `CoordinatorController`, `RegisterAdoratorController`, `LoginController`, `ExportController`), Spring Security config (`WEB-INF/spring-security.xml`), servlet context (`WEB-INF/spring/appServlet/servlet-context.xml`), JSP views under `src/main/resources/webapp`. OAuth2 login supported via Google and Facebook. Depends on adoration-core and adoration-database.
- **adoration-engine** — the executable entry point. `AdorationApplication.main()` delegates to `AdorationBootstrap`, which reads a `*.properties` file path from `args[0]`, initializes the Hibernate session factory (`SessionFactoryHelper`) and then an embedded Jetty server (`WebAppServer`, in adoration-webapp) configured for HTTPS with a keystore. Packaged as a fat jar via the Shadow plugin (`shadowJar`, task `adoration-engine-all.jar`); `AdorationApplication.arguments` is a NOSONAR-flagged static reference used by tests. Depends on all three other modules.

Module dependency direction is strictly core → database → webapp → engine; do not introduce reverse or sideways dependencies between them.

The root `adoration-application/build.gradle` defines the `release` task, which zips the shadow jar together with `adoration.conf.properties`, `readme.txt`, the keystore/cert, and the Excel report templates (`config/security/*.xlsx`) into `release/`.

## Configuration

`adoration-engine/adoration.conf.properties` is the runtime config template (webapp port, HTTPS keystore, Hibernate DB connection, SMTP, OAuth client ids/secrets, captcha secret, Excel export paths) — the app is started with the path to a populated copy of this file as its single command-line argument. `config/database/db_script.sql` creates the schema. `config/security/Readme.md` documents how to regenerate the local dev keystore/cert with `keytool`/`openssl`.

## Excel/report integration

adoration-webapp reads/writes `.xlsx` templates (Apache POI) for adorator registers and daily/hourly/coordinator info, in both Hungarian and English variants (`config/security/{hu,en}_*.xlsx`), driven by the `*_file_name` properties in the conf file.

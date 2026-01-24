# Workday – Hobbyprojekt

Workday er et hobbyprojekt til at understøtte daglig planlægning for en tømrer/arbejdsdag med rollerne Admin (Mester) og Svend. Projektet tilbyder dashboard, projekthåndtering, materialeberegner, bestillinger, timerregistrering, kalender og foto-upload – alt i et enkelt Spring Boot/Thymeleaf webinterface.

## Funktioner
- Oversigt for Svend
  - Dagens projekt, seneste bestillinger, og "Timer i dag"
  - Mine opgaver: registrer timer og tag billede direkte fra projektlisten
  - Kalender for de næste 5 dage baseret på planlagte opgaver
  - Adresse vises som klikbart Google Maps-link
- Projekter
  - Admin opretter projekter med startdato og starttid, beskrivelse, adresse, prioritet og arbejdstype
  - Svend ser kun tildelte projekter
- Materialeberegner
  - Gulv, Vinduer/Lister, Gips/Isolering, Lægter
  - Beregner bruttoareal med spild og kan oprette materialeanmodning til Admin
- Bestillinger
  - Svend kan oprette materialeanmodninger
  - Admin ser nye anmodninger, kan godkende/afvise, og eksportere PDF med firmalogo
- Roller og ansvar
  - Admin sætter kun prioritet (Low/Medium/High)
  - Svend sætter status (Startet/I gang/Færdig)
- Timerregistrering og arbejdstimer
  - Svend registrerer timer pr. projekt og dato
  - Summering af timer pr. dag vises i dashboard
- Foto-upload
  - Svend kan uploade billeder pr. projekt til dokumentation

## Teknologi
- Spring Boot 3.2.5 (MVC, Security, JPA)
- Thymeleaf (server-side templates)
- MySQL eller H2 database (konfigureres via application.properties)
- Maven wrapper (mvnw)
- Playwright (E2E browser-tests)

## Krav
- Java 21
- Maven (inkluderet via Maven wrapper)
- (Valgfrit) MySQL 8.0+ hvis du vil bruge MySQL i stedet for H2

## Hurtig start

### 1. Clone projektet
```bash
git clone <dit-repository-url>
cd tommer
```

### 2. Kør med H2 (in-memory database - nemmest)
Projektet kan køre direkte med H2 in-memory database uden yderligere konfiguration:

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux/Mac
# Sørg for at du bruger Java 21
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
./mvnw spring-boot:run
```

Åbn http://localhost:8080 i din browser.

**Demo-brugere (oprettes automatisk ved første kørsel):**
- Admin: `admin@workday.dk` / `admin123`
- Svend: `svend@workday.dk` / `svend123`

### 3. Kør med MySQL (valgfrit)
Hvis du foretrækker MySQL:

1. Opret en MySQL database:
   ```sql
   CREATE DATABASE workday;
   ```

2. Kopiér eksempel-filen:
   ```bash
   # Windows
   copy src\main\resources\application-local.properties.example application-local.properties
   
   # Linux/Mac
   cp src/main/resources/application-local.properties.example application-local.properties
   ```

3. Rediger `application-local.properties` og indtast dine MySQL credentials:
   ```properties
   DB_URL=jdbc:mysql://localhost:3306/workday?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&createDatabaseIfNotExist=true
   DB_USER=root
   DB_PASSWORD=din_password
   DB_DRIVER=com.mysql.cj.jdbc.Driver
   DB_DIALECT=org.hibernate.dialect.MySQLDialect
   ```

4. Kør applikationen:
   ```bash
   mvnw.cmd spring-boot:run
   # eller
   ./mvnw spring-boot:run
   ```

**Note:** `application-local.properties` er allerede i `.gitignore` og bliver ikke committet til git.

## Data og database
- **H2 in-memory**: Nulstilles ved hver genstart. Perfekt til udvikling og test.
- **MySQL**: Persisteret data. Konfigureres via `application-local.properties`.
- **JPA**: Genererer skema automatisk (`spring.jpa.hibernate.ddl-auto=update`)
- **Reference-skript**: `src/main/resources/schema.sql` (kompatibel med både H2 og MySQL)

## Tests

### Unit og Integration Tests (JUnit 5)
Kør alle tests:
```bash
# Windows
mvnw.cmd test

# Linux/Mac
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
./mvnw test
```

Tests dækker:
- Service-lag (MaterialCalculatorService)
- Controller-slice tests (MockMvc)
- Integration tests (fuld Spring context med H2)
- Application context loading

Tests bruger automatisk H2 in-memory database og kræver ingen ekstra konfiguration.

### End-to-End Tests (Playwright)
For at køre E2E tests i rigtig browser:

1. Installer dependencies (kun første gang):
   ```bash
   npm install --no-audit --no-fund
   npm run playwright:install
   ```

2. Start applikationen i en terminal:
   ```bash
   # Windows
   mvnw.cmd spring-boot:run
   
   # Linux/Mac
   ./mvnw spring-boot:run
   ```
   Vent til applikationen er startet (du ser "Started TommerApplication" i konsollen).

3. Kør E2E tests i en anden terminal:
   ```bash
   npm run test:e2e
   ```

**Yderligere Playwright kommandoer:**
- `npm run test:e2e:ui` - Kør tests med UI (interaktiv)
- `npm run test:e2e:headed` - Kør tests med synlig browser (ikke headless)

**E2E tests dækker:**
- Login flows (Admin og Svend)
- Dashboard navigation
- Materialeberegner og bestillingsflow
- Projekter side
- Tests findes under `e2e/` (fx `login.spec.ts`, `calculator.spec.ts`, `projects.spec.ts`)

**Note:** E2E tests forventer at applikationen kører på http://localhost:8080 med demo-brugere oprettet (sker automatisk ved første kørsel).

## Projektstruktur
```
tommer/
├── src/
│   ├── main/
│   │   ├── java/dk/tommer/workday/
│   │   │   ├── config/          # Spring konfiguration (Security, DataInitializer)
│   │   │   ├── Controller/      # MVC controllers (Admin, Svend)
│   │   │   ├── Entity/          # JPA entities
│   │   │   ├── repository/      # Spring Data repositories
│   │   │   ├── Service/         # Business logic
│   │   │   └── dto/             # Data Transfer Objects
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf HTML templates
│   │       ├── static/          # CSS, JS, billeder
│   │       ├── application.properties
│   │       └── schema.sql       # Database reference-skript
│   └── test/
│       ├── java/                # Test classes
│       └── resources/
│           └── application.properties  # Test konfiguration (H2)
├── e2e/                         # Playwright E2E tests
├── pom.xml                      # Maven dependencies
├── package.json                 # Node dependencies (Playwright)
└── README.md
```

## Vigtige sider
- **Admin**:
  - Dashboard, Projekter, Bestillinger (PDF-eksport)
- **Svend**:
  - Dashboard, Projekter (Mine opgaver), Beregner, Bestillinger

## Arkitektur (kort)
- **Controllers**: Admin og Svend sider/flows
- **Entities og Repositories**: Project, User, MaterialOrder, WorkLog, DayPlan, WorkType, Company
- **Services**: MaterialCalculatorService, ProjectService, UserService
- **Templates**: Thymeleaf HTML under `src/main/resources/templates`
- **Sikkerhed**: Spring Security med rolle-baseret adgang
- **Database**: JPA/Hibernate med automatisk skema-generering

## Troubleshooting

### Port 8080 er optaget
Ændr port i `src/main/resources/application.properties`:
```properties
server.port=8081
```

### Database fejl med MySQL
- Tjek at MySQL kører
- Verificer credentials i `application-local.properties`
- Tjek at databasen `workday` eksisterer (eller at `createDatabaseIfNotExist=true` virker)

### Tests fejler
- Sørg for at du kører tests med `mvnw test` (ikke `mvn test`)
- Tests bruger automatisk H2 og kræver ingen ekstra setup

## Formål
Projektet er lavet som et praktisk eksperiment med værktøjer og arbejdsgange i en tømrerhverdag. Det er ikke tænkt til produktion uden yderligere hardening, datamodning, og driftssikkerhed.

## Licens
Privat hobbyprojekt.

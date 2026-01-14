# Workday – Hobbyprojekt

Workday er et hobbyprojekt til at understøtte daglig planlægning for en tømrer/arbejdsdag med rollerne Admin (Mester) og Svend. Projektet tilbyder dashboard, projekthåndtering, materialeberegner, bestillinger, timerregistrering, kalender og foto-upload – alt i et enkelt Spring Boot/Thymeleaf webinterface.

## Funktioner
- Oversigt for Svend
  - Dagens projekt, seneste bestillinger, og “Timer i dag”
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
- Spring Boot (MVC, Security)
- Thymeleaf (server-side templates)
- H2 in-memory database (udvikling)
- Maven wrapper (mvnw)

## Hurtig start
- Krav: Java 17+ og internet (for Maven wrapper)
- Byg og kør
  ```bash
  ./mvnw -q -DskipTests package
  ./mvnw spring-boot:run
  ```
  Åbn http://localhost:8080
- Demo-brugere (oprettes automatisk)
  - Admin: admin@workday.dk / admin123
  - Svend: svend@workday.dk / svend123

## Data og database
- H2 in-memory (nulstilles ved genstart)
- JPA genererer skema automatisk (spring.jpa.hibernate.ddl-auto=update)
- En reference/initialiseringsskript findes i `src/main/resources/schema.sql` (H2-kompatibel)

## Vigtige sider
- Admin
  - Dashboard, Projekter, Bestillinger (PDF-eksport)
- Svend
  - Dashboard, Projekter (Mine opgaver), Beregner, Bestillinger

## Arkitektur (kort)
- Controllers: Admin og Svend sider/flows
- Entities og Repositories: Project, User, MaterialOrder, WorkLog, DayPlan, WorkType, Company
- Services: MaterialCalculatorService, ProjectService, UserService
- Templates: Thymeleaf HTML under `src/main/resources/templates`
- Sikkerhed: Spring Security med rolle-baseret adgang

## Formål
Projektet er lavet som et praktisk eksperiment med værktøjer og arbejdsgange i en tømrerhverdag. Det er ikke tænkt til produktion uden yderligere hardening, datamodning, og driftssikkerhed.

## Licens
Privat hobbyprojekt.


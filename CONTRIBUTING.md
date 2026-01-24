# Retningslinjer for bidrag – Workday

Tak fordi du vil bidrage til Workday. Projektet er et privat hobbyprojekt, der fokuserer på enkelhed, praktisk funktionalitet og stabilitet. Følg nedenstående retningslinjer for at sikre ensartet kvalitet.

## Kom i gang lokalt
- Krav: Java 21+ (Maven wrapper inkluderet)
- Hurtig start med H2 (standard - ingen ekstra setup):
  ```bash
  # Windows
  mvnw.cmd spring-boot:run
  
  # Linux/Mac
  export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
  ./mvnw spring-boot:run
  ```
  Åbn http://localhost:8080
- Demo-brugere oprettes automatisk ved første kørsel:
  - Admin: `admin@workday.dk` / `admin123`
  - Svend: `svend@workday.dk` / `svend123`
- MySQL (valgfrit):
  - Kopiér `src/main/resources/application-local.properties.example` til `application-local.properties`
  - Udfyld MySQL credentials (se README.md for detaljer)

## Branching
- Arbejd på feature-branches, ikke direkte på main.
- Navngivning:
  - `feature/<kort-beskrivelse>`
  - `fix/<kort-beskrivelse>`
  - `docs/<kort-beskrivelse>`

## Commits
- Skriv korte, klare commit-beskeder i bydeform.
- Brug gerne Conventional Commits:
  - `feat: tilføj svend kalenderoversigt`
  - `fix: vis Google Maps link for adresser`
  - `docs: opdater README med starttid`
  - `refactor: ryd op i ProjectController`

## Pull Requests
- Beskriv ændringer, motivation og påvirkede områder.
- Tjekliste før PR:
  - Projektet bygger lokalt uden fejl (`mvnw.cmd package` eller `./mvnw package`).
  - Alle tests kører (`mvnw.cmd test` eller `./mvnw test`).
  - Ingen hemmeligheder/credentials i kode eller logs.
  - UI matcher eksisterende tema (admin.css).
  - Roller respekteres (Admin: kun prioritet; Svend: kun status).
  - Nye features har tests (unit/integration som relevant).

## Kode-standard
- Backend (Java/Spring Boot):
  - Følg eksisterende lagdeling: Controller → Service → Repository → Entity/DTO.
  - Brug JPA-annoteringer som i eksisterende entities (enum som STRING).
  - Sørg for korrekt RBAC i controllers (Spring Security).
  - Valider input og håndter fejl uden at lække følsomme data.
- Templates (Thymeleaf):
  - Brug de eksisterende farver/temaer fra `admin.css`.
  - Brug `th:if`, `th:each`, `th:href`, `th:text` som i projektets skabeloner.
  - Tilføj CSRF-felter conditionelt, hvor form-posts bruges.
- Sikkerhed:
  - Ingen hardcodede nøgler/secrets.
  - Log aldrig adgangskoder eller tokens.

## Database
- **Standard**: H2 in-memory database (ingen setup påkrævet, nulstilles ved genstart).
- **Valgfrit**: MySQL kan konfigureres via `application-local.properties` (se README.md).
- JPA genererer skema automatisk (`spring.jpa.hibernate.ddl-auto=update`).
- Reference-skema: `src/main/resources/schema.sql` (kompatibel med både H2 og MySQL).
- Tabelnavne følger @Table: `users`, `projects`, `material_orders`, `work_types`, `day_plans`, `work_logs`, `company`.
- Enums gemmes som `VARCHAR` (STRING).
- **Tests**: Bruger automatisk H2 via test-profil (`@ActiveProfiles("test")`).

## Tests og verifikation
- **Kør alle tests**:
  ```bash
  # Windows
  mvnw.cmd test
  
  # Linux/Mac
  export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
  ./mvnw test
  ```
- Tests bruger automatisk H2 in-memory database (ingen ekstra konfiguration).
- Test-klasser skal bruge `@ActiveProfiles("test")` for at aktivere test-konfiguration.
- **Test-dækning**:
  - Unit tests: Service-lag (fx `MaterialCalculatorServiceTest`)
  - Controller tests: MockMvc slice tests (fx `SvendCalculatorControllerWebTest`)
  - Integration tests: Fuld Spring context (fx `WorkdayIntegrationTest`)
  - E2E tests: Application context loading (fx `WorkdayE2ETest`)
- **Playwright E2E tests** (valgfrit, men anbefalet for UI-ændringer):
  ```bash
  # 1. Start applikationen
  mvnw.cmd spring-boot:run
  
  # 2. I anden terminal - kør Playwright tests
  npm run test:e2e
  ```
  - Tests findes i `e2e/` mappen
  - Konfiguration: `playwright.config.ts`
  - Kræver at applikationen kører på http://localhost:8080
- **Byg projektet**:
  ```bash
  mvnw.cmd package
  # eller
  ./mvnw package
  ```
- **Manuelt check** (efter ændringer):
  - Login flows (Admin/Svend)
  - Projekter (oprettelse/visning/tildeling)
  - Materialeberegner og bestillingsflow
  - Timerregistrering og dashboardvisning
  - Klikbart Google Maps-link for adresser

## UI og design
- Hold dig til samme layout som Admin Dashboard (sidebar, kort, tabeller).
- Brug korte, danske labels (“Startet/I gang/Færdig”, “Prioritet”).

## Issues
- Beskriv:
  - Trin for reproduktion
  - Forventet vs. faktisk adfærd
  - Relevante logs (uden hemmeligheder)
  - Browser/OS hvis UI-relateret

## Releases
- Brug tags for versioner: `v0.1.0`, `v0.2.0`, …
- Saml ændringer, der er stabile, og opdater README ved større features.

## Licens
Privat hobbyprojekt. Bidrag modtages som OSS-praksis, men projektet er ikke produktionsklart uden yderligere hardening.


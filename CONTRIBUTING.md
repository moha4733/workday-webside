# Retningslinjer for bidrag – Workday

Tak fordi du vil bidrage til Workday. Projektet er et privat hobbyprojekt, der fokuserer på enkelhed, praktisk funktionalitet og stabilitet. Følg nedenstående retningslinjer for at sikre ensartet kvalitet.

## Kom i gang lokalt
- Krav: Java 17+
- Build og kør:
  ```bash
  ./mvnw -q -DskipTests package
  ./mvnw spring-boot:run
  ```
  Åbn http://localhost:8080
- Demo-brugere oprettes automatisk:
  - Admin: admin@workday.dk / admin123
  - Svend: svend@workday.dk / svend123

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
  - Projektet bygger lokalt uden fejl (`./mvnw -q -DskipTests package`).
  - Ingen hemmeligheder/credentials i kode eller logs.
  - UI matcher eksisterende tema (admin.css).
  - Roller respekteres (Admin: kun prioritet; Svend: kun status).

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
- Udvikling bruger H2 in-memory (nulstilles ved genstart).
- JPA genererer skema (`spring.jpa.hibernate.ddl-auto=update`).
- Reference-skema: `src/main/resources/schema.sql` (H2-kompatibel).
- Tabelnavne følger @Table: `users`, `projects`, `material_orders`, `work_types`, `day_plans`, `work_logs`, `company`.
- Enums gemmes som `VARCHAR` (STRING).

## Tests og verifikation
- Kør tests (hvis tilføjet): `./mvnw test`
- Byg: `./mvnw -q package`
- Manuelt check:
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


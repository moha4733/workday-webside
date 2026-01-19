import { defineConfig } from '@playwright/test';

/**
 * Playwright E2E test konfiguration for Workday
 * 
 * Krav:
 * - Spring Boot applikation skal køre på http://localhost:8080
 * - Demo-brugere skal være oprettet (sker automatisk ved første kørsel):
 *   - Admin: admin@workday.dk / admin123
 *   - Svend: svend@workday.dk / svend123
 * 
 * Kør tests:
 *   1. Start applikationen: mvnw.cmd spring-boot:run (eller ./mvnw spring-boot:run)
 *   2. I en anden terminal: npm run test:e2e
 */
export default defineConfig({
  testDir: 'e2e',
  timeout: 30_000, // 30 sekunder timeout per test
  use: {
    baseURL: 'http://localhost:8080',
    headless: true, // Sæt til false for at se browser under test
    screenshot: 'only-on-failure', // Tag screenshot ved fejl
    video: 'retain-on-failure' // Optag video ved fejl
  },
  reporter: [['list']], // Konsol output
  // Retry failed tests én gang
  retries: 1,
  // Maksimalt antal parallelle workers
  workers: 1
});

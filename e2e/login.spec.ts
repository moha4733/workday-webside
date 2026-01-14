import { test, expect } from '@playwright/test';

test.describe('Login og dashboard', () => {
  test('Svend kan logge ind og se dashboard', async ({ page }) => {
    await page.goto('/login');
    await page.fill('#username', 'svend@workday.dk');
    await page.fill('#password', 'svend123');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/svend/dashboard');
    await expect(page.locator('nav.sidebar')).toBeVisible();
    await expect(page.locator('text=Velkommen')).toBeVisible();
    await expect(page.locator('a:has-text("Projekter")')).toBeVisible();
    await expect(page.locator('a:has-text("Beregner")')).toBeVisible();
    await expect(page.locator('a:has-text("Bestillinger")')).toBeVisible();
  });
});

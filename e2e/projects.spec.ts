import { test, expect } from '@playwright/test';

test.describe('Svend projekter side', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.fill('#username', 'svend@workday.dk');
    await page.fill('#password', 'svend123');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/svend/dashboard');
  });

  test('Side indlæses uden 500 og viser overskrift', async ({ page }) => {
    await page.click('a:has-text("Projekter")');
    await page.waitForURL('**/svend/projects');
    await expect(page.locator('h1')).toHaveText('Mine Projekter');
  });
});


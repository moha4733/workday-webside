import { test, expect } from '@playwright/test';

test.describe('Beregner og bestilling', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login');
    await page.fill('#username', 'svend@workday.dk');
    await page.fill('#password', 'svend123');
    await page.click('button[type="submit"]');
    await page.waitForURL('**/svend/dashboard');
  });

  test('Flooring beregning og opret bestilling', async ({ page }) => {
    await page.goto('/svend/calculator');
    await page.selectOption('#calcType', 'floor');
    await page.fill('input[name="length"]', '4');
    await page.fill('input[name="width"]', '3');
    await page.fill('input[name="wastePercentage"]', '10');
    await page.fill('input[name="packageSize"]', '5');
    await page.click('button:has-text("Beregn")');

    await expect(page.locator('table.result-table')).toBeVisible();
    await expect(page.locator('td').nth(0)).toHaveText('12.0');
    await expect(page.locator('td').nth(1)).toHaveText('1.21');
    await expect(page.locator('td').nth(2)).toHaveText('13.2');
    await expect(page.locator('td').nth(3)).toHaveText('3');

    await page.click('button:has-text("Opret materialeanmodning")');

    await page.goto('/svend/orders');
    await expect(page.locator('table')).toBeVisible();
    await expect(page.locator('tbody tr').first()).toContainText('Gulvmateriale bestilling: 13.2 m2');
  });
});

import { expect, test } from '@playwright/test';

test('main navigation scrolls to sections', async ({ page }) => {
  await page.goto('/');

  await page.getByRole('button', { name: 'About' }).click();
  await expect(page.locator('#about')).toBeVisible();

  await page.getByRole('button', { name: 'This site' }).click();
  await expect(page.locator('#this-site')).toBeVisible();
});

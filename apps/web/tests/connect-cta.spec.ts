import { expect, test } from '@playwright/test';

test('connect CTA scrolls to connect section', async ({ page }) => {
  await page.goto('/');

  await page.getByRole('button', { name: 'Connect' }).click();

  const connectSection = page.locator('#connect');
  await expect(connectSection).toBeVisible();
});

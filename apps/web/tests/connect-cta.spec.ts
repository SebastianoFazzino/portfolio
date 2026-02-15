import { expect, test } from '@playwright/test';

test('connect CTA scrolls to connect section', async ({ page }) => {
  await page.goto('/');

  const openMenu = page.getByRole('button', { name: 'Open menu' });
  if (await openMenu.isVisible()) {
    await openMenu.click();
  }

  await page.getByRole('button', { name: 'Connect' }).click();

  await expect(page.locator('#connect')).toBeVisible();
});

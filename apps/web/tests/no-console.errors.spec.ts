import { expect, test } from '@playwright/test';

test('no console errors on homepage', async ({ page }) => {
  const errors: string[] = [];

  page.on('console', (msg) => {
    if (msg.type() === 'error') errors.push(msg.text());
  });

  await page.route('**/api/ping', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ ok: true }),
    });
  });

  await page.route('**/ping', async (route) => {
    await route.fulfill({
      status: 200,
      contentType: 'application/json',
      body: JSON.stringify({ ok: true }),
    });
  });

  await page.route('**/*', async (route) => {
    const url = route.request().url();
    if (url.includes('localhost') && url.endsWith('/ping')) {
      await route.fulfill({
        status: 200,
        contentType: 'application/json',
        body: JSON.stringify({ ok: true }),
      });
      return;
    }
    await route.continue();
  });

  await page.goto('/', { waitUntil: 'domcontentloaded' });
  await page.waitForTimeout(750);

  expect(errors).toEqual([]);
});

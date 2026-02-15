import { expect, Page, test } from '@playwright/test';

async function openMobileMenuIfPresent(page: Page) {
  const openMenu = page.getByRole('button', { name: 'Open menu' });
  if (await openMenu.isVisible()) {
    await openMenu.click();
    // wait for menu item container to appear (pick any item)
    await expect(page.getByRole('button', { name: 'About' })).toBeVisible();
    return true;
  }
  return false;
}

test('main navigation scrolls to sections', async ({ page }) => {
  await page.goto('/');

  const isMobile = await openMobileMenuIfPresent(page);

  await page.getByRole('button', { name: 'About' }).click();
  await expect(page.locator('#about')).toBeVisible();

  if (isMobile) {
    await openMobileMenuIfPresent(page);
  }

  const projectsBtn = page.getByRole('button', { name: 'Projects' });
  await expect(projectsBtn).toBeVisible();

  // Mobile Safari sometimes reports "not stable" due to animations/scroll.
  // Force avoids the stability check that times out.
  await projectsBtn.click({ force: true });

  await expect(page.locator('#projects')).toBeVisible();
});

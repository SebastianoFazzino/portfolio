import {expect, test} from '@playwright/test';

test('brand button scrolls to top', async ({ page }) => {
    await page.goto('/');
    await page.evaluate(() => window.scrollTo(0, 3000));
    await page.getByRole('button', { name: /scroll to top/i }).click();

    await expect
        .poll(
            async () =>
                page.evaluate(() =>
                    Math.max(
                        window.scrollY,
                        document.documentElement.scrollTop,
                        document.body.scrollTop
                    )
                ),
            { timeout: 5000 }
        )
        .toBeLessThan(150);
});

import { test, expect } from '@playwright/test';
import path from 'path';

test('파일 업로드 후 요약 시작 버튼 클릭', async ({ page }) => {
  await page.goto('http://localhost:3000/');

  // 1. 첫 페이지 '로그인' 버튼 클릭
  await page.getByRole('button', { name: '로그인' }).click();

  // 2. 로그인
  await page.getByPlaceholder('name@example.com').fill('minwoo-417@naver.com');
  await page.locator('#password').fill('alsdn417');
  await page.locator('button[type="submit"]').click();

  // 3. 대시보드로 이동 대기
  await page.waitForURL('**/dashboard', { timeout: 10000 });

  // 4. '새 동영상 요약하기' 버튼 클릭
  await page.getByRole('button', { name: '새 동영상 요약하기' }).click();

  // 5. 파일 업로드
  const filePath = path.resolve(
    process.env.HOME || '',
    'Downloads/진정한 남자들은 DB를 쓰지 않습니다.mp4'
  );
  await page.setInputFiles('input[type="file"]', filePath);

  // 6. "요약 시작" 버튼 클릭
  await page.getByRole('button', { name: '요약 시작' }).click();

  // 7. 이후 동작은 필요시 추가
});
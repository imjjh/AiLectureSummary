import dotenv from 'dotenv';
import path from 'path';
dotenv.config({ path: path.resolve(__dirname, '../../../.env.e2e') }); // 루트에 env.e2e 위치
import { test, expect } from '@playwright/test';

test('파일 업로드 후 요약 시작 버튼 클릭', async ({ page }) => {
  await page.goto('http://localhost:3000/');

  // 1. 첫 페이지 '로그인' 버튼 클릭
  await page.getByRole('button', { name: '로그인' }).click();

  const email = process.env.TEST_EMAIL;
  const password = process.env.TEST_PASSWORD;

  // 2. 로그인
  await page.getByPlaceholder('name@example.com').fill(email || 'test@example.com');
  await page.locator('#password').fill(password || 'testpassword');
  await page.locator('button[type="submit"]').click();

  // 3. 대시보드로 이동 대기
  await page.waitForURL('**/dashboard', { timeout: 10000 });

  // 4. '새 동영상 요약하기' 버튼 클릭
  await page.getByRole('button', { name: '새 동영상 요약하기' }).click();

  // 5. 파일 업로드, 요약하기 버튼 클릭
  const filePath = path.resolve(__dirname, '../fixtures/test-video.mp4');
  const fileInput = await page.locator('input[type="file"]');
  await fileInput.setInputFiles(filePath);
  await page.getByRole('button', { name: '요약 시작' }).click();


  //////////////////////// 필요시 아래 코드 주석 해제 후 사용 /////////////////
  console.log('🧪 테스트 완료. 종료를 원하면 수동으로 브라우저 닫기 또는 Ctrl+C');

  // 수동 종료 대기
  await new Promise(() => { });
});
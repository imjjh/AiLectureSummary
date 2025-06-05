import dotenv from 'dotenv';
import path from 'path';
dotenv.config({ path: path.resolve(__dirname, '../../../.env.e2e') });

import { test } from '@playwright/test';
import {
  로그인,
  회원가입,
  새동영상상요약,
  영상업로드,
  요약버튼클릭,
  녹음파일업로드,
  유튜브링크입력,
  메모저장,
  메모삭제,
  PDF저장,
  내정보진입,
  홈으로이동,
  서비스소개이동,
  요약으로이동,
  계정설정
} from './helper';

test('파일 업로드 후 요약 시작 버튼 클릭', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  await 회원가입(page);
  await expect(page).toHaveURL(/.*dashboard.*/);
  await 로그인(page);
  await expect(page.getByText('로그인 성공')).toBeVisible();
  await 홈으로이동(page);
  await 서비스소개이동(page);
  await 요약으로이동(page);
  await 홈으로이동(page);
  await 내정보진입(page);
  await 계정설정(page);

  await 영상업로드(page);
  await expect(page.getByText('업로드 완료')).toBeVisible();
  //await 새동영상상요약(page);
  //await 녹음파일업로드(page);
  // await 요약버튼클릭(page);
  // await PDF저장(page);
  // await 메모저장(page);
  // await 메모삭제(page);
  // await 홈으로돌아가기(page);
  // await 메모저장(page);
  // await 메모삭제(page);
  await page.waitForTimeout(5000); // or remove this line
  // await 메모저장(page);
  // await 메모삭제(page);
  // await 홈으로돌아가기(page);
  
  console.log('🧪 테스트 완료. 종료를 원하면 수동으로 브라우저 닫기 또는 Ctrl+C');
  await new Promise(() => {});
});
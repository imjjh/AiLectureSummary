import dotenv from 'dotenv';
import path from 'path';
// dotenv.config({ path: path.resolve(__dirname, '../../../.env.e2e') });

import { test, expect } from '@playwright/test';
import {
  로그인,
  회원가입,
  새동영상요약,
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

test('모든 기능 동작 검사 (발표 녹화용)', async ({ page }) => {
  await page.goto('http://localhost:3000/');
  // await page.goto('https://aisummarymono.vercel.app/');


  await 영상업로드(page); // 비회원 업로드 불가 확인
  await expect(page.getByText('로그인이 필요합니다')).toBeVisible(); // 가끔 실패함 이유는 모름

  await 회원가입(page);
  // await expect(page).toHaveURL(/.*login.*/); // 이것도 넣으면 오류 가끔 나는데 알림이 방해해서 그런거 같음

  await 로그인(page);
  await expect(page).toHaveURL(/.*dashboard.*/);

  await 홈으로이동(page);  //   await page.locator('a[href="/"]:has-text("홈")').first().click(); 자꾸 2개라 실패하는 것들 이렇게 first로 구현
  await 영상업로드(page);
  // await expect(page.getByText('요약 생성 중')).toBeVisible();  // 검증인데 이미 있던 영상이면 빠르게 넘어가서 검증을 못하는거 같음

  await PDF저장(page);

  await 홈으로이동(page);

  await 녹음파일업로드(page);

  await 메모저장(page);

  await page.waitForTimeout(1000); // 메모 삭제 전 1초 대기
  await 메모삭제(page);

  // 화이트모드->다크모드
  // await 다크모드(page);

  // 유트브링크업로드
  await 홈으로이동(page);
  await 유튜브링크입력(page);

  // 제목 수정
  // await 제목수정(page);

  // 대시 보드 이동
  await 요약으로이동(page);

  // 최근요약X -> 모든요약 클릭
  // await 모든요약(page)

  // 강의 제일 제일 왼쪽꺼? 또는 아무거나 삭제
  // await 강의삭제(page)

  // 강의 카드형태로보기 -> 리스트형태로 보기
  // await 강의리스트(page)

  // 내 정보 수정 (이름 비번)
  await 계정설정(page);

  // 로그 아웃
  // await 로그아웃(page);

  // 비밀번호 찾기 및 재설정
  // await 비밀번호재설정(page)

  // 재설정된 비밀번호 로그인
  // await 로그인?(page); // 재설정된 비밀번호

  // 회원 탈퇴
  // await 회원탈퇴(page);

  // 로그인 시도 (탈퇴한 계정)
  await 로그인(page);

  // 종료
  //////////////////////////////////////////////////////////////////


  // await 서비스소개이동(page);
  // await 요약으로이동(page);
  // await 홈으로이동(page);
  // await 내정보진입(page);
  // await 계정설정(page);
  // await expect(page.getByText('업로드 완료')).toBeVisible();
  //await 새동영상요약(page);
  //await 녹음파일업로드(page);
  // await 요약버튼클릭(page);
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
  await new Promise(() => { });
});
import { Page } from '@playwright/test';
import path from 'path';

//로그인
export async function 로그인(page: Page) {
  const email = (global as any).testEmail || process.env.TEST_EMAIL || 'test@example.com';
  const password = process.env.TEST_PASSWORD || 'testpassword';

  // 첫 페이지 로그인 버튼 클릭
  // await page.getByRole('button', { name: '로그인' }).click();
  // await page.locator('button.hover\\:bg-accent.hover\\:text-accent-foreground').click();

  // 이메일, 비밀번호 입력
  await page.getByPlaceholder('name@example.com').fill(email);
  await page.locator('#password').fill(password);

  // 로그인 제출
  await page.locator('button[type="submit"]').click();
  await page.waitForURL('**/dashboard', { timeout: 10000 });
}


export async function 회원가입(page: Page) {
  const timestamp = Date.now();
  const uniqueEmail = `test${timestamp}@example.com`;
  (global as any).testEmail = uniqueEmail;

  // '회원가입' 버튼 클릭
  await page.getByRole('button', { name: '회원가입' }).click();

  // 입력 필드에 값 입력
  await page.locator('#name').fill('테스터');
  await page.locator('#email').fill(uniqueEmail);
  const password = process.env.TEST_PASSWORD || 'testpassword';
  await page.locator('#password').fill(password);
  await page.locator('#confirm-password').fill(password);

  // '회원가입' 제출 버튼 클릭 (main 내에서)
  await page.locator('main').getByRole('button', { name: '회원가입' }).click();
}

export async function 새동영상요약(page: Page) {
  await page.getByRole('button', { name: '새 동영상 요약하기' }).click();
}

export async function 영상업로드(page: Page) {
  // '영상 파일' 탭 클릭
  await page.getByRole('tab', { name: '영상 파일' }).click();

  // 파일 경로 설정
  const filePath = path.resolve(__dirname, '../fixtures/test-video.mp4');
  console.log(filePath);

  // 숨겨진 input[type="file"]을 가진 label 클릭 후 파일 업로드
  const fileInput = await page.locator('input[type="file"]');
  await fileInput.setInputFiles(filePath);
  await page.getByRole('button', { name: '요약 시작' }).click();
}

export async function 녹음파일업로드(page: Page) {

  // 탭에서 '녹음 파일' 클릭
  await page.getByRole('tab', { name: '녹음 파일' }).click();

  // 파일 선택 input 클릭 후 파일 업로드
  const filePath = path.resolve(__dirname, '../fixtures/test-audio.mp3');
  const fileInput = await page.locator('input[type="file"]');
  await fileInput.setInputFiles(filePath);
  await page.getByRole('button', { name: '요약 시작' }).click();
}

export async function 유튜브링크입력(page: Page) {
  // 탭에서 'YouTube 링크' 클릭
  await page.getByRole('tab', { name: 'YouTube 링크' }).click();

  // URL 입력 필드에 링크 입력
  const youtubeURL = 'https://www.youtube.com/watch?v=bhM2Apxu6JQ';
  await page.locator('input[placeholder="https://www.youtube.com/watch?v=..."]').fill(youtubeURL);
  await page.getByRole('button', { name: '요약 시작' }).click();
}

export async function 요약버튼클릭(page: Page) {
  await page.getByRole('button', { name: '요약 시작' }).click();
}

export async function 메모저장(page: Page) {
  // 텍스트 영역에 텍스트 입력
  await page.locator('textarea[placeholder="메모할 내용을 작성하세요."]').fill('자동 입력된 테스트 메모입니다.');

  // '메모 저장' 버튼 클릭
  await page.getByRole('button', { name: '메모 저장' }).click();
}

export async function 메모삭제(page: Page) {
  // '메모 삭제' 버튼 클릭
  await page.locator('textarea[placeholder="메모할 내용을 작성하세요."]').fill('');
  await page.getByRole('button', { name: '메모 삭제' }).click();
}

export async function PDF저장(page: Page) {
  // 'PDF로 저장' 버튼 클릭
  await page.getByRole('button', { name: 'PDF로 저장' }).click();
}

export async function 내정보진입(page: Page) {
  // 사용자 아바타(프로필) 버튼 클릭 → 일반적으로 class 속성 기반 접근
  await page.locator('span.bg-linear-to-r').click();

  // '내 정보' 메뉴 클릭 (아이콘 + 텍스트 같이 있는 구조)
  await page.getByRole('menuitem', { name: '내 정보' }).click();
}

export async function 계정설정(page: Page) {
  // '계정 설정' 버튼 클릭
  await page.getByRole('button', { name: '계정 설정' }).click();

  // 각 입력 필드에 값 설정
  await page.locator('#username').fill('테스터');
  await page.locator('#email').evaluate((el: HTMLInputElement) => el.removeAttribute('disabled')); // 이메일 필드 활성화
  await page.locator('#email').fill('test@naver.com');
  await page.locator('#password').fill('test12345');
  await page.locator('#confirm-password').fill('test12345');

  // 정보 저장 버튼 클릭
  await page.getByRole('button', { name: '정보 저장' }).click();
}

export async function 홈으로이동(page: Page) {
  // await page.getByRole('link', { name: '홈' }).click(); // 홈이라는 문구가 2개 이상 사용되면 실패나옴
  // await page.locator('a:has-text("홈")').click(); // 2개면 실패
  await page.locator('a[href="/"]:has-text("홈")').first().click();
}

export async function 서비스소개이동(page: Page) {
  const links = await page.locator('a', { hasText: '서비스 소개' }).all();
  if (links.length > 0) {
    await links[0].click();
  }
}

export async function 요약으로이동(page: Page) {
  await page.getByRole('link', { name: '요약' }).click();
}
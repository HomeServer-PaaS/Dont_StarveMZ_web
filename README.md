굶지마MZ 포털 - 로컬 MVP

이 프로젝트는 Vue 화면, Spring Boot REST API, MyBatis, MySQL DB를 Docker Compose로 실행합니다. MySQL 서비스 정의와 초기 스키마는 별도 로컬 DB 프로젝트를 Compose include로 재사용합니다.

실행 준비
1. .env.example을 .env로 복사합니다.
2. MYSQL_ROOT_PASSWORD, MYSQL_PASSWORD, JWT_SECRET 값을 로컬 전용 값으로 변경합니다.
3. Docker Compose 2.20.3 이상을 사용합니다.

시작
  docker compose up --build -d
  docker compose ps

접속
  http://localhost:8080

중지
  docker compose down

DB 데이터까지 지우고 처음부터 초기화할 때만 다음 명령을 사용합니다.
  docker compose down -v

화면 기능
- 공개 콘텐츠 목록과 상세 화면
- 로컬 데모 로그인 및 로그아웃
- 상세 화면의 찜 추가·해제
- 마이페이지의 찜 목록
- 상태 변경 요청의 CSRF 토큰 확인
- MySQL 데이터 볼륨에 사용자·찜 저장

로컬 데모 제한
- 화면 예시 콘텐츠는 실제 정책이나 금융상품이 아닌 가짜 샘플입니다.
- 로컬 데모 로그인은 Kakao OAuth2를 대신하지 않으며 DEMO_AUTH_ENABLED=true일 때만 사용할 수 있습니다.
- 실제 Kakao client credentials와 온통청년 API 연결은 아직 구성되지 않았습니다.
- 데모 로그인은 미리 정한 하나의 로컬 사용자 ID를 사용하므로 복수 계정 간 권한 분리 시나리오는 아직 검증하지 않습니다.
- 쿠키 Secure 플래그는 localhost HTTP 개발에 맞춰 기본 false입니다. HTTPS 운영 환경에서는 true로 설정해야 합니다.

환경
- 화면: Vue 3, Vite, Nginx
- API: Spring Boot 3.5, Java 21, MyBatis
- DB: MySQL 8.4
- 브라우저와 API는 Nginx를 통해 동일 출처 /api 경로로 통신합니다.

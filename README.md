# 굶지마MZ 포털

청년 금융·생활 혜택 정보를 살펴보고, 관심 있는 항목을 저장하는 로컬 MVP입니다. 현재 화면과 기능 확인을 위한 샘플 콘텐츠와 로컬 데모 로그인을 제공합니다.

## 주요 기능

- 공개 콘텐츠 목록과 상세 보기
- 로컬 데모 로그인 및 로그아웃
- 콘텐츠 찜하기·해제와 마이페이지 찜 목록
- JWT HttpOnly 쿠키 인증과 상태 변경 요청의 CSRF 토큰 처리
- MySQL 볼륨을 통한 사용자·찜 데이터 보존

## 구성

```text
브라우저 ──> Vue 3 / Nginx ── /api ──> Spring Boot REST API ──> MySQL 8.4
```

- **화면:** Vue 3, Vite, Nginx
- **API:** Java 21, Spring Boot 3.5, Spring Security, MyBatis
- **데이터베이스:** MySQL 8.4
- **실행:** Docker Compose

브라우저 요청은 Nginx를 거쳐 같은 출처의 `/api` 경로로 API에 전달됩니다.

## 실행 준비

- Docker Engine과 Docker Compose v2.20.3 이상
- 이 저장소와 로컬 DB 프로젝트가 형제 디렉터리로 배치되어 있어야 합니다. `compose.yaml`은 `../local test 목적 DB Container/docker-compose.yml`을 포함하고, DB 프로젝트의 `init/` SQL로 스키마와 샘플 데이터를 초기화합니다. 경로를 바꿔 배치했다면 `compose.yaml`의 `include.path`도 함께 수정하세요.

저장소 루트에서 환경 파일을 만들고 값을 설정합니다.

```bash
cp .env.example .env
```

`.env`의 비밀번호와 JWT 서명 키를 로컬 전용 값으로 바꾸세요. `JWT_SECRET`은 32바이트 이상이어야 하며, 임의 키는 다음처럼 만들 수 있습니다.

```bash
openssl rand -hex 32
```

| 변수 | 용도 | 기본값 |
| --- | --- | --- |
| `MYSQL_ROOT_PASSWORD` | MySQL root 비밀번호 | 필수 설정 |
| `MYSQL_DATABASE` | 애플리케이션 DB 이름 | `guljima_mz_portal` |
| `MYSQL_USER` / `MYSQL_PASSWORD` | API의 DB 접속 계정 | `portal_app` / 필수 설정 |
| `MYSQL_PORT` | 호스트에 공개할 MySQL 포트 | `3306` |
| `JWT_SECRET` | 로그인 JWT 서명 키, 32바이트 이상 | 필수 설정 |
| `DEMO_AUTH_ENABLED` | 로컬 데모 로그인 활성화 | `true` |
| `APP_COOKIE_SECURE` | JWT 쿠키의 Secure 옵션 | `false` |
| `APP_PORT` | 웹 화면 포트 | `8080` |

`.env`에는 비밀값을 넣고 Git에는 커밋하지 마세요. `.env.example`은 변수 이름과 형식만 제공합니다.

## 실행

```bash
docker compose up --build -d
docker compose ps
```

컨테이너가 준비되면 브라우저에서 `http://localhost:8080`을 엽니다. 웹 화면은 기본적으로 `127.0.0.1:8080`에만 공개됩니다. API 상태는 다음 주소에서 확인할 수 있습니다.

```bash
curl http://localhost:8080/api/health
```

## 화면과 API

| 기능 | 화면 경로 | API |
| --- | --- | --- |
| 콘텐츠 목록 | `/` | `GET /api/contents` |
| 콘텐츠 상세 | `/contents/:id` | `GET /api/contents/{id}` |
| 로그인 상태 확인 | 공통 | `GET /api/auth/me` |
| 로컬 데모 로그인·로그아웃 | 공통 | `POST /api/auth/demo-login`, `POST /api/auth/logout` |
| 찜 목록 | `/mypage/favorites` | `GET /api/me/favorites` |
| 찜 추가·해제 | 상세 화면 | `POST`·`DELETE /api/me/favorites/{contentId}` |
| CSRF 토큰·상태 확인 | 공통 | `GET /api/csrf`, `GET /api/health` |

찜 API는 로그인해야 사용할 수 있습니다. 화면은 상태를 바꾸는 요청에 CSRF 토큰을 자동으로 포함합니다.

## 종료와 데이터 초기화

컨테이너를 중지합니다. DB 데이터는 named volume에 남습니다.

```bash
docker compose down
```

DB까지 완전히 삭제하고 초기 상태로 되돌릴 때만 다음 명령을 실행하세요. 이 명령은 로컬 MySQL 볼륨의 데이터를 삭제합니다.

```bash
docker compose down -v
```

초기 SQL은 DB 볼륨이 처음 만들어질 때만 적용됩니다. SQL을 수정해도 기존 볼륨에는 자동으로 다시 적용되지 않습니다.

## 현재 범위와 제한

- `LOCAL_DEMO`로 표시되는 화면 콘텐츠는 기능 확인용 가짜 데이터입니다. 실제 정책, 금융상품, 자격 조건이나 지원 금액을 안내하지 않습니다.
- 데모 로그인은 Kakao OAuth 로그인이 아니며, 하나의 고정된 로컬 데모 사용자를 사용합니다. `DEMO_AUTH_ENABLED=false`로 비활성화할 수 있습니다.
- Kakao OAuth 자격 증명과 외부 정책 데이터 API는 아직 연결되어 있지 않습니다.
- 기본 쿠키 설정은 localhost HTTP 개발용입니다. HTTPS 운영 환경에 배포하려면 `APP_COOKIE_SECURE=true`와 운영용 보안·인증 구성을 별도로 적용해야 합니다.

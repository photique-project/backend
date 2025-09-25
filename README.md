# 📖 Summary

> photique 프로젝트의 서버입니다.

> [!NOTE]
> 테스트 커버리지 100%를 목표로 테스트 코드 작성을 진행하고 있습니다. 이에 더해서, 리팩토링도 함께 진행되고 있고 수정된 API도 존재합니다.

<br>

[![Organization](https://img.shields.io/badge/GitHub-Organization-white?logo=github&style=flat)](https://github.com/photique-project)<br>
[![Frontend](https://img.shields.io/badge/GitHub-Frontend-blue?logo=github&style=flat)](https://github.com/photique-project/frontend)<br>
[![ERD](https://img.shields.io/badge/ERD-photique-purple?logo=erd&style=flat)](https://www.erdcloud.com/d/exY4do6Mumbr6z6fE)

<br><br>

# 🛠️ Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java-007396?logo=java&logoColor=white&style=flat-square)
![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white&style=flat-square)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?logo=springboot&logoColor=white&style=flat-square)
![JWT](https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white&style=flat-square)
![STOMP](https://img.shields.io/badge/STOMP-6DB33F?style=flat-square)

### Database

![MySQL](https://img.shields.io/badge/MySQL-4479A1?logo=mysql&logoColor=white&style=flat-square)
![Redis](https://img.shields.io/badge/Redis-DC382D?logo=redis&logoColor=white&style=flat-square)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-005571?logo=elasticsearch&logoColor=white&style=flat-square)

### Monitoring

![Grafana](https://img.shields.io/badge/Grafana-F46800?logo=grafana&logoColor=white&style=flat-square)
![Promtail](https://img.shields.io/badge/Promtail-0E3A5A?style=flat-square)
![Loki](https://img.shields.io/badge/Loki-0E3A5A?style=flat-square)
![Prometheus](https://img.shields.io/badge/Prometheus-E6522C?logo=prometheus&logoColor=white&style=flat-square)
![Node Exporter](https://img.shields.io/badge/Node%20Exporter-6E7F80?style=flat-square)

### CI/CD

![GitHub](https://img.shields.io/badge/GitHub-181717?logo=github&logoColor=white&style=flat-square)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?logo=githubactions&logoColor=white&style=flat-square)
![Docker](https://img.shields.io/badge/Docker-2496ED?logo=docker&logoColor=white&style=flat-square)
![AWS EC2](https://img.shields.io/badge/AWS%20EC2-FF9900?logo=amazonaws&logoColor=white&style=flat-square)
![GCP](https://img.shields.io/badge/GCP-4285F4?logo=googlecloud&logoColor=white&style=flat-square)

### Etc.

![Nginx](https://img.shields.io/badge/Nginx-009639?logo=nginx&logoColor=white&style=flat-square)
![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?logo=amazonaws&logoColor=white&style=flat-square)
![Postman](https://img.shields.io/badge/Postman-FF6C37?logo=postman&logoColor=white&style=flat-square)

<br><br>

# 📑 API

> 아래 노션 링크를 통해서 API 상세 스펙을 확인할 수 있습니다.

[![Notion](https://img.shields.io/badge/Notion-API%20Document-black?logo=notion&logoColor=white&style=flat)](https://bronze-humerus-068.notion.site/API-Document-1a2207dd9eb880febe54ec49c63c76a3?source=copy_link)

### 인증

| API 기능          | METHOD | URL                          |
|-----------------|--------|------------------------------|
| 로그인             | POST   | `/api/v1/auth/login`         |
| 로그아웃            | POST   | `/api/v1/auth/logout`        |
| 인증 메일 발송 - 회원가입 | POST   | `/api/v1/auth/mail/join`     |
| 인증 메일 발송 - 비번찾기 | POST   | `/api/v1/auth/mail/password` |
| 인증 메일 요청        | POST   | `/api/v1/auth/code`          |
| 내 아이디 조회        | GET    | `/api/v1/auth/me`            |

### 유저

| API 기능     | METHOD | URL                                |
|------------|--------|------------------------------------|
| 닉네임 중복검사   | GET    | `/api/v1/users/nickname`           |
| 비밀번호 찾기    | PATCH  | `/api/v1/users/password`           |
| 회원가입       | POST   | `/api/v1/users`                    |
| 회원정보 조회    | GET    | `/api/v1/users/{userId}`           |
| 회원정보 수정    | PATCH  | `/api/v1/users/{userId}`           |
| 회원탈퇴       | DELETE | `/api/v1/users/{userId}`           |
| 유저 검색      | GET    | `/api/v1/users`                    |
| 팔로우 요청     | POST   | `/api/v1/users/{userId}/follows`   |
| 언팔 요청      | DELETE | `/api/v1/users/{userId}/follows`   |
| 팔로워 리스트 조회 | GET    | `/api/v1/users/{userId}/follower`  |
| 팔로잉 리스트 조회 | GET    | `/api/v1/users/{userId}/following` |

### 단일작품

| API 기능         | METHOD | URL                                                       |
|----------------|--------|-----------------------------------------------------------|
| 단일작품 생성        | POST   | `/api/v1/singleworks`                                     |
| 단일작품 조회        | GET    | `/api/v1/singleworks/{singleworkId}`                      |
| 단일작품 수정        | PATCH  | `/api/v1/singleworks/{singleworkId}`                      |
| 단일작품 삭제        | DELETE | `/api/v1/singleworks/{singleworkId}`                      |
| 단일작품 검색        | GET    | `/api/v1/singleworks`                                     |
| 단일작품 좋아요 추가    | POST   | `/api/v1/singleworks/{singleworkId}/like`                 |
| 단일작품 좋아요 삭제    | DELETE | `/api/v1/singleworks/{singleworkId}/like`                 |
| 단일작품 댓글 생성     | POST   | `/api/v1/singleworks/{singleworkId}/comments`             |
| 단일작품 댓글 리스트 조회 | GET    | `/api/v1/singleworks/{singleworkId}/comments`             |
| 단일작품 댓글 수정     | PATCH  | `/api/v1/singleworks/{singleworkId}/comments/{commentId}` |
| 단일작품 댓글 삭제     | DELETE | `/api/v1/singleworks/{singleworkId}/comments/{commentId}` |
| 좋아요한 단일작품 조회   | GET    | `/api/v1/singleworks/like`                                |
| 내 단일작품 조회      | GET    | `/api/v1/singleworks/me`                                  |

### 전시회

| API 기능         | METHOD | URL                                                       |
|----------------|--------|-----------------------------------------------------------|
| 전시회 생성         | POST   | `/api/v1/exhibitions`                                     |
| 전시회 조회         | GET    | `/api/v1/exhibitions/{exhibitionId}`                      |
| 전시회 삭제         | DELETE | `/api/v1/exhibitions/{exhibitionId}`                      |
| 전시회 검색         | GET    | `/api/v1/exhibitions`                                     |
| 전시회 좋아요 추가     | POST   | `/api/v1/exhibitions/{exhibitionId}/like`                 |
| 전시회 좋아요 삭제     | DELETE | `/api/v1/exhibitions/{exhibitionId}/like`                 |
| 전시회 북마크 추가     | POST   | `/api/v1/exhibitions/{exhibitionId}/bookmark`             |
| 전시회 북마크 삭제     | DELETE | `/api/v1/exhibitions/{exhibitionId}/bookmark`             |
| 전시회 감상평 생성     | POST   | `/api/v1/exhibitions/{exhibitionId}/comments`             |
| 전시회 감상평 리스트 조회 | GET    | `/api/v1/exhibitions/{exhibitionId}/comments`             |
| 전시회 감상평 수정     | PATCH  | `/api/v1/exhibitions/{exhibitionId}/comments/{commentId}` |
| 전시회 감상평 삭제     | DELETE | `/api/v1/exhibitions/{exhibitionId}/comments/{commentId}` |
| 북마크한 전시회 조회    | GET    | `/api/v1/exhibitions/bookmark`                            |
| 좋아요한 전시회 조회    | GET    | `/api/v1/exhibitions/like`                                |
| 내 전시회 조회       | GET    | `/api/v1/exhibitions/me`                                  |

### 전시회 채팅

| API 기능   | METHOD | URL                        |
|----------|--------|----------------------------|
| 채팅 서버 연결 | GET    | `/api/v1/chats/connection` |

### 알림

| API 기능       | METHOD | URL                                                     |
|--------------|--------|---------------------------------------------------------|
| 알림 리스트 조회    | GET    | `/api/v1/users/{userId}/notifications`                  |
| 알림 읽음 처리     | PATCH  | `/api/v1/users/{userId}/notifications/{notificationId}` |
| 알림 전체 읽음 처리  | PATCH  | `/api/v1/users/{userId}/notifications`                  |
| 알림 삭제        | DELETE | `/api/v1/users/{userId}/notifications/{notificationId}` |
| 읽지 않은 알림 카운팅 | GET    | `/api/v1/users/{userId}/notifications/unread`           |

<br><br>

# 🚀 CI/CD Pipeline

<img src="https://github.com/user-attachments/assets/84e2af0e-c9e5-43c6-96f3-5a0c56756f0f" width="800"/>

### 개발 서버 배포 파이프라인 - AWS EC2 free tier

1. 로컬에서 변경사항 commit
2. main 브랜치로 push
3. main -> develop 브랜치로 pr 보냄으로써 개발 서버 배포 워크 플로우 실행
4. 정상 배포되면 merge

### 프로덕트 서버 배포 파이프라인 - GCP VM

1. 로컬에서 변경사항 commit
2. main 브랜치로 push
3. main -> release 브랜치로 pr 보냄으로써 개발 서버 배포 워크 플로우 실행
4. 정상 배포되면 merge

<br><br>

# 💥 Tech Selection & Trouble Shooting

| 타이틀                                  | 주요 내용                                               | 문서 링크                                                                                                 |
|--------------------------------------|-----------------------------------------------------|-------------------------------------------------------------------------------------------------------|
| 🔐 스프링 인증인가 도입기                      | 인증-인가 적용 방식 결정                                      | [자세히 보기](https://bronze-humerus-068.notion.site/15e207dd9eb88121b36bf0881566af56?pvs=4)               |
| 🚀 개발 서버 배포 파이프라인 구축기                | 개발 서버 초기 세팅 및 무중단 배포 적용                             | [자세히 보기](https://bronze-humerus-068.notion.site/15e207dd9eb8814584b2ef55b9b96637?pvs=4)               |
| 👁️ 모니터링 시스템 구축기                     | 그라파나를 활용한 로그, 시스템 메트릭 시각화                           | [자세히 보기](https://bronze-humerus-068.notion.site/16b207dd9eb88059bb54f3c53db2be0d?pvs=4)               |
| 🍽️ 설정파일 관리 전환기                      | 깃허브 서브모듈 기반의 설정파일 관리                                | [자세히 보기](https://bronze-humerus-068.notion.site/177207dd9eb880849e7dd77ce3720471?pvs=4)               |
| 🔍 검색 기능 도입기                         | Elasticsearch 도입                                    | [자세히 보기](https://bronze-humerus-068.notion.site/177207dd9eb8809ea49cf7621acc55df?pvs=4)               |
| ↩️ S3 이미지는 누가 롤백함?                   | ThreadedLocal을 활용하여 @Transactional의 결과를 감지하는 리스너 추가 | [자세히 보기](https://bronze-humerus-068.notion.site/S3-19c207dd9eb88001a37feee1d22e14d0?pvs=4)            |
| ↪️️ 그럼, Elasticsearch 데이터는 누가 롤백함??? | S3 롤백과 같은 동작을 하는 es 데이터 담당 리스너 추가                   | [자세히 보기](https://bronze-humerus-068.notion.site/Elasticsearch-19c207dd9eb880679cd4fefc4d7665a2?pvs=4) |
| 💬 채팅방 구현기                           | WebSocket with STOMP 도입을 통한 채팅 기능 추가                | [자세히 보기](https://bronze-humerus-068.notion.site/19f207dd9eb880dc9481dc6dc3fd58e8?pvs=4)               |
| 💀 DB connection 고갈                  | JPA의 open-in-view: false 설정                         | [자세히 보기](https://bronze-humerus-068.notion.site/DB-connection-1a3207dd9eb880f4b449fd194aa3335c?pvs=4) |
| 📣 알림 서비스 비동기 처리                     | SSE 알림을 @Async 처리                                   | [자세히 보기](https://bronze-humerus-068.notion.site/1c8207dd9eb88087a183c090c8e2cc5a?pvs=4)               |
| 💨 캐싱 적용하기                           | Redis 기반 캐싱 적용                                      | [자세히 보기](https://bronze-humerus-068.notion.site/1d3207dd9eb8809f99cbfe5982473dce?pvs=4)               |
| 🚛 Nginx 최대로 활용하기                    | Nginx 커넥션 설정 및 rate limiting 추가                     | [자세히 보기](https://bronze-humerus-068.notion.site/Nginx-1d3207dd9eb8808b866beb362f09f745?pvs=4)         |
| 🐳 도커 볼륨 마운트 이해하기                    | 도커의 볼륨 마운트 기준 이해                                    | [자세히 보기](https://bronze-humerus-068.notion.site/1d3207dd9eb880909754c91b17e2bbd0?pvs=4)               |
| 👀 조회수 업데이트 레이스 컨디션                  | RDBMS 게시글 조회수 업데이트 방치 및 검색 데이터 조회수 스케줄러 처리          | [자세히 보기](https://bronze-humerus-068.notion.site/1da207dd9eb880658126c720b1acd797?pvs=4)               |
| 🧪 API 처리 스레드 수 조절하기                 | jmeter 부하테스트를 통한 톰캣 스레드, hikari pool 커넥션 사이즈 최적화    | [자세히 보기](https://bronze-humerus-068.notion.site/API-1da207dd9eb88033a9f5f48cdfc98c19?pvs=4)           |
| 🫢 단일쿼리가 30번씩 나간다고?!                 | JPQL을 통한 join 처리                                    | [자세히 보기](https://bronze-humerus-068.notion.site/30-1df207dd9eb88002bec1f0d3f3d98292?pvs=4)            |
| 🛫 쿼리 최적화 경험하기                       | Lazy Loading 문제 개선하기                                | [자세히 보기](https://bronze-humerus-068.notion.site/1e1207dd9eb880e08bf7c45815ce4964?pvs=4)               |
| 🚀 프로덕트 서버 배포 파이프라인 구축기              | 기존에 구축해놨던 파이프라인을 활용하여 GCP에 프로덕트 서버 배포               | [자세히 보기](https://bronze-humerus-068.notion.site/1e2207dd9eb880eebe20e5561b233842?pvs=4)               |

<br><br>


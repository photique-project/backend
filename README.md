# 📖 Summary

> photique 프로젝트의 서버입니다. Java + SpringBoot 기반으로 작성되었으며, REST API 부터 실시간 양방향 통신, 캐싱 등 다양한 기술이 적용되었습니다.

<br>

[![Organization](https://img.shields.io/badge/GitHub-Organization-white?logo=github&style=flat)](https://github.com/photique-project)<br>
[![Frontend](https://img.shields.io/badge/GitHub-Frontend-blue?logo=github&style=flat)](https://github.com/photique-project/backend)<br>
[![ERD](https://img.shields.io/badge/ERD-photique-purple?logo=erd&style=flat)](https://www.erdcloud.com/d/exY4do6Mumbr6z6fE)

<br><br>

# 🛠️ Tech Stack

### Backend

![Java](https://img.shields.io/badge/Java-007396?logo=java&logoColor=white&style=flat-square)
![Gradle](https://img.shields.io/badge/Gradle-02303A?logo=gradle&logoColor=white&style=flat-square)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?logo=springboot&logoColor=white&style=flat-square)
![JWT](https://img.shields.io/badge/JWT-000000?logo=jsonwebtokens&logoColor=white&style=flat-square)
![STOMP](https://img.shields.io/badge/STOMP-6DB33F?style=flat-square)
![SSE](https://img.shields.io/badge/SSE-FF9900?style=flat-square)

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

> 각 도메인 별로 REST API 가 있으며, 웹소켓과 SSE 연결 엔드포인트를 포함하여 총 52개의 API가 존재합니다. 아래 노션 링크를 통해서 API 상세 스펙을 확인할 수 있습니다.
>
[![Notion](https://img.shields.io/badge/Notion-API%20Document-black?logo=notion&logoColor=white&style=flat)](https://bronze-humerus-068.notion.site/API-Document-ce0d15ba8f174e9c92b75b6e74794267?pvs=4)

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
| 알림 서비스 연결    | GET    | `/api/v1/users/{userId}/notifications/subscribe`        |
| 알림 리스트 조회    | GET    | `/api/v1/users/{userId}/notifications`                  |
| 알림 읽음 처리     | PATCH  | `/api/v1/users/{userId}/notifications/{notificationId}` |
| 알림 전체 읽음 처리  | PATCH  | `/api/v1/users/{userId}/notifications`                  |
| 알림 삭제        | DELETE | `/api/v1/users/{userId}/notifications/{notificationId}` |
| 읽지 않은 알림 카운팅 | GET    | `/api/v1/users/{userId}/notifications/unread`           |

<br><br>

# 🚀 CI/CD Pipeline

<br><br>

# 💥 Tech Selection & Trouble Shooting

<br><br>

# 🙋🏻 How to Use

<br><br>
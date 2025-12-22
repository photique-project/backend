# 📖 Summary

> photique 프로젝트의 서버입니다.

> [!NOTE]
> 안전하고 확장 가능한 서버를 위해, 현재 리팩토링과 테스트 코드 작성을 진행하고 있습니다.

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

[![Notion](https://img.shields.io/badge/Notion-API%20Document-black?logo=notion&logoColor=white&style=flat)](https://bronze-humerus-070.notion.site/API-27a207dd9eb880a6966de954ca28a677)

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

| 타이틀                     | 주요 내용                                               | 문서 링크                                                                                         |
|-------------------------|-----------------------------------------------------|-----------------------------------------------------------------------------------------------|
| 🔐 스프링 인증인가 도입기         | 인증-인가 적용 방식 결정                                      | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb881308079c45986e1703c)             |
| 🚀 개발 서버 배포 파이프라인 구축기   | 개발 서버 초기 세팅 및 무중단 배포 적용                             | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb88167962df27d4e2ce498)             |
| 👁️ 모니터링 시스템 구축기        | 그라파나를 활용한 로그, 시스템 메트릭 시각화                           | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb88149a5e3e00a3785992c)             |
| 🍽️ 설정파일 관리 전환기         | 깃허브 서브모듈 기반의 설정파일 관리                                | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb8818cb036dc132369c2d6)             |
| 🔍 검색 기능 도입기            | Elasticsearch 도입                                    | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb881ba8a72e5282bb2c566)             |
| ↩️ S3 이미지는 누가 롤백함?      | ThreadedLocal을 활용하여 @Transactional의 결과를 감지하는 리스너 추가 | [자세히 보기](https://bronze-humerus-070.notion.site/S3-27b207dd9eb8815d9571f2983ea3bdea)          |
| 💬 채팅방 구현기              | WebSocket with STOMP 도입을 통한 채팅 기능 추가                | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb8814e9307d0f0440a8a8f)             |
| 🚛 Nginx 최대로 활용하기       | Nginx 커넥션 설정 및 rate limiting 추가                     | [자세히 보기](https://bronze-humerus-070.notion.site/Nginx-27b207dd9eb881c0a93fdd1fdd9d2a24)       |
| 🐳 도커 볼륨 마운트 이해하기       | 도커의 볼륨 마운트 기준 이해                                    | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb881079553fb125894e607)             |
| 👀 조회수 업데이트 레이스 컨디션     | RDBMS 게시글 조회수 업데이트 방치 및 검색 데이터 조회수 스케줄러 처리          | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb8810b9fa5fa5d3cf877c6)             |
| 🧪 API 처리 스레드 수 조절하기    | jmeter 부하테스트를 통한 톰캣 스레드, hikari pool 커넥션 사이즈 최적화    | [자세히 보기](https://bronze-humerus-070.notion.site/API-27b207dd9eb881eab739ea6ae4b76f01)         |
| 🫢 단일쿼리가 30번씩 나간다고?!    | JPQL을 통한 join 처리                                    | [자세히 보기](https://bronze-humerus-070.notion.site/30-27b207dd9eb8814b9b2adb029ff4c6d0)          |
| 🚀 프로덕트 서버 배포 파이프라인 구축기 | 기존에 구축해놨던 파이프라인을 활용하여 GCP에 프로덕트 서버 배포               | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb88116ab15d506816c6b0a)             |
| 🔮 인터셉터 → 스프링 시큐리티 도입   | 기존 인증 방식인 인터셉터에서 스프링 시큐리티로 업데이트                     | [자세히 보기](https://bronze-humerus-070.notion.site/27b207dd9eb88159acc2c484d99009d0)             |
| 🍐 유저 검색: Like vs FTS   | MySQL을 통한 유저 검색에서 성능 평가 및 Like(접두어) 쿼리 사용           | [자세히 보기](https://bronze-humerus-070.notion.site/Like-vs-FTS-27b207dd9eb8812db40edfa92983cb3a) |

<br><br>


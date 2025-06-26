# 2025_CHALLKATHON_LaVoz_BE
# Logtism
자폐 스펙트럼 장애 아동의 행동을 기록하고 분석하여 케어팀 간 협업을 돕는 디지털 플랫폼

## 서비스 설명

### 프로젝트 개요
이 프로젝트는 자폐 스펙트럼 장애(ASD)를 가진 아동을 돌보는 과정에서 발생하는 케어 제공자 간 정보 단절 문제를 해결하기 위해 개발된 행동 기록 및 분석 플랫폼입니다.

### 주요 목적
- 자폐 아동의 행동과 감정을 체계적으로 기록하고 공유 가능한 협업 노트 시스템을 제공
- AI 기반 분석을 통해 행동 패턴과 트리거 요인을 시각화하고 대처 전략 수립에 도움 제공
- 보호자, 의료진, 교사 간 정보 공유 및 협업 효율성 향상

## 개발자 소개

**[박세현]**
- GitHub: [@username](https://github.com/username)
- Email: [이메일 주소]
- 역할: [백엔드 api 개발 및 검색 엔진 구축]

**[최윤서]**
- GitHub: [@username](https://github.com/username)
- Email: [이메일 주소]
- 역할: [백엔드 api 개발 및 서버 배포]

## 사용 기술 스택

### Backend
- [백엔드 프레임워크]
- [사용한 언어]
- [데이터베이스]

### 개발 도구
- [버전 관리 도구]
- [빌드 도구]
- [테스팅 도구]

## 파일 구조

```
2025_CHALLKATHON_LaVoz_BE/
├── .gradle/
├── .idea/
├── build/
├── gradle/
├── out/
├── src/
│   └── main/
│       ├── java/
│       │   └── com.LaVoz.LaVoZ/
│       │       ├── common/
│       │       │   ├── config/               # OpenAI, Security, Swagger 설정
│       │       │   ├── exception/            # 예외 정의 및 처리
│       │       │   └── security/             # 사용자 인증 및 JWT 관련
│       │       ├── domain/                   # Entity 및 Enum
│       │       ├── openai/                   # OpenAI 연동 및 프롬프트 관리
│       │       ├── repository/               # JPA Repository 인터페이스
│       │       ├── search/                   # 노트 검색 기능 (Elastic 등)
│       │       ├── service/                  # 비즈니스 로직 처리
│       │       └── web/
│       │           ├── apiResponse/          # API 응답 포맷 (성공/실패)
│       │           ├── controller/           # REST API 컨트롤러
│       │           └── dto/                  # 요청 및 응답 DTO
│       └── resources/
│           ├── static/
│           ├── templates/
│           └── application.yml               # Spring Boot 설정
├── test/                                      # 테스트 코드
├── .gitignore
├── build.gradle
├── gradlew
├── gradlew.bat
├── HELP.md
├── README.md
└── settings.gradle
```

## 기능별 소개

### [기능명 1]
[기능에 대한 설명을 작성하세요]

**주요 특징:**
- [특징 1]
- [특징 2]
- [특징 3]

### [기능명 2]
[기능에 대한 설명을 작성하세요]

**구현 방식:**
- [구현 방식 1]
- [구현 방식 2]

### [기능명 3]
[기능에 대한 설명을 작성하세요]

**사용 방법:**
1. [사용 단계 1]
2. [사용 단계 2]
3. [사용 단계 3]

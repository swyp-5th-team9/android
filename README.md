### 모여볼 ⚾

> KBO 팬을 위한 스포츠 펍 탐색 서비스

같은 팀을 응원하는 팬들과 함께 야구를 즐길 수 있는 스포츠 펍을 지도 기반으로 탐색하고,
KBO 경기 일정에 맞춰 내가 응원하는 구단 경기를 상영해주는 펍을 찾아볼 수 있는 안드로이드 앱입니다.

<br>

### 🗺️ 주요 기능

| 기능 | 설명 |
|------|------|
| 지도 기반 펍 탐색 | 네이버 지도 SDK를 활용한 내 주변 스포츠 펍 탐색 |
| 다중 카테고리 필터 | 구단, 위치, 분위기 등 다양한 조건으로 펍 필터링 |
| 펍 상세 정보 | 영업시간, 위치, 전화번호, 편의시설 정보 제공 |
| KBO 일정 캘린더 | 구단별 경기 일정 확인 및 날짜별 펍 탐색 연동 |
| 위시리스트 | 관심 펍 북마크 및 마이페이지에서 관리 |
| 소셜 로그인 | 카카오 / 네이버 소셜 로그인 지원 |

<br>

### 🛠 Tech Stack

### Architecture
- **MVVM + Contract** 패턴
- Google's Recommended App Architecture (Presentation / Domain / Data / Core)
- Jetpack Compose Navigation (Type-safe routes)

### Android
| Category | Library |
|----------|---------|
| UI | Jetpack Compose, Material3 |
| Navigation | Navigation Compose |
| DI | Hilt |
| Network | Retrofit2, OkHttp3 |
| Image | Coil3 |
| Local Storage | DataStore Preferences |
| Map | Naver Map SDK |
| Login | Kakao SDK, Naver OAuth |
| Push | Firebase (Analytics) |
| Serialization | Kotlinx Serialization |
| Logging | Timber |

<br>

### 🏗 Project Structure

```
app/src/main/java/org/app/
├── core
│   ├── common
│   ├── designsystem
│   ├── extension
│   ├── local
│   ├── network
│   └── util
├── data
│   ├── di
│   ├── local
│   ├── mapper
│   ├── remote
│   └── repository
├── domain
│   ├── model
│   ├── mapper
│   └── usecase
└── presentation
    ├── home
    ├── pubdetail
    ├── schedule
    ├── mypage
    ├── onboarding
    └── main
```

<br>

## 👩‍💻 Developer

| 이름 | 역할 |
|------|------|
| 한유빈 | Android(developer) |

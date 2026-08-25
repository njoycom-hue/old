# 두뇌톡톡

치매 예방을 돕는 안드로이드 두뇌 훈련 앱입니다. 고령자 본인이 직접 사용하는 것을 기준으로
큰 글씨/큰 버튼, 단순한 내비게이션을 우선했습니다. 로그인이나 서버 없이 기기에만 기록을 저장합니다.

## 게임 구성

- **기억력 카드 짝맞추기**: 같은 그림 카드를 찾는 기억력 게임 (시도 횟수/시간 측정)
- **순서 기억하기**: 점점 길어지는 순서를 그대로 따라 누르는 게임 (사이먼 게임 방식)
- **빠른 암산**: 덧셈/뺄셈 4지선다 10문제
- **다른 것 찾기**: 같은 범주가 아닌 낱말을 찾는 인지 훈련 10문제

각 게임의 최고 기록과 플레이 기록은 기기에 저장되며, "나의 기록" 화면에서 연속 플레이일수(스트릭)와
게임별 최근 기록을 확인할 수 있습니다. "설정" 화면에서 앱 전체 글자 크기를 조절할 수 있습니다.

## 기술 스택

- Kotlin + Jetpack Compose (Material 3)
- MVVM (ViewModel + StateFlow), Hilt를 이용한 의존성 주입
- Room — 로컬 게임 기록 저장
- Jetpack DataStore — 글자 크기 등 사용자 설정 저장
- Navigation-Compose
- 서버/네트워킹 없음 (오프라인 전용)

## 프로젝트 구조

```
app/src/main/java/com/dunoetoktok/app/
  MainActivity.kt, DunoeTokTokApplication.kt
  navigation/        내비게이션 그래프, 라우트 정의
  ui/
    theme/           고령친화 팔레트 + 조절 가능한 타이포 스케일
    components/      공용 컴포저블 (버튼, 카드, 상단바 등)
    home/            홈 화면
    games/{memory,sequence,math,oddword}/  게임 4종 화면 + ViewModel
    stats/           기록/스트릭 화면
    settings/        설정 화면
  data/
    local/           Room DB, DAO, Entity
    repository/      GameRepository
    settings/        DataStore 기반 SettingsRepository
  di/                Hilt 모듈
  model/             GameType, GameResult, TextScale 등 도메인 모델
  util/              암산 문제 생성기, 낱말 카테고리, 스트릭 계산 (순수 함수, 유닛 테스트 대상)
```

## 빌드 및 실행

Android Studio(최신 안정 버전)로 프로젝트 루트를 열면 필요한 SDK 컴포넌트를 자동으로 내려받고
동기화합니다. 터미널에서는 다음과 같이 빌드할 수 있습니다.

```bash
./gradlew assembleDebug   # 디버그 APK 빌드
./gradlew test            # 유닛 테스트 실행 (게임 로직 관련)
```

> 이 기록은 Android SDK가 설치되지 않은 원격 개발 환경에서 작성되었습니다. 소스 코드는
> 표준 Android/Compose 관례에 따라 세심하게 작성했지만, `./gradlew assembleDebug`로 실제 컴파일
> 검증은 하지 못했습니다. Android Studio에서 열어 동기화 후 빌드해 주세요.

## 다음 단계 아이디어

- 실제 효과음/음성 안내 추가
- 실제 런처 아이콘 디자인 (현재는 벡터로 만든 임시 아이콘)
- Compose UI 테스트, Room 마이그레이션 테스트 추가
- 난이도 조절 옵션 (카드 수, 문제 자릿수 등)

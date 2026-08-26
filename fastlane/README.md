# Fastlane — 모여볼 릴리즈 자동화

빌드·테스트·배포를 한 줄 명령으로 실행한다. 배포는 **Firebase App Distribution**(테스터 배포)을 사용한다.

## Lanes

| Lane | 설명 | 명령 |
|------|------|------|
| `test` | ktlint + 단위 테스트 | `bundle exec fastlane android test` |
| `beta` | 빌드 후 테스터에게 배포 (Firebase App Distribution) | `bundle exec fastlane android beta` |

`beta` 옵션:

```bash
# 릴리즈 노트 / 대상 그룹 지정 (groups 미지정 시 기본값 "모여볼")
bundle exec fastlane android beta notes:"공유 기능 추가" groups:"모여볼"
```

## 최초 1회 세팅

### 1) fastlane 설치

시스템 gem 폴더 대신 **프로젝트 로컬(`vendor/bundle`)** 에 설치한다(권한 이슈 회피, `.gitignore` 처리됨).

```bash
gem install bundler
bundle config set --local path 'vendor/bundle'
bundle install          # Gemfile + fastlane/Pluginfile 의 플러그인까지 설치
```

> **권한 오류**(`Permission denied @ rb_sysopen ... /opt/homebrew/.../plugins/rdoc_plugin.rb`)가 나면,
> 위 `bundle config set --local path 'vendor/bundle'` 를 먼저 실행한 뒤 `bundle install` 하면 된다.
> (Homebrew Ruby 전역 폴더에 쓰려다 막히는 문제 — 로컬 설치로 우회)

설치 확인:

```bash
bundle exec fastlane lanes
```

### 2) Firebase App Distribution 인증 (서비스 계정)

CI/자동화에서는 서비스 계정 JSON을 쓴다.

1. [Google Cloud Console](https://console.cloud.google.com) → 해당 Firebase 프로젝트 → **서비스 계정** 생성
2. 역할: **Firebase App Distribution Admin** 부여
3. JSON 키 다운로드 → 안전한 경로에 저장 (**절대 git에 커밋 금지**)
4. 환경변수로 경로 지정:

```bash
export FIREBASE_SERVICE_ACCOUNT_JSON="/절대/경로/service-account.json"
```

> 로컬에서 잠깐 테스트만 할 땐 `firebase login` 후 `FIREBASE_SERVICE_ACCOUNT_JSON` 없이 실행해도 된다(대화형 인증).

### 3) 테스터 그룹

Firebase 콘솔 → App Distribution → **테스터 및 그룹** 에서 그룹을 만들고 테스터 이메일을 추가한다.

> ⚠️ **`groups:` 값은 그룹의 표시 이름이 아니라 `alias`(별칭)와 정확히 일치해야 한다.**
> 현재 프로젝트 그룹 alias는 **`모여볼`** 이고, `Fastfile`의 기본값도 이걸로 설정돼 있다.
> (다른 그룹을 쓰려면 콘솔에서 alias를 확인해 `groups:"<alias>"` 로 지정)

## 참고

- **Firebase App ID**는 `app/google-services.json` 에서 자동으로 읽는다. 파일이 없는 환경(CI 등)에서는 `FIREBASE_APP_ID` 환경변수로 대체한다.
- 현재 `beta`는 **debug 서명 APK**를 배포한다(별도 upload keystore 불필요). 릴리즈 서명(Play 앱 서명 + upload keystore)이 준비되면 `Fastfile`의 `build_type`을 `"Release"`로 바꾼다.
- Google Play 스토어 직접 배포(`supply`)는 upload keystore + Play Console 서비스 계정이 준비되면 `Fastfile`의 주석 처리된 `deploy` 레인을 활성화한다. (#34 후속)

## 문제 해결

- **`[!] Invalid request (Google::Apis::ClientError)`** — 업로드는 됐는데 "Distributing release"에서 실패하면, `groups:` 의 **그룹 alias가 콘솔과 불일치**하는 경우다. 콘솔에서 alias를 확인해 정확히 맞춘다. (현재 alias: `모여볼`)
- **`fastlane requires your locale to be set to UTF-8`** / 한글 릴리즈 노트 깨짐 — 로케일을 UTF-8로 설정한다:
  ```bash
  export LANG=en_US.UTF-8 LC_ALL=en_US.UTF-8
  ```
- `fastlane/README.md` 는 `Fastfile` 상단의 `skip_docs` 로 자동 덮어쓰기를 막아둔 상태다(직접 작성한 가이드 유지).

## CI (GitHub Actions) 연동 예시

```yaml
- uses: ruby/setup-ruby@v1
  with: { ruby-version: '3.3', bundler-cache: true }
- name: Distribute to testers
  env:
    FIREBASE_SERVICE_ACCOUNT_JSON: ${{ github.workspace }}/sa.json
    GOOGLE_SERVICES_JSON: ${{ secrets.GOOGLE_SERVICES_JSON }}
  run: |
    echo "$GOOGLE_SERVICES_JSON" > app/google-services.json
    echo '${{ secrets.FIREBASE_SERVICE_ACCOUNT }}' > sa.json
    bundle exec fastlane android beta
```

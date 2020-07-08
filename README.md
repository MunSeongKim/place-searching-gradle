# Place-searching
> Open API를 활용한 장소 검색 서비스입니다.

## Requirements
애플리케이션 빌드 또는 실행을 위한 요구 조건:
- gradle wrapper를 사용하여 별도의 요구 사항은 없습니다.
- wrapper를 사용하지 않을 경우 아래의 조건을 따릅니다.
    - JDK 1.8
    - Gradle 6.4.1

## Running the application locally
로컬에서 실행 방법
> 아래 내용은 차후 수정 예정
```
1. IDE 사용
    - `com.mskim.search.place.PlaceSearchingApplication.main()` 실행
2. Maven 사용
    - `mvn spring-boot:run`
> 1, 2번의 방법이 불가능 할 경우 아래의 방법으로 실행하시면 됩니다.
>
> [place-searching.jar download](https://github.com/MunSeongKim/place-searching/raw/master/dist/place-searching-0.1.0-SNAPSHOT.jar) 후
> `java -jar place-searching-0.0.1-SNAPSHOT.jar`로 실행합니다.
>
> 해당 방법은 `JRE 1.8` 이상이 설치가 필요합니다.
    
- 실행 후 http://localhost:8080 으로 접속 가능합니다.
```
## How to use of service
1. 로그인
    - `default 계정`(munseong.kim:test)로 접속
2. 장소 검색
    - `Input 박스`에 검색어를 입력 후 `Search`버튼으로 검색
3. 장소 상세 조회
    - 해당 결과 클릭시 팝업을 통해 상세 결과 확인
        - 결과 내용: `카테고리`, `지번, 도로명 주소`, `연락처`, `지도 정보`
4. 인기 검색어 조회
    - 화면 우측 상단에 출력되는 목록을 통해 확인
    - `Refresh` 버튼: 수동으로 목록 새로고침
    - `Stop Auto` 버튼: 자동 새로고침 기능 해제

## A function of service provided
1. 로그인
    - Spring Security를 이용하여 ID/Password 인증 설정
2. 장소 검색
    - Kakao Local API를 이용한 장소 검색 및 페이징 기능 제공
    - 검색 결과 캐싱 적용
3. 장소 상세 조회
    - 캐시된 장소 데이터를 사용하여 상세 정보 제공
        - 캐시 탐색 실패시, 검색 결과자 저장된 캐시에서 2차 탐색
    - KaKao Map API를 이용하여 지도 출력 제공
4. 인기 검색어 조회
    - 화면 우측에 고정하여 현재 인기 검색어(최대 10개)를 출력 제공
    - 검색어와 함께 검색된 횟수 출력 제공
    - 자동 새로고침 적용, 간격: 10초, 해당 기능을 정지할 수 있는 버튼 제공
    - 수동 새로고침 기능 제공
    
## Service URLs
| 구분        | URL                | Description        |
| ---------- |------------------- | ------------------ |
| 웹 서비스   | /view/auth/sign_in | 로그인 페이지        |
|            | "", /              | 장소 검색 페이지     |
|            | /view/place        |                    |
|            | /view/place/search | 장소 검색 결과 페이지 |
| API 서비스  | /api/keywords/hot  | 인기 검색어 조회     |
|            | /api/places/{id}   | 장소 상세 조회       |

## Libraries & Open-Sources
- Server-side

    | name            | description     |
    | --------------- | --------------- |
    | Spring MVC      | 웹 서비스 제공    |
    | JPA/Hibernate   | ORM 기능 제공    |
    | H2 Database     | 데이터베이스      |
    | Lombok          | Object 생성 지원 |
    | Mustache        | 뷰 템플릿 변환    |
    | JUnit5          | 테스트 도구      |
    | Spring Security | 테스트 지원      |
    | Kakao Local API | 장소 검색 API    |
    
- Client-side

    | name            | description                   |
    | --------------- | ----------------------------- |
    | Bootstrap 4.5   | CSS 프레임워크                  |
    | jQuery 3.5.1    | 부트스트랩 의존성, Ajax 통신 지원 |
    | mustache.js     | 클라이언트 템플릿 데이터 바인딩   |
    | Kakao Map API   | 장소 상세 조회시 지도 출력       |
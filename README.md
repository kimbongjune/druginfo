# 💊 이약모야
- 의약품을 조회하고 효능과 부작용 등을 조회할 수 있습니다.
- 의약품 복용시간 알람을 등록하고 복용 시간을 알림받을 수 있습니다.
- 잔여 의약품 개수를 등록하고 복용 회차에 따른 재고량 미리 알림을 받을 수 있습니다.
<br><br>

## 📎 목차
  - [프로젝트 개요](#-프로젝트-개요) 
  - [사용 기술](#-사용-기술)
  - [프로젝트 전체 구조](#️-프로젝트-전체-구조)
  - [화면](#-화면)
  - [프로젝트 상세](#-프로젝트-상세)
  - [앱 설치 링크](#-앱-설치-링크)
  - [웹소스 깃허브 링크](#-웹소스-깃허브-링크)
<br><br>

## 📜 프로젝트 개요
- 일반적으로 의약품을 복용하는 사용자들은 약품의 포장을 버린 후에는 해당 의약품에 대한 정보를 쉽게 찾기 어려워집니다. 이러한 문제를 해결하기 위해, 알약의 이름이나 식별 정보만을 입력하면 해당 의약품에 대한 상세 정보와 부작용을 조회할 수 있으면 좋겠다는 아이디어에서 시작되었습니다.<br><br>
- 하나의 알람 그룹에 여러 개의 알람을 등록할 수 있도록 설계되었으며, 각 의약품에 대한 고유 알람을 설정해 사용자에게 알림을 제공합니다. 또한, '보유 의약품 알람 개수 미리 알림' 기능을 통해 사용자가 의약품을 전부 소모하기 전 잔여 의약품 개수를 미리 숙지할 수 있도록 도와줍니다.
<br><br>

## 🛠 사용 기술
- Java 11
- Kotlin 1.7.10
- Spring Boot 2.7.12
- Spring Data JPA 2.7.12
- Gradle 7.5.1
- Coroutine 1.6.1
- Android Jetpack Compose(Room Database 2.4.3, Datastore 1.0.0)
- Retrofit 2.9.0
- AWS EC2(ubuntu 22.04)
- AWS RDS(MySQL 8.0)
- AWS S3
- AWS Code Deploy
- Git & GitHub
- GitHub Actions
- Nginx 1.18.0
<br><br>

## 🗺️ 프로젝트 전체 구조
![test  Copy of Untitled](https://kimbongjune.s3.amazonaws.com/druginfo/document/structure2.png)

<br><br>

## 📱 화면
- Figma를 이용한 화면 디자인
링크 : [Figma](https://www.figma.com/file/aCKYbqfqbG7eoNVFq48Su6/Untitled?type=design&node-id=0%3A1&t=edcxHdPCdZFHOUKU-1)

### 애플리케이션 화면
<div>
  <kbd><img border="1px" width=200, src="https://kimbongjune.s3.amazonaws.com/druginfo/document/search.png">
  <img width="200" src="https://kimbongjune.s3.amazonaws.com/druginfo/document/search_result.png">
  <img width="200" src="https://kimbongjune.s3.amazonaws.com/druginfo/document/search_detail.png">
  <img width="200" src="https://kimbongjune.s3.amazonaws.com/druginfo/document/before_alarm.png">
</div>
  <br><br>
<div>
  <kbd><img width="200" src="https://kimbongjune.s3.amazonaws.com/druginfo/document/add_alarm.png">
  <img width="200" src="https://kimbongjune.s3.amazonaws.com/druginfo/document/after_alarm.png">
</div>
<br><br>
  
## 📝 프로젝트 상세 
[노션에서 보기](https://abounding-clownfish-456.notion.site/cfaf2813854c49c48f21f31ed49201ef)
<br><br>
  
## 💾 앱 설치 링크
[안드로이드 앱 설치 링크](https://play.google.com/store/apps/details?id=com.nocdu.druginformation)
<br><br>
  
## 🔗 웹소스 깃허브 링크
[웹소스 깃허브 링크](https://github.com/kimbongjune/druginfo-web)
<br><br>

<img width="1920" height="1080" alt="mobilia 메인 이미지" src="https://github.com/user-attachments/assets/43638fe1-3f77-4f42-a711-3d5f686ae0b0" />

<div align=center>
  
**Spring Boot 기반 가구 쇼핑몰 팀 프로젝트**<br>
다양한 브랜드의 가구를 탐색하고 구매할 수 있는 온라인 가구 쇼핑몰입니다.<br>
🔗 AWS URL : https://themobilia.shop

</div>

## 프로젝트 개요
- **개발 기간:** 2026.09 ~ 2026.10
- **개발 인원:** 5명

팀프로젝트 결과물을 개인 포트폴리오 목적으로 정리한 저장소입니다.<br> 
상품 및 브랜드 데이터는 학습·시연용 더미 데이터입니다.

## 포트폴리오
프로젝트의 기획, 설계, 주요 기능 및 구현 과정은 아래 포트폴리오에서 자세히 확인할 수 있습니다.<br>
[📑 포트폴리오 보기](docs/MOBILIA_portfolio.pdf)

## 주요 기능
- **상품 탐색:** 전체 상품, 공간별 가구, 신상품, 브랜드별 상품 조회 및 검색
- **상품 상세:** 상품 정보, 평균 별점, 후기 개수, 관련 상품 조회
- **구매:** 수량 선택, 장바구니, 주문 및 주문조회
- **고객 서비스:** 상품 후기 및 Q&A
- **관리자:** 상품·회원·주문 관리, ADMIN·MANAGER 권한별 접근 제어
- **반응형 화면:** PC·모바일에 맞춘 메뉴 및 레이아웃

## 담당 작업

### 상품 목록 및 상세 화면
- 상품 목록 및 신상품 페이지 구성
- 브랜드별 배너와 소개 문구 적용
- 상품 상세페이지 정보 정렬 및 구매 영역 UI 개선
- 재고에 따른 품절 표시와 수량 입력 비활성화 처리

### 공통 메뉴 및 관리자 화면
- 모바일 상단 메뉴와 장바구니 배지 정렬 개선
- 관리자 모바일 상단바 및 메뉴 구성
- 회원 정보 수정 화면의 공통 CSS 충돌 및 여백 수정
<!--
## 화면 소개
### 메인 화면
<img width="1654" height="3195" alt="localhost_8088_" src="https://github.com/user-attachments/assets/2b91ed38-0979-497c-9abe-0b461ec2ab68" />

### 상품 상세 화면
<img width="1649" height="1309" alt="localhost_8088_products_detail_122" src="https://github.com/user-attachments/assets/4825c7ba-2de5-4d71-b20e-c05941f9229d" />
-->
## 기술 스택
#### Backend
![Java](https://img.shields.io/badge/Java-ED8B00?style=flat-square)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=flat-square)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white)
#### Frontend
![HTML5](https://img.shields.io/badge/HTML5-E34F26?style=flat-square&logo=html5&logoColor=white)
![CSS3](https://img.shields.io/badge/CSS3-1572B6?style=flat-square)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=flat-square&logo=javascript&logoColor=black)
![Bootstrap](https://img.shields.io/badge/Bootstrap-7952B3?style=flat-square&logo=bootstrap&logoColor=white)
#### Database
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white)
#### Deployment
![AWS Lightsail](https://img.shields.io/badge/AWS_Lightsail-FF9900?style=flat-square)
![Ubuntu](https://img.shields.io/badge/Ubuntu-E95420?style=flat-square&logo=ubuntu&logoColor=white)
#### Tools
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ_IDEA-000000?style=flat-square&logo=intellijidea&logoColor=white)
![MySQL Workbench](https://img.shields.io/badge/MySQL_Workbench-4479A1?style=flat-square&logo=mysql&logoColor=white)


## 팀원별 역할
| 팀원 | 역할 | 담당 기능 |
|:---:|:---:|:---|
| 백지영 | 팀장 | 일정 관리, 업무 분배, 작업 파일 취합<br> 코드 리팩토링, 더미 데이터 취합<br> 테스트, 공통 UI 디자인<br> 상품 목록 조회/정렬/페이징 |
| 김동건 | 팀원 | 상품 등록, 회원가입, 로그인<br> 장바구니 조회/추가/삭제, 아이콘 개수 표시<br> 주문 조회 검색/정렬/페이징, 접기/펼치기<br> 상세 수량 선택, 후기 댓글(등록/수정/삭제)/추천 |
| 김인회 | 팀원 | 관리자 상품 관리(조회)<br> footer/로그인/회원가입/장바구니 페이지 디자인<br> 상품 상세 관련 상품 슬라이드<br> Q&A 등록/조회/수정/삭제 |
| 김태민 | 팀원 | 상품 검색/페이징<br> 로그아웃, 회원정보 수정, 회원 탈퇴<br> 상품 주문(장바구니/상세 주문, 배송정보 입력)<br> 주문 상세조회/취소, 상품 후기 등록/조회/수정 |
| 박진우 | 팀원 | 상품 수정/삭제<br> 관리자 상품/회원 관리(수정/삭제/검색/정렬/페이징)<br> 관리자 주문 관리(주문/배송 상태 변경 등) |

# 💸 Money Planner (Muffler) - Backend

> 사용자의 소비 습관 개선을 돕는 소비 관리 앱, 머플러

<br>

📅 2023.12 ~ 2024.12
- PM & Designer : 1명
- iOS Developers : 4명
- Backend Developers : 4명

<br>

## 🔖 Overview
'Money Planner'(머플러)는 사용자의 소비 습관 개선을 돕는 소비 관리 앱입니다. 
<br>
목표 금액과 카테고리별 예산을 설정하고 하루 단위로 소비 기록과 자가 평가를 할 수 있으며, 목표 기간 동안의 소비 데이터를 리포트로 확인할 수 있습니다.

<br>

## 🔗 AppStore
2024.08 - 2024.12 (현재 운영 종료)
<br>
https://apps.apple.com/kr/app/%EB%A8%B8%ED%94%8C%EB%9F%AC/id6476785127

<br>

## 🌟 Key Feature
[📱 Prototype](https://www.figma.com/proto/jeEpiASglrUzk15LK6QzlT/%EB%A8%B8%ED%94%8C%EB%9F%AC-%EB%A8%B8%EB%8B%88%ED%94%8C%EB%9E%98%EB%84%88--UI-%EB%94%94%EC%9E%90%EC%9D%B8?node-id=3783-15135&starting-point-node-id=3783%3A15253)

<p align="center">
  <img width="48%" alt="image" src="https://github.com/user-attachments/assets/538fd927-b9ac-4dc6-9e9d-61641736124a" />
  <img width="48%" alt="image" src="https://github.com/user-attachments/assets/f15e74f8-f5b4-4901-a877-6620aab8b38f" />
</p>
<p align="center">
  <img width="48%" alt="image" src="https://github.com/user-attachments/assets/d2707dce-1c11-423d-b4ee-a5ba765a3b0d" />
  <img width="48%"alt="image" src="https://github.com/user-attachments/assets/af27e0b6-b8ca-406d-9305-18c98f7b4fb1" />
</p>
<p align="center">
  <img width="48%"alt="image" src="https://github.com/user-attachments/assets/588db6e1-9f88-4b95-b8b9-51a601303429" />
  <img width="48%" alt="image" src="https://github.com/user-attachments/assets/1fe9c8c5-bab6-48da-a9c1-ea1cc1cb7b77" />
</p>

<br>

## 📁 Entity Relationship Diagram
![MoneyPlanner (2)](https://github.com/user-attachments/assets/c12dabda-ed76-4146-9f67-be72cd0f69ca)


## 🖥️ Backend 
- ERD 개발 및 구조 설계
- 하루, 목표 기간, 하루 단위 소비 데이터 조회 (Category 필터링 포함)
- 소비 리포트 구현 (Goal 대비, 하루 평균 소비, 카테고리별 리포트)
- 하루 소비 평가 등록 및 수정
- 소비 데이터 검색
- 문의 메일 전송

<br>

## 🛠️ Tech Stack
- Java 11
- Spring Boot 2.7
- JPA (Hibernate)
- MySQL
- AWS (EC2, S3, CodeDeploy)
- Swagger (API Documentation)


# DemoAPp

Ethereum wallet demo app

## Contents

- [API](#api)
  - [API Description](#api-description)
- [Development](#development)
  - [Requirements](#requirements)
  - [Test](#test)
  - [Run](#run)

## API

### API Summary

1. 이더리움 블록 / 트랜잭션 온체인 데이터를 폴링하여 DB에 기록 함

- 웹소켓이 아닌 이유
  - 소켓 서버 관리가 어렵다.
  - 기록할 온체인 데이터가 실시간으로 기록되야할 필요는 없다.

2. 큐를 이용해 비동기 처리

- 주기적으로 블록 / 트랜잭션 조회하고 기록할 때 그 양이 많을 수 있다.
  각 데이터를 쪼개서 메시지를 큐에 쌓고 처리한다.
- 응답이 오래걸리는 온체인 액션은 큐를 통해 처리한다.

### API Description

1. 지갑 생성

   패스워드를 입력받아, ethererum 계좌 생성 후 DB에 지갑 데이터 기록합니다.
   패스워드는 암호화 해서 저장합니다.

2. 지갑 ETH 잔액 조회

   주소를 입력받아 해당 지갑의 잔액을 조회합니다.

3. 출금

   서비스 내부 지갑에서 외부 이더리움 지갑으로 ETH를 출금합니다.
   Transfer를 큐를 통해 처리합니다.

4. 입출금 히스토리 조회

   트랜잭션 상태 변화 히스토리를 조회합니다.

## Development

### Requirements

- docker
- docker-compose

### Test

```zsh
$ ./gradlew test
```

### Run

```zsh
$ docker-compose up --build
```

# 숙소 예약 시스템 Cloud Native Application 개

# 시나리오
- 집주인은 숙소를 등록 할 수 있다. 
- 고객은 숙소를 선택해 예약할 수 있다.
- 고객이 결제를 진행하면 예약이 확정되고 숙소가 예약 불가 상태가 된다.
- 고객이 예약을 취소하면 결제가 취소되고 숙소가 예약 가능 상태가 된다.
- 고객은 숙소 예약 가능 여부를 확인할 수 있다.

# 어플리케이션 실행

## 주키퍼 실행
```
$ ./zookeeper-server-start.sh ../config/zookeeper.properties
```

## 카프카 실행
```
$ ./kafka-server-start.sh ../config/server.properties
```

## message 수신하기
```
$ ./kafka-console-consumer.sh --bootstrap-server http://localhost:9092 --topic housebook --from-beginning
```
# 숙소 예약 시스템 Cloud Native Application 개

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
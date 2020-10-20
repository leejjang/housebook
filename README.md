# 과제 - 숙소예약 서비스

### Table of contents

- [과제 - 숙소예약 서비스](#---)
  - [서비스 시나리오](#서비스-시나리오)
  - [구현:](#구현-)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 퍼시스턴스](#폴리글랏-퍼시스턴스)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [동기식 호출](#동기식-호출)
    - [비동기식 호출 과 Eventual Consistency](#비동기식-호출-과-Eventual-Consistency)
  
---
# 서비스 시나리오

## 기능적 요구사항
1. 집주인은 숙소를 등록할 수 있다
2. 고객은 숙소를 예약할 수 있다. 
3. 고객이 결제를 진행하면, 예약이 확정되고, 숙소가 예약 불가 상태가 된다.
4. 고객이 예약을 취소하면, 결제가 취소되고, 숙소가 예약 가능 상태가 된다.
5. 고객은 숙소 예약 가능 여부를 확인할 수 있다.


## 비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 예약건은 숙소 대여가 성립하지 않는다. (Sync 호출)
2. 장애격리
    1. 관리자 숙소관리 기능이 수행되지 않더라도 예약은 항상 받을 수 있어야 한다. (Async:Event-driven, Eventual Consistency)
    2. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다. (Circuit breaker)
3. 성능
    1. 고객이 대여 현황을 예약 시스템에서 항상 확인 할 수 있어야 한다. (CQRS)
    2. 결제, 예약 정보가 변경 될 때 마다 숙소 재고가 변경될 수 있어야 한다. (Event driven)

---
# 구현
분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라, 각 BC별로 대변되는 마이크로 서비스들을 스프링부트와 자바로 구현하였다.    
구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다 (각자의 포트넘버는 8081 ~ 808n 이다)
```
cd gateway
mvn spring-boot:run  

cd book
mvn spring-boot:run 

cd payment
mvn spring-boot:run  

cd house
mvn spring-boot:run

cd mypage
mvn spring-boot:run  

```


---
## DDD 의 적용
- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언
```
package housebook;

import org.springframework.beans.BeanUtils;

import javax.persistence.*;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long houseId;
    .../... 중략  .../...
    private Double housePrice;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long gethouseId() {
        return houseId;
    }
    public void sethouseId(Long houseId) {
        this.houseId = houseId;
    }
    .../... 중략  .../...

}
```



- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록    
데이터 접근 어댑터를 자동 생성하기 위하여 Spring Data REST 의 RestRepository 를 적용
```
package housebook;
import org.springframework.data.repository.PagingAndSortingRepository;
    public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long>{

}
```
   
   
---
#### 적용 후 REST API 의 테스트

1. 숙소1 등록
``` http POST http://localhost:8083/houses id=1 status=WAITING houseName=신라호텔 housePrice=200000 ```

<img width="457" alt="숙소등록1" src="https://user-images.githubusercontent.com/54618778/96413666-f0074e80-1226-11eb-88ca-1278f0077fc9.png">


2. 숙소2 등록
``` http POST http://localhost:8083/houses id=2 status=WAITING houseName=SK펜션 housePrice=500000 ```

<img width="463" alt="숙소등록2" src="https://user-images.githubusercontent.com/54618778/96413673-f269a880-1226-11eb-9b1e-62ad3f98cd30.png">


3. 숙소1 예약 
``` http POST http://localhost:8081/books id=1 status=BOOKED houseId=1 bookDate=20201016 housePrice=200000 ```

<img width="448" alt="숙소예약1" src="https://user-images.githubusercontent.com/54618778/96413678-f4336c00-1226-11eb-8665-1ed312adbed1.png">


4. 숙소2 예약
``` http POST http://localhost:8081/books id=2 status=BOOKED houseId=2 bookDate=20201016 housePrice=500000 ```

<img width="450" alt="숙소예약2" src="https://user-images.githubusercontent.com/54618778/96413681-f4cc0280-1226-11eb-8f6c-f3d0e03c0456.png">


5. 숙소2 예약 취소
``` http PUT http://localhost:8081/books id=2 status=BOOK_CANCELED houseId=2 ```

<img width="451" alt="숙소취소" src="https://user-images.githubusercontent.com/54618778/96413687-f5fd2f80-1226-11eb-87fd-2f8c7ea695c5.png">


6. 예약 보기
```http GET localhost:8081/books ```

<img width="573" alt="예약상태보기" src="https://user-images.githubusercontent.com/54618778/96413688-f695c600-1226-11eb-9659-11ba9322f19d.png">


7. 숙소 보기 
``` http GET localhost:8083/houses ```

<img width="591" alt="숙소상태보기" src="https://user-images.githubusercontent.com/54618778/96413674-f3023f00-1226-11eb-830e-d6ab51cb745b.png">


8. 숙소 예약된 상태 (MyPage)
``` http GET localhost:8084/mypages/7 ```

<img width="569" alt="숙소예약된상태" src="https://user-images.githubusercontent.com/54618778/96413683-f5649900-1226-11eb-8ec6-a384afb76ead.png">


9. 숙소 예약취소된 상태 (MyPage)
``` http GET localhost:8084/mypages/9 ```

<img width="545" alt="MyPage_예약취소" src="https://user-images.githubusercontent.com/54618778/96413690-f72e5c80-1226-11eb-9a1e-72df208097fc.png">


---
## 폴리글랏 퍼시스턴스
모두 H2 메모리DB를 적용하였다.  
다양한 데이터소스 유형 (RDB or NoSQL) 적용 시 데이터 객체에 @Entity 가 아닌 @Document로 마킹 후, 기존의 Entity Pattern / Repository Pattern 적용과 데이터베이스 제품의 설정 (pom.xml) 만으로 가능하다.

```
--pom.xml // hsqldb 추가 예시
<dependency>

<groupId>org.hsqldb</groupId>

<artifactId>hsqldb</artifactId>

<version>2.4.0</version>

<scope>runtime</scope>

</dependency>
```

---
## 동기식 호출
Book → Payment 간 호출은 동기식 일관성 유지하는 트랜잭션으로 처리.     
호출 프로토콜은 이미 앞서 Rest Repository 에 의해 노출되어있는 REST 서비스를 FeignClient 를 이용하여 호출.     

```
BookApplication.java.
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableBinding(KafkaProcessor.class)
@EnableFeignClients
public class BookApplication {
    protected static ApplicationContext applicationContext;
    public static void main(String[] args) {
        applicationContext = SpringApplication.run(BookApplication.class, args);
    }
}
```

FeignClient 방식을 통해서 Request-Response 처리.     
Feign 방식은 넷플릭스에서 만든 Http Client로 Http call을 할 때, 도메인의 변화를 최소화 하기 위하여 interface 로 구현체를 추상화.    
→ 실제 Request/Response 에러 시 Fegin Error 나는 것 확인   




- 예약 받은 직후(@PostPersist) 결제 요청함
```
-- Book.java
    @PostPersist
    public void onPostPersist(){
        Booked booked = new Booked();
        BeanUtils.copyProperties(this, booked);
        booked.publishAfterCommit();

        //Following code causes dependency to external APIs
        // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

        housebook.external.Payment payment = new housebook.external.Payment();
        // mappings goes here
        
        payment.setBookId(booked.getId());
        payment.setHouseId(booked.getHouseId());
        ...// 중략 //...

        BookApplication.applicationContext.getBean(housebook.external.PaymentService.class)
            .paymentRequest(payment);

    }
```



- 동기식 호출에서는 호출 시간에 따른 타임 커플링이 발생하며, 결제 시스템이 장애가 나면 주문도 못받는다는 것을 확인함.   
```
Book -- (http request/response) --> Payment

# Payment 서비스 종료

# Book 등록
http http://localhost:8081/books id=1 status=BOOKED houseId=1 bookDate=20201016 housePrice=200000    #Fail!!!!
```
Payment를 종료한 시점에서 상기 Book 등록 Script 실행 시, 500 Error 발생.
("Could not commit JPA transaction; nested exception is javax.persistence.RollbackException: Error while committing the transaction")   
![](images/결제서비스_중지_시_예약시도.png)   


---
## 비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트

Payment가 이루어진 후에(PAID) House시스템으로 이를 알려주는 행위는 동기식이 아니라 비 동기식으로 처리.   
House 시스템의 처리를 위하여 결제주문이 블로킹 되지 않아도록 처리.   
이를 위하여 결제이력에 기록을 남긴 후에 곧바로 결제승인이 되었다는 도메인 이벤트를 카프카로 송출한다(Publish).   

- House 서비스에서는 PAID 이벤트에 대해서 이를 수신하여 자신의 정책을 처리하도록 PolicyHandler 를 구현한다:   
```
@Service
public class PolicyHandler{

    @Autowired
    HouseRepository houseRepository;
    
    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_Rent(@Payload Paid paid){
        if(paid.isMe()){
            System.out.println("##### listener Rent : " + paid.toJson());

            Optional<House> optional = houseRepository.findById(paid.getHouseId());
            House house = optional.get();
            house.setBookId(paid.getBookId());
            house.setStatus("RENTED");

            houseRepository.save(house);
        }
    }
```

- House 시스템은 주문/결제와 완전히 분리되어있으며, 이벤트 수신에 따라 처리되기 때문에, House 시스템이 유지보수로 인해 잠시 내려간 상태라도 주문을 받는데 문제가 없다:
```
# House Service 를 잠시 내려놓음 (ctrl+c)

#PAID 처리
http http://localhost:8082/payments id=1 status=PAID bookId=1 houseId=1 paymentDate=20201016 housePrice=200000 #Success!!

#결제상태 확인
http http://localhost:8082/payments  #제대로 Data 들어옴   

#House 서비스 기동
cd house
mvn spring-boot:run

#House 상태 확인
http http://localhost:8083/houses     # 제대로 kafka로 부터 data 수신 함을 확인
```


---



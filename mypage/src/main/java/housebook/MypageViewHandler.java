package housebook;

import housebook.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MypageViewHandler {


    @Autowired
    private MypageRepository mypageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenBooked_then_CREATE_1 (@Payload Booked booked) {
        try {
            if (booked.isMe()) {
                // view 객체 생성
                Mypage mypage = new Mypage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setBookId(booked.getId());
                mypage.setHouseId(booked.getHouseId());
                mypage.setStatus(booked.getStatus());
                mypage.setHousePrice(booked.getHousePrice());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenPaid_then_CREATE_2 (@Payload Paid paid) {
        try {
            if (paid.isMe()) {
                // view 객체 생성
                Mypage mypage = new Mypage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setBookId(paid.getId());
                mypage.setHouseId(paid.getHouseId());
                mypage.setStatus(paid.getStatus());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenHouseRegistered_then_CREATE_3 (@Payload HouseRegistered houseRegistered) {
        try {
            if (houseRegistered.isMe()) {
                // view 객체 생성
                Mypage mypage = new Mypage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setHouseId(houseRegistered.getId());
                mypage.setStatus(houseRegistered.getStatus());
                mypage.setHousePrice(houseRegistered.getHousePrice());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whenHouseRented_then_UPDATE_1(@Payload HouseRented houseRented) {
        try {
            if (houseRented.isMe()) {
                // view 객체 생성
                Mypage mypage = new Mypage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setHouseId(houseRented.getId());
                mypage.setStatus(houseRented.getStatus());
                mypage.setBookId(houseRented.getBookId());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
                // view 객체 조회
//                List<Mypage> mypageList = mypageRepository.findByHouseId(houseRented.getId());
//                for(Mypage mypage : mypageList){
//                    // view 객체에 이벤트의 eventDirectValue 를 set 함
//                    mypage.setStatus(houseRented.getStatus());
//                    // view 레파지 토리에 save
//                    mypageRepository.save(mypage);
//                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void whenHouseRentCanceled_then_UPDATE_2(@Payload HouseRentCanceled houseRentCanceled) {
        try {
            if (houseRentCanceled.isMe()) {
                // view 객체 생성
                Mypage mypage = new Mypage();
                // view 객체에 이벤트의 Value 를 set 함
                mypage.setHouseId(houseRentCanceled.getId());
                mypage.setStatus(houseRentCanceled.getStatus());
                mypage.setBookId(houseRentCanceled.getBookId());
                // view 레파지 토리에 save
                mypageRepository.save(mypage);
                // view 객체 조회
//                List<Mypage> mypageList = mypageRepository.findByHouseId(houseRentCanceled.getId());
//                for(Mypage mypage : mypageList){
//                    // view 객체에 이벤트의 eventDirectValue 를 set 함
//                    mypage.setStatus(houseRentCanceled.getStatus());
//                    // view 레파지 토리에 save
//                    mypageRepository.save(mypage);
//                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
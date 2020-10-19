package housebook;

import housebook.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.Optional;

@Service
public class PolicyHandler{

    @Autowired
    HouseRepository houseRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void onStringEventListener(@Payload String eventString){

    }

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaymentCanceled_RentCancel(@Payload PaymentCanceled paymentCanceled){

        if(paymentCanceled.isMe()){
            System.out.println("##### listener RentCancel : " + paymentCanceled.toJson());

            Optional<House> optional = houseRepository.findById(paymentCanceled.getHouseId());
            House house = optional.get();
            house.setBookId(null);
            house.setStatus(paymentCanceled.getStatus());

            houseRepository.save(house);
        }
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverPaid_Rent(@Payload Paid paid){

        if(paid.isMe()){
            System.out.println("##### listener Rent : " + paid.toJson());

            Optional<House> optional = houseRepository.findById(paid.getHouseId());
            House house = optional.get();
            house.setBookId(paid.getBookId());
            house.setStatus(paid.getStatus());

            houseRepository.save(house);
        }
    }

}

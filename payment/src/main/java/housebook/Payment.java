package housebook;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="Payment_table")
public class Payment {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long houseId;
    private String status;
    private Long bookId;
    private String paymentDate;
    private String paymentCancelDate;
    private Double housePrice;

    @PostPersist
    public void onPostPersist(){
        System.out.println("##### onPostPersist status = " + this.getStatus());
        if (this.getStatus().equals("BOOKED") || this.getStatus().equals("PAID")) {
            Paid paid = new Paid();
            BeanUtils.copyProperties(this, paid);
            paid.setStatus("PAID");
            paid.publishAfterCommit();
        }
    }

    @PreUpdate
    public void onPreUpdate(){
        System.out.println("##### onPostUpdate status = " + this.getStatus());
        if (this.getStatus().equals("BOOK_CANCELED") || this.getStatus().equals("PAYMENT_CANCELED")) {
            PaymentCanceled paymentCanceled = new PaymentCanceled();
            BeanUtils.copyProperties(this, paymentCanceled);
            paymentCanceled.setStatus("PAYMENT_CANCELED");
            paymentCanceled.publishAfterCommit();
        }
    }

    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }
    public String getPaymentCancelDate() {
        return paymentCancelDate;
    }

    public void setPaymentCancelDate(String paymentCancelDate) {
        this.paymentCancelDate = paymentCancelDate;
    }
    public Double getHousePrice() {
        return housePrice;
    }

    public void setHousePrice(Double housePrice) {
        this.housePrice = housePrice;
    }
}

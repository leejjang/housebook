package housebook;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;

@Entity
@Table(name="House_table")
public class House {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private Long bookId;
    private String status;
    private String description;
    private String houseName;
    private Double price;

    @PostPersist
    public void onPostPersist(){
        System.out.println("##### onPostPersist status = " + this.getStatus());
        if (this.getStatus().equals("WAITING")) {
            HouseRegistered houseRegistered = new HouseRegistered();
            BeanUtils.copyProperties(this, houseRegistered);
            houseRegistered.publishAfterCommit();
        }
    }

    @PostUpdate
    public void onPostUpdate(){
        System.out.println("##### onPostUpdate status = " + this.getStatus());
        if (this.getStatus().equals("PAID")) {
            HouseRented houseRented = new HouseRented();
            BeanUtils.copyProperties(this, houseRented);
            houseRented.setStatus("RENTED");
            houseRented.publishAfterCommit();
        }

        if (this.getStatus().equals("PAYMENT_CANCELED")) {
            HouseRentCanceled houseRentCanceled = new HouseRentCanceled();
            BeanUtils.copyProperties(this, houseRentCanceled);
            houseRentCanceled.setStatus("RENTED_CANCELED");
            houseRentCanceled.publishAfterCommit();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }




}

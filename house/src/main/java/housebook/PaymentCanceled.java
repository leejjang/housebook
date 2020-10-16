
package housebook;

public class PaymentCanceled extends AbstractEvent {

  private Long id;
  private Long bookId;
  private Long houseId;
  private String status;

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

	public Long getHouseId() {
		return houseId;
	}

	public void setHouseId(Long houseId) {
		this.houseId = houseId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}

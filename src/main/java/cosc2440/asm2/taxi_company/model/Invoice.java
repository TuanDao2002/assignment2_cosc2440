package cosc2440.asm2.taxi_company.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Table
@Entity(name = "invoice")
public class Invoice {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int totalCharge;

//    @OneToOne(mappedBy = "booking")
//    @JsonIgnoreProperties(value = "invoice")
//    private Booking booking;

    @Column(nullable = false)
    private ZonedDateTime dateCreated;

    public Invoice() {

    }

    public Invoice(int totalCharge) {
        this.totalCharge = totalCharge;
    }

    public int getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(int totalCharge) {
        this.totalCharge = totalCharge;
    }

//    public Booking getBooking() {
//        return booking;
//    }
//
//    public void setBooking(Booking booking) {
//        this.booking = booking;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

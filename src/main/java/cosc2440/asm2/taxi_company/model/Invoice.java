package cosc2440.asm2.taxi_company.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Table
@Entity(name = "invoice")
public class Invoice {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceID;

    @Column
    private int totalCharge;

    // Booking own the join column so this will be mapped by "invoice", the table name of Invoice class
    @OneToOne(mappedBy = "invoice")
    // set the name of join column with Booking class
    @JoinColumn(name = "invoiceID")
    @JsonIgnoreProperties(value = "invoice")
    private Booking booking;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    @JsonIgnoreProperties("invoice")
    private Driver driver;

    @Column(nullable = false)
    private ZonedDateTime dateCreated;

    public Invoice() {
        this.dateCreated = ZonedDateTime.now();
    }

    public Invoice(int totalCharge, Driver driver) {
        this.totalCharge = totalCharge;
        this.driver = driver;
        this.dateCreated = ZonedDateTime.now();
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(int totalCharge) {
        this.totalCharge = totalCharge;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public Long getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(Long invoiceID) {
        this.invoiceID = invoiceID;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }
}

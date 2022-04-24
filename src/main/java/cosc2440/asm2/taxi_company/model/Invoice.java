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
    private double totalCharge;

    // Booking own the join column so this will be mapped by "invoice", the table name of Invoice class
    @OneToOne(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // set the name of join column with Booking class
    @JoinColumn(name = "invoiceID", nullable = false)
    @JsonIgnoreProperties(value = "invoice", allowSetters = true)
    private Booking booking;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "driverID", nullable = false)
    @JsonIgnoreProperties(value = "invoice")
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

    public double getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(double totalCharge) {
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

    @Override
    public String toString() {
        return "Invoice{" +
                "invoiceID=" + invoiceID +
                ", totalCharge=" + totalCharge +
                ", dateCreated=" + dateCreated +
                '}';
    }
}

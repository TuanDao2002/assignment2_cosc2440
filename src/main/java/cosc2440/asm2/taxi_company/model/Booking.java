package cosc2440.asm2.taxi_company.model;

import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

@Entity
@Table(name = "booking")
public class Booking {
    private static final String datetimePattern = "HH:mm:ss dd-MM-uuuu";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(datetimePattern);

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingID;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    @JsonFormat(pattern = datetimePattern)
    private LocalDateTime pickUpDatetime;

    @Column
    @JsonFormat(pattern = datetimePattern)
    private LocalDateTime dropOffDatetime = null;

    @Column
    private int distance;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    // set name for the join column in Booking and the name of reference column is the ID column of Invoice
    @JoinColumn(name = "invoiceID", nullable = false)
    @JsonIgnoreProperties(value = "booking", allowSetters = true)
    private Invoice invoice;

    @Column(nullable = false)
    private ZonedDateTime dateCreated;

    public Booking() {
        this.dateCreated = ZonedDateTime.now();
    }

    public Booking(String startLocation, String endLocation, String dateString, Invoice invoice) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        setPickUpDatetime(dateString);
        this.invoice = invoice;
        this.dateCreated = ZonedDateTime.now();
    }

    public Long getBookingID() {
        return bookingID;
    }

    public void setBookingID(Long bookingID) {
        this.bookingID = bookingID;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getPickUpDatetime() {
        return pickUpDatetime != null ? pickUpDatetime.format(DATE_TIME_FORMATTER) : null;
    }

    public void setPickUpDatetime(String dateTimeString) {
        this.pickUpDatetime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER.withResolverStyle(ResolverStyle.STRICT));
    }

    // require this annotation as dropOffDatetime can be null => can create duplicate keys in JSON object
    @JsonProperty("dropOffDatetime")
    public String getDropOffDateTime() {
        return dropOffDatetime != null ? dropOffDatetime.format(DATE_TIME_FORMATTER) : null;
    }

    public void setDropOffDateTime(String dateTimeString) {
        this.dropOffDatetime = dateTimeString == null ? null : LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER.withResolverStyle(ResolverStyle.STRICT));
    }

    @JsonIgnore
    public LocalDateTime getDropOffDatetimeObj() {
        return dropOffDatetime;
    }

    @JsonIgnore
    public LocalDateTime getPickUpDatetimeObj() {
        return pickUpDatetime;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DATE_TIME_FORMATTER;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingID=" + bookingID +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", pickUpDatetime=" + pickUpDatetime +
                ", dropOffDatetime=" + dropOffDatetime +
                ", distance=" + distance +
                ", dateCreated=" + dateCreated +
                '}';
    }
}

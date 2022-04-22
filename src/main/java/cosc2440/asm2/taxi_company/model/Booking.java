package cosc2440.asm2.taxi_company.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cascade;

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
    private Long id;

    @Column(nullable = false)
    private String startLocation;

    @Column(nullable = false)
    private String endLocation;

    @Column(nullable = false)
    @JsonFormat(pattern = datetimePattern)
    private LocalDateTime pickUpDatetime;

    @Column
    LocalDateTime dropOffDateTime;

//    @OneToOne(cascade = CascadeType.ALL)
//    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
//    @JsonIgnoreProperties(value = "booking")
//    private Invoice invoice;

    @Column(nullable = false)
    private ZonedDateTime dateCreated;

    public Booking() {
        this.dateCreated = ZonedDateTime.now();
    }

    public Booking(String startLocation, String endLocation, String dateString) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.pickUpDatetime = LocalDateTime.parse(dateString, DATE_TIME_FORMATTER.withResolverStyle(ResolverStyle.STRICT));
        this.dateCreated = ZonedDateTime.now();
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getPickUpDatetime() {
        return pickUpDatetime;
    }

    public void setPickUpDatetime(String dateTimeString) {
        this.pickUpDatetime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER.withResolverStyle(ResolverStyle.STRICT));
    }

    public LocalDateTime getDropOffDateTime() {
        return dropOffDateTime;
    }

    public void setDropOffDateTime(String dateTimeString) {
        this.dropOffDateTime = LocalDateTime.parse(dateTimeString, DATE_TIME_FORMATTER.withResolverStyle(ResolverStyle.STRICT));
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }
}

package cosc2440.asm2.taxi_company.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.OptBoolean;
import org.springframework.format.annotation.DateTimeFormat;

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
    Long id;

    @Column(nullable = false)
    String startLocation;

    @Column(nullable = false)
    String endLocation;

    @Column(nullable = false)
    @JsonFormat(pattern = datetimePattern)
    LocalDateTime pickUpDatetime;

    @Column
    LocalDateTime dropOffDateTime;

    @Column(nullable = false)
    ZonedDateTime dateCreated;

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

    public void setPickUpDatetime(String dateString) {
        this.pickUpDatetime = LocalDateTime.parse(dateString, DATE_TIME_FORMATTER.withResolverStyle(ResolverStyle.STRICT));
    }

    public LocalDateTime getDropOffDateTime() {
        return dropOffDateTime;
    }

    public void setDropOffDateTime(LocalDateTime dropOffDateTime) {
        this.dropOffDateTime = dropOffDateTime;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }
}

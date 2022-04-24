package cosc2440.asm2.taxi_company.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "driver")
public class Driver {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String licenseNumber;

    @Column
    private String phoneNumber;

    @Column
    private double rating;

    @OneToOne(mappedBy = "driver")
    @JoinColumn(name = "id")
    @JsonIgnore
    private Car car;

    @OneToOne(mappedBy = "driver")
    @JoinColumn(name = "driverID")
    @JsonIgnoreProperties("driver")
    private Invoice invoice;

    @Column(nullable = false)
    private ZonedDateTime dateCreated;

    public Driver() {
        this.dateCreated = ZonedDateTime.now();
    }

    public Driver(Long id, String licenseNumber, String phoneNumber, double rating) {
        this.id = id;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.dateCreated = ZonedDateTime.now();
    }

    public Driver(String licenseNumber, String phoneNumber, double rating) {
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.dateCreated = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", rating=" + rating +
                '}';
    }
}

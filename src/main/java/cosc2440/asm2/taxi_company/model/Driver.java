package cosc2440.asm2.taxi_company.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

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
    @JsonIgnoreProperties(value = "driver")
    private Car car;

    public Driver() {
    }

    public Driver(Long id, String licenseNumber, String phoneNumber, double rating) {
        this.id = id;
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    public Driver(String licenseNumber, String phoneNumber, double rating) {
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
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

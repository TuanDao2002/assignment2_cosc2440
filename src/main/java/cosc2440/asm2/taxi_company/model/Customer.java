package cosc2440.asm2.taxi_company.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "customer")
public class Customer {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String phoneNumber;

    @Column
    private String address;

    @Column(nullable = false)
    private ZonedDateTime dateCreated;

    public Customer() {
        this.dateCreated = ZonedDateTime.now();
    }

    public Customer(long id, String phoneNumber, String address) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateCreated = ZonedDateTime.now();
    }

    public Customer(String phoneNumber, String address) {
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateCreated = ZonedDateTime.now();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ZonedDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(ZonedDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", dateCreated=" + dateCreated +
                '}';
    }
}

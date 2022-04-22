package cosc2440.asm2.taxi_company.model;

import javax.persistence.*;

@Entity
@Table(name = "car")
public class Car {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String VIN;

    @Column
    private String make;

    @Column
    private String model;

    @Column
    private String color;

    @Column
    private boolean isConvertible;

    @Column
    private double rating;

    @Column
    private String licensePlate;

    @Column
    private double ratePerKilometer;

    public Car() {
    }

    public Car(String VIN, String make, String model, String color, boolean isConvertible, double rating, String licensePlate, double ratePerKilometer) {
        this.VIN = VIN;
        this.make = make;
        this.model = model;
        this.color = color;
        this.isConvertible = isConvertible;
        this.rating = rating;
        this.licensePlate = licensePlate;
        this.ratePerKilometer = ratePerKilometer;
    }

    public Car(String make, String model, String color, boolean isConvertible, double rating, String licensePlate, double ratePerKilometer) {
        this.make = make;
        this.model = model;
        this.color = color;
        this.isConvertible = isConvertible;
        this.rating = rating;
        this.licensePlate = licensePlate;
        this.ratePerKilometer = ratePerKilometer;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isConvertible() {
        return isConvertible;
    }

    public void setConvertible(boolean convertible) {
        isConvertible = convertible;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public double getRatePerKilometer() {
        return ratePerKilometer;
    }

    public void setRatePerKilometer(double ratePerKilometer) {
        this.ratePerKilometer = ratePerKilometer;
    }

    @Override
    public String toString() {
        return "Car{" +
                "VIN='" + VIN + '\'' +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", color='" + color + '\'' +
                ", isConvertible=" + isConvertible +
                ", rating=" + rating +
                ", licensePlate='" + licensePlate + '\'' +
                ", ratePerKilometer=" + ratePerKilometer +
                '}';
    }
}

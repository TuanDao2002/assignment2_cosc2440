package cosc2440.asm2.taxi_company.model;

import javax.persistence.*;

@Table
@Entity(name = "invoice")
public class Invoice {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

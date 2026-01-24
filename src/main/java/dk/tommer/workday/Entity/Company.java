package dk.tommer.workday.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "cvr_number")
    private String cvrNumber;

    @Column(name = "standard_hourly_rate")
    private Double standardHourlyRate;

    public Company() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCvrNumber() {
        return cvrNumber;
    }

    public void setCvrNumber(String cvrNumber) {
        this.cvrNumber = cvrNumber;
    }

    public Double getStandardHourlyRate() {
        return standardHourlyRate;
    }

    public void setStandardHourlyRate(Double standardHourlyRate) {
        this.standardHourlyRate = standardHourlyRate;
    }
}


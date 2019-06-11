package org.kenta.kosugi.employee.model;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Employee class for employee.EMPLOYEE table.
 */
@Entity
@Table(name = "EMPLOYEE", indexes = {
        @Index(name = "IDX_FIRST_NAME", columnList = "FIRST_NAME"),
        @Index(name = "IDX_MIDDLE_NAME", columnList = "MIDDLE_NAME"),
        @Index(name = "IDX_LAST_NAME", columnList = "LAST_NAME")
})
@NamedQueries({
        @NamedQuery(name = "Employee.findAll", query = "select a from Employee a where a.leavedDate is not null"),
        @NamedQuery(name = "Employee.findByName", query = "select a from Employee a where (a.firstName like :name or a.lastName like :name) and a.leavedDate is not null")
})
public class Employee implements Serializable {

    private static final long serialVersionUID = -7763827188716065700L;

    @Id
    @Column(name = "ID", length = 8)
    public String id;

    @Column(name = "FIRST_NAME", length = 32)
    public String firstName;

    @Column(name = "MIDDLE_NAME", length = 32, nullable = true)
    public String middleName;

    @Column(name = "LAST_NAME", length = 32)
    public String lastName;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public Employee boss;

    private Calendar hireDate;

    public void setHireDate(String hireDate) throws ParseException {
        this.hireDate = this.parse(hireDate);
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "HIRE_DATE", nullable = false)
    public Calendar getHireDate() {
        return hireDate;
    }

    private Calendar leavedDate;

    public void setLeavedDate(String leavedDate) throws ParseException {
        this.leavedDate = this.parse(leavedDate);
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "LEAVED_DATE")
    public Calendar getLeavedDate(){
        return this.leavedDate;
    }

    @Transient
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    @Transient
    private Calendar parse(String date) throws ParseException{
        if(date == null){
            return null;
        }

        Calendar cal = Calendar.getInstance();

        cal.setTime(simpleDateFormat.parse(date));

        return cal;
    }

    /**
     * Constructor
     */
    public Employee() {
    }

    /**
     * Constructor for Employee class without boss object.
     * @param id Company id.
     * @param firstName First Name.
     * @param middleName Middle Name.
     * @param lastName Last Name.
     * @param hireDate Hired Date.
     */
    public Employee(String id, String firstName, String middleName, String lastName, String hireDate) throws ParseException {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.setHireDate(hireDate);
    }
}

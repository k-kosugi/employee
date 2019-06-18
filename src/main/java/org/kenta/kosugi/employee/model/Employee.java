package org.kenta.kosugi.employee.model;

import javax.persistence.*;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        @NamedQuery(name = "Employee.findAll", query = "select a from Employee a where a.leavedDate is null"),
        @NamedQuery(name = "Employee.findByName", query = "select a from Employee a where (a.firstName like :name or a.lastName like :name) and a.leavedDate is not null")
})
public class Employee implements Serializable {

    private static final long serialVersionUID = -7763827188716065700L;

    /**
     * Primary key for this EMPLOYEE table.
     */
    @Id
    @Column(name = "ID", length = 8)
    private String id;

    /**
     * Get the primary key for this employee.
     * @return return the primary key.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the primary key for this employee.
     * @param id primary key
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * First Name associated with this employee object.
     */
    @Column(name = "FIRST_NAME", length = 32)
    private String firstName;

    /**
     * Get the first name associated with this employee object.
     *
     * @return The first name associated with this employee object.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Set first name to employee object.
     *
     * @param firstName First name associated with this employee object.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "MIDDLE_NAME", length = 32, nullable = true)
    private String middleName;

    /**
     * Get Middle name associated with this employee.
     *
     * @return Middle name associated with this employee.
     */
    public String getMiddleName() {
        return this.middleName;
    }

    /**
     * Set Middle name to this employee.
     */
    public void setMiddleName() {
        this.middleName = middleName;
    }

    @Column(name = "LAST_NAME", length = 32)
    private String lastName;

    /**
     * Get Last name associated with this employee object.
     *
     * @return Last name associated with this employee object.
     */
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName() {
        this.lastName = lastName;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Employee boss;

    /**
     * Return the boss object associated with this employee.
     *
     * @return The boss object associated with this employee.
     */
    public Employee getBoss() {
        return this.boss;
    }

    /**
     * Set boss object to this employee.
     *
     * @param boss The boss object to this employee.
     */
    public void setBoss(Employee boss) {
        this.boss = boss;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "HIRE_DATE", nullable = false)
    private Date hireDate;

    /**
     * Set the hired date associated with this employee.
     *
     * @param hireDate Hired Date.
     * @throws ParseException
     */
    public void setHiredDate(String hireDate) throws ParseException {
        this.hireDate = this.parse(hireDate);
    }

    /**
     * Get the hired Date associated with this employee.
     *
     * @return Hire Date associated with this employee.
     */
    public Date getHireDate() {
        return hireDate;
    }

    /**
     * The leaved Date.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "LEAVED_DATE")
    private Date leavedDate;

    /**
     * Set leaved date to employee object.
     *
     * @param leavedDate Leaved Date.
     * @throws ParseException Throw new ParseException when casting the String to Calendar object.
     */
    public void setLeavedDate(String leavedDate) throws ParseException {
        this.leavedDate = this.parse(leavedDate);
    }

    /**
     * Get the leaved Date associated with this employee.
     *
     * @return Leaved Date associated with this employee.
     */
    public Date getLeavedDate() {
        return this.leavedDate;
    }

    @Transient
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * Cast String object to Calendar object.
     *
     * @param date The date of String for example "20190901"
     * @return Return the Calendar object.
     * @throws ParseException
     */
    @Transient
    private Date parse(String date) throws ParseException {

        if (date == null) {
            return null;
        }

        return simpleDateFormat.parse(date);

    }

    /**
     * Constructor
     */
    public Employee() {
    }

    /**
     * Constructor for Employee class without boss object.
     *
     * @param id         Company id.
     * @param firstName  First Name.
     * @param middleName Middle Name.
     * @param lastName   Last Name.
     * @param hireDate   Hired Date.
     */
    public Employee(String id, String firstName, String middleName, String lastName, String hireDate) throws ParseException {

        this.id = id;
        this.firstName = firstName;

        if (!"".equals(middleName)) {
            // set the null when middleName is empty.
            this.middleName = middleName;
        }

        this.lastName = lastName;
        this.setHiredDate(hireDate);

    }

}

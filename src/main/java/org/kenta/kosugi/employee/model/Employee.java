package org.kenta.kosugi.employee.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "EMPLOYEE", indexes = {
        @Index(name = "IDX_FIRST_NAME", columnList = "FIRST_NAME"),
        @Index(name = "IDX_MIDDLE_NAME", columnList = "MIDDLE_NAME"),
        @Index(name = "IDX_LAST_NAME", columnList = "LAST_NAME")
})
@NamedQueries({
        @NamedQuery(name = "Employee.findAll", query = "select a from Employee a"),
        @NamedQuery(name = "Employee.findByName", query = "select a from Employee a where a.firstName like :name or a.lastName like :name")
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

}

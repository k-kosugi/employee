package org.kenta.kosugi.employee.api;

import org.kenta.kosugi.employee.model.Employee;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/employee")
@Produces(MediaType.APPLICATION_JSON)
@RequestScoped
public class EmployeeResource {

    @PersistenceContext(name = "MyPU")
    private EntityManager em;

    @GET
    @Path("/hello")
    public List<Employee> findAll(){
        return this.em
                .createNamedQuery("Employee.findAll", Employee.class)
                .getResultList();
    }

}

package org.kenta.kosugi.employee.api;

import org.kenta.kosugi.employee.model.Employee;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.util.List;

/**
 * Employee class.
 */
@Path("/employee")
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class EmployeeResource {

    @PersistenceContext(name = "MyPU")
    private EntityManager em;

    /**
     * Get the all employees members from EMPLOYEE table.
     *
     * @return List of the employees.
     */
    @GET
    @Path("/all")
    public List<Employee> all(){
        return this.em.createNamedQuery("Employee.findAll", Employee.class)
                .getResultList();
    }

    @GET
    @Path("/find/{id}")
    public Employee find(@PathParam("id") String id){
        return this.em.find(Employee.class, id);
    }

    @POST
    @Path("/regist/{id}")
    public Response registEmployee(
            @PathParam("id") String id,
            @QueryParam("firstName") String firstName,
            @QueryParam("middleName") String middleName,
            @QueryParam("lastName") String lastName,
            @QueryParam("hireDate") String hireDate){

        try {
            Employee employee = new Employee(id, firstName, middleName, lastName, hireDate);

            this.em.persist(employee);
        } catch (ParseException e) {
            return Response.status(500).encoding(e.toString()).build();
        }

        return Response.ok().build();
    }

    @PUT
    @Path("/regist/{id}/boss")
    public Response registBoss(
            @PathParam("id") String id, @QueryParam("bossId") String bossId){

        Employee employee = this.em.find(Employee.class, id);
        if(employee == null){
            return Response.status(404).encoding("Can't found employee with employeeid = " + id).build();
        }

        Employee boss = this.em.find(Employee.class, bossId);
        if(boss == null){
            return Response.status(404).encoding("Can't found employee with bossId = " + boss).build();
        }

        employee.boss = boss;

        this.em.merge(employee);

        return Response.ok().build();

    }

    @DELETE
    @Path("/leave/{id}")
    public Response leave(@PathParam("id") String id, @QueryParam("leavedDate") String leavedDate){
        Employee employee = this.em.find(Employee.class, id);
        try {
            employee.setHireDate(leavedDate);
        } catch (ParseException e) {
            return Response.status(400).encoding(e.toString()).build();
        }

        this.em.merge(employee);

        return Response.ok().build();
    }
}

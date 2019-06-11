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
    public List<Employee> all() {
        return this.em.createNamedQuery("Employee.findAll", Employee.class)
                .getResultList();
    }

    /**
     * Find employee object with using employee id.
     *
     * @param id employee id.
     * @return Return employee object with using employee id.
     */
    @GET
    @Path("/find/{id}")
    public Employee find(@PathParam("id") String id) {
        return this.em.find(Employee.class, id);
    }

    /**
     * Regist the employee object to EMPLOYEE table.
     *
     * @param id         Employee id.
     * @param firstName  First Name.
     * @param middleName Middle Name.
     * @param lastName   Last Name.
     * @param hiredDate  Hired Date.
     * @return Return 200 OK response when request success.
     */
    @POST
    @Path("/regist/{id}")
    public Response registEmployee(
            @PathParam("id") String id,
            @QueryParam("firstName") String firstName,
            @QueryParam("middleName") String middleName,
            @QueryParam("lastName") String lastName,
            @QueryParam("hiredDate") String hiredDate) {

        try {
            // Find the employee object by using employee id.
            Employee employee = new Employee(id, firstName, middleName, lastName, hiredDate);

            this.em.persist(employee);
        } catch (ParseException e) {
            return Response.status(500).encoding(e.toString()).build();
        }

        return Response.ok().build();
    }

    /**
     * Regist the boss object to employee object.
     *
     * @param id     Employee id.
     * @param bossId Boss's employee id.
     * @return Return the 200 OK response when regist request success.
     * Return the 404 NG response when regist request fail.
     */
    @PUT
    @Path("/regist/{id}/boss")
    public Response registBoss(
            @PathParam("id") String id, @QueryParam("bossId") String bossId) {

        // Find the employee object by using employee id.
        Employee employee = this.em.find(Employee.class, id);
        if (employee == null) {
            return Response.status(404).encoding("Can't found employee with employeeid = " + id).build();
        }

        // Find the boss object by using boss's employee id.
        Employee boss = this.em.find(Employee.class, bossId);
        if (boss == null) {
            return Response.status(404).encoding("Can't found employee with bossId = " + boss).build();
        }

        // Set boss object to employee object.
        employee.setBoss(boss);

        this.em.merge(employee);

        return Response.ok().build();

    }

    /**
     * Delete the employee object from this EMPLOYEE table.
     * Set the leaved date to this employee object.
     *
     * @param id         employee id
     * @param leavedDate Leaved Date.
     * @return Return the 200 OK response when the request success.
     * If response fail, return 400 response.
     */
    @DELETE
    @Path("/leave/{id}")
    public Response leave(@PathParam("id") String id, @QueryParam("leavedDate") String leavedDate) {

        // Find employee object by id.
        Employee employee = this.em.find(Employee.class, id);
        try {
            employee.setLeavedDate(leavedDate);
        } catch (ParseException e) {
            return Response.status(400).encoding(e.toString()).build();
        }

        // merge employee object
        this.em.merge(employee);

        // Return 200 response code.
        return Response.ok().build();

    }
}

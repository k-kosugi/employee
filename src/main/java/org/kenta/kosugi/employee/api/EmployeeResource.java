package org.kenta.kosugi.employee.api;

import org.kenta.kosugi.employee.model.Employee;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Stateless
@Path("/employee")
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeResource implements Serializable {

    private int count = 0;

    @PersistenceContext(name = "MyPU")
    private EntityManager em;

    @GET
    @Path("/all")
    public List<Employee> findAll() {
        if (this.count++ % 10 == 0) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return this.em
                .createNamedQuery("Employee.findAll", Employee.class)
                .getResultList();
    }

    @GET
    @Path("/find/{id}")
    public Employee findById(@PathParam("id") String id) {
        return this.em
                .find(Employee.class, id);
    }

    @POST
    @Path("/regist/{id}")
    public Response registEmployee(@PathParam("id") String id, @QueryParam("firstName") String firstName, @QueryParam("middleName") String middleName, @QueryParam("lastName") String lastName, @QueryParam("hireDate") String hireDate) {

        Calendar calendar = Calendar.getInstance();

        // Formatter
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        // Cast
        try {
            calendar.setTime(simpleDateFormat.parse(hireDate));
        } catch (ParseException e) {
            return Response.status(500).encoding(e.toString()).build();
        }

        // Create employee object.
        Employee employee= new Employee();

        employee.id = id;
        employee.firstName = firstName;
        employee.middleName = "".equals(middleName) ? null : middleName;
        employee.lastName = lastName;
        employee.hireDate = calendar;

        // Persist
        this.em.persist(employee);

        return Response.ok().build();
    }

    @POST
    @Path("/regist/{id}/boss/{bossId}")
    public Response registBoss(@PathParam("id") String id, @PathParam("bossId") String bossId){

        Employee employee = this.em.find(Employee.class, id);
        if(employee == null){
            return Response.status(404).encoding("Could not find employee(id = " + id + ").").build();
        }

        Employee boss = this.em.find(Employee.class, bossId);
        if(boss == null){
            return Response.status(404).encoding("Could not find employee(bossId = " + bossId + ").").build();
        }

        // Set the boss object to employee.
        employee.boss = boss;

        // update
        this.em.merge(employee);

        return Response.ok().build();

    }

}

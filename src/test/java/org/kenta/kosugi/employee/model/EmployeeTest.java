package org.kenta.kosugi.employee.model;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.text.ParseException;
import java.util.Calendar;

import static org.junit.Assert.*;

public class EmployeeTest {

    @Test
    public void getFirstName001() {
        var target = new Employee();
        var firstName = target.getFirstName();

        assertEquals(null, firstName);
    }

    @Test
    public void getFirstName002() {
        var target = new Employee();
        target.setFirstName("");
        var firstName = target.getFirstName();

        assertEquals("", firstName);
    }

    @Test
    public void getFirstName003() {
        var target = new Employee();
        target.setFirstName("Kenta");
        var firstName = target.getFirstName();

        assertEquals("Kenta", firstName);
    }

    @Test
    public void getFirstName004() {
        var target = new Employee();
        target.setFirstName("研太");
        var firstName = target.getFirstName();

        assertEquals("研太", firstName);
    }

    @Test
    public void setFirstName() {
        var target = new Employee();

    }

    @Test
    public void getMiddleName() {
    }

    @Test
    public void setMiddleName() {
    }

    @Test
    public void getLastName() {
    }

    @Test
    public void setLastName() {
    }

    @Test
    public void getBoss() {
    }

    @Test
    public void setBoss() {
    }


    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void setHiredDate001() {
        var employee = new Employee();

        try {
            employee.setHiredDate("20191011");
        } catch (ParseException e) {
            fail();
        }
    }

    @Test
    public void setHiredDate002() throws ParseException {
        expectedException.expect(ParseException.class);
        expectedException.expectMessage("Unparseable date: \"Kenta\"");

        var employee = new Employee();

        employee.setHiredDate("Kenta");
    }

    @Test
    public void getHireDate() {
    }

    @Test
    public void setLeavedDate() {
    }

    @Test
    public void getLeavedDate() {
    }
}
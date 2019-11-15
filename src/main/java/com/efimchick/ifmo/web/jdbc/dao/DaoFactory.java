package com.efimchick.ifmo.web.jdbc.dao;

import com.efimchick.ifmo.web.jdbc.ConnectionSource;
import com.efimchick.ifmo.web.jdbc.domain.Department;
import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DaoFactory {

    private Employee employeeMapRow(ResultSet resultSet){
        try {
            BigInteger id = new BigInteger(String.valueOf(resultSet.getInt("ID")));

            FullName fullName = new FullName(
                    resultSet.getString("FIRSTNAME"),
                    resultSet.getString("LASTNAME"),
                    resultSet.getString("MIDDLENAME")
            );

            Position position = Position.valueOf(resultSet.getString("POSITION"));

            LocalDate hiredate = LocalDate.parse(resultSet.getString("HIREDATE"));

            BigDecimal salary = new BigDecimal(String.valueOf(resultSet.getInt("SALARY")));

            BigInteger managerID = BigInteger.valueOf(resultSet.getInt("MANAGER"));

            BigInteger departmentID = BigInteger.valueOf(resultSet.getInt("DEPARTMENT"));

            return new Employee(id, fullName, position, hiredate, salary, managerID, departmentID);
        }
        catch (SQLException e) {
            return null;
        }
    }

    private Department departmentMapRow(ResultSet resultSet) {
        try {
            BigInteger id = BigInteger.valueOf(resultSet.getInt("ID"));

            String name = resultSet.getString("NAME");

            String location = resultSet.getString("LOCATION");

            return new Department(id, name, location);
        }
        catch (SQLException e) {
            return null;
        }
    }

    private List<Employee> getEmployees() throws SQLException{
        List<Employee> allEmployees = new ArrayList<>();

        Connection connection = ConnectionSource.instance().createConnection();
        ResultSet resultset = connection.createStatement().executeQuery("select * from Employee");
        while (resultset.next()){
            Employee emp = employeeMapRow(resultset);
            allEmployees.add(emp);
        }
        return allEmployees;
    }

    private List<Department> getDepartment() throws SQLException{
        List<Department> allDepartments = new ArrayList<>();
        Connection connection = ConnectionSource.instance().createConnection();
        ResultSet resultset = connection.createStatement().executeQuery("select * from DEPARTMENT");
        while (resultset.next()){
            Department dep = departmentMapRow(resultset);
            allDepartments.add(dep);
        }
        return allDepartments;
    }

    public EmployeeDao employeeDAO() throws SQLException {
        List<Employee> employees = getEmployees();
        return new EmployeeDao() {
            @Override
            public List<Employee> getByDepartment(Department department) {
                List<Employee> departments = new ArrayList<>();
                for(Employee emp : employees){

                    if (Objects.equals(emp.getDepartmentId(), department.getId())){
                        departments.add(emp);
                    }
                }
                return departments;
            }

            @Override
            public List<Employee> getByManager(Employee employee) {
                List<Employee> managers = new ArrayList<>();
                for (Employee emp : employees){
                    if(Objects.equals(emp.getManagerId(), employee.getId())){
                        managers.add(emp);
                    }
                }

                return managers;
            }

            @Override
            public Optional<Employee> getById(BigInteger Id) {
                for (Employee emp : employees){
                    if (Objects.equals(emp.getId(), Id)){
                        return Optional.of(emp);
                    }
                }
                return Optional.empty();
            }

            @Override
            public List<Employee> getAll() {
                return employees;
            }

            @Override
            public Employee save(Employee employee) {
                if (employee != null){
                    for (Employee emp : employees){
                        if (Objects.equals(emp.getId(), employee.getId())){
                            employees.remove(emp);
                        }
                    }
                    employees.add(employee);
                }
                return employee;
            }

            @Override
            public void delete(Employee employee) {
                if (employee != null){
                    employees.remove(employee);
                }
            }
        };


    }

    public DepartmentDao departmentDAO() throws SQLException {
        List<Department> departments = getDepartment();
        return new DepartmentDao() {
            @Override
            public Optional<Department> getById(BigInteger Id) {
                for (Department dep : departments){
                    if (Objects.equals(dep.getId(), Id)){
                        return Optional.of(dep);
                    }
                }
                return Optional.empty();
            }
            @Override
            public List<Department> getAll() {
                return departments;
            }

            @Override
            public Department save(Department department) {
                if (department != null){
                    for (Department dep : departments){
                        if (Objects.equals(department.getId(), dep.getId())){
                            departments.remove(dep);
                        }
                    }
                    departments.add(department);
                }
                return department;
            }

            @Override
            public void delete(Department department) {
                departments.remove(department);
            }
        };
    }
}

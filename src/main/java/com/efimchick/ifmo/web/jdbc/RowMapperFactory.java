package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.time.LocalDate;

public class RowMapperFactory {

    public RowMapper<Employee> employeeRowMapper() {
        
        return resultSet -> {
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
                Employee emp = new Employee(id, fullName, position, hiredate, salary);
                return emp;
            }
            catch (SQLException e) {
                return null;
            }
        };
    }
}

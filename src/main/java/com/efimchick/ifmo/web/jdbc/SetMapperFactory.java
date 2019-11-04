package com.efimchick.ifmo.web.jdbc;

import com.efimchick.ifmo.web.jdbc.domain.Employee;
import com.efimchick.ifmo.web.jdbc.domain.FullName;
import com.efimchick.ifmo.web.jdbc.domain.Position;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SetMapperFactory {

    public SetMapper<Set<Employee>> employeesSetMapper() {
        return resultSet -> {
            Set<Employee> ResEmp = new HashSet<>();
            try{
                while (resultSet.next()){
                    Employee emp = mapRow(resultSet);
                    ResEmp.add(emp);
                }
            }
            catch (SQLException e) {
                System.out.println(e.getMessage());

                return null;
            }
            return ResEmp;
        };
    }

    private Employee mapRow(ResultSet resultSet){
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

            Employee manager = null;

            if (resultSet.getString("MANAGER") != null) {
                int  cur = resultSet.getRow();
                int mId = resultSet.getInt("MANAGER");
                resultSet.absolute(0);
                while (resultSet.next()) {
                    if (mId == resultSet.getInt("ID")) {
                        manager = mapRow(resultSet);
                        break;
                    }
                }
                resultSet.absolute(cur);
            }
            return new Employee(id, fullName, position, hiredate, salary, manager);
        }
        catch (SQLException e) {
            return null;
        }
    }
}

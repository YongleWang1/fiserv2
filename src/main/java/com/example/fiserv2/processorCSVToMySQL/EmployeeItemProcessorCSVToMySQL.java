package com.example.fiserv2.processorCSVToMySQL;

import java.io.File;
import java.io.FileWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.fiserv2.Employee;
import com.example.fiserv2.configCSVToMySQL.BatchConfigurationCSVToMySQL;

import java.util.Date;

public class EmployeeItemProcessorCSVToMySQL implements ItemProcessor<Employee, Employee> {
	 
 
	 Date date = new Date();


	@Override
    public Employee process(Employee emp) throws Exception {
    	
        String name = emp.getName();
     
        if(name.equals("")) {
        	
       	 String failedEmployee = "This employee's name is missed: "
       	 +"id: " + emp.getId()
         +", dept_id: " + emp.getDept_id() 
 		 +", manager_id: "+ emp.getManager_id()
 		 +", Today's date is: "+ date.toString() + "\n";
       	 
       	 BatchConfigurationCSVToMySQL.failedEmployees.add(failedEmployee);
        	return null;
        }
        String successfulEmployee = "This employee is processsed successfully: "
        + "id: " + emp.getId() 
        + ", name: " + emp.getName()
        + ", dept_id: " + emp.getDept_id()
        + ", manager_id: "+ emp.getManager_id()
        + ", Today's date is: "+ date.toString() + "\n";
        BatchConfigurationCSVToMySQL.successfulEmployees.add(successfulEmployee);
        return emp;
    }
  
 
}

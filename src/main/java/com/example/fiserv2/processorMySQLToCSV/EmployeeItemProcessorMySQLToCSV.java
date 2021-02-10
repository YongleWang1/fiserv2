package com.example.fiserv2.processorMySQLToCSV;

import org.springframework.batch.item.ItemProcessor;

import com.example.fiserv2.Employee;

public class EmployeeItemProcessorMySQLToCSV implements ItemProcessor<Employee, Employee>{

	@Override
	public Employee process(Employee employee) throws Exception {
		return employee;
	}

}

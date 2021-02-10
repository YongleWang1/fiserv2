package com.example.fiserv2.processorCSVToMySQL;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.fiserv2.Dept;
import com.example.fiserv2.configCSVToMySQL.BatchConfigurationCSVToMySQL;


public class DeptItemProcessorCSVToMySQL implements ItemProcessor<Dept, Dept>{
	 Date date = new Date();
	@Override
	public Dept process(Dept dept) throws Exception {
	      
	         String name = dept.getName();
	        if(name.equals("")) {
	        	String failedDept = "This dept's name is missed: "
	        	       	 +"id: " + dept.getId()
	        	       	 + ", Today's date is: "+ date.toString() + "\n";
	        	BatchConfigurationCSVToMySQL.failedDepts.add(failedDept);
	        	return null;
	        }
	        String successfulDept = "This dept is processsed successfully: "
	                + "id: " + dept.getId() 
	                + ", name: " + dept.getName()
	                + ", Today's date is: "+ date.toString() + "\n";
	        BatchConfigurationCSVToMySQL.successfulDepts.add(successfulDept);
	        return dept;
	}

}

package com.example.fiserv2.Listeners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import com.example.fiserv2.configCSVToMySQL.BatchConfigurationCSVToMySQL;

@Component
public class JobListener implements JobExecutionListener {
	long start = 0;
	@Override
	public void beforeJob(JobExecution jobExecution) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
	}

	@Override
	public void afterJob(JobExecution jobExecution){
		try {
		File failed_file = new File("failed_records.csv");
 	    failed_file.createNewFile();
 	    FileWriter failed_writer = new FileWriter(failed_file);
 	    
		File successful_file = new File("successful_records.csv");
		successful_file.createNewFile(); 
        FileWriter successful_writer = new FileWriter(successful_file);
        
             for(String failedEmployee: BatchConfigurationCSVToMySQL.failedEmployees) {
    	         failed_writer.write(failedEmployee);
                 }
             for(String failedDept: BatchConfigurationCSVToMySQL.failedDepts) {
    	         failed_writer.write(failedDept);
                 }
             for(String failedManager: BatchConfigurationCSVToMySQL.failedManagers) {
    	         failed_writer.write(failedManager);
                 }
             
             for(String successfulEmployee: BatchConfigurationCSVToMySQL.successfulEmployees) {
    	         successful_writer.write(successfulEmployee);
                 }
             for(String successfulDept: BatchConfigurationCSVToMySQL.successfulDepts) {
    	         successful_writer.write(successfulDept);
                 }
             for(String successfulManager: BatchConfigurationCSVToMySQL.successfulManagers) {
    	         successful_writer.write(successfulManager);
                 }
      

             
             Integer failed_num = BatchConfigurationCSVToMySQL.failedDepts.size() 
            		 + BatchConfigurationCSVToMySQL.failedEmployees.size()
            		 + BatchConfigurationCSVToMySQL.failedManagers.size();
             String num_of_failed = "The number of failed records is: " + failed_num.toString();
             failed_writer.write(num_of_failed);
   
             Integer successful_num = BatchConfigurationCSVToMySQL.successfulDepts.size() 
            		 + BatchConfigurationCSVToMySQL.successfulEmployees.size()
            		 + BatchConfigurationCSVToMySQL.successfulManagers.size();
             String num_of_successful = "The number of successful records is: " + successful_num.toString();
             successful_writer.write(num_of_successful);
             
             failed_writer.flush();
             failed_writer.close();
             successful_writer.flush();
             successful_writer.close();
             
             long finish = System.currentTimeMillis();
             long timeElapsed = finish - start;
             
		System.out.println("failed num: "+ failed_num);
		System.out.println("successful num: "+successful_num);
		System.out.println("timeElapsed: "+timeElapsed);
		
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}

}

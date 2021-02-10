package com.example.fiserv2.processorCSVToMySQL;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.example.fiserv2.Manager;
import com.example.fiserv2.configCSVToMySQL.BatchConfigurationCSVToMySQL;

public class ManagerItemProcessorCSVToMySQL implements ItemProcessor<Manager, Manager>{
	 Date date = new Date();
	@Override
	public Manager process(Manager manager) throws Exception {
         String name = manager.getName();
        if(name.equals("")) {
        	String failedManager = "This manager's name is missed: "
        	       	 +"id: " + manager.getId()
        	       	 + ", Today's date is: "+ date.toString() + "\n";
        	BatchConfigurationCSVToMySQL.failedManagers.add(failedManager);
        	return null;
        }
        String successfulManager = "This manager is processsed successfully: "
                + "id: " + manager.getId() 
                + ", name: " + manager.getName()
                + ", Today's date is: "+ date.toString() + "\n";
        BatchConfigurationCSVToMySQL.successfulManagers.add(successfulManager);
        return manager;
	}

	
}

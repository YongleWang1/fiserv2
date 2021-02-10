package com.example.fiserv2.processorMySQLToCSV;

import org.springframework.batch.item.ItemProcessor;

import com.example.fiserv2.Manager;

public class ManagerItemProcessorMySQLToCSV implements ItemProcessor<Manager, Manager>{

	@Override
	public Manager process(Manager manager) throws Exception {
		// TODO Auto-generated method stub
		return manager;
	}

}

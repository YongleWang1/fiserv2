package com.example.fiserv2.processorMySQLToCSV;

import org.springframework.batch.item.ItemProcessor;

import com.example.fiserv2.Dept;

public class DeptItemProcessorMySQLToCSV implements ItemProcessor<Dept, Dept>{

	@Override
	public Dept process(Dept dept) throws Exception {
		// TODO Auto-generated method stub
		return dept;
	}

}

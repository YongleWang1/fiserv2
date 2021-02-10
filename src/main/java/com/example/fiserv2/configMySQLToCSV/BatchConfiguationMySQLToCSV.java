package com.example.fiserv2.configMySQLToCSV;

import java.io.File;
import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.RowMapper;

import com.example.fiserv2.Dept;
import com.example.fiserv2.Employee;
import com.example.fiserv2.Manager;
import com.example.fiserv2.processorMySQLToCSV.DeptItemProcessorMySQLToCSV;
import com.example.fiserv2.processorMySQLToCSV.EmployeeItemProcessorMySQLToCSV;
import com.example.fiserv2.processorMySQLToCSV.ManagerItemProcessorMySQLToCSV;
import com.opencsv.CSVWriter;
import java.io.FileWriter;

@Configuration
@ComponentScan(value = "com.example.fiserv2.configCSVToMySQL")
@EnableBatchProcessing
public class BatchConfiguationMySQLToCSV {
	 @Autowired
	 public JobBuilderFactory jobBuilderFactory;
	 
	 @Autowired
	 public StepBuilderFactory stepBuilderFactory;
	 
	 @Autowired
	 public DataSource dataSource;
	 
	 Date date = new Date();
	 
	ExecutorService es = Executors.newFixedThreadPool(5);	// max 5 threads
	
	List<String> failedEmployees = new ArrayList<String>();
	List<String> successfulEmployees = new ArrayList<String>();
	
	 @Bean
	 public JdbcCursorItemReader<Employee> readerEmployeeMySQLToCSV(){
	  JdbcCursorItemReader<Employee> reader = new JdbcCursorItemReader<Employee>();
	  reader.setDataSource(dataSource);
	  reader.setSql("SELECT id, name, dept_id, manager_id FROM employee");
	  reader.setRowMapper(new UserRowMapperEmployee());
	  
	  return reader;
	 }
	 
	 public class UserRowMapperEmployee implements RowMapper<Employee>{

		  @Override
		  public Employee mapRow(ResultSet rs, int rowNum) throws SQLException {
		   Employee employee = new Employee();
		   employee.setId(rs.getInt("id"));
		   employee.setName(rs.getString("name"));
		   employee.setDept_id(rs.getInt("dept_id"));
		   employee.setManager_id(rs.getInt("manager_id"));
		   return employee;
		  }
		  
		 }
	 
	 @Bean
	 public JdbcCursorItemReader<Dept> readerDeptMySQLToCSV(){
	  JdbcCursorItemReader<Dept> reader = new JdbcCursorItemReader<Dept>();
	  reader.setDataSource(dataSource);
	  reader.setSql("SELECT id, name FROM dept");
	  reader.setRowMapper(new UserRowMapperDept());
	  
	  return reader;
	 }
	 
	 public class UserRowMapperDept implements RowMapper<Dept>{

		  @Override
		  public Dept mapRow(ResultSet rs, int rowNum) throws SQLException {
		   Dept dept = new Dept();
		   dept.setId(rs.getInt("id"));
		   dept.setName(rs.getString("name"));
		   return dept;
		  }
		  
		 }
	 
	 @Bean
	 public JdbcCursorItemReader<Manager> readerManagerMySQLToCSV(){
	  JdbcCursorItemReader<Manager> reader = new JdbcCursorItemReader<Manager>();
	  reader.setDataSource(dataSource);
	  reader.setSql("SELECT id, name FROM manager");
	  reader.setRowMapper(new UserRowMapperManager());
	  
	  return reader;
	 }
	 
	 public class UserRowMapperManager implements RowMapper<Manager>{

		  @Override
		  public Manager mapRow(ResultSet rs, int rowNum) throws SQLException {
		   Manager manager = new Manager();
		   manager.setId(rs.getInt("id"));
		   manager.setName(rs.getString("name"));
		   return manager;
		  }
		  
		 }
	 
	 @Bean
	 public EmployeeItemProcessorMySQLToCSV processorEmployeeMySQLToCSV(){
	  return new EmployeeItemProcessorMySQLToCSV();
	 }
	 @Bean
	 public DeptItemProcessorMySQLToCSV processorDeptMySQLToCSV(){
	  return new DeptItemProcessorMySQLToCSV();
	 }
	 @Bean
	 public ManagerItemProcessorMySQLToCSV processorManagerMySQLToCSV(){
	  return new ManagerItemProcessorMySQLToCSV();
	 }
	 @Bean
	 public FlatFileItemWriter<Employee> writerEmployeeMySQLToCSV(){
			FlatFileItemWriter<Employee> writer = new FlatFileItemWriter<Employee>();
			writer.setResource(new ClassPathResource("employee.csv"));
			
			DelimitedLineAggregator<Employee> lineAggregator = new DelimitedLineAggregator<Employee>();
			lineAggregator.setDelimiter(",");
			
			BeanWrapperFieldExtractor<Employee>  fieldExtractor = new BeanWrapperFieldExtractor<Employee>();
			fieldExtractor.setNames(new String[]{"id","name","dept_id","manager_id"});
			lineAggregator.setFieldExtractor(fieldExtractor);
			
			writer.setLineAggregator(lineAggregator);
			return writer;
	 }
	 @Bean
	 public FlatFileItemWriter<Dept> writerDeptMySQLToCSV(){
			FlatFileItemWriter<Dept> writer = new FlatFileItemWriter<Dept>();
			writer.setResource(new ClassPathResource("dept.csv"));
			
			DelimitedLineAggregator<Dept> lineAggregator = new DelimitedLineAggregator<Dept>();
			lineAggregator.setDelimiter(",");
			
			BeanWrapperFieldExtractor<Dept>  fieldExtractor = new BeanWrapperFieldExtractor<Dept>();
			fieldExtractor.setNames(new String[]{"id","name"});
			lineAggregator.setFieldExtractor(fieldExtractor);
			
			writer.setLineAggregator(lineAggregator);
			return writer;
	 }
	 @Bean
	 public FlatFileItemWriter<Manager> writerManagerMySQLToCSV(){
			FlatFileItemWriter<Manager> writer = new FlatFileItemWriter<Manager>();
			writer.setResource(new ClassPathResource("manager.csv"));
			
			DelimitedLineAggregator<Manager> lineAggregator = new DelimitedLineAggregator<Manager>();
			lineAggregator.setDelimiter(",");
			
			BeanWrapperFieldExtractor<Manager>  fieldExtractor = new BeanWrapperFieldExtractor<Manager>();
			fieldExtractor.setNames(new String[]{"id","name"});
			lineAggregator.setFieldExtractor(fieldExtractor);
			
			writer.setLineAggregator(lineAggregator);
			return writer;
	 }
	 @Bean("step4")
	 public Step step4() {
	  return stepBuilderFactory.get("step4").<Employee, Employee> chunk(200)
	    .reader(readerEmployeeMySQLToCSV())
	    .processor(processorEmployeeMySQLToCSV())
	    .writer(writerEmployeeMySQLToCSV())
	    .build();
	 }
	 @Bean("step5")
	 public Step step5() {
	  return stepBuilderFactory.get("step5").<Dept, Dept> chunk(200)
	    .reader(readerDeptMySQLToCSV())
	    .processor(processorDeptMySQLToCSV())
	    .writer(writerDeptMySQLToCSV())
	    .build();
	 }
	 @Bean("step6")
	 public Step step6() {
	  return stepBuilderFactory.get("step6").<Manager, Manager> chunk(200)
	    .reader(readerManagerMySQLToCSV())
	    .processor(processorManagerMySQLToCSV())
	    .writer(writerManagerMySQLToCSV())
	    .build();
	 }
	 @Bean
	 public Job exportEmployeeJob() throws InterruptedException, ExecutionException {
		 Callable<Job> callable = () -> {
			   return jobBuilderFactory.get("exportEmployeeJob")
	    .incrementer(new RunIdIncrementer())
	    .flow(step4())
	    .end()
	    .build();
		 };
		
	    Future<Job> future = es.submit(callable); 
	    return future.get();
	    
	 }
	 @Bean
	 public Job exportDeptJob() throws InterruptedException, ExecutionException {
		 Callable<Job> callable = () -> {
			   return jobBuilderFactory.get("exportDeptJob")
	    .incrementer(new RunIdIncrementer())
	    .flow(step5())
	    .end()
	    .build();
		 };
		 Future<Job> future = es.submit(callable);
		    return future.get();
	
	 }
	 @Bean
	 public Job exportManagerJob() throws InterruptedException, ExecutionException {
		 Callable<Job> callable = () -> {
			   return jobBuilderFactory.get("exportManagerJob")
	    .incrementer(new RunIdIncrementer())
	    .flow(step6())
	    .end()
	    .build();
		 };
		 Future<Job> future = es.submit(callable);
		    return future.get();
	 }
}

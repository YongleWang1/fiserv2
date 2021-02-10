package com.example.fiserv2.configCSVToMySQL;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.fiserv2.Dept;
import com.example.fiserv2.Employee;
import com.example.fiserv2.Manager;
import com.example.fiserv2.Listeners.JobListener;
import com.example.fiserv2.processorCSVToMySQL.DeptItemProcessorCSVToMySQL;
import com.example.fiserv2.processorCSVToMySQL.EmployeeItemProcessorCSVToMySQL;
import com.example.fiserv2.processorCSVToMySQL.ManagerItemProcessorCSVToMySQL;

import net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.DiscoveryStrategy.Explicit;

@Configuration
@Component
@EnableBatchProcessing
public class BatchConfigurationCSVToMySQL{
	
    @Autowired
    public StepBuilderFactory stepBuilderFactory;
 
    @Autowired
    public JobBuilderFactory jobBuilderFactory;
    
    @Autowired
    public JobListener jobListener;
    
    public static List<String> failedEmployees = new ArrayList<String>();
	public static List<String> successfulEmployees = new ArrayList<String>();
	
    public static List<String> failedDepts = new ArrayList<String>();
	public static List<String> successfulDepts = new ArrayList<String>();
	
    public static List<String> failedManagers = new ArrayList<String>();
	public static List<String> successfulManagers = new ArrayList<String>();
	
	ExecutorService es = Executors.newFixedThreadPool(5);	// max 5 threads
    
    // tag::readerwriterprocessor[]
    @Bean
    public FlatFileItemReader readerEmployeeCSVToMySQL() {
        return new FlatFileItemReaderBuilder()
            .name("EmployeeItemReader")
            .resource(new ClassPathResource("employee.csv"))
            .delimited()
            .names(new String[]{"id", "name", "dept_id", "manager_id"})
            .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
                setTargetType(Employee.class);
            }})
            .build();
    }
 
    @Bean 
   public FlatFileItemReader readerDeptCSVToMySQL() {
    	return new FlatFileItemReaderBuilder()
    			.name("DeptItemReader")
                .resource(new ClassPathResource("dept.csv"))
                .delimited()
                .names(new String[] {"id", "name"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
                    setTargetType(Dept.class);
                }})
                .build();
    }
    
    @Bean 
   public FlatFileItemReader readerManagerCSVToMySQL() {
    	return new FlatFileItemReaderBuilder()
    			.name("ManagerItemReader")
                .resource(new ClassPathResource("manager.csv"))
                .delimited()
                .names(new String[] {"id", "name"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper() {{
                    setTargetType(Manager.class);
                }})
                .build();
    }
    
    @Bean
    public EmployeeItemProcessorCSVToMySQL processorEmployeeCSVToMySQL() {
        return new EmployeeItemProcessorCSVToMySQL();
    }
    
    @Bean
    public DeptItemProcessorCSVToMySQL processorDeptCSVToMySQL() {
        return new DeptItemProcessorCSVToMySQL();
    }
    
    @Bean
    public ManagerItemProcessorCSVToMySQL processorManagerCSVToMySQL() {
        return new ManagerItemProcessorCSVToMySQL();
    }
    
    @Bean("writerEmployeeCSVToMySQL")
    public JdbcBatchItemWriter writerEmployeeCSVToMySQL(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO employee_new (id, name, dept_id, manager_id) VALUES (:id, :name, :dept_id, :manager_id)")
            .dataSource(dataSource)
            .build();
    }
    
    @Bean("writerDeptCSVToMySQL")
    public JdbcBatchItemWriter writerDeptCSVToMySQL(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO dept_new (id, name) VALUES (:id, :name)")
            .dataSource(dataSource)
            .build();
    }
    
    @Bean("writerManagerCSVToMySQL")
    public JdbcBatchItemWriter writerManagerCSVToMySQL(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder()
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .sql("INSERT INTO manager_new (id, name) VALUES (:id, :name)")
            .dataSource(dataSource)
            .build();
    }
    
    // end::readerwriterprocessor[]
 
    // tag::jobstep[]
   
    @Bean
    public Job importEmployeeJob(@Qualifier("step1") Step step1) throws Exception {
    	Callable<Job> callable = () -> {
    		return jobBuilderFactory.get("importEmployeeJob")
    	            .incrementer(new RunIdIncrementer())
    	            .listener(jobListener)
    	            .flow(step1)
    	            .end()
    	            .build();
    	};
        Future<Job> future = es.submit(callable);
        return future.get();
    }
    @Bean
    public Job importDeptJob(@Qualifier("step2") Step step2) throws Exception {
    	Callable<Job> callable = () -> {
    		  return jobBuilderFactory.get("importDeptJob")
            .incrementer(new RunIdIncrementer())
            .listener(jobListener)
            .flow(step2)
            .end()
            .build();
    	};
        Future<Job> future = es.submit(callable);
        return future.get();
    }
    
    @Bean
    public Job importManagerJob(@Qualifier("step3") Step step3) throws Exception {
    	Callable<Job> callable = () -> {
    		 return jobBuilderFactory.get("importManagerJob")
            .incrementer(new RunIdIncrementer())
            .listener(jobListener)
            .flow(step3)
            .end()
            .build();
    	};
        Future<Job> future = es.submit(callable);
        return future.get();
    }
    @Bean("step1")
    public Step step1(@Qualifier("writerEmployeeCSVToMySQL")JdbcBatchItemWriter writerEmployee) {

        return stepBuilderFactory.get("step1")
            .<Employee, Employee> chunk(50)
            .reader(readerEmployeeCSVToMySQL())
            .processor(processorEmployeeCSVToMySQL())
            .writer(writerEmployee)
            .faultTolerant()
            .skipLimit(1)
            .skip(Exception.class)
            .build();
    }
    
    @Bean("step2")
    public Step step2(@Qualifier("writerDeptCSVToMySQL")JdbcBatchItemWriter writerDept) {
    	  
          return   stepBuilderFactory.get("step2")
            .<Dept, Dept> chunk(50)
            .reader(readerDeptCSVToMySQL())
            .processor(processorDeptCSVToMySQL())
            .writer(writerDept)
            .faultTolerant()
            .skipLimit(1)
            .skip(Exception.class)
            .build();
    }
    
    @Bean("step3")
    public Step step3(@Qualifier("writerManagerCSVToMySQL")JdbcBatchItemWriter writerManager) {
    	  
          return   stepBuilderFactory.get("step3")
            .<Manager, Manager> chunk(50)
            .reader(readerManagerCSVToMySQL())
            .processor(processorManagerCSVToMySQL())
            .writer(writerManager)
            .faultTolerant()
            .skipLimit(1)
            .skip(Exception.class)
            .build();
    }
    // end::jobstep[]


}

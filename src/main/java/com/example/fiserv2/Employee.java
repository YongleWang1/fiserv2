package com.example.fiserv2;

public class Employee {

	private int id;
	private String name;
	private int manager_id;
	private int dept_id;
	public Employee() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Employee(int id, String name, int manager_id, int dept_id) {
		super();
		this.id = id;
		this.name = name;
		this.manager_id = manager_id;
		this.dept_id = dept_id;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getManager_id() {
		return manager_id;
	}
	public void setManager_id(int manager_id) {
		this.manager_id = manager_id;
	}
	public int getDept_id() {
		return dept_id;
	}
	public void setDept_id(int dept_id) {
		this.dept_id = dept_id;
	}
	@Override
	public String toString() {
		return "Employee [id=" + id + ", name=" + name + ", manager_id=" + manager_id + ", dept_id=" + dept_id + "]";
	}
	
	
}

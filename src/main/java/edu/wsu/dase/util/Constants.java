package edu.wsu.dase.util;

public class Constants {

	/*Not implemented yet
	*/
	/*public static String[] entityType = { "OBJECTPROPERTY", "DATAPROPERTY", "ANNOTATIONPROPERTY", "DATATYPE", "CLASS",
			"INDIVIDUAL" };*/
	
	public Constants(String a){
		this.a = a;
	}
	String a;
	
	public static void main(String[] args){
		Constants obj1 = new Constants("a");
		Constants obj2 = new Constants("a");
		if(obj1.equals(obj2)){
			System.out.println("equal");
		}else{
			System.out.println("not");
		}
	}
}

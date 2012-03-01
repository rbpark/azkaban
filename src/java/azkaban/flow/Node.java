package azkaban.flow;

public abstract class Node {
	private String id;
	private String name;
	
	public class Param {
		private String name;
		private String description;
		private boolean required;
		private String defaultValue;
		
		public Param(String name, String description, boolean required, String defaultValue) {
			this.name = name;
			this.description = description;
			this.required = required;
			this.defaultValue = defaultValue;
		}
	}
}
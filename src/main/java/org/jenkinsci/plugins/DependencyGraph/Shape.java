package org.jenkinsci.plugins.DependencyGraph;

/**
 * Helper class for graphviz dot file generation that stores information
 * about one module.
 */
public class Shape {
	private String shapeName;
	private String org;
	private String name;
	private String rev;
	private String loc;
	
	/**
	 * Constructs a {@link Shape}.
	 * @param shapeName
	 * 				shape name (for identification)
	 * @param org	
	 * 				organisation
	 * @param name	
	 * 				name
	 * @param rev	
	 * 				revision
	 */
	public Shape(String shapeName, String org, String name, String rev) {
		this.shapeName=shapeName;
		this.org=org;
		this.name=name;
		this.rev=rev;
		this.loc=null;
	}
	
	/**
	 * Constructs a {@link Shape}.
	 * @param shapeName
	 * 				shape name (for identification)
	 * @param org	
	 * 				organisation
	 * @param name	
	 * 				name
	 * @param rev	
	 * 				revision
	 * @param loc	
	 * 				location (uri)
	 */
	public Shape(String shapeName, String org, String name, String rev, String loc) {
		this.shapeName=shapeName;
		this.org=org;
		this.name=name;
		this.rev=rev;
		this.loc=loc;
	}
	
	/**
	 * Tests if location information is available.
	 * @return true if location information is available,
	 * 			false if it is not
	 */
	public boolean hasLoc() {
		if (loc != null) return true;
		return false;
	}
	
	/**
	 * @return organisation
	 */
	public String getOrg() {
		return org;
	}

	/**
	 * @return name of the module
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return location of the module, can be null
	 */
	public String getLoc() {
		return loc;
	}
	
	/**
	 * @return revision
	 */
	public String getRev() {
		return rev;
	}
	
	/**
	 * @return String that identifies the Shape
	 */
	public String getShapeName() {
		return shapeName;
	}
	
	/**
	 * Tests if two Shapes contain identical information.
	 * @param s
	 * 				the Shape to compare against
	 * @return true if information is identical,
	 * 				false if it is not
	 */
	public boolean isEqual(Shape s) {
		if(this.org.equals(s.getOrg()) 
				&& this.name.equals(s.getName()) 
				&& this.rev.equals(s.getRev())) {
			return true;
		}
		return false;
	}
	
	/**
	 * Outputs the dependency information in graphviz dot format.
	 * @return dependency information as a String
	 */
	public String toString() {
		if (this.hasLoc()) {
			return (shapeName + " [shape=box, label=\"" + org + "\n" + name + "\n" + rev 
						+"\", URL=\"" + loc 
						+ "\" target=\"_graphviz\"]");
		}
		return (shapeName + " [shape=box, label=\"" + org + "\n" + name + "\n" + rev +"\"]");
	}
}

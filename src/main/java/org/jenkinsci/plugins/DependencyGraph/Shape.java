package org.jenkinsci.plugins.DependencyGraph;

public class Shape {
	private String shapeName;
	private String org;
	private String name;
	private String rev;
	private String loc;
	
	public Shape(String sn, String o, String n, String r) {
		shapeName=sn;
		org=o;
		name=n;
		rev=r;
		loc=null;
	}
	public Shape(String sn, String o, String n, String r, String l) {
		shapeName=sn;
		org=o;
		name=n;
		rev=r;
		loc=l;
	}
	
	public boolean hasLoc() {
		if (loc != null) return true;
		return false;
	}
	public String getOrg() {
		return org;
	}

	public String getName() {
		return name;
	}

	public String getLoc() {
		return loc;
	}
	
	public String getRev() {
		return rev;
	}
	
	public String getShapeName() {
		return shapeName;
	}
	
	public boolean isEqual(Shape s) {
		if(this.org.equals(s.getOrg()) && this.name.equals(s.getName()) && this.rev.equals(s.getRev())) return true;
		return false;
	}
	
	public String toString() {
		if (this.hasLoc()) {
			return (shapeName + " [shape=box, label=\"" + org + "|" + name + "|" + rev +"\", URL=\"" + loc + "\"]");
		}
		return (shapeName + " [shape=box, label=\"" + org + "|" + name + "|" + rev +"\"]");
	}
}

package org.jenkinsci.plugins.DependencyGraph;

import hudson.model.Action;

/**
 * Keeps the dependency graphs associated with the action.
 */
public final class DependencyGraphAction implements Action {
	private String svg;
	private String jpg;
	private int[] number;
	private String buildDir;
	private String jobName;
	
	/**
	 * Constructs a {@link DependencyGraphAction}.
	 * @param svg
	 * 				location of the dependency graph image (.svg)
	 * @param jpg
	 * 				location of the dependency graph image (.jpg)
	 * @param n
	 * 				n[0]: overall number of dependencies,
	 * 				n[1]: number of direct dependencies
	 */
	public DependencyGraphAction(String svg, String jpg, int[] n, String bd, String jn) {
		super();
		this.svg = svg;
		this.jpg = jpg;
		number = new int[2];
		buildDir = bd;
		jobName = jn;
		System.arraycopy( n, 0, number, 0, 2 );
	}
	
	/**
	 * Copy constructor for {@link DependencyGraphAction}.
	 * @param a
	 * 				an existing DependencyGraphAction
	 */
	public DependencyGraphAction(DependencyGraphAction a) {
		this.svg = a.getSvg();
		this.jpg = a.getJpg();
		this.number = a.getN();
		this.buildDir = a.getBuildDir();
		this.jobName = a.getJobName();
	}
	
	public String getJobName() {
		return jobName;
	}
	
	public String getBuildDir() {
		return buildDir;
	}

	/**
	 * @return location of the dependency graph as an svg image
	 */
	public String getSvg() {
		return svg;
	}
	
	/**
	 * @return location of the dependency graph as a jpg image
	 */
	public String getJpg() {
		return jpg;
	}
	
	/**
	 * @return int array with overall number of dependencies and number of direct dependencies
	 */
	public int[] getN() {
		int[] n = new int[2];
		System.arraycopy( number, 0, n, 0, 2 );
		return n;
	}
	
	/**
	 * @return overall number of dependencies
	 */
	public String getNIndirect() {
		return Integer.toString(number[0]);
	}
	
	/**
	 * @return number of direct dependencies
	 */
	public String getNDirect() {
		return Integer.toString(number[1]);
	}
	
	/**
	 * Gets the action display name.
	 * @return the display name
	 */
	public String getDisplayName() {
		return "Dependency Graph";
	}

	/**
	 * Gets the URL name for this action.
	 * @return the URL name
	 */
	public String getUrlName() {
		return "DependencyGraph";
	}
	
    /**
     * This action doesn't provide an icon file.
     * @return null
     */
    public String getIconFileName() {
        return null;
    }



}

package org.jenkinsci.plugins.DependencyGraph;

import hudson.model.Action;

/**
 * DependencyGraphAction keeps the dependency graphs associated with the action.
 */
public final class DependencyGraphAction implements Action {
	private String svg;
	private String jpg;
	private int[] number;
	
	/**
	 * Constructs a DependencyGraphAction.
	 * @param svg
	 * 				location of the dependency graph image (.svg)
	 * @param jpg
	 * 				location of the dependency graph image (.jpg)
	 * @param n
	 * 				n[0]: overall number of dependencies
	 * 				n[1]: number of direct dependencies
	 */
	public DependencyGraphAction(String svg, String jpg, int[] n) {
		super();
		this.svg = svg;
		this.jpg = jpg;
		number = new int[2];
		System.arraycopy( n, 0, number, 0, 2 );
	}
	
	/**
	 * Copy constructor for DependencyGraphAction.
	 */
	public DependencyGraphAction(DependencyGraphAction a) {
		this.svg = a.getSVG();
		this.jpg = a.getJPG();
		this.number = a.getN();
	}

	public String getSVG() {
		return svg;
	}
	
	public String getJPG() {
		return jpg;
	}
	
	public int[] getN() {
		int[] n = new int[2];
		System.arraycopy( number, 0, n, 0, 2 );
		return n;
	}
	
	public int getNIndirect() {
		return number[0];
	}
	
	public int getNDirect() {
		return number[1];
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
     * This action doesn't provide any icon file.
     * @return null
     */
    public String getIconFileName() {
        return null;
    }



}

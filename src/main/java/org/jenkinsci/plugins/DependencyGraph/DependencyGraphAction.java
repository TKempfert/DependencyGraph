package org.jenkinsci.plugins.DependencyGraph;

import hudson.model.Action;

/**
 * {link RoundhouseAction} keeps the style and fact associated with the action.
 * For more info, please watch <a
 * href="http://www.youtube.com/watch?v=Vb7lnpk3tRY"
 * >http://www.youtube.com/watch?v=Vb7lnpk3tRY</a>
 * @author cliffano
 */
public final class DependencyGraphAction implements Action {
	private String svg;
	private String jpg;
	private int[] number;
	
	/**
	 * Constructs a RoundhouseAction with specified style and fact.
	 * n must contain exactly two ints: 
	 * n[0] = overall number of dependencies
	 * n[1] = number of direct dependencies only
	 */
	public DependencyGraphAction(String svg, String jpg, int[] n) {
		super();
		this.svg = svg;
		this.jpg = jpg;
		number = new int[2];
		System.arraycopy( n, 0, number, 0, 2 );
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
	 * return the display name
	 */
	public String getDisplayName() {
		return "Dependency Graph";
	}

	/**
	 * Gets the URL name for this action.
	 * return the URL name
	 */
	public String getUrlName() {
		return "DependencyGraph";
	}
	
    /**
     * This action doesn't provide any icon file.
     * return null
     */
    public String getIconFileName() {
        return null;
    }



}

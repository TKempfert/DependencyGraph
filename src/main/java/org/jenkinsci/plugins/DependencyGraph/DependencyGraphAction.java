package org.jenkinsci.plugins.DependencyGraph;

import hudson.model.Action;
import java.io.File;

/**
 * Keeps the dependency graphs associated with the action.
 */
public final class DependencyGraphAction implements Action {
	/**
	 * The file name of the dependency graph as an svg image.
	 */
	private String svg;
	
	/**
	 * The file name of the dependency graph as a jpg image.
	 */
	private String jpg;
	
	/** 
	 * The number of dependencies: <br/>
	 * number[0] = all dependencies<br/>
	 * number[1] = direct dependencies only
	 */
	private int[] number;
	
	/**
	 * The build directory relative to the workspace path.
	 */
	private String buildDir;
	
	/**
	 * The name of the Jenkins Job.
	 */
	private String jobName;
	
	private String workspace;
	
	/**
	 * Constructs a {@link DependencyGraphAction}.
	 * @param svg
	 * 				location of the dependency graph image (.svg)
	 * @param jpg
	 * 				location of the dependency graph image (.jpg)
	 * @param n
	 * 				must have two elements:<br>
	 * 				n[0]: overall number of dependencies,
	 * 				n[1]: number of direct dependencies
	 */
	public DependencyGraphAction(String svg, String jpg, int[] n, String ws, String bd, String jn) {
		super();
		this.svg = svg;
		this.jpg = jpg;
		this.number = new int[2];
		this.workspace = ws;
		this.buildDir = bd;
		this.jobName = jn;
		System.arraycopy( n, 0, number, 0, 2 );
	}
	
	public boolean getOk() {
		if ((new File(workspace + "/" + buildDir + "/" + svg)).exists()) return true;
		return false;
	}
	
	/**
	 * @return the name of the Jenkins Job as a String
	 */
	public String getJobName() {
		return jobName;
	}
	
	/**
	 * @return the build directory relative to the workspace path as a String
	 */
	public String getBuildDir() {
		return buildDir;
	}

	/**
	 * @return name of the dependency graph as an svg image
	 */
	public String getSvg() {
		return svg;
	}
	
	/**
	 * @return name of the dependency graph as a jpg image
	 */
	public String getJpg() {
		return jpg;
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

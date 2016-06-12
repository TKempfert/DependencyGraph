package org.jenkinsci.plugins.DependencyGraph;

import hudson.model.Action;
import hudson.model.AbstractProject;

/**
 * Action that gets the dependency graph from the last successful build.
 */
public final class JobGraphAction implements Action {
	private AbstractProject<?, ?> project;
	
	public JobGraphAction(AbstractProject<?, ?> project) {
		super();
		this.project = project;
	}
	
	public String getJpg() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getJpg();
	}
	
	public String getSvg() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getSvg();
	}
	
	public String getJobName() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getJobName();
	}
	
	public String getBuildDir() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getBuildDir();
	}
	
	public String getNIndirect() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getNIndirect();
	}
	
	public String getNDirect() {
		return project.getLastSuccessfulBuild().getAction(DependencyGraphAction.class).getNDirect();
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

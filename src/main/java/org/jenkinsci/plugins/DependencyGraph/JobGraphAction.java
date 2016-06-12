package org.jenkinsci.plugins.DependencyGraph;

import hudson.model.Action;
import hudson.model.AbstractProject;

/**
 * Keeps the dependency graphs associated with the action.
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

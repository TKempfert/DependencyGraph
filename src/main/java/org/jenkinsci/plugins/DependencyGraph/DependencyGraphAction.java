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
	/**
	 * Constructs a RoundhouseAction with specified style and fact.
	 */
	public DependencyGraphAction() {
		super();
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

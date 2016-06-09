package org.jenkinsci.plugins.DependencyGraph;

import hudson.FilePath;
import hudson.tasks.BatchFile;
import hudson.tasks.CommandInterpreter;
import hudson.util.FormValidation;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Builder;
import hudson.tasks.BuildStepDescriptor;
import jenkins.tasks.SimpleBuildStep;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.QueryParameter;
import hudson.model.Result;

import javax.servlet.ServletException;
import java.io.IOException;
import hudson.tasks.Shell;

/*
 * This class executes shell scripts
 */
public class ShellExecutor {

	public static void dotToImages(String path, String file){
		String svgScript = "dot -Tsvg " + path + file + ".dot -o " 
				+ path + file + ".svg ";
		String jpgScript = "dot -Tjpg " + path + file + ".dot -o " 
				+ path + file + ".jpg ";
		System.out.println(svgScript);
		try {
			Runtime.getRuntime().exec(svgScript);
			Runtime.getRuntime().exec(jpgScript);
		} catch (IOException e) {
			System.err.println("crap");
		}
		System.out.println("executed dot in shell");
	}

}


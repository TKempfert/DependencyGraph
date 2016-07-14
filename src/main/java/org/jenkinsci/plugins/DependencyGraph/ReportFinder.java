package org.jenkinsci.plugins.DependencyGraph;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.ListIterator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.nio.charset.Charset;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


/**
 * Finds the xml ivy report and saves location information. 
 */
public class ReportFinder {
		/**
		 * file name of xml ivy report
		 */
		private String reportLocation;
		
		/**
		 * build directory (relative to workspace)
		 */
		private String buildDir;
		
		/**
		 * name of the ivy project (organisation-module)
		 */
		private String projectName;
		
		/**
		 * path to the workspace
		 */
		private String workspace;
		
		/**
		 * Constructs a ReportFinder.
		 * @param workspace
		 * 					directory where the build.xml is stored
		 */
		public ReportFinder(String workspace) {
			reportLocation = null;
			buildDir = null;
			projectName = null;
			this.workspace = workspace;
		}
		
		/**
		 * Searches for the ivy report and saves the path information
		 */
		public void find() {
			try {
			projectName = findModuleName(workspace + "/ivy.xml");
			buildDir = findBuildDir(workspace, "/build.xml");
			reportLocation = findReportLocation(workspace + "/" + buildDir, projectName);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
		/**
		 * @return String representation of the build directory (relative to workspace)
		 */
		public String getBuildDir() {
			if (buildDir == null) find();
			return buildDir;
		}
		
		/**
		 * @return String: file name of the ivy report
		 */
		public String getReportLocation() {
			if (reportLocation == null) find();
			return reportLocation;
		}
		
		/**
		 * @return project name as a String
		 */
		public String getModuleName() {
			if (projectName == null) find();
			return projectName;
		}
	
		/**
		 * Finds the location of the xml ivy report.
		 * @param path
		 * 				directory where the report can be found
		 * @param projectName
		 * 				the name of the project
		 * @return location of the report as a String
		 */
		private static String findReportLocation(String path, String projectName) {
			String file = "";
			
			File root = new File(path);
			String regex = Pattern.quote(projectName) + ".*\\.xml";
			
			File[] matching;
			if(!root.isDirectory()) {
			        throw new IllegalArgumentException(root+" is no directory.");
			}
			final Pattern p = Pattern.compile(regex); // careful: could also throw an exception!   
			
			matching = root.listFiles(new FileFilter(){
			        @Override
			        public boolean accept(File file) {
			            return p.matcher(file.getName()).matches();
			        }
			    });
			if (matching != null && matching.length == 1) {
				file = matching[0].toString();
			}
			return file;
		}
	
		/**
		 * Finds the build directory.
		 * @param workspace
		 * 					path to workspace as a String
		 * @param buildXML
		 * 					path to build.xml as a String (relative to workspace)
		 * @return the build directory (relative to workspace)
		 */
		private static String findBuildDir(String workspace, String buildXML) {
			String buildDir = "";
			String name;

			try {			
				File inputFile = new File(workspace + "/" + buildXML);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("project");
				assert(nList.getLength() == 1);

				Element project = (Element) nList.item(0);

				// Traverse list of all targets to find report target
				NodeList targets = project.getElementsByTagName("target");
				for (int m = 0; m < targets.getLength(); m++) {

					Element target = (Element) targets.item(m);
					name = target.getAttribute("name");

					// Check if it is the correct target with name="report" attribute
					if (name.equals("report")) {
						NodeList reports = target.getElementsByTagName("ivy:report");
						assert(nList.getLength()==1);
						Element report = (Element) reports.item(0);
						buildDir = report.getAttribute("todir");
						
						// check if buildDir is a variable
						if ((Pattern.compile("\\$\\{.*\\}")).matcher(buildDir).find()) {							
							buildDir = buildDir.replace("${", "").replace("}", "");
							// find corresponding value
							NodeList properties = project.getElementsByTagName("property");
							for (int n = 0; n < properties.getLength(); n++) {
								Element property = (Element)properties.item(n);
								if (buildDir.equals(property.getAttribute("name"))) {
									buildDir = property.getAttribute("value");
								}
							}
						}
					}
				}

			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (SAXException se) {
				se.printStackTrace();
			}
			return buildDir;
		}
	

	/**
	 * Reads organisation and module name from ivy.xml and concatenates them as 
	 * org-module. This forms part of the name of the ivy report file.
	 * @param ivyXML
	 * 				complete path to the ivy.xml as a String
	 * @return ivy project name as a String
	 */
	private static String findModuleName(String ivyXML) {
		String pname = "";

		try {			
			File inputFile = new File(ivyXML);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("info");
			assert(nList.getLength() == 1);

			Element project = (Element) nList.item(0);
			pname = project.getAttribute("organisation") + "-" + project.getAttribute("module");

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		}

		return pname;
	}
	
}
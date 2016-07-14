package org.jenkinsci.plugins.DependencyGraph;

import java.io.BufferedWriter;
import java.io.File;
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

/**
 * Extracts information from an xml ivy report.
 */
public class IvyReportParser {
	/**
	 * Extracts the dependency tree from an xml ivy report and generates
	 * a graphviz dot file.
	 * @param input
	 * 				input file
	 * @param output
	 * 				output file
	 * @param indirect
	 * 				true: all dependencies are resolved <br>
	 * 				false: only direct dependencies are resolved
	 * @return int array with overall number of dependencies and number of
	 * 				direct dependencies
	 */
	public static int[] xmlToDot(String input, String output, boolean indirect) {
		String org;
		String name;
		String rev;
		String loc;

		String callerOrg;
		String callerName;
		String callerRev;

		String shapeName;

		Shape compareShape = null;
		boolean isDirect = true;
		boolean containsDirect = false;

		//Overall number of dependencies (different revisions are counted separately)
		int nIndirect = 0;
		
		//Number of direct dependencies (different revisions are counted separately)
		int nDirect = 0;

		try {			
			ArrayList<Shape> shapeL = new ArrayList<Shape>();
			ListIterator<Shape> it;

			File outputFile = new File(output);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), Charset.defaultCharset()));

			bw.write("digraph G {");
			bw.newLine();

			try {
				File inputFile = new File(input);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(inputFile);
				doc.getDocumentElement().normalize();

				// Get info for root element to later test against
				// -> tell if dependency is direct or indirect
				NodeList infoL = doc.getElementsByTagName("info");
				if (infoL.getLength() > 0) {
					Element info = (Element) infoL.item(0);
					org = info.getAttribute("organisation");
					name = info.getAttribute("module");
					rev = info.getAttribute("revision");
					compareShape = new Shape("root", org, name, rev);
				}

				NodeList nList = doc.getElementsByTagName("dependencies");
				assert(nList.getLength() == 1);

				Shape tempShape;
				Shape tempCaller;
				Shape temp;
				boolean exists = false;

				Element dependencies = (Element) nList.item(0);

				// Traverse list of all modules to extract info,
				// different revisions and caller info
				NodeList modules = dependencies.getElementsByTagName("module");
				for (int m = 0; m < modules.getLength(); m++) {

					Element module = (Element) modules.item(m);
					org = module.getAttribute("organisation");
					name = module.getAttribute("name");

					NodeList revList = module.getElementsByTagName("revision");
					for(int r = 0; r < revList.getLength(); r++) {
						Element revision = (Element) revList.item(r);
						rev = revision.getAttribute("name");

						shapeName = "shape" + shapeL.size();
						// Look if the source location of the module is known
						NodeList meta = revision.getElementsByTagName("metadata-artifact");
						if (meta.getLength() > 0) {
							loc = ((Element)meta.item(0)).getAttribute("origin-location");
							loc = loc.replace("\\", "/");
							tempShape = new Shape(shapeName, org, name, rev, loc);
						}
						else {
							tempShape = new Shape(shapeName, org, name, rev);
						}

						// Each module should be added to shapeL only once:
						// check whether module already exists
						it = shapeL.listIterator();
						while(it.hasNext()) {
							temp = it.next();
							if (temp.isEqual(tempShape)) {
								tempShape = temp;
								exists = true;
								break;
							}	
						}
						if (!exists) {
							shapeL.add(tempShape);
							nIndirect++;}
						exists = false;

						containsDirect = false;

						// Traverse list of all callers and write dependency information
						// into graphviz file
						NodeList callers = revision.getElementsByTagName("caller");
						for (int c = 0; c < callers.getLength(); c++) {
							Element caller = (Element) callers.item(c);
							callerOrg = caller.getAttribute("organisation");
							callerName = caller.getAttribute("name");
							callerRev = caller.getAttribute("callerrev");

							// Test if it is a direct dependency
							if (compareShape.isEqual(new Shape("vgl",callerOrg,callerName,callerRev))) {
								isDirect = true;
								containsDirect = true;
								nDirect++;
							}
							else isDirect = false;

							// Add new dependency if it is direct or if indirect dependencies should also be shown
							if (indirect || isDirect) {
								shapeName = "shape" + shapeL.size();
								tempCaller = new Shape(shapeName, callerOrg, callerName, callerRev);
								it = shapeL.listIterator();
								while(it.hasNext()) {
									temp = it.next();
									if (temp.isEqual(tempCaller)) {
										tempCaller = temp;
										exists = true;
										break;
									}	
								}
								if (!exists) {shapeL.add(tempCaller);}
								exists = false;
								bw.write(tempCaller.getShapeName() + " -> " + tempShape.getShapeName());
								bw.newLine();
							}
						}
						// If only direct dependencies are considered
						// and a module is not called by the root element
						// then remove it from the list of modules
						if (!indirect && !containsDirect) {
							shapeL.remove(tempShape);
						}
					}
				}
				// Write display info for the dependencies into the file
				it = shapeL.listIterator();
				while(it.hasNext()) {
					bw.write(it.next().toString());
					bw.newLine();
				}
			} catch (NullPointerException np) {
				np.printStackTrace();
			}
			bw.write("}");
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		}

		return (new int[] {nIndirect, nDirect});
	}

}

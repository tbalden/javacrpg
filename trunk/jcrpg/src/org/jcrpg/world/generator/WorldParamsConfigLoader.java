/*
 *  This file is part of JavaCRPG.
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jcrpg.world.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
/**
 * 
 * @author eburriel@yahoo.com
 *
 */
public class WorldParamsConfigLoader {

	public static String DEFAULT_CONFIG_FILENAME = "./data/ai/world/default_worldparams.xml";
	/**
	 * DOM model of the config file.
	 */
	Document mainConfigDocument;

	/**
	 * Load the DOM from an InputStream.
	 * 
	 * @param is .
	 * @return The DOM.
	 */
	void loadDocument(InputStream is) {

		Document vReturnValue = null;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder builder = dbfactory.newDocumentBuilder();

			mainConfigDocument = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	void loadDefaultDocument() {
		InputStream is = null;
		try {
			is = new FileInputStream(DEFAULT_CONFIG_FILENAME);
			this.loadDocument(is);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public WorldParams getWorldParams() {
		loadDefaultDocument();
		WorldParams vReturnValue = new WorldParams();
		loadSizeParams(vReturnValue);
		loadRandom(vReturnValue);
		loadClimats(vReturnValue);
		loadGeology(vReturnValue);
		return vReturnValue;
	}

	public WorldParams getWorldParams(InputStream is) {
		loadDocument(is);
		WorldParams vReturnValue = new WorldParams();
		loadSizeParams(vReturnValue);
		loadRandom(vReturnValue);
		loadClimats(vReturnValue);
		loadGeology(vReturnValue);
		return vReturnValue;
	}
	
	public WorldParams getWorldParams(File file) {
		
		InputStream is = null;
		try {
			new FileInputStream(file);
			this.loadDocument(is);
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		WorldParams vReturnValue = new WorldParams();
		loadSizeParams(vReturnValue);
		loadRandom(vReturnValue);
		loadClimats(vReturnValue);
		loadGeology(vReturnValue);
		return vReturnValue;
	}
	private void loadRandom(WorldParams pWorldParams) {
		Node randomNode = null;

		randomNode = mainConfigDocument.getElementsByTagName("random").item(0);
		NamedNodeMap attributes = randomNode.getAttributes();

		pWorldParams.setRandomSeed(Integer.parseInt(attributes.getNamedItem(
				"seed").getNodeValue()));

	}

	private void loadSizeParams(WorldParams pWorldParams) {
		Node paramNode = null;

		paramNode = mainConfigDocument.getElementsByTagName("size-params")
				.item(0);
		NamedNodeMap attributes = paramNode.getAttributes();

		pWorldParams.setSizeX(Integer.parseInt(attributes.getNamedItem("sizeX")
				.getNodeValue()));
		pWorldParams.setSizeY(Integer.parseInt(attributes.getNamedItem("sizeY")
				.getNodeValue()));
		pWorldParams.setSizeZ(Integer.parseInt(attributes.getNamedItem("sizeZ")
				.getNodeValue()));

		pWorldParams.setMagnification(Integer.parseInt(attributes.getNamedItem(
			"magnification").getNodeValue()));
		pWorldParams.setGeoNormalSize(Integer.parseInt(attributes.getNamedItem(
			"magnification").getNodeValue()));

		pWorldParams.setHeightRatio(Float.parseFloat(attributes.getNamedItem(
				"heightRatio").getNodeValue()));
		
		pWorldParams.setGeoNormalSize(Integer.parseInt(attributes.getNamedItem(
			"normalSize").getNodeValue()));
	}

	/**
	 * 
	 * @param pWorldParams
	 */
	private void loadGeology(WorldParams pWorldParams) {
		Node geologyNode = null;
		Node mainStreamNode = null;
		Node additionalNode = null;

		geologyNode = mainConfigDocument.getElementsByTagName("geology")
				.item(0);
		NodeList geologyChildrenNodeList = geologyNode.getChildNodes();

		for (int indice = 0; indice < geologyChildrenNodeList.getLength(); indice++) {
			short nodeType = geologyChildrenNodeList.item(indice).getNodeType();

			if (nodeType != Node.ELEMENT_NODE) {
				continue;
			}
			String nodeName = geologyChildrenNodeList.item(indice)
					.getNodeName();
			if (nodeName.equals("main-stream")) {
				mainStreamNode = geologyChildrenNodeList.item(indice);
			} else if (nodeName.equals("additionnal")) {
				additionalNode = geologyChildrenNodeList.item(indice);
			}

		}
		//
		NamedNodeMap attributesGeology = geologyNode.getAttributes();
		pWorldParams.setFoundationGeo(attributesGeology.getNamedItem(
				"foundation-id").getNodeValue());
		pWorldParams.setLandDensity(Integer.parseInt(attributesGeology
				.getNamedItem("landDensity").getNodeValue()));
		pWorldParams.setLandMass(Integer.parseInt(attributesGeology
				.getNamedItem("landMass").getNodeValue()));
		// *********************** Main Stream
		// ************************************
		{
			NodeList mainStreamChildrenNodeList = mainStreamNode
					.getChildNodes();

			List<String> msGeoRef = new ArrayList<String>();
			List<Integer> msGeoRefLikeness = new ArrayList<Integer>();

			for (int indice = 0; indice < mainStreamChildrenNodeList
					.getLength(); indice++) {
				short nodeType = mainStreamChildrenNodeList.item(indice)
						.getNodeType();

				if (nodeType != Node.ELEMENT_NODE) {
					continue;
				}
				String nodeName = mainStreamChildrenNodeList.item(indice)
						.getNodeName();
				if (nodeName.equals("geo")) {
					Node currentGeoNode = mainStreamChildrenNodeList
							.item(indice);
					NamedNodeMap attributesCurrentGeoNode = currentGeoNode
							.getAttributes();
					msGeoRef.add(attributesCurrentGeoNode
							.getNamedItem("ref-id").getNodeValue());
					msGeoRefLikeness.add(new Integer(attributesCurrentGeoNode
							.getNamedItem("likeness").getNodeValue()));
				}

			}
			String geos[] = new String[msGeoRef.size()];
			pWorldParams.setGeos(msGeoRef.toArray(geos));
			int geosLikeness[] = new int[msGeoRefLikeness.size()];
			int indice = 0;
			for (Integer value : msGeoRefLikeness) {
				geosLikeness[indice++] = value.intValue();
			}
			pWorldParams.setGeoLikenessValues(geosLikeness);
		}

		// *********************** Additional
		// ************************************
		{
			NodeList childrenNodeList = additionalNode.getChildNodes();

			List<String> msGeoRef = new ArrayList<String>();
			List<Integer> msGeoRefLikeness = new ArrayList<Integer>();

			for (int indice = 0; indice < childrenNodeList.getLength(); indice++) {
				short nodeType = childrenNodeList.item(indice).getNodeType();

				if (nodeType != Node.ELEMENT_NODE) {
					continue;
				}
				String nodeName = childrenNodeList.item(indice).getNodeName();
				if (nodeName.equals("geo")) {
					Node currentGeoNode = childrenNodeList.item(indice);
					NamedNodeMap attributesCurrentGeoNode = currentGeoNode
							.getAttributes();
					msGeoRef.add(attributesCurrentGeoNode
							.getNamedItem("ref-id").getNodeValue());
					msGeoRefLikeness.add(new Integer(attributesCurrentGeoNode
							.getNamedItem("likeness").getNodeValue()));
				}

			}
			String geos[] = new String[msGeoRef.size()];
			pWorldParams.setAdditionalGeos(msGeoRef.toArray(geos));
			int geosLikeness[] = new int[msGeoRefLikeness.size()];
			int indice = 0;
			for (Integer value : msGeoRefLikeness) {
				geosLikeness[indice++] = value.intValue();
			}
			pWorldParams.setAdditionalGeoLikenessValues(geosLikeness);
		}

	}

	/**
	 * 
	 * @param pWorldParams
	 */
	private void loadClimats(WorldParams pWorldParams) {
		Node climatsRefNode = null;

		climatsRefNode = mainConfigDocument.getElementsByTagName("climats-ref")
				.item(0);

		{
			NodeList childrenNodeList = climatsRefNode.getChildNodes();

			List<String> msClimatRef = new ArrayList<String>();
			List<Integer> msClimatSizeMuls = new ArrayList<Integer>();

			for (int indice = 0; indice < childrenNodeList.getLength(); indice++) {
				short nodeType = childrenNodeList.item(indice).getNodeType();

				if (nodeType != Node.ELEMENT_NODE) {
					continue;
				}
				String nodeName = childrenNodeList.item(indice).getNodeName();
				if (nodeName.equals("climatBelt")) {
					Node currentNode = childrenNodeList.item(indice);
					NamedNodeMap attributesCurrentNode = currentNode
							.getAttributes();
					msClimatRef.add(attributesCurrentNode.getNamedItem("id")
							.getNodeValue());
					msClimatSizeMuls.add(new Integer(attributesCurrentNode
							.getNamedItem("sizeMuls").getNodeValue()));
				}
			}
			String climates[] = new String[msClimatRef.size()];
			pWorldParams.setClimates(msClimatRef.toArray(climates));
			int climatSizeMuls[] = new int[msClimatSizeMuls.size()];
			int indice = 0;
			for (Integer value : msClimatSizeMuls) {
				climatSizeMuls[indice++] = value.intValue();
			}
			pWorldParams.setClimateSizeMuls(climatSizeMuls);
		}

	}

}

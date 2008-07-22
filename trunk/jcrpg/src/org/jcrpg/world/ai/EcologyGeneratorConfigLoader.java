package org.jcrpg.world.ai;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.impl.arctic.Arctic;
import org.jcrpg.world.climate.impl.continental.Continental;
import org.jcrpg.world.climate.impl.desert.Desert;
import org.jcrpg.world.climate.impl.tropical.Tropical;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * Helper that loads config file (XML) for generate EcologyGenerator config.
 * 
 * @author eburriel@yahoo.com
 * @author $Author$
 * @version $Revision$
 * 
 */
public class EcologyGeneratorConfigLoader {

	/**
	 * DOM model of the config file.
	 */
	Document mainConfigDocument;

	/**
	 * Load the DOM from an InpputStream.
	 * 
	 * @param is .
	 * @return The DOM.
	 */
	Document loadDocument(InputStream is) {
		Document vReturnValue = null;
		try {
			DocumentBuilderFactory dbfactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder builder = dbfactory.newDocumentBuilder();

			mainConfigDocument = builder.parse(is);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vReturnValue;
	}

	/**
	 * Obtain climatRefs.
	 * 
	 * @return .
	 */
	Map<String, Class<? extends ClimateBelt>> loadClimatRef() {
		Map<String, Class<? extends ClimateBelt>> vReturn = new HashMap<String, Class<? extends ClimateBelt>>();

		vReturn.put("Arctic", Arctic.class);
		vReturn.put("Continental", Continental.class);
		vReturn.put("Desert", Desert.class);
		vReturn.put("Tropical", Tropical.class);
		return vReturn;
	}

	boolean usePredatorMode() {
		try{
			NodeList nl = mainConfigDocument.getElementsByTagName("ecology");
			Node n = nl.item(0);
			NamedNodeMap map = n.getAttributes();
			Node att =map.getNamedItem("predator-mode");
			String value = att.getNodeValue();
		if (value.equalsIgnoreCase("true")) {
			return true;
		} else {
			return false;
		}
		}
		catch(Exception e)
		{
			return false;
		}

	}

	/**
	 * Get the bestiaire
	 * 
	 * @return
	 */
	Map<String, Class<? extends EntityDescription>> loadBestiary() {
		Map<String, Class<? extends EntityDescription>> bestiary = new HashMap<String, Class<? extends EntityDescription>>();

		NodeList animals = mainConfigDocument.getElementsByTagName("bestiary")
				.item(0).getChildNodes();
		for (int indice = 0; indice < animals.getLength(); indice++) {
			short nodeType = animals.item(indice).getNodeType();
			// explorer les attributs de chaque animal
			if (nodeType == Node.ELEMENT_NODE) {
				NamedNodeMap attributes = animals.item(indice).getAttributes();
				String id = attributes.getNamedItem("id").getNodeValue();
				String implemetation = attributes
						.getNamedItem("implementation").getNodeValue();

				System.out.println(implemetation);
				try {
					bestiary.put(id, (Class<? extends EntityDescription>) Class
							.forName(implemetation));
				} catch (ClassNotFoundException cnfe) {

					cnfe.printStackTrace();
				}
			}

		}

		return bestiary;
	}

	Collection<EcologyGeneratorPopulation> loadPopulations(
			Map<String, Class<? extends EntityDescription>> bestiary,
			Map<String, Class<? extends ClimateBelt>> climatsRef) {

		Collection<EcologyGeneratorPopulation> vReturn = new ArrayList<EcologyGeneratorPopulation>();
		NodeList populations = mainConfigDocument.getElementsByTagName(
				"populations").item(0).getChildNodes();
		for (int indice = 0; indice < populations.getLength(); indice++) {
			try {
				short nodeType = populations.item(indice).getNodeType();
				// explorer les attributs de chaque population
				if (nodeType != Node.ELEMENT_NODE) {
					continue;
				}

				// explorer les attributs de chaque population
				NamedNodeMap attributes = populations.item(indice)
						.getAttributes();
				String refAnimalId = attributes.getNamedItem("ref-animal-id")
						.getNodeValue();
				Class<? extends EntityDescription> entityDescriptionClass = bestiary
						.get(refAnimalId);
				String refClimat = attributes.getNamedItem("ref-climat")
						.getNodeValue();
				Class<? extends ClimateBelt> climatBeltClass = climatsRef
						.get(refClimat);
				String chanceOfAppearance = "25";
				try {
					chanceOfAppearance = attributes.getNamedItem(
							"percentage-chance-of-appearance").getNodeValue();
				} catch (Exception e) {
					chanceOfAppearance = "25";
				}
				EcologyGeneratorPopulation egp = new EcologyGeneratorPopulation();
				egp.setClimatBeltClass(climatBeltClass);
				egp.setEntityClass(entityDescriptionClass);
				egp.setPercentageChanceofAppearance(new Integer(
						chanceOfAppearance));
				// options
				try {
					egp.setPrefixName(attributes.getNamedItem("prefix-name")
							.getNodeValue());
				} catch (Exception e) {
				}
				try {
					egp.setMaxInWorld(Integer.parseInt(attributes.getNamedItem(
							"max-in-world").getNodeValue()));
				} catch (Exception e) {
				}
				try {
					egp.setMinInGroup(Integer.parseInt(attributes.getNamedItem(
							"min-in-group").getNodeValue()));
				} catch (Exception e) {
				}

				try {
					egp.setMaxInGroup(Integer.parseInt(attributes.getNamedItem(
							"max-in-group").getNodeValue()));
				} catch (Exception e) {
				}

				try {
					egp.setPredatorOnFoodPercentage(Integer.parseInt(attributes
							.getNamedItem("predator-on-food-percentage")
							.getNodeValue()));
				} catch (Exception e) {
				}
				try {
					String position = attributes.getNamedItem(
							"food-chain-position").getNodeValue();
					EcologyGeneratorPopulation.FoodChainType positionFCT = EcologyGeneratorPopulation.FoodChainType
							.valueOf(

							position);
					egp.setPositionInFoodChain(positionFCT);

				} catch (Exception e) {

				}

				try {
					egp.setPredatorRange(Integer.parseInt(attributes
							.getNamedItem("predator-range").getNodeValue()));

				} catch (Exception e) {
				}

				vReturn.add(egp);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return vReturn;
	}

}

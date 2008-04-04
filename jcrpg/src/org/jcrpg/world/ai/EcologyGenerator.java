/*
 *  This file is part of JavaCRPG.
 *  Copyright (C) 2008 Illes Pal Zoltan
 *
 *  JavaCRPG is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JavaCRPG is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jcrpg.world.ai;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jcrpg.util.HashUtil;
import org.jcrpg.world.ai.fauna.AnimalEntityDescription;
import org.jcrpg.world.climate.ClimateBelt;
import org.jcrpg.world.climate.CubeClimateConditions;
import org.jcrpg.world.place.World;
import org.jcrpg.world.time.Time;

/**
 * Ecology Generator.
 * Can be used in two modes:
 * predator : use AnimalDescriptionFood Entities and predatorOnFoodPercentage:
 * non-predator : use predatorHuntingNeeds
 * @author eburriel@yahoo.com
 * 
 */
public class EcologyGenerator {

	public static Logger LOGGER = Logger.getLogger(EcologyGenerator.class
			.getName());

	public static String DEFAULT_CONFIG_FILENAME = "./data/ai/ecology/default_ecology.xml";
	/**
	 * Specify generation.
	 */
	boolean predatorMode = true;
	/**
	 * Key is an Key for EntityDescription. Value is canonicalClassName of
	 * EntityDescription;
	 */
	Map<String, Class<? extends EntityDescription>> bestiary;

	/**
	 * 
	 */
	Map<String, Class<? extends ClimateBelt>> climatRefs;

	Collection<EcologyGeneratorPopulation> populations;

	Map<Class<? extends EntityDescription>, Integer> entityCreationStats;

	public EcologyGenerator() {
		LOGGER.setLevel(Level.FINEST);
		init(null);
	}

	public EcologyGenerator(InputStream is) {
		LOGGER.setLevel(Level.FINEST);
		init(is);
	}

	private void init(InputStream is) {
		InputStream vIs = is;
		if (is == null) {
			try {
				is = new FileInputStream(new File(DEFAULT_CONFIG_FILENAME));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		EcologyGeneratorConfigLoader loader = new EcologyGeneratorConfigLoader();
		loader.loadDocument(is);
		this.predatorMode = loader.usePredatorMode();
		bestiary = loader.loadBestiary();
		climatRefs = loader.loadClimatRef();
		populations = loader.loadPopulations(bestiary, climatRefs);

		entityCreationStats = new HashMap<Class<? extends EntityDescription>, Integer>();
	}

	private void addCreationStat(Class<? extends EntityDescription> aclass) {
		Integer counter = entityCreationStats.get(aclass);
		if (counter != null) {
			entityCreationStats.put(aclass, new Integer(counter + 1));
		} else {
			entityCreationStats.put(aclass, new Integer(1));
		}
	}

	private void logCreationStats() {
		LOGGER
				.fine("-------------------------logCreationStats()----------------------------- ");

		for (Class<? extends EntityDescription> vClass : entityCreationStats
				.keySet()) {
			LOGGER.fine("Entity " + vClass.getSimpleName() + ":"
					+ entityCreationStats.get(vClass) + " groups.");

		}
		LOGGER
				.fine("------------------------------------------------------------------------ ");
	}

	/**
	 * Generate an Ecology for the World.
	 * (http://fr.wikipedia.org/wiki/Chaine_alimentaire) To simplify Animal
	 * World can be divided : 
	 * Herbivore : eat plant 
	 * Primary Carnivore primaire : eat herbivore 
	 * Secondary Carnivore : eat Primary carnivore
	 * 
	 * @param world
	 * @return
	 * @throws Exception
	 */
	public Ecology generateEcology(World pWorld) throws Exception {
		Ecology vEcology = new Ecology(pWorld.engine);
		HuntingMap vHerbivoresHuntingMap = generateHerbivores(vEcology, pWorld);
		if( predatorMode )
		{
			HuntingMap vPrimaryCarnivoresHuntingMap = generatePredatorPrimaryCarnivores(
					vEcology, pWorld, vHerbivoresHuntingMap);
			HuntingMap vSecondaryCarnivoresHuntingMap = generatePredatorSecondaryCarnivores(
					vEcology, pWorld, vPrimaryCarnivoresHuntingMap);	
		}
		else
		{
			HuntingMap vPrimaryCarnivoresHuntingMap = generatePrimaryCarnivores(
					vEcology, pWorld, vHerbivoresHuntingMap);
			HuntingMap vSecondaryCarnivoresHuntingMap = generateSecondaryCarnivores(
					vEcology, pWorld, vPrimaryCarnivoresHuntingMap);	
		}
		
		HuntingMap vOthersHuntingMap = generateNotInFoodChain(vEcology, pWorld);
		logCreationStats();
		return vEcology;
	}

	/**
	 * Generate herbivore.
	 * 
	 * @param pWorld
	 * @return
	 * @throws Exception
	 */
	public HuntingMap generateHerbivores(Ecology pEcology, World pWorld)
			throws Exception {

		HuntingMap results = new HuntingMap();

		Collection<EcologyGeneratorPopulation> herbivorePopulations = EcologyGeneratorPopulation
				.extractThoseOfType(populations,
						EcologyGeneratorPopulation.FoodChainType.HERBIVORE);

		int nX = 20;
		int nY = 20;

		int currentIndex = 0;
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				int wX = 1 + (int) ((pWorld.realSizeX * 1f / nX) * i);
				int wY = pWorld.getSeaLevel(1);
				int wZ = (int) ((pWorld.realSizeZ * 1f / nY) * j);

				// get climatic conditions of the current position
				CubeClimateConditions ccc = pWorld.getCubeClimateConditions(
						new Time(), wX, wY, wZ, false);
				Class<? extends ClimateBelt> beltClass = ccc.belt.getClass();

				for (EcologyGeneratorPopulation population : herbivorePopulations) {
					if (beltClass.equals(population.getClimatBeltClass())) {
						int rollPresent = HashUtil.mixPercentage(wX, wY, wZ
								+ population.hashCode());

						if (rollPresent <= population
								.getPercentageChanceofAppearance()) {
							EntityDescription desc = (EntityDescription) population
									.getEntityClass().newInstance();
							int id = pEcology.getNextEntityId();
							String entityId = population.getPrefixName() + "#"
									+ (currentIndex++);
							// determine the number of members in the group

							int min = population.getMinInGroup();
							int numberInTheGroup = min;
							int max = population.getMaxInGroup();
							int ecart = (max - min);
							if (ecart > 0) {
								int rollNb = HashUtil.mixPercentage(wX, wY
										+ population.hashCode(), wZ);
								numberInTheGroup += ((ecart * rollNb) / 100);
							}
							EntityInstance entity = new EntityInstance(desc,
									pWorld, pEcology, id, entityId,
									numberInTheGroup, wX, wY, wZ);
							PositionInTheWorld pitw = new PositionInTheWorld(
									wX, wY, wZ);
							results.put(pitw, entity);
							pEcology.addEntity(entity);
							LOGGER.finest("addEntity " + entityId + " (" + wX
									+ "," + wY + "," + wZ + ") nb="
									+ numberInTheGroup);
							population.incrementsNumberOfGroupsInWorld();
							addCreationStat(population.getEntityClass());

						}
					}
				}
			}
		}

		return results;
	}

	public HuntingMap generateNotInFoodChain(Ecology pEcology, World pWorld)
			throws Exception {

		HuntingMap results = new HuntingMap();

		Collection<EcologyGeneratorPopulation> herbivorePopulations = EcologyGeneratorPopulation
				.extractThoseOfType(populations, null);

		int nX = 20;
		int nY = 20;

		int currentIndex = 0;
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				int wX = 1 + (int) ((pWorld.realSizeX * 1f / nX) * i);
				int wY = pWorld.getSeaLevel(1);
				int wZ = (int) ((pWorld.realSizeZ * 1f / nY) * j);

				// get climatic conditions of the current position
				CubeClimateConditions ccc = pWorld.getCubeClimateConditions(
						new Time(), wX, wY, wZ, false);
				Class<? extends ClimateBelt> beltClass = ccc.belt.getClass();

				for (EcologyGeneratorPopulation population : herbivorePopulations) {
					if (beltClass.equals(population.getClimatBeltClass())) {
						int rollPresent = HashUtil.mixPercentage(wX, wY, wZ
								+ population.hashCode());

						if (rollPresent <= population
								.getPercentageChanceofAppearance()) {
							EntityDescription desc = (EntityDescription) population
									.getEntityClass().newInstance();
							int id = pEcology.getNextEntityId();
							String entityId = population.getPrefixName() + "#"
									+ (currentIndex++);
							// determine the number of members in the group

							int min = population.getMinInGroup();
							int numberInTheGroup = min;
							int max = population.getMaxInGroup();
							int ecart = (max - min);
							if (ecart > 0) {
								int rollNb = HashUtil.mixPercentage(wX, wY
										+ population.hashCode(), wZ);
								numberInTheGroup += ((ecart * rollNb) / 100);
							}
							EntityInstance entity = new EntityInstance(desc,
									pWorld, pEcology, id, entityId,
									numberInTheGroup, wX, wY, wZ);
							PositionInTheWorld pitw = new PositionInTheWorld(
									wX, wY, wZ);
							results.put(pitw, entity);
							pEcology.addEntity(entity);
							LOGGER.finest("addEntity " + entityId + " (" + wX
									+ "," + wY + "," + wZ + ") nb="
									+ numberInTheGroup);
							population.incrementsNumberOfGroupsInWorld();
							addCreationStat(population.getEntityClass());

						}
					}
				}
			}
		}

		return results;
	}

	/**
	 * 
	 * @param pEcology
	 * @param pWorld
	 * @param herbivoresHuntingMap
	 * @return
	 * @throws Exception
	 */
	public HuntingMap generatePredatorSecondaryCarnivores(Ecology pEcology,
			World pWorld, HuntingMap pHuntingMap) throws Exception {

		HuntingMap vHuntingMap = new HuntingMap(pHuntingMap);

		HuntingMap results = new HuntingMap();

		Collection<EcologyGeneratorPopulation> secondaryCarnivoresPopulations = EcologyGeneratorPopulation
				.extractThoseOfType(
						populations,
						EcologyGeneratorPopulation.FoodChainType.SECONDARY_CARNIVORE);

		int nX = 20;
		int nY = 20;

		int currentIndex = 0;
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				int wX = 1 + (int) ((pWorld.realSizeX * 1f / nX) * i);
				int wY = pWorld.getSeaLevel(1);
				int wZ = (int) ((pWorld.realSizeZ * 1f / nY) * j);

				PositionInTheWorld currentPos = new PositionInTheWorld(wX, wY,
						wZ);
				// get climatic conditions of the current position
				CubeClimateConditions ccc = pWorld.getCubeClimateConditions(
						new Time(), wX, wY, wZ, false);
				Class<? extends ClimateBelt> beltClass = ccc.belt.getClass();

				for (EcologyGeneratorPopulation population : secondaryCarnivoresPopulations) {
					if (beltClass.equals(population.getClimatBeltClass())) {

						EntityDescription desc = (EntityDescription) population
								.getEntityClass().newInstance();
						if (desc instanceof AnimalEntityDescription) {
							AnimalEntityDescription animalDesc = (AnimalEntityDescription) desc;

							Collection<EntityInstance> huntingTargets = vHuntingMap
									.getEntitiesInHuntingRangeFromPosition(
											currentPos, population
													.getPredatorRange());

							Collection<EntityInstance> approvedHuntingTargets = new ArrayList<EntityInstance>();
							// extract only species in foodEntities
							int countTargets = 0;
							for (EntityInstance huntingTarget : huntingTargets) {
								for (Class vClass : animalDesc
										.getFoodEntities()) {
									String vCurrentClass = vClass.getCanonicalName();
									String vHuntingClass = vClass.getCanonicalName();
									if (vCurrentClass.equals(vHuntingClass))
										{
										approvedHuntingTargets
												.add(huntingTarget);
										countTargets = countTargets
												+ huntingTarget.numberOfMembers;
										break;
									}
								}
							}
							// count number of targets
							int maxPossiblePredators = (countTargets * population
									.getPredatorOnFoodPercentage()) / 100;

							int id = pEcology.getNextEntityId();
							String entityId = population.getPrefixName() + "#"
									+ (currentIndex++);
							// determine the number of members in the group

							int min = population.getMinInGroup();
							int numberInTheGroup = min;
							int max = population.getMaxInGroup();
							if (max > maxPossiblePredators) {
								max = maxPossiblePredators;
							}
							int ecart = (max - min);
							if (ecart > 0) {
								int rollNb = HashUtil.mixPercentage(wX, wY
										+ population.hashCode(), wZ);
								numberInTheGroup += ((ecart * rollNb) / 100);
							}
							EntityInstance entity = new EntityInstance(desc,
									pWorld, pEcology, id, entityId,
									numberInTheGroup, wX, wY, wZ);
							PositionInTheWorld pitw = new PositionInTheWorld(
									wX, wY, wZ);
							results.put(pitw, entity);
							pEcology.addEntity(entity);
							LOGGER.finest("addEntity " + entityId + " (" + wX
									+ "," + wY + "," + wZ + ") nb="
									+ numberInTheGroup + " with "
									+ huntingTargets.size()
																	+ " hunting target group(s) of " +countTargets
									+" members in total maxPossiblePredators="+maxPossiblePredators);
							population.incrementsNumberOfGroupsInWorld();
							addCreationStat(population.getEntityClass());
							// delete targets from huntingMap
							// numberInTheGroup *
							// population.getPredatorOnFoodPercentage() in
							// huntingMap
							// TODO
						}

					}
				}
			}
		}
		return results;
	}
	
	
	/**
	 * 
	 * @param pEcology
	 * @param pWorld
	 * @param herbivoresHuntingMap
	 * @return
	 * @throws Exception
	 */
	public HuntingMap generatePredatorPrimaryCarnivores(Ecology pEcology,
			World pWorld, HuntingMap pHerbivoresHuntingMap) throws Exception {

		HuntingMap herbivoresHuntingMap = new HuntingMap(pHerbivoresHuntingMap);

		HuntingMap results = new HuntingMap();

		Collection<EcologyGeneratorPopulation> primaryCarnivoresPopulations = EcologyGeneratorPopulation
				.extractThoseOfType(
						populations,
						EcologyGeneratorPopulation.FoodChainType.PRIMARY_CARNIVORE);

		int nX = 20;
		int nY = 20;

		int currentIndex = 0;
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				int wX = 1 + (int) ((pWorld.realSizeX * 1f / nX) * i);
				int wY = pWorld.getSeaLevel(1);
				int wZ = (int) ((pWorld.realSizeZ * 1f / nY) * j);

				PositionInTheWorld currentPos = new PositionInTheWorld(wX, wY,
						wZ);
				// get climatic conditions of the current position
				CubeClimateConditions ccc = pWorld.getCubeClimateConditions(
						new Time(), wX, wY, wZ, false);
				Class<? extends ClimateBelt> beltClass = ccc.belt.getClass();

				for (EcologyGeneratorPopulation population : primaryCarnivoresPopulations) {
					if (beltClass.equals(population.getClimatBeltClass())) {

						EntityDescription desc = (EntityDescription) population
								.getEntityClass().newInstance();
						if (desc instanceof AnimalEntityDescription) {
							AnimalEntityDescription animalDesc = (AnimalEntityDescription) desc;

							if( animalDesc.getFoodEntities().size() == 0)
							{
								LOGGER.finest("no FoodEntities for " + animalDesc.getClass() );
								break;
							}
							Collection<EntityInstance> huntingTargets = herbivoresHuntingMap
									.getEntitiesInHuntingRangeFromPosition(
											currentPos, population
													.getPredatorRange());

							Collection<EntityInstance> approvedHuntingTargets = new ArrayList<EntityInstance>();
							// extract only species in foodEntities
							int countTargets = 0;
							for (EntityInstance huntingTarget : huntingTargets) {
								for (Class vClass : animalDesc
										.getFoodEntities()) {
									
									String vCurrentClass = vClass.getCanonicalName();
									String vHuntingClass = vClass.getCanonicalName();
									if (vCurrentClass.equals(vHuntingClass)) {
										approvedHuntingTargets
												.add(huntingTarget);
										countTargets = countTargets
												+ huntingTarget.numberOfMembers;
										break;
									}
								}
							}
							// count number of targets
							int maxPossiblePredators = (countTargets * population
									.getPredatorOnFoodPercentage()) / 100;

							int id = pEcology.getNextEntityId();
							String entityId = population.getPrefixName() + "#"
									+ (currentIndex++);
							// determine the number of members in the group

							int min = population.getMinInGroup();
							int numberInTheGroup = min;
							int max = population.getMaxInGroup();
							if (max > maxPossiblePredators) {
								max = maxPossiblePredators;
							}
							int ecart = (max - min);
							if (ecart > 0) {
								int rollNb = HashUtil.mixPercentage(wX, wY
										+ population.hashCode(), wZ);
								numberInTheGroup += ((ecart * rollNb) / 100);
							}
							EntityInstance entity = new EntityInstance(desc,
									pWorld, pEcology, id, entityId,
									numberInTheGroup, wX, wY, wZ);
							PositionInTheWorld pitw = new PositionInTheWorld(
									wX, wY, wZ);
							results.put(pitw, entity);
							pEcology.addEntity(entity);
							LOGGER.finest("addEntity " + entityId + " (" + wX
									+ "," + wY + "," + wZ + ") nb="
									+ numberInTheGroup + " with "
									+ huntingTargets.size()
									+ " hunting target group(s) of " +countTargets +" members in total maxPossiblePredators="+maxPossiblePredators);
							population.incrementsNumberOfGroupsInWorld();
							addCreationStat(population.getEntityClass());
							// delete targets from huntingMap
							// numberInTheGroup *
							// population.getPredatorOnFoodPercentage() in
							// huntingMap
							// TODO
						}

					}
				}
			}
		}
		return results;
	}

	public HuntingMap generatePrimaryCarnivores(Ecology pEcology, World pWorld,
			HuntingMap herbivoresHuntingMap) throws Exception {

		HuntingMap results = new HuntingMap();

		Collection<EcologyGeneratorPopulation> primaryCarnivoresPopulations = EcologyGeneratorPopulation
				.extractThoseOfType(
						populations,
						EcologyGeneratorPopulation.FoodChainType.PRIMARY_CARNIVORE);

		int nX = 20;
		int nY = 20;

		int currentIndex = 0;
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				int wX = 1 + (int) ((pWorld.realSizeX * 1f / nX) * i);
				int wY = pWorld.getSeaLevel(1);
				int wZ = (int) ((pWorld.realSizeZ * 1f / nY) * j);

				PositionInTheWorld currentPos = new PositionInTheWorld(wX, wY,
						wZ);
				// get climatic conditions of the current position
				CubeClimateConditions ccc = pWorld.getCubeClimateConditions(
						new Time(), wX, wY, wZ, false);
				Class<? extends ClimateBelt> beltClass = ccc.belt.getClass();

				for (EcologyGeneratorPopulation population : primaryCarnivoresPopulations) {
					if (beltClass.equals(population.getClimatBeltClass())) {
						int rollPresent = HashUtil.mixPercentage(wX, wY, wZ
								+ population.hashCode());

						if (rollPresent <= population
								.getPercentageChanceofAppearance()) {
							// check if there are enough of herbivores in the
							// range

							Collection<EntityInstance> huntingTargets = herbivoresHuntingMap
									.getEntitiesInHuntingRangeFromPosition(
											currentPos, population
													.getPredatorRange());
							if ((huntingTargets == null)
									|| (huntingTargets.size() < population
											.getPredatorHuntingNeeds())) {

								break;
							}

							EntityDescription desc = (EntityDescription) population
									.getEntityClass().newInstance();
							int id = pEcology.getNextEntityId();
							String entityId = population.getPrefixName() + "#"
									+ (currentIndex++);
							// determine the number of members in the group

							int min = population.getMinInGroup();
							int numberInTheGroup = min;
							int max = population.getMaxInGroup();
							int ecart = (max - min);
							if (ecart > 0) {
								int rollNb = HashUtil.mixPercentage(wX, wY
										+ population.hashCode(), wZ);
								numberInTheGroup += ((ecart * rollNb) / 100);
							}
							EntityInstance entity = new EntityInstance(desc,
									pWorld, pEcology, id, entityId,
									numberInTheGroup, wX, wY, wZ);
							PositionInTheWorld pitw = new PositionInTheWorld(
									wX, wY, wZ);
							results.put(pitw, entity);
							pEcology.addEntity(entity);
							LOGGER.finest("addEntity " + entityId + " (" + wX
									+ "," + wY + "," + wZ + ") nb="
									+ numberInTheGroup + " with "
									+ huntingTargets.size()
									+ " hunting target(s)");
							population.incrementsNumberOfGroupsInWorld();
							addCreationStat(population.getEntityClass());

						}
					}
				}
			}
		}

		return results;
	}

	public HuntingMap generateSecondaryCarnivores(Ecology pEcology,
			World pWorld, HuntingMap primaryHuntingMap) throws Exception {

		HuntingMap results = new HuntingMap();

		Collection<EcologyGeneratorPopulation> secondaryCarnivoresPopulations = EcologyGeneratorPopulation
				.extractThoseOfType(
						populations,
						EcologyGeneratorPopulation.FoodChainType.SECONDARY_CARNIVORE);

		int nX = 20;
		int nY = 20;

		int currentIndex = 0;
		for (int i = 0; i < nX; i++) {
			for (int j = 0; j < nY; j++) {
				int wX = 1 + (int) ((pWorld.realSizeX * 1f / nX) * i);
				int wY = pWorld.getSeaLevel(1);
				int wZ = (int) ((pWorld.realSizeZ * 1f / nY) * j);

				PositionInTheWorld currentPos = new PositionInTheWorld(wX, wY,
						wZ);
				// get climatic conditions of the current position
				CubeClimateConditions ccc = pWorld.getCubeClimateConditions(
						new Time(), wX, wY, wZ, false);
				Class<? extends ClimateBelt> beltClass = ccc.belt.getClass();

				for (EcologyGeneratorPopulation population : secondaryCarnivoresPopulations) {
					if (beltClass.equals(population.getClimatBeltClass())) {
						int rollPresent = HashUtil.mixPercentage(wX, wY, wZ
								+ population.hashCode());

						if (rollPresent <= population
								.getPercentageChanceofAppearance()) {
							// check if there are enough of herbivores in the
							// range

							Collection<EntityInstance> huntingTargets = primaryHuntingMap
									.getEntitiesInHuntingRangeFromPosition(
											currentPos, population
													.getPredatorRange());
							if ((huntingTargets == null)
									|| (huntingTargets.size() < population
											.getPredatorHuntingNeeds())) {

								break;
							}

							EntityDescription desc = (EntityDescription) population
									.getEntityClass().newInstance();
							int id = pEcology.getNextEntityId();
							String entityId = population.getPrefixName() + "#"
									+ (currentIndex++);
							// determine the number of members in the group

							int min = population.getMinInGroup();
							int numberInTheGroup = min;
							int max = population.getMaxInGroup();
							int ecart = (max - min);
							if (ecart > 0) {
								int rollNb = HashUtil.mixPercentage(wX, wY
										+ population.hashCode(), wZ);
								numberInTheGroup += ((ecart * rollNb) / 100);
							}
							EntityInstance entity = new EntityInstance(desc,
									pWorld, pEcology, id, entityId,
									numberInTheGroup, wX, wY, wZ);
							PositionInTheWorld pitw = new PositionInTheWorld(
									wX, wY, wZ);
							results.put(pitw, entity);
							pEcology.addEntity(entity);
							LOGGER.finest("addEntity " + entityId + " (" + wX
									+ "," + wY + "," + wZ + ") nb="
									+ numberInTheGroup + " with "
									+ huntingTargets.size()
									+ " hunting target(s)");
							population.incrementsNumberOfGroupsInWorld();
							addCreationStat(population.getEntityClass());

						}
					}
				}
			}
		}

		return results;
	}

	/**
	 * Generate an Ecology for the World.
	 * (http://fr.wikipedia.org/wiki/Chaine_alimentaire) To simplify Animal
	 * World can be divided : Herbivore : eat plant Carnivore primaire : eat
	 * herbivore Carnivore secondaire : eat carnivore primaire
	 * 
	 * @param pWorld
	 * @return
	 * @throws Exception
	 */

	public Map<String, Class<? extends EntityDescription>> getBestiary() {
		return bestiary;
	}

	public void setBestiary(
			Map<String, Class<? extends EntityDescription>> bestiary) {
		this.bestiary = bestiary;
	}

	public class PositionInTheWorld {
		int X;
		int Y;
		int Z;

		PositionInTheWorld(int pX, int pY, int pZ) {
			X = pX;
			Y = pY;
			Z = pZ;
		}

		/**
		 * @return the x
		 */
		public int getX() {
			return X;
		}

		/**
		 * @param x
		 *            the x to set
		 */
		public void setX(int x) {
			X = x;
		}

		/**
		 * @return the y
		 */
		public int getY() {
			return Y;
		}

		/**
		 * @param y
		 *            the y to set
		 */
		public void setY(int y) {
			Y = y;
		}

		/**
		 * @return the z
		 */
		public int getZ() {
			return Z;
		}

		/**
		 * @param z
		 *            the z to set
		 */
		public void setZ(int z) {
			Z = z;
		}

		public int getXZ_LDistanceFrom(PositionInTheWorld pos) {
			int resultX = pos.getX() - this.getX();
			if (resultX < 0)
				resultX = -resultX;
			int resultY = pos.getY() - this.getY();
			if (resultY < 0)
				resultY = -resultY;
			return resultX + resultY;
		}

	}

	public class HuntingMap {
		Map<PositionInTheWorld, EntityInstance> huntingMap = new HashMap<PositionInTheWorld, EntityInstance>();

		public HuntingMap() {
			super();
		}

		public HuntingMap(HuntingMap hm) {
			super();
			for (PositionInTheWorld pos : hm.huntingMap.keySet()) {
				this.put(pos, hm.huntingMap.get(pos));
			}
		}

		public void put(PositionInTheWorld pos, EntityInstance entity) {
			huntingMap.put(pos, entity);
		}

		/**
		 * Get Entities in range. range is calculated in L mode.
		 * 
		 * @param position
		 * @param range
		 * @return
		 */
		public Collection<EntityInstance> getEntitiesInHuntingRangeFromPosition(
				PositionInTheWorld pPosition, int pRange) {
			Collection<EntityInstance> result = new ArrayList<EntityInstance>();

			for (PositionInTheWorld pos : huntingMap.keySet()) {
				if (pos.getXZ_LDistanceFrom(pPosition) < pRange) {
					result.add(huntingMap.get(pos));
				}
			}

			return result;
		}

	}

}

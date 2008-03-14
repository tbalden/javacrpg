package org.jcrpg.world.ai;

import java.util.ArrayList;
import java.util.Collection;

import org.jcrpg.world.climate.ClimateBelt;

public class EcologyGeneratorPopulation {
	/**
	 * See a sample of food web http://www.eelsinc.org/id64.html
	 */	
	enum FoodChainType { HERBIVORE, PRIMARY_CARNIVORE, SECONDARY_CARNIVORE };
	
	Class<? extends EntityDescription> entityClass;
	
	Class<? extends ClimateBelt> climatBeltClass;
	
	Integer percentageChanceofAppearance;
	
	String prefixName;
	
	Integer maxInGroup=40;
	
	Integer minInGroup=2;
	
	Integer currentNumberOfGroupsInWorld=0;
	
	Integer maxInWorld=200;

	FoodChainType positionInFoodChain;
	
	Integer predatorHuntingNeeds = 2;
	
	/**
	 * Range for hunting.
	 */
	Integer predatorRange = 2; 
	
	/**
	 * @return the positionInFoodChain
	 */
	public FoodChainType getPositionInFoodChain() {
		return positionInFoodChain;
	}

	/**
	 * @param positionInFoodChain the positionInFoodChain to set
	 */
	public void setPositionInFoodChain(FoodChainType positionInFoodChain) {
		this.positionInFoodChain = positionInFoodChain;
	}

	/**
	 * @return the predatorRange
	 */
	public Integer getPredatorRange() {
		return predatorRange;
	}

	/**
	 * @param predatorRange the predatorRange to set
	 */
	public void setPredatorRange(Integer predatorRange) {
		predatorRange = predatorRange;
	}

	/**
	 * @return the entityClass
	 */
	public Class<? extends EntityDescription> getEntityClass() {
		return entityClass;
	}

	public void incrementsNumberOfGroupsInWorld()
	{
		currentNumberOfGroupsInWorld++;
	}
	
	/**
	 * @param entityClass the entityClass to set
	 */
	public void setEntityClass(Class<? extends EntityDescription> entityClass) {
		this.entityClass = entityClass;
		if(prefixName == null)
		{
			prefixName = this.entityClass.getSimpleName();
		}
	}

	/**
	 * @return the climatBeltClass
	 */
	public Class<? extends ClimateBelt> getClimatBeltClass() {
		return climatBeltClass;
	}

	/**
	 * @param climatBeltClass the climatBeltClass to set
	 */
	public void setClimatBeltClass(Class<? extends ClimateBelt> climatBeltClass) {
		this.climatBeltClass = climatBeltClass;
	}

	/**
	 * @return the percentageChanceofAppearance
	 */
	public Integer getPercentageChanceofAppearance() {
		if( currentNumberOfGroupsInWorld>= maxInWorld)
			return 0;
		
		return percentageChanceofAppearance;
	}

	/**
	 * @param percentageChanceofAppearance the percentageChanceofAppearance to set
	 */
	public void setPercentageChanceofAppearance(Integer percentageChanceofAppearance) {
		this.percentageChanceofAppearance = percentageChanceofAppearance;
	}

	/**
	 * @return the prefixName
	 */
	public String getPrefixName() {
		return prefixName;
	}

	/**
	 * @param prefixName the prefixName to set
	 */
	public void setPrefixName(String prefixName) {
		this.prefixName = prefixName;
	}

	/**
	 * @return the maxInGroup
	 */
	public Integer getMaxInGroup() {
		return maxInGroup;
	}

	/**
	 * @param maxInGroup the maxInGroup to set
	 */
	public void setMaxInGroup(Integer maxInGroup) {
		this.maxInGroup = maxInGroup;
	}

	/**
	 * @return the minInGroup
	 */
	public Integer getMinInGroup() {
		return minInGroup;
	}

	/**
	 * @param minInGroup the minInGroup to set
	 */
	public void setMinInGroup(Integer minInGroup) {
		this.minInGroup = minInGroup;
	}

	/**
	 * @return the currentNumberOfGroupsInWorld
	 */
	public Integer getCurrentNumberOfGroupsInWorld() {
		return currentNumberOfGroupsInWorld;
	}

	/**
	 * @param currentNumberOfGroupsInWorld the currentNumberOfGroupsInWorld to set
	 */
	public void setCurrentNumberOfGroupsInWorld(Integer currentNumberOfGroupsInWorld) {
		this.currentNumberOfGroupsInWorld = currentNumberOfGroupsInWorld;
	}

	/**
	 * @return the maxInWorld
	 */
	public Integer getMaxInWorld() {
		return maxInWorld;
	}

	/**
	 * @param maxInWorld the maxInWorld to set
	 */
	public void setMaxInWorld(Integer maxInWorld) {
		this.maxInWorld = maxInWorld;
	}

	public static Collection<EcologyGeneratorPopulation> extractThoseOfType( Collection<EcologyGeneratorPopulation> collec , FoodChainType pType)
	{
		Collection<EcologyGeneratorPopulation> result = new ArrayList<EcologyGeneratorPopulation>(); 
		if(collec != null)
		{
			for(EcologyGeneratorPopulation eco : collec)
			{
				if(pType==null)
				{
					if(eco.getPositionInFoodChain() == null)
					{
						result.add(eco);
					}
					
				}
				else if(pType.equals(eco.getPositionInFoodChain()))
				{
					result.add(eco);
				}
			}
		}
		return result;
	}

	/**
	 * @return the predatorHuntingNeeds
	 */
	public Integer getPredatorHuntingNeeds() {
		return predatorHuntingNeeds;
	}

	/**
	 * @param predatorHuntingNeeds the predatorHuntingNeeds to set
	 */
	public void setPredatorHuntingNeeds(Integer predatorHuntingNeeds) {
		this.predatorHuntingNeeds = predatorHuntingNeeds;
	}
	
}

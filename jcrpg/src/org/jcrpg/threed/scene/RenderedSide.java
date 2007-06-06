package org.jcrpg.threed.scene;

public class RenderedSide {

	public String[] modelName, textureName;
	
	public RenderedSide(String[] modelName, String[] textureName)
	{
		this.modelName = modelName;
		this.textureName = textureName;		
	}
	public RenderedSide(String modelName, String textureName)
	{
		this.modelName = new String[]{modelName};
		this.textureName = new String[]{textureName};		
	}
	
}

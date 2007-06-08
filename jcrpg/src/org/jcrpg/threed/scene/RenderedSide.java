package org.jcrpg.threed.scene;


public class RenderedSide {

	
	public SimpleModel[] objects;
	
	public RenderedCube parent = null;
	
	public RenderedSide(SimpleModel[] objects)
	{
		this.objects = objects;
	}
	public RenderedSide(String modelName, String textureName)
	{
		objects = new SimpleModel[] {new SimpleModel(modelName,textureName)};
	}
	
	
	
	
	public RenderedCube getParent() {
		return parent;
	}
	public void setParent(RenderedCube parent) {
		this.parent = parent;
	}
	
	
	
}

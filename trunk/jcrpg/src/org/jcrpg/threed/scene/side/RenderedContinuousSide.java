package org.jcrpg.threed.scene.side;

import org.jcrpg.threed.scene.model.SimpleModel;

public class RenderedContinuousSide extends RenderedSide{

	public SimpleModel[] continuous;
	public SimpleModel[] oneSideContinuousNormal;
	public SimpleModel[] oneSideContinuousOpposite;
	public SimpleModel[] nonContinuous;
	
	public RenderedContinuousSide(SimpleModel[] objects, SimpleModel[] continuous, SimpleModel[] oneSideContinuousNormal,SimpleModel[] oneSideContinuousOpposite,SimpleModel[] nonContinuous)
	{
		super(objects);
		this.continuous = continuous;
		this.nonContinuous = nonContinuous;
		this.oneSideContinuousNormal = oneSideContinuousNormal;
		this.oneSideContinuousOpposite = oneSideContinuousOpposite;
		type = RS_CONTINUOUS;
	}
	
}

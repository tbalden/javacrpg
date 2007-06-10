package org.jcrpg.threed.input.action;

import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public abstract class CKeyAction extends KeyInputAction{

	long timeStart = 0;
	
	static long TIME_TO_ENSURE = 10; 
	
	Camera camera;
	ClassicKeyboardLookHandler handler;
	public CKeyAction(ClassicKeyboardLookHandler handler, Camera cam)
	{
		this.handler = handler;
		camera = cam;		
	}
	
	protected void ensureTimeStart()
	{
		timeStart = System.currentTimeMillis();	
	}
	protected void ensureTimeStop()
	{
        if (timeStart+TIME_TO_ENSURE>System.currentTimeMillis())
        {
        	try{
        		Thread.sleep((timeStart+TIME_TO_ENSURE)-System.currentTimeMillis());
        	} catch (Exception ex)
        	{
        		
        	}
        }
	}
	protected void moveDirection(float steps, Vector3f from, Vector3f toReach)
	{
    	for (float i=0; i<=steps; i++)
        {
    		ensureTimeStart();
    		float x, y, z;
    		x = (1/steps)* i * toReach.x;
    		y = (1/steps)* i * toReach.y;
    		z = (1/steps)* i * toReach.z;
    		
    		x += (1/steps) * (steps-i) * from.x;
    		y += (1/steps) * (steps-i) * from.y;
    		z += (1/steps) * (steps-i) * from.z;
    		
    		camera.setDirection(new Vector3f(x,y,z));
    		
            camera.update();
            handler.core.updateCam();
            ensureTimeStop();
    
        }
		
	}
	
	protected void movePosition(float steps, Vector3f from, Vector3f toReach)
	{
    	for (float i=0; i<=steps; i++)
        {
    		ensureTimeStart();
    		float x, y, z;
    		x = (1/steps)* i * toReach.x;
    		y = (1/steps)* i * toReach.y;
    		z = (1/steps)* i * toReach.z;
    		
    		x += (1/steps) * (steps-i) * from.x;
    		y += (1/steps) * (steps-i) * from.y;
    		z += (1/steps) * (steps-i) * from.z;
    		
    		camera.setLocation(new Vector3f(x,y,z));
    		
            camera.update();
            handler.core.updateCam();
            ensureTimeStop();
    
        }
		
	}
	
}

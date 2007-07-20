package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.KeyInputAction;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public abstract class CKeyAction extends KeyInputAction{

	long timeStart = 0;
	
	static long TIME_TO_ENSURE = J3DCore.TIME_TO_ENSURE; 
	
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
	protected float ensureTimeStop()
	{
        if (timeStart+TIME_TO_ENSURE>System.currentTimeMillis())
        {
        	try{
        		Thread.sleep((timeStart+TIME_TO_ENSURE)-System.currentTimeMillis());
        	} catch (Exception ex)
        	{
        		
        	}
        	return 0;
        }
        return ((System.currentTimeMillis()-timeStart)/(TIME_TO_ENSURE*1f))-1f;
	}
	protected void turnDirection(float steps, Vector3f from, Vector3f toReach)
	{
		turnDirection(steps, from, toReach,false);
	}
	
	//	temporary matrix to hold rotation
    //private static final Matrix3f incr = new Matrix3f();
    
    protected void turnDirection(float steps, Vector3f from, Vector3f toReach, boolean almost)
	{
		float skipStep = 0f;
    	for (float i=0; i<=steps; i++)
        {
    		if (almost && i==0) continue;
    		if (almost && i==steps) continue;
    		ensureTimeStart();
    		float x, y, z;
    		x = (1/steps)* i * toReach.x;
    		y = (1/steps)* i * toReach.y;
    		z = (1/steps)* i * toReach.z;
    		
    		x += (1/steps) * (steps-i) * from.x;
    		y += (1/steps) * (steps-i) * from.y;
    		z += (1/steps) * (steps-i) * from.z;

/*            incr.fromAngleNormalAxis(-90/steps, camera.getUp());
            incr.mult(camera.getUp(), camera.getUp());
            incr.mult(camera.getLeft(), camera.getLeft());
            incr.mult(camera.getDirection(), camera.getDirection());*/
    		
    		camera.setDirection(new Vector3f(x,y,z));
    		// TODO !!!
    		camera.setLeft(new Vector3f(0,0,0));
    		//camera.setUp(new Vector3f(0,1,0));
    		
            camera.update();
            //camera.normalize();
            handler.core.updateDisplay(from);
            skipStep+= ensureTimeStop();
            if (skipStep>1f) {
            	i+=(int)skipStep;
            	skipStep=0f;
            }
            if (i>steps) break;
    
        }
		
	}

    
    protected void turnDirection(Vector3f from, Vector3f toReach, int percent)
	{
   		//ensureTimeStart();
		float x, y, z;
		x = (1 / 100f) * percent * toReach.x;
		y = (1 / 100f) * percent * toReach.y;
		z = (1 / 100f) * percent * toReach.z;

		x += (1 / 100f) * (100 - percent) * from.x;
		y += (1 / 100f) * (100 - percent) * from.y;
		z += (1 / 100f) * (100 - percent) * from.z;

		/*
		 * incr.fromAngleNormalAxis(-90/steps, camera.getUp());
		 * incr.mult(camera.getUp(), camera.getUp());
		 * incr.mult(camera.getLeft(), camera.getLeft());
		 * incr.mult(camera.getDirection(), camera.getDirection());
		 */

		camera.setDirection(new Vector3f(x, y, z));
		// TODO !!!
		camera.setLeft(new Vector3f(0, y/10f, 0));
		//camera.setUp(new Vector3f(x,0,0));

		camera.update();
		// camera.normalize();
		handler.core.updateDisplay(from);
		
	}
    
    protected void setLookUpDown()
    
    {
        Vector3f toReach = null;
        
        Vector3f from = J3DCore.turningDirectionsUnit[handler.core.viewDirection];
        if (handler.lookUpDownPercent<0)
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.BOTTOM];
        else
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.TOP];
        //handler.core.turnLeft();
    	
        //float steps = J3DCore.MOVE_STEPS*2;
        
        turnDirection(from, toReach, Math.abs(handler.lookUpDownPercent));

    }
    
    
	protected void movePosition(float steps, Vector3f from, Vector3f toReach)
	{
		float skipStep = 0f;
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
            camera.normalize();
            handler.core.updateDisplay(from);
            skipStep+= ensureTimeStop();
            if (skipStep>1f) {
            	i+=(int)skipStep;
            	skipStep=0f;
            }
            if (i>steps) break;
    
        }
		
	}
	
}

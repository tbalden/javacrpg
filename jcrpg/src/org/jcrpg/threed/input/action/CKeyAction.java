package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.KeyInputAction;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Node;

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
	
    
    protected void turnDirection(float steps, Vector3f from, Vector3f toReach, boolean almost)
	{
		float skipStep = 0f;
   	for (float i=0; i<=steps; i++)
        {
    		//if (almost && i==0) continue;
    		//if (almost && i==steps) continue;
    		ensureTimeStart();
    		float x, y, z;
    		x = (1/steps)* i * toReach.x;
    		y = (1/steps)* i * toReach.y;
    		z = (1/steps)* i * toReach.z;
    		
    		x += (1/steps) * (steps-i) * from.x;
    		y += (1/steps) * (steps-i) * from.y;
    		z += (1/steps) * (steps-i) * from.z;
    		
    		setCameraDirection(camera, x, y, z);
    		
            camera.normalize();
            camera.update();
            handler.core.updateDisplay(from);
            skipStep+= ensureTimeStop();
            if (skipStep>1f) {
            	i+=(int)skipStep;
            	skipStep=0f;
            }
            if (i>steps) break;
    
        }
		
	}
    
    /**
     * Sets a camera's direction to a new x,y,z dir, setting its Up and Left too with rotation matrix.
     * @param camera
     * @param x
     * @param y
     * @param z
     */
    private void setCameraDirection(Camera camera,  float x,float y,float z)
    {
    	setCameraDirection(camera, null,x, y, z);
    }
    
    /**
     * Sets a camera's direction to a new x,y,z dir, setting its Up and Left too with rotation matrix with an internal direction setting,
     * good for look up/down (needs a two step rotation).
     * @param camera
     * @param internalDirection
     * @param x
     * @param y
     * @param z
     */
    private void setCameraDirection(Camera camera, Vector3f internalDirection, float x,float y,float z)
    {
    	Matrix3f rotMat = new Matrix3f();    	
		Vector3f dirOrigo = new Vector3f(0.000f,0.000f,-1);
    	Vector3f left = new Vector3f(-1,0,0);
    	Vector3f up = new Vector3f(0,1,0);
		
		if (internalDirection!=null)
		{
	    	Vector3f dirNew = internalDirection;
	    	dirNew.normalizeLocal();
	    	rotMat.fromStartEndVectors(dirOrigo, dirNew);
	    	
	    	rotMat.mult(left, left);
	    	rotMat.mult(up, up);
	    	
	    	if (dirNew.x==0 && dirNew.y == 0 && dirNew.z == 1) up.mult(-1f); 
	    	up.normalize();
	    	left.normalize();
	    	dirOrigo = dirNew;
		}
		
    	Vector3f dirNew = new Vector3f(x,y,z);
    	dirNew.normalizeLocal();
    	rotMat.fromStartEndVectors(dirOrigo, dirNew);
    	
    	rotMat.mult(left, left);
    	rotMat.mult(up, up);
    	
    	// this code is needed for Y axis bottom-down problem...
    	if (internalDirection!=null && internalDirection.x==0 && internalDirection.y == 0 && internalDirection.z == 1) {
    		left.y *= -1;
			left.z *= -1;
			left.x *= -1;
			up.y *= -1;
			up.z *= -1;
			up.x *= -1;
    		
    	} else
    	if (dirNew.x==0 && dirNew.y == 0 && dirNew.z == 1) {
    		left.y*=-1;
    		left.z*=-1;
    		left.x*=-1;
    		up.y*=-1;
    		up.z*=-1;
    		up.x*=-1;
    	}
    	
    	up.normalize();
    	left.normalize();
    	
    	camera.setDirection(dirNew);
    	camera.setUp(up);
    	camera.setLeft(left);
    	camera.normalize();
    	
    }

    
    /**
     * Turns between to direction to a certain percent (good for look up/down).
     * @param from
     * @param toReach
     * @param percent
     */
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


		setCameraDirection(camera, from, x,y,z);

    	camera.update();
		
		handler.core.updateDisplay(from);
		
	}
    
    protected void setLookUpDown()
    
    {
        Vector3f toReach = null;
        
        Vector3f from = J3DCore.turningDirectionsUnit[handler.core.viewDirection];
        if (handler.lookUpDownPercent<0)
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.BOTTOM];//[handler.core.viewDirection];
        else
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.TOP];//J3DCore.topRotationDirections[handler.core.viewDirection];
        
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

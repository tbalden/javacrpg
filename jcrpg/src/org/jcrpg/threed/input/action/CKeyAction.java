/*
 *  This file is part of JavaCRPG.
 *	Copyright (C) 2007 Illes Pal Zoltan
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

package org.jcrpg.threed.input.action;

import org.jcrpg.threed.J3DCore;
import org.jcrpg.threed.input.ClassicKeyboardLookHandler;

import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.FastMath;
import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

public abstract class CKeyAction extends KeyInputAction{

	public boolean performActionCheck(InputActionEvent arg0) {
		if (handler.eventCatched || handler.lock) return false;
		return true;
	}
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
	
    
    protected void turnDirection(float steps, Vector3f from, Vector3f toReach, boolean almost) {
		float skipStep = 0f;
		for (float i = 0; i <= steps; i++) {
			// if (almost && i==0) continue;
			// if (almost && i==steps) continue;
			ensureTimeStart();
			float x, y, z;
			x = (1 / steps) * i * toReach.x;
			y = (1 / steps) * i * toReach.y;
			z = (1 / steps) * i * toReach.z;

			x += (1 / steps) * (steps - i) * from.x;
			y += (1 / steps) * (steps - i) * from.y;
			z += (1 / steps) * (steps - i) * from.z;

			setCameraDirection(camera, x, y, z);

			camera.normalize();
			camera.update();
			handler.core.updateDisplay(from);
			skipStep += ensureTimeStop();
			if (skipStep > 1f) {
				i += (int) skipStep;
				skipStep = 0f;
			}
			if (i > steps)
				break;

		}

	}
    
    protected void turnDirectionAndMove(float steps, Vector3f from, Vector3f toReach, Vector3f fromPos, Vector3f toPos, boolean almost) {
		long fromTime;
		float x1, y1, z1;
		float x, y, z;
		float currentPercent = 0;
		while (true)
		{
			fromTime = System.currentTimeMillis();
    		x1 = (1/steps)* currentPercent * toPos.x;
    		y1 = (1/steps)* currentPercent * toPos.y;
    		z1 = (1/steps)* currentPercent * toPos.z;
    		
    		x1 += (1/steps) * (steps-currentPercent) * fromPos.x;
    		y1 += (1/steps) * (steps-currentPercent) * fromPos.y;
    		z1 += (1/steps) * (steps-currentPercent) * fromPos.z;
    		
    		y1+=FastMath.sin((FastMath.PI/steps)*currentPercent)/10;
    		camera.setLocation(new Vector3f(x1,y1,z1));
    		if (J3DCore.SETTINGS.WATER_SHADER)
    		{
    			J3DCore.waterEffectRenderPass.setWaterHeight(camera.getLocation().y);
    		}
			x = (1 / steps) * currentPercent * toReach.x;
			y = (1 / steps) * currentPercent * toReach.y;
			z = (1 / steps) * currentPercent * toReach.z;

			x += (1 / steps) * (steps - currentPercent) * from.x;
			y += (1 / steps) * (steps - currentPercent) * from.y;
			z += (1 / steps) * (steps - currentPercent) * from.z;

			setCameraDirection(camera, x, y, z);
			camera.normalize();
			camera.update();
		
            handler.core.updateDisplayCalmer(from);

            long timePast = System.currentTimeMillis()-fromTime;
            currentPercent += timePast/20f;
            if (steps<=currentPercent) break;
		}

		float skipStep = 0f;
		/*Vector3f center = J3DCore.getInstance().getCurrentLocation();
		fromPos = center.add(fromPos.negate());
		toPos = center.add(toPos.negate());*/
/*		for (float i = 0; i <= steps; i++) {
			// if (almost && i==0) continue;
			// if (almost && i==steps) continue;
			ensureTimeStart();
    		x1 = (1/steps)* i * toPos.x;
    		y1 = (1/steps)* i * toPos.y;
    		z1 = (1/steps)* i * toPos.z;
    		
    		x1 += (1/steps) * (steps-i) * fromPos.x;
    		y1 += (1/steps) * (steps-i) * fromPos.y;
    		z1 += (1/steps) * (steps-i) * fromPos.z;
    		
    		y1+=FastMath.sin((FastMath.PI/steps)*i)/10;
    		
    		camera.setLocation(new Vector3f(x1,y1,z1));
    		if (J3DCore.WATER_SHADER)
    		{
    			J3DCore.waterEffectRenderPass.setWaterHeight(camera.getLocation().y);
    		}
 
            float x, y, z;
			x = (1 / steps) * i * toReach.x;
			y = (1 / steps) * i * toReach.y;
			z = (1 / steps) * i * toReach.z;

			x += (1 / steps) * (steps - i) * from.x;
			y += (1 / steps) * (steps - i) * from.y;
			z += (1 / steps) * (steps - i) * from.z;

			setCameraDirection(camera, x, y, z);

			camera.normalize();
			camera.update();
			handler.core.updateDisplay(from);
			skipStep += ensureTimeStop();
			if (skipStep > 1f) {
				i += (int) skipStep;
				skipStep = 0f;
			}
			if (i > steps)
				break;

		}
*/
	}

    /**
	 * Sets a camera's direction to a new x,y,z dir, setting its Up and Left too
	 * with rotation matrix.
	 * 
	 * @param camera
	 * @param x
	 * @param y
	 * @param z
	 */
    public static void setCameraDirection(Camera camera,  float x,float y,float z)
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
    private static void setCameraDirection(Camera camera, Vector3f internalDirection,
			float x, float y, float z) {
		Matrix3f rotMat = new Matrix3f();
		Vector3f dirOrigo = new Vector3f(0f, 0f, -1);
		Vector3f left = new Vector3f(-1, 0, 0);
		Vector3f up = new Vector3f(0, 1, 0);

		if (internalDirection != null) {
			Vector3f dirNew = internalDirection;
			dirNew = dirNew.normalize();
			rotMat.fromStartEndVectors(dirOrigo, dirNew);

			rotMat.mult(left, left);
			rotMat.mult(up, up);

			up.normalize();
			left.normalize();
			dirOrigo = dirNew;
		}

		Vector3f dirNew = new Vector3f(x, y, z);
		dirNew.normalizeLocal();
		rotMat.fromStartEndVectors(dirOrigo, dirNew);

		rotMat.mult(left, left);
		rotMat.mult(up, up);

		// this code is needed for Y axis bottom-down problem...
		if (internalDirection != null && internalDirection.x == 0
				&& internalDirection.y == 0 && internalDirection.z == 1) {
			left.multLocal(-1);
			up.multLocal(-1);

		} else if (dirNew.x == 0 && dirNew.y == 0 && dirNew.z == 1) {
			left.multLocal(-1);
			up.multLocal(-1);
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
	 * 
	 * @param from
	 * @param toReach
	 * @param percent
	 */
    protected void turnDirection(Vector3f from, Vector3f toReach, int percent)
	{
		float x, y, z;
		x = (1 / 100f) * percent * toReach.x;
		y = (1 / 100f) * percent * toReach.y;
		z = (1 / 100f) * percent * toReach.z;

		x += (1 / 100f) * (100 - percent) * from.x;
		y += (1 / 100f) * (100 - percent) * from.y;
		z += (1 / 100f) * (100 - percent) * from.z;


		setCameraDirection(camera, from, x,y,z);

        camera.normalize();
        camera.update();
        handler.core.updateDisplay(from);
		
	}
    
    /**
     * Sets Upward or Downward look based on the precentage stored in handler.
     */
    protected void setLookVertical()
    
    {
        Vector3f toReach = null;
        
        Vector3f from = J3DCore.turningDirectionsUnit[handler.core.gameState.getCurrentRenderPositions().viewDirection];
        if (handler.lookUpDownPercent<0)
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.BOTTOM];//[handler.core.gameState.viewDirection];
        else
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.TOP];//J3DCore.topRotationDirections[handler.core.gameState.viewDirection];
        
        turnDirection(from, toReach, Math.abs(handler.lookUpDownPercent));

    }
    /**
     * Sets Left or Right look based on the precentage stored in handler.
     */
    protected void setLookHorizontal()
    
    {
        Vector3f toReach = null;
        
        Vector3f from = J3DCore.turningDirectionsUnit[handler.core.gameState.getCurrentRenderPositions().viewDirection];
        if (handler.lookLeftRightPercent<0) {
        	int vdN = handler.core.gameState.getCurrentRenderPositions().viewDirection-1;
        	if (vdN<0) vdN = 3;
        	toReach = J3DCore.turningDirectionsUnit[vdN];
        }
        else {
        	int vdN = handler.core.gameState.getCurrentRenderPositions().viewDirection+1;
        	if (vdN>3) vdN = 0;
        	toReach = J3DCore.turningDirectionsUnit[vdN];
        }
        
        turnDirection(from, toReach, Math.abs(handler.lookLeftRightPercent));

    }
    protected void setLookVerHor()
    {
        Vector3f toReach = null;
        
        Vector3f from = J3DCore.turningDirectionsUnit[handler.core.gameState.getCurrentRenderPositions().viewDirection];
        if (handler.lookUpDownPercent<0)
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.BOTTOM];//[handler.core.gameState.viewDirection];
        else
        	toReach = J3DCore.turningDirectionsUnit[J3DCore.TOP];//J3DCore.topRotationDirections[handler.core.gameState.viewDirection];
        
        Vector3f toReachHor = null;
        
        if (handler.lookLeftRightPercent<0) {
        	int vdN = handler.core.gameState.getCurrentRenderPositions().viewDirection-1;
        	if (vdN<0) vdN = 3;
        	toReachHor = J3DCore.turningDirectionsUnit[vdN];
        }
        else {
        	int vdN = handler.core.gameState.getCurrentRenderPositions().viewDirection+1;
        	if (vdN>3) vdN = 0;
        	toReachHor = J3DCore.turningDirectionsUnit[vdN];
        }

		float x1, y1, z1;
		x1 = (1 / 100f) * Math.abs(handler.lookUpDownPercent) * toReach.x;
		y1 = (1 / 100f) * Math.abs(handler.lookUpDownPercent) * toReach.y;
		z1 = (1 / 100f) * Math.abs(handler.lookUpDownPercent) * toReach.z;

		x1 += (1 / 100f) * (100 - Math.abs(handler.lookUpDownPercent)) * from.x;
		y1 += (1 / 100f) * (100 - Math.abs(handler.lookUpDownPercent)) * from.y;
		z1 += (1 / 100f) * (100 - Math.abs(handler.lookUpDownPercent)) * from.z;
		toReach = new Vector3f(x1, y1, z1);
		
		float x2, y2, z2;
		x2 = (1 / 100f) * Math.abs(handler.lookLeftRightPercent) * toReachHor.x;
		y2 = (1 / 100f) * Math.abs(handler.lookLeftRightPercent) * toReachHor.y;
		z2 = (1 / 100f) * Math.abs(handler.lookLeftRightPercent) * toReachHor.z;

		x2 += (1 / 100f) * (100 - Math.abs(handler.lookLeftRightPercent)) * from.x;
		y2 += (1 / 100f) * (100 - Math.abs(handler.lookLeftRightPercent)) * from.y;
		z2 += (1 / 100f) * (100 - Math.abs(handler.lookLeftRightPercent)) * from.z;
		toReachHor = new Vector3f(x2, y2, z2);
		
		toReachHor = toReach.normalize().add(toReachHor.normalize()).normalize();


		setCameraDirection(camera, from, toReachHor.x,toReachHor.y,toReachHor.z);

        camera.normalize();
        camera.update();
        handler.core.updateDisplay(from);
    	
    }
  
	protected void movePosition(float steps, Vector3f from, Vector3f toReach)
	{
		movePosition(steps, from, toReach,false);
	}
	protected void movePosition(float steps, Vector3f from, Vector3f toReach,boolean sinusoid)
	{
		
		long fromTime;
		float x, y, z;
		float currentPercent = 0;
		while (true)
		{
			fromTime = System.currentTimeMillis();
    		x = (1/steps)* currentPercent * toReach.x;
    		y = (1/steps)* currentPercent * toReach.y;
    		z = (1/steps)* currentPercent * toReach.z;
    		
    		x += (1/steps) * (steps-currentPercent) * from.x;
    		y += (1/steps) * (steps-currentPercent) * from.y;
    		z += (1/steps) * (steps-currentPercent) * from.z;
    		
    		y+=FastMath.sin((FastMath.PI/steps)*currentPercent)/10;
    		camera.setLocation(new Vector3f(x,y,z));
    		if (J3DCore.SETTINGS.WATER_SHADER)
    		{
    			J3DCore.waterEffectRenderPass.setWaterHeight(camera.getLocation().y);
    		}
    		
            handler.core.updateTimeRelated();
            //InputSystem.update();
            //camera.update();
            handler.core.updateDisplayCalmer(from);

            long timePast = System.currentTimeMillis()-fromTime;
            currentPercent += timePast/20f;
    		if (steps<=currentPercent) break;
		}
		
	}
	
}

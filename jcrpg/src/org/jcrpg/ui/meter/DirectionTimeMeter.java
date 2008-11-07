/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.ui.meter;

import java.io.File;

import org.jcrpg.ui.HUD;
import org.jcrpg.world.time.Time;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Spatial.LightCombineMode;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

/**
 * Direction and time-o-meter HUD element.
 * @author pali
 *
 */
public class DirectionTimeMeter {

	
	public HUD hud;
	
	
	public Quad quad;
	public Quad quad_sign_dir;
	public Quad quad_sign_sun;
	public Texture base_tex;
	public Texture sign_dir;
	public Texture sign_sun;
	
	public DirectionTimeMeter(HUD hud) throws Exception
	{
		this.hud = hud;
		Image baseImage = TextureManager.loadImage(new File("./data/ui/meter.png").toURI().toURL(),true);
		Image signDirImage = TextureManager.loadImage(new File("./data/ui/sign1.png").toURI().toURL(),true);
		Image signSunImage = TextureManager.loadImage(new File("./data/ui/sign_sun.png").toURI().toURL(),true);
		base_tex = new Texture2D();
		base_tex.setImage(baseImage);
		sign_dir = new Texture2D();
		sign_dir.setImage(signDirImage);
		sign_sun = new Texture2D();
		sign_sun.setImage(signSunImage);
		
        TextureState state = hud.core.getDisplay().getRenderer().createTextureState();
		state.setTexture(base_tex, 0);

		TextureState state1 = hud.core.getDisplay().getRenderer().createTextureState();
		state1.setTexture(sign_dir, 0);
        
        TextureState state2 = hud.core.getDisplay().getRenderer().createTextureState();
		state2.setTexture(sign_sun, 0);
		
		quad = new Quad("METER",hud.core.getDisplay().getWidth()/13, (hud.core.getDisplay().getHeight()/9));
		quad.setRenderState(state);
		quad.setRenderState(hud.hudAS);
        quad.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        quad.setLocalTranslation(new Vector3f((hud.core.getDisplay().getWidth()/24.7f),(hud.core.getDisplay().getHeight()/18.5f),0));
        quad.setLightCombineMode(LightCombineMode.Off);
        quad.updateRenderState();

		quad_sign_dir = new Quad("SIGN_DIR",hud.core.getDisplay().getWidth()/13, (hud.core.getDisplay().getHeight()/9));
		quad_sign_dir.setRenderState(state1);
		quad_sign_dir.setRenderState(hud.hudAS);
        quad_sign_dir.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        quad_sign_dir.setLocalTranslation(new Vector3f((hud.core.getDisplay().getWidth()/24.7f),(hud.core.getDisplay().getHeight()/18.5f),0));
        quad_sign_dir.setLightCombineMode(LightCombineMode.Off);
        quad_sign_dir.updateRenderState();

		quad_sign_sun = new Quad("SIGN_SUN",hud.core.getDisplay().getWidth()/13, (hud.core.getDisplay().getHeight()/9));
		quad_sign_sun.setRenderState(state2);
		quad_sign_sun.setRenderState(hud.hudAS);
        quad_sign_sun.setRenderQueueMode(Renderer.QUEUE_ORTHO);  
        quad_sign_sun.setLocalTranslation(new Vector3f((hud.core.getDisplay().getWidth()/24.7f),(hud.core.getDisplay().getHeight()/18.5f),0));
        quad_sign_sun.setLightCombineMode(LightCombineMode.Off);
        quad_sign_sun.updateRenderState();
		
	}
	Quaternion q = new Quaternion();
	Quaternion q_d = new Quaternion();
	Vector3f axis = new Vector3f(0,0,1);
	
	public void updateQuad(int direction, Time time)
	{
		
		q.fromAngleAxis(FastMath.PI*(-2*(time.hour*1f)/time.maxHour), axis);
		quad_sign_sun.setLocalRotation(q);
		quad_sign_sun.updateRenderState();
		
		q_d.fromAngleAxis(FastMath.PI*(-2*(direction*6*1f)/24), axis);
		quad_sign_dir.setLocalRotation(q_d);
		quad_sign_dir.updateRenderState();
	}
	
}

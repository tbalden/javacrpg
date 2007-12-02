/*
 *  This file is part of JavaCRPG.
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

package org.jcrpg.ui.meter;

import java.io.File;

import org.jcrpg.ui.HUD;
import org.jcrpg.world.time.Time;

import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

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
		base_tex = new Texture();
		base_tex.setImage(baseImage);
		sign_dir = new Texture();
		sign_dir.setImage(signDirImage);
		sign_sun = new Texture();
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

		quad_sign_dir = new Quad("SIGN_DIR",hud.core.getDisplay().getWidth()/13, (hud.core.getDisplay().getHeight()/9));
		quad_sign_dir.setRenderState(state1);
		quad_sign_dir.setRenderState(hud.hudAS);

		quad_sign_sun = new Quad("SIGN_SUN",hud.core.getDisplay().getWidth()/13, (hud.core.getDisplay().getHeight()/9));
		quad_sign_sun.setRenderState(state2);
		quad_sign_sun.setRenderState(hud.hudAS);
		
	}
	float f = 0;
	public void updateQuad(int direction, Time time)
	{
		
		Quaternion q = new Quaternion();
		q.fromAngleAxis(FastMath.PI/f, new Vector3f(0,0,1));
		//sign_sun.setRotation(q);
		f+=0.011f;
		quad_sign_sun.setLocalRotation(q);
		//if (f>10) f = 0;
		quad_sign_sun.updateRenderState();
	}
	
}

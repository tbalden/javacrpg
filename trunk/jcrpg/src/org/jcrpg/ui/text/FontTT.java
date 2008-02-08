/*
 * Updated on June 21, 2006
 *
 * Written by Jeremy Adams
 *
 */
package org.jcrpg.ui.text;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * @author Jeremy Adams (elias4444)
 *
 */
public class FontTT {

	private Texture[] charactersp, characterso;
	private HashMap<String, IntObject> charlistp = new HashMap<String, IntObject>();
	private HashMap<String, IntObject> charlisto = new HashMap<String, IntObject>();
	private int kerneling;
	private int fontsize = 32;
	private Font font;
	private class IntObject {
		public int charnum;
		IntObject(int charnumpass) {
			charnum = charnumpass;
		}
	}

	public FontTT(Font font, int fontresolution, int extrakerneling) {

		this.kerneling = extrakerneling;
		this.font = font;
		fontsize = fontresolution;

		//TextureState.forceNonPowerOfTwoTextureSizeUsage();

		createPlainSet();
		createOutlineSet();
	}

	private BufferedImage getFontImage(char ch) {
		Font tempfont;
		tempfont = font.deriveFont((float)fontsize);
		//Create a temporary image to extract font size
		BufferedImage tempfontImage = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)tempfontImage.getGraphics();
		g.setFont(tempfont);
		FontMetrics fm = g.getFontMetrics();
		int charwidth = fm.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fm.getHeight();
		if (charheight <= 0) {
			charheight = fontsize;
		}

		//Create another image for texture creation
		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth,charheight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D)fontImage.getGraphics();
		gt.setFont(tempfont);

		//gt.setColor(Color.RED);
		//gt.fillRect(0, 0, charwidth, fontsize);
		gt.setColor(Color.WHITE);
		int charx = -fm.getLeading();
		int chary = 0;
		gt.drawString(String.valueOf(ch), (charx), (chary) + fm.getAscent());

		return fontImage;

	}


	private BufferedImage getOutlineFontImage(char ch) {
		Font tempfont;
		tempfont = font.deriveFont((float)fontsize);

		//Create a temporary image to extract font size
		BufferedImage tempfontImage = new BufferedImage(1,1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D)tempfontImage.getGraphics();
		g.setFont(tempfont);
		FontMetrics fm = g.getFontMetrics();
		int charwidth = fm.charWidth(ch);

		if (charwidth <= 0) {
			charwidth = 1;
		}
		int charheight = fm.getHeight();
		if (charheight <= 0) {
			charheight = fontsize;
		}

		//Create another image for texture creation
		int ot = (int)((float)fontsize/24f);

		BufferedImage fontImage;
		fontImage = new BufferedImage(charwidth + 2*ot,charheight + 2*ot, BufferedImage.TYPE_INT_ARGB);
		Graphics2D gt = (Graphics2D)fontImage.getGraphics();
		gt.setFont(tempfont);

		//gt.setColor(Color.RED);
		//gt.fillRect(0, 0, charwidth, fontsize);
		gt.setColor(Color.WHITE);
		int charx = -fm.getLeading() + ot;
		int chary = ot;
		gt.drawString(String.valueOf(ch), (charx) + ot, (chary) + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) - ot, (chary) + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx), (chary) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx), (chary) - ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) + ot, (chary) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) + ot, (chary) - ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) - ot, (chary) + ot + fm.getAscent());
		gt.drawString(String.valueOf(ch), (charx) - ot, (chary) - ot + fm.getAscent());

		float ninth = 1.0f / 9.0f;
		float[] blurKernel = {
		     ninth, ninth, ninth,
		     ninth, ninth, ninth,
		     ninth, ninth, ninth
		};
		BufferedImageOp blur = new ConvolveOp(new Kernel(3, 3, blurKernel));

		BufferedImage returnimage = blur.filter(fontImage, null);

		return returnimage;

	}


	
	
	private void createPlainSet() {
		charactersp = new Texture[256];

		for(int i=0;i<256;i++) {
			char ch = (char)i;

			BufferedImage fontImage = getFontImage(ch);

			charactersp[i] = TextureManager.loadTexture(fontImage, Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);

			charlistp.put(String.valueOf(ch), new IntObject(i));

			fontImage = null;
		}

	}
	
	private void createOutlineSet() {
		characterso = new Texture[256];

		for(int i=0;i<256;i++) {
			char ch = (char)i;

			BufferedImage fontImage = getOutlineFontImage(ch);

			characterso[i] = TextureManager.loadTexture(fontImage, Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, true);

			charlisto.put(String.valueOf(ch), new IntObject(i));

			fontImage = null;
		}

	}

	public Node createText(String text, float size, ColorRGBA color, boolean centered) {
		float fontsizeratio = size/fontsize;
		
		Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
		
		
		Node returnnode = new Node();

		int tempkerneling = (int)(kerneling * fontsizeratio);
		int k = 0;

		float startwidth;
		if (centered) {
			startwidth = -(getWidth(text, size))/2f;
		} else {
			startwidth = 0;
		}
		for (int i=0; i < text.length(); i++) {
			String tempstr = text.substring(i,i+1);

			Quad tempquad;
			float mywidth;
			k = ((charlistp.get(tempstr))).charnum;
			tempquad = new Quad(tempstr,charactersp[k].getImage().getWidth()*fontsizeratio,charactersp[k].getImage().getHeight()*fontsizeratio);
			mywidth = charactersp[k].getImage().getWidth() * fontsizeratio;

			if (i == 0) {
				tempquad.setLocalTranslation(new Vector3f(startwidth, 0, 0));
			} else {
				Quad lastquad = (Quad)returnnode.getChild(returnnode.getQuantity() - 1);
				float tempx =  (lastquad.getCenter()).x + ( ((BoundingBox)(lastquad.getWorldBound())).xExtent) + mywidth/2f + tempkerneling;
				Vector3f tempvec = new Vector3f(tempx,0,0);
				tempquad.setLocalTranslation(tempvec);
			}


			TextureState ts = renderer.createTextureState();
			ts.setEnabled(true);
			ts.setTexture(charactersp[k]);
			tempquad.setRenderState(ts);

			tempquad.setDefaultColor(color);

			tempquad.setModelBound(new BoundingBox());
			tempquad.updateModelBound();

			returnnode.attachChild(tempquad);

		}

		AlphaState as1 = renderer.createAlphaState();
		as1.setBlendEnabled( true );
		as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
		as1.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
		as1.setEnabled( true );

		returnnode.setRenderState(as1);

		returnnode.setLightCombineMode(LightState.OFF);

		return returnnode;

	}


	public Node createOutlinedText(String text, float size, ColorRGBA color, ColorRGBA outlinecolor, boolean centered) {
		float fontsizeratio = size/fontsize;
		
		Renderer renderer = DisplaySystem.getDisplaySystem().getRenderer();
		
		if (color.a < outlinecolor.a) {
			outlinecolor.a = color.a;
		}
		
		Node returnnode = new Node();

		int tempkerneling = (int)(kerneling * fontsizeratio);
		int k = 0;

		float startwidth;
		if (centered) {
			startwidth = -(getWidth(text, size))/2f;
		} else {
			startwidth = 0;
		}
		for (int i=0; i < text.length(); i++) {
			String tempstr = text.substring(i,i+1);

			Quad tempquad;
			Quad tempquadoutline;
			float mywidth;
			k = ((charlistp.get(tempstr))).charnum;
			tempquad = new Quad(tempstr,charactersp[k].getImage().getWidth()*fontsizeratio,charactersp[k].getImage().getHeight()*fontsizeratio);
			tempquadoutline = new Quad(tempstr,characterso[k].getImage().getWidth()*fontsizeratio,characterso[k].getImage().getHeight()*fontsizeratio);
			mywidth = charactersp[k].getImage().getWidth() * fontsizeratio;

			if (i == 0) {
				tempquad.setLocalTranslation(new Vector3f(startwidth, 0, 0));
				tempquadoutline.setLocalTranslation(new Vector3f(startwidth, 0, -0.01f));
			} else {
				Quad lastquad = (Quad)returnnode.getChild(returnnode.getQuantity() - 2);
				float tempx =  (lastquad.getCenter()).x + ( ((BoundingBox)(lastquad.getWorldBound())).xExtent) + mywidth/2f + tempkerneling;
				Vector3f tempvec = new Vector3f(tempx,0,0);
				tempquad.setLocalTranslation(tempvec);
				tempquadoutline.setLocalTranslation(new Vector3f(tempx, 0, -0.01f));
			}

			TextureState ts1 = renderer.createTextureState();
			ts1.setEnabled(true);
			ts1.setTexture(characterso[k]);
			tempquadoutline.setRenderState(ts1);
			
			tempquadoutline.setDefaultColor(outlinecolor);
			tempquadoutline.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
			tempquadoutline.setModelBound(new BoundingBox());
			tempquadoutline.updateModelBound();

			returnnode.attachChild(tempquadoutline);

			TextureState ts = renderer.createTextureState();
			ts.setEnabled(true);
			ts.setTexture(charactersp[k]);
			tempquad.setRenderState(ts);

			tempquad.setDefaultColor(color);
			
			tempquad.setModelBound(new BoundingBox());
			tempquad.updateModelBound();
			
			returnnode.attachChild(tempquad);

		}

		AlphaState as1 = renderer.createAlphaState();
		as1.setBlendEnabled( true );
		as1.setSrcFunction( AlphaState.SB_SRC_ALPHA );
		as1.setDstFunction( AlphaState.DB_ONE_MINUS_SRC_ALPHA );
		as1.setEnabled( true );

		returnnode.setRenderState(as1);

		returnnode.setLightCombineMode(LightState.OFF);

		return returnnode;

	}


	public int getWidth(String whatchars, float size) {
		float fontsizeratio = size/fontsize;

		int tempkerneling = (int)(kerneling*fontsizeratio);
		float totalwidth = 0;
		int k = 0;
		for (int i=0; i < whatchars.length(); i++) {
			String tempstr = whatchars.substring(i,i+1);
			k = ((charlistp.get(tempstr))).charnum;
			totalwidth += charactersp[k].getImage().getWidth()*fontsizeratio + tempkerneling;
		}
		return (int)totalwidth;

	}



}
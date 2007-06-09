package org.jcrpg.threed.input;

import org.jcrpg.threed.J3DCore;

import com.jme.input.InputHandler;
import com.jme.input.MouseLookHandler;
import com.jme.renderer.Camera;

public class ClassicInputHandler  extends InputHandler {

    private ClassicMouseLookHandler mouseLookHandler;
    private ClassicKeyboardLookHandler keyboardLookHandler;

    /**
     * @return handler for keyboard controls
     */
    public ClassicKeyboardLookHandler getKeyboardLookHandler() {
        return keyboardLookHandler;
    }

    /**
     * @return handler for mouse controls
     */
    public ClassicMouseLookHandler getMouseLookHandler() {
        return mouseLookHandler;
    }

    
    public ClassicInputHandler(J3DCore core, Camera cam)
    {
    	keyboardLookHandler = new ClassicKeyboardLookHandler(core,cam);
        addToAttachedHandlers( keyboardLookHandler );
    	//mouseLookHandler = new MouseLookHandler(cam,2.0f);//ClassicMouseLookHandler(cam);
    	mouseLookHandler = new ClassicMouseLookHandler(cam);
        addToAttachedHandlers( mouseLookHandler );
    	
    }
    
}

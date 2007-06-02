package org.jcrpg.threed.input;

import com.jme.input.InputHandler;
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

    
    public ClassicInputHandler(Camera cam)
    {
    	keyboardLookHandler = new ClassicKeyboardLookHandler(cam);
        addToAttachedHandlers( keyboardLookHandler );
    	mouseLookHandler = new ClassicMouseLookHandler(cam);
        addToAttachedHandlers( mouseLookHandler );
    	
    }
    
}

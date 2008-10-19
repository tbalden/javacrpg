package org.jcrpg.ui.window.layout;

import org.jcrpg.ui.window.InputWindow;
import org.jcrpg.ui.window.element.input.InputBase;

/**
 * Simple UI layout, which uses columns to arrange elements.
 * Example:
 * <pre>
 * </pre>
 *
 * @author goq669
 */
public class SimpleLayout {

//    private float xPosRatio;
//    private float yPosRatio;
//    private float colWidthRatio;
//    private float rowHeightRatio;
    private int columns;
    private int[] columnRows;

    private float startX;
    private float startY;
    private float columnWidth;
    private float rowHeight;

    /**
     * New Simple Layout
     * 
     * @param xPosRatio relative x position ratio
     * @param yPosRatio relative y position ratio
     * @param colWidthRatio column width ratio
     * @param rowHeightRatio row height ratio
     * @param columns columns number
     */
    public SimpleLayout(InputWindow w, float xPosRatio, float yPosRatio, float colWidthRatio, float rowHeightRatio, int columns) {
//        this.xPosRatio = xPosRatio;
//        this.yPosRatio = yPosRatio;
//        this.colWidthRatio = colWidthRatio;
//        this.rowHeightRatio = rowHeightRatio;
        this.columns = columns;
        // pre calculations
        startX = w.core.getDisplay().getWidth()*(xPosRatio);
        startY = w.core.getDisplay().getHeight()*(1f-yPosRatio);
        columnWidth = w.core.getDisplay().getWidth()*(colWidthRatio);
        rowHeight = w.core.getDisplay().getHeight()*(rowHeightRatio);
        columnRows = new int[columns];
        for (int i = 0; i < columnRows.length; i++) {
            columnRows[i] = 0;
        }
    }

    public void addToColumn(int column, InputBase inputElement) {
        float elemX = startX + columnWidth*column;
        float elemY = startY + rowHeight*columnRows[column];
        float elemWidth = columnWidth;
        float elemHeight = rowHeight;
        // TODO: ez az init meg nincs a rendszerben!
        //inputElement.init(elemX,elemY,elemWidth,elemHeight);
        columnRows[column]++;
    }

}

package org.jcrpg.ui.window.layout;

import java.util.ArrayList;

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

    private float xPosRatio;
    private float yPosRatio;
    private float colWidthRatio;
    private float rowHeightRatio;
    private int columns;
    private int[] columnRows;
    private ArrayList<InputBase> addedElements;

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
        this.xPosRatio = xPosRatio;
        this.yPosRatio = yPosRatio;
        this.colWidthRatio = colWidthRatio;
        this.rowHeightRatio = rowHeightRatio;
        this.columns = columns;
        addedElements = new ArrayList<InputBase>();
        // rows counter init
        columnRows = new int[columns];
        for (int i = 0; i < columnRows.length; i++) {
            columnRows[i] = 0;
        }
    }

    /**
     * Adds an element to a column.
     * @param column column index
     * @param inputElement element to add
     */
    public void addToColumn(int column, InputBase inputElement) {
        if (column > this.columns) {
            throw new IllegalArgumentException("The given column index ("+column+") is bigger then maximum ("+this.columns+")");
        }
        float elemX = this.xPosRatio + this.colWidthRatio*column;
        float elemY = this.yPosRatio + this.rowHeightRatio*columnRows[column];
        float elemWidth = this.colWidthRatio;
        float elemHeight = this.rowHeightRatio;
        // TODO: implement init!
        //inputElement.init(elemX,elemY,elemWidth,elemHeight);
        addedElements.add(inputElement);
        columnRows[column]++;
    }

}

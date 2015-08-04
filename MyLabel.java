/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package astar;

import javax.swing.JLabel;

/**
 *
 * @author Jarek
 */
class MyLabel extends JLabel
{
    protected int pX;
    protected int pY;

    public MyLabel(int x, int y)
    {
        pX = x;
        pY = y;
    }

    public int getPX() {
        return pX;
    }

    public int getPY() {
        return pY;
    }
}

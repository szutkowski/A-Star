/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package astar;

/**
 *
 * @author Jarek
 */
public class Point {
    int x;
    int y;
    float value;
    
    Point parent;

    public Point()
    {
    }

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Point(int x, int y, Point parent)
    {
        this(x, y);
        this.parent = parent;
    }
    
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Point getParent() {
        return parent;
    }

    public void setParent(Point parent) {
        this.parent = parent;
    }
    
    public String getUnique()
    {
        return this.x + "," + this.y;
    }
    
    @Override
    public String toString()
    {
        return this.x + "," + this.y;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package astar;

import java.awt.Color;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 *
 * @author Jarek
 */
public class AStarThread extends Observable implements Runnable{
//    public DataReader dr = new DataReader();
    
    public int[][] grid;
    public float[][] distances;
    
    //opoznienie w dzialaniu
    protected int delay = 0;
    
    private JLabel[][] labelArray;
    
    public boolean isPaused = false;
    public boolean isStopped = false;
    
    public Point start;
    public Point target;
    
    //punkt podswietlony
    public Point tmpPoint;
    
    protected JFrame frame;
    
    public boolean targetFound = false;
    
    public ArrayList<Point> openList = new ArrayList<>();
    public ArrayList<Point> closedList = new ArrayList<>();

    public AStarThread(MainFrame frame, Point start, Point meta, JLabel[][] labelArray, int[][] gridArray, int delay)
    {
        this.frame = frame;
        this.start = start;
        this.target = meta;
        this.labelArray = labelArray;
        this.delay = delay;
        grid = gridArray;
        distances = new float[grid.length][grid.length];
    }
    
    @Override
    public void run()
    {
        calculate();
        setChanged();
        notifyObservers();
    }
    
    private final void calculate()
    {
        //ilosc operacji
        int count = 0;
        
        addToCloseList(start);
        getNeighbours(start);
        
        while(openList.isEmpty() == false && targetFound == false)
        {
            while(isPaused)
            {
                System.out.println("pause");
                if(isStopped){return;}
            }
            if(isStopped){return;}
            
            Point smallest = getSmallestFromOpen();
            Point ordered = findLastOccuredValue(smallest.value);
            
            getNeighbours(ordered);
            
            count++;
            
        }
//        System.out.println("Operation count: " + count);
        if(targetFound == false)
        {
            JOptionPane.showMessageDialog(frame, "Way has not been found!", "", JOptionPane.ERROR_MESSAGE);
//            System.out.println("Way has not been found!");
        }
        else
        {
            fillWay(target);
//            showGrid();
//            saveResult();
//            showDistances();
        }
    }
      
    /*
     * Liczenie odlgelosci f(n) = g(n) + h(n)
     */
    private float Euklides(Point point)
    {
        int fromParent = 0;
        
        Point parent = point.getParent();
        if(parent != null)
        {
            do
            {
                fromParent ++;
            }
            while((parent = parent.getParent()) != null);
        }
                
         float distance = (float)(fromParent + Math.sqrt(Math.pow((double) point.x - (double) target.x, 2) + Math.pow((double) point.y - (double) target.y, 2)));
         return distance;
    }
    
    /*
     * Wypełnia drogę liczbą 3 w tablicy grid
     */
    private void fillWay(Point point)
    {
        resetWay();
        Point parent = point.getParent();
        
        grid[point.x][point.y] = 3;
        
        if(parent != null)
        {
            labelArray[parent.x][parent.y].setBackground(Color.GREEN);
            grid[parent.x][parent.y] = 3;
            while((parent = parent.getParent()) != null)
            {
                if(parent.x != start.x || parent.y != start.y)
                {
                    labelArray[parent.x][parent.y].setBackground(Color.GREEN);
                }
                grid[parent.x][parent.y] = 3;
            }
        }
    }
    
    /*
     * sets all 3 from grid to 0
     */
    private void resetWay()
    {
        for(int i = 0; i < grid.length; i++)
        {
            for(int j = 0; j < grid[i].length; j++)
            {
                if(grid[i][j] == 3)
                {
                    grid[i][j] = 0;
                }
            }
        }        
    }
    
    private void showGrid()
    {
        System.out.println("Path from start to target:");
        for(int i = 0; i < grid.length; i++)
        {
            for(int j = 0; j < grid[i].length; j++)
            {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    /*
     * Zapisuje trasę do pliku(3 - trasa, 5 - przeszkoda)
     */
    private void saveResult()
    {
        try
        {
            FileWriter writer = new FileWriter("grid.txt");
            
            for(int i = 0; i < grid.length; i++)
            {
                for(int j = 0; j < grid[i].length; j++)
                {
                    writer.write(String.valueOf(grid[i][j]) + " ");
                }
                writer.write(System.lineSeparator());
            }
            writer.close();
        }
        catch(Exception e)
        {
            
        }
    }
    
    private void showDistances()
    {
        System.out.println("Distances from particular cells to target:");
        for(int i = 0; i < distances.length; i++)
        {
            for(int j = 0; j < distances[i].length; j++)
            {
                System.out.printf("[%.2f] ", distances[i][j]);
            }
            System.out.println();
        }
    }
    
    private void getNeighbours(Point point)
    {
        try
        {
            Thread.sleep(delay);
        }
        catch(Exception e){}
        
        int rowLength = grid.length;
        int colLength = grid[point.x].length;
        
        /*
         * dodawanie do zamknietej listy
         */
        addToCloseList(point);
        removeFromOpen(point);
        
        //dolny
        if(point.x + 1 < rowLength)
        {
            goToSibling(point.x + 1, point.y, point);
        }
        
        //lewy
        if(point.y - 1 >= 0)
        {
            goToSibling(point.x, point.y - 1, point);
        }
        
        //gorny
        if(point.x - 1 >= 0)
        {
            goToSibling(point.x - 1, point.y, point);
        }        
        
        //prawy
        if(point.y + 1 < colLength)
        {
            goToSibling(point.x, point.y + 1, point);
        }
    }
    
    /*
     * dodawanie elementow sasiadujacych do otwartej listy
     */
    private void goToSibling(int x, int y, Point parent)
    {
        Point tmpPoint = new Point(x, y);
        if(grid[tmpPoint.x][tmpPoint.y] != 5)
        {
            tmpPoint.setParent(parent);
            tmpPoint.value = Euklides(tmpPoint);
            
            if((x == target.x && y == target.y) )
            {
                target = tmpPoint;
                targetFound = true;
                return;
            }
            
            addToOpenList(tmpPoint);
        }        
    }
    
    /*
     * Metoda dodaje punkt do otwartej listy
     * Sprawdza i rozwiązuje konflikty
     */
    private void addToOpenList(Point point)
    {
        if(closeContains(point))
        {
            return;
        }
        for(Point p : openList)
        {
            if(p.getUnique().equals(point.getUnique()))
            {
                if(point.value < p.value)
                {
                    p.setParent(point.getParent());
                    distances[point.x][point.y] = point.value;
                    labelArray[point.x][point.y].setText(String.valueOf(point.value));
                }
                return;
            }
        }
        labelArray[point.x][point.y].setText(String.format("%.2f", point.value));
        labelArray[point.x][point.y].setBackground(Color.YELLOW);        
        
        distances[point.x][point.y] = point.value;
        openList.add(point);
    }
    
    /*
     * Metoda dodaje punkty do lizty zamknietej
     */
    private void addToCloseList(Point point)
    {
        if(tmpPoint != null)
        {
            try
            {
                labelArray[tmpPoint.x][tmpPoint.y].setBackground(new Color(204,204,255));
            }
            catch(Exception e){}
            
        }
        
        if(point.x != start.x || point.y != start.y)
        {
            tmpPoint = point;
            labelArray[point.x][point.y].setBackground(new Color(141,238,238));
        }
        closedList.add(point);
    }
    
    /*
     * Zwraca najwcześniejsze wystapienie punktu z dana wartością
     */
    private Point findLastOccuredValue(float value)
    {
        for(int i = openList.size() - 1; i >= 0; i--)
        {
            if(openList.get(i).value == value)
            {
                return openList.get(i);
            }
        }
        return null;
    }
    
    /*
     * sprawdza czy lista zamknięta zawiera dany punkt
     */
    private boolean closeContains(Point point)
    {
        for(Point p : closedList)
        {
            if(p.getUnique().equals(point.getUnique()))
            {
                return true;
            }
        }
        return false;
    }
    
    /*
     * sprawdza czy lista otwarta zawiera dany punkt
     */    
    private boolean openContains(Point point)
    {
        for(Point p : openList)
        {
            if(p.getUnique().equals(point.getUnique()))
            {
                return true;
            }
        }
        return false;
    }
    
    /*
     * usuwa punkt z listy otwartej
     */
    private void removeFromOpen(Point point)
    {
        for(int i = 0; i < openList.size(); i++)
        {
            if(openList.get(i).getUnique().equals(point.getUnique()))
            {
                openList.remove(i);
                return;
            }
        }
    }
    
    /*
     * zwraca punkt z najmniejszą wartością z listy otwartej
     */
    private Point getSmallestFromOpen()
    {
        if(openList.isEmpty())
        {
            return null;
        }
        else if(openList.size() == 1)
        {
            return openList.get(0);
        }
        
        Point p = openList.get(0);
        
        for(int i = 1; i < openList.size(); i++)
        {
            if(openList.get(i).value < p.value)
            {
                p = openList.get(i);
            }
        }
        return p;
    }
}

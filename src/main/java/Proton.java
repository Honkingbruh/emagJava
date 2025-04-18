import javafx.geometry.Point3D;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Proton
{
    int charge = 1;
    double x;
    double y;
    double z;
    double actualX;
    double actualY;
    double actualZ;
    double fX;
    double fY;
    double fZ;
    double mass = 1.67262192 * Math.pow(10, -27);
    double centerX;
    double centerY;
    double centerZ;
    int fieldArrowNum = 50;
    int fieldLineNum = 6;
    int frames = 0;
    ArrayList<FieldLine> fieldLines = new ArrayList<>();
    ArrayList<Double[]> movements = new ArrayList<>();
    ArrayList<Double> times = new ArrayList<>();
    public Proton(double initX, double initY, double initZ, double time)
    {
        x = initX - 25;
        y = initY - 25;
        z = initZ - 25;
        centerX = initX;
        centerY = initY;
        centerZ = initZ;
        /*for(int j = 0; j < fieldLineNum; j++)
        {
            fieldLines.add(new FieldLine(centerX + 5*Math.cos(j*2*Math.PI/fieldLineNum), centerY + 5*Math.sin(j*2*Math.PI/fieldLineNum)));
        }*/
        movements.add(new Double[]{x, y, z, time});
        times.add(time);
        frames++;
    }
    public Proton(Point3D initPos, double time, Panel panel)
    {
        x = initPos.getX() - 25 + 800;//because the only time the point version of the constructor is called is when it doesn't account for the mid of the screen
        y = initPos.getY() - 25 + 450;
        z = initPos.getZ() - 25;
        actualX = panel.screenToCoordsX(x);
        actualY = panel.screenToCoordsX(x);
        actualY = panel.screenToCoordsX(x);
        centerX = initPos.getX();
        centerY = initPos.getY();
        centerZ = initPos.getZ();
        /*for(int j = 0; j < fieldLineNum; j++)
        {
            fieldLines.add(new FieldLine(centerX + 5*Math.cos(j*2*Math.PI/fieldLineNum), centerY + 5*Math.sin(j*2*Math.PI/fieldLineNum)));
        }*/
        if(frames % 50 == 49 && movements.size() >= 20)
        {
            movements = new ArrayList<>(movements.subList(movements.size() - 20, movements.size()));
            times = new ArrayList<Double>(times.subList(movements.size() - 20, movements.size()));
        }
        movements.add(new Double[]{x, y, z, time});
        times.add(time);
        frames++;
    }
    public void updateForces(Panel panel)
    {
        Point3D tempPoint = panel.getForce(x, y, z,true, charge);
        //panel.printPoint3D(tempPoint);
        if(!Double.isNaN(tempPoint.getY() + tempPoint.getZ()))
        {
            fX += tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.cos(tempPoint.getY());
            fY += tempPoint.getX() * Math.sin(tempPoint.getZ()) * Math.sin(tempPoint.getY());
            fZ += tempPoint.getX() * Math.cos(tempPoint.getZ());
        }
    }
    public void update(Graphics g, Panel panel, double time)
    {
        //fX *= 0.999;
        //fY *= 0.999;
        //fZ *= 0.999;
        //System.out.println(fX);
        if(panel.moveObjects && !(panel.parentSim.userPressed && getBounds().contains(panel.parentSim.lastPoint)))
        {
            x += fX * panel.timeStep * Math.pow(10, -21) / mass;
            y += fY * panel.timeStep * Math.pow(10, -21) / mass;
            //System.out.println(x + " " + y + " " + z + "m" + " q" + fX);
            z += fZ * panel.timeStep * Math.pow(10, -21) / mass;
        }
        else if(panel.parentSim.userPressed && getBounds().contains(panel.parentSim.lastPoint))
        {
            fX = 0;
            fY = 0;
            fZ = 0;
        }
        g.setColor(new Color(255,0,0));
        //System.out.println(x + " " + y);
        g.fillOval((int)x - 25, (int)y - 25, 50, 50);
        movements.add(new Double[]{x,y,z,time});
        times.add(time);
        frames++;
    }
    public void updateCenter()
    {
        centerX = x;
        centerY = y;
        centerZ = z;
        /*for(int j = 0; j < fieldLineNum; j++)
        {
            fieldLines.get(j).startX = centerX + 35*Math.cos(j*2*Math.PI/fieldLineNum);
            fieldLines.get(j).startY = centerY + 35*Math.sin(j*2*Math.PI/fieldLineNum);
        }*/
    }
    public void addCoords(double addX, double addY, double addZ, double time)
    {
        x += addX;
        y += addY;
        z += addZ;
        centerX += addX;
        centerY += addY;
        centerZ += addZ;
        movements.add(new Double[]{x,y,z,time});
        times.add(time);
        frames++;
    }
    public Rectangle getBounds()
    {
        return new Rectangle((int)x, (int)y, 50, 50);
    }
    public void drawFieldLines(Graphics g, Panel panel)
    {
        for(FieldLine i: fieldLines)
        {
            i.drawFieldArrows(g, panel);
        }
    }
    public Point3D getPos()
    {
        return new Point3D(x, y, z);
    }
    public Double[] getPosAtTime(double time)
    {
        for(int i = 1; i < times.size(); i++)
        {
            if(times.get(i) > time)
            {
                return movements.get(i-1);
            }
        }
        return movements.getLast();
    }
}

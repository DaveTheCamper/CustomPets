package me.davethecamper.custompets.objects;

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class Coords {
	
	public Coords(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	

	private double x;
	private double y;
	private double z;
	
	
	public double getX() {return this.x;}
	
	public double getY() {return this.y;}
	
	public double getZ() {return this.z;}
	
	public Vector getAsVector() {return new Vector(x, y, z);}
	
	public EulerAngle getAsEulerAngle() {return new EulerAngle(x, y, z);}
	
	public double distance(Coords c) {return Math.abs(Math.abs(this.x)-Math.abs(c.getX())) + Math.abs(Math.abs(this.y)-Math.abs(c.getY())) + Math.abs(Math.abs(this.z)-Math.abs(c.getZ()));}
	
	public void addX(double add) {this.x += add;}

	public void addY(double add) {this.y += add;}

	public void addZ(double add) {this.z += add;}
	
	public void resetX() {this.x = 0;}
	
	public void resetY() {this.y = 0;}
	
	public void resetZ() {this.z = 0;}
	
}

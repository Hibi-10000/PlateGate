/**
package com.github.hibi_10000.plugins.plategate;
**/
/**
//Use:none
import com.google.gson.JsonObject;

public class GateListJson {
	
	public static String get(JsonObject jo, String target) {
		return jo.get(target).getAsString();
	}
	
	public static String getName(JsonObject jo) {return jo.get("name").getAsString();}
	
	public static String getOwner(JsonObject jo) {return jo.get("owner").getAsString();}
	
	public static String getTo(JsonObject jo) {return jo.get("to").getAsString();}
	
	public static String getX(JsonObject jo) {return jo.get("x").getAsString();}
	
	public static String getY(JsonObject jo) {return jo.get("y").getAsString();}
	
	public static String getZ(JsonObject jo) {return jo.get("z").getAsString();}
	
	public static String getRotate(JsonObject jo) {return jo.get("rotate").getAsString();}
	
	public static String getWorld(JsonObject jo) {return jo.get("world").getAsString();}
	
	public static String getBeforeblock(JsonObject jo) {return jo.get("beforeblock").getAsString();}
}
**/


/**
//Use:Jackson
public class GateListJson {
	private String name;
	private String owner;
	private String to;
	private String x;
	private String y;
	private String z;
	private String rotate;
	private String world;
	private String beforeblock;
	
	public String getName() {return name;}
	
	public String getOwner() {return owner;}
	
	public String getTo() {return to;}
	
	public String getX() {return x;}
	
	public String getY() {return y;}
	
	public String getZ() {return z;}
	
	public String getRotate() {return rotate;}
	
	public String getWorld() {return world;}
	
	public String getBeforeblock() {return beforeblock;}
	
	public void setName(String name) {this.name = name;}
	
	public void setOwner(String owner) {this.owner = owner;}
	
	public void setTo(String to) {this.to = to;}
	
	public void setX(String x) {this.x = x;}
	
	public void setY(String y) {this.y = y;}
	
	public void setZ(String z) {this.z = z;}
	
	public void setRotate(String rotate) {this.rotate = rotate;}
	
	public void setWorld(String world) {this.world = world;}
	
	public void setBeforeblock(String beforeblock) {this.beforeblock = beforeblock;}
}
**/
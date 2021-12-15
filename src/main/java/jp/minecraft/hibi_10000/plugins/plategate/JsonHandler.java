package jp.minecraft.hibi_10000.plugins.plategate;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonHandler {

	private PlateGate instance;
	public JsonHandler(PlateGate instance) {
		this.instance = instance;
	}
	
	/**/
	//Use:Gson
	
	public JsonArray JsonFileRead() {
		try {
			Gson gson = new Gson();
			Stream<String> lines = Files.lines(instance.gatelistjsonfile.toPath());
			String content = lines.collect(Collectors.joining(System.lineSeparator()));
			JsonArray j = gson.fromJson(content, JsonArray.class);
			lines.close();
			return j;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean JsonFileWrite(JsonArray json) {
		try {
			Gson write = new GsonBuilder().setPrettyPrinting().create();
			String writetojson = write.toJson(json);
			FileWriter fw = new FileWriter(instance.gatelistjsonfile, false);
			fw.write(writetojson);
			fw.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public List<JsonObject> JsonRead(Player ownerplayer, String togate) {
		
		JsonObject nullobj = new JsonObject();
		nullobj.addProperty("name", "null");
		List<JsonObject> nullobjlist = new ArrayList<JsonObject>();
		nullobjlist.add(nullobj);
		
		JsonArray j = JsonFileRead();
		if (j == null) return nullobjlist;
		
		List<JsonObject> jolist = new ArrayList<JsonObject>();
		
		if (ownerplayer != null) {
			for (JsonElement element : j.getAsJsonArray()) {
				JsonObject jo = element.getAsJsonObject();
				if (jo.get("owner").getAsString().equalsIgnoreCase(ownerplayer.getUniqueId().toString())) {
					jolist.add(jo);
				}
			}
			if (jolist.size() != 0) return jolist;
			return nullobjlist;
			
		} else if (togate != null) {
			for (JsonElement element : j.getAsJsonArray()) {
				
				JsonObject jo = element.getAsJsonObject();
				if (jo.get("to").getAsString() == togate) {
					jolist.add(jo);
				}
			}
			if (jolist.size() != 0) return jolist;
			return nullobjlist;
		}
		
		return nullobjlist;
	}
	
	public JsonObject JsonRead(String gatename, Location loc) {
		
		JsonObject nullobj = new JsonObject();
		nullobj.addProperty("name", "null");
		
		JsonArray j = JsonFileRead();
		
		if (j == null) return nullobj;
		
		
		if (gatename != null) {
			for (JsonElement element : j.getAsJsonArray()) {
				
				JsonObject jo = element.getAsJsonObject();
				if (jo.get("name").getAsString().equalsIgnoreCase(gatename)) {
					return jo;
				}
				
				//System.out.println(jo.get("name").getAsString() + "\"" + gatename + "\"");
				
			}
			
		} else if (loc != null) {
			
			for (JsonElement element : j.getAsJsonArray()) {
				
				JsonObject jo = element.getAsJsonObject();
				if (jo.get("x").getAsString().equalsIgnoreCase(String.valueOf(loc.getBlockX()))
						&& jo.get("y").getAsString().equalsIgnoreCase(String.valueOf(loc.getBlockY()))
						&& jo.get("z").getAsString().equalsIgnoreCase(String.valueOf(loc.getBlockZ()))
						&& jo.get("world").getAsString().equalsIgnoreCase(loc.getWorld().getName())
						) {
					return jo;
				}
				/*System.out.println(loc.getWorld().getName());
				System.out.println(loc.getBlockX());
				System.out.println(loc.getBlockY());
				System.out.println(loc.getBlockZ());
				System.out.println("");
				System.out.println(loc.getX());
				System.out.println(loc.getY());
				System.out.println(loc.getZ());
				System.out.println("");*/
			}
		}
		return nullobj;
	}
	
	
	
	
	public boolean JsonWrite(String gatename, Player ownerplayer, String togate,
			Location loc, Material beforeblockm) {
		String owneruuid = ownerplayer.getUniqueId().toString();
		String beforeblock = beforeblockm.toString();
		String x = String.valueOf(loc.getBlockX());
		String y = String.valueOf(loc.getBlockY());
		String z = String.valueOf(loc.getBlockZ());
		String world = loc.getWorld().getName();
		//float yaw = loc.getYaw();
		String d = "south";
		
		BlockFace pf = ownerplayer.getFacing();
		
		if (/*(yaw >= 315 || yaw <= 45) ||  */pf == BlockFace.SOUTH){
			d = "south";
		} else if (/*(yaw > 45 && yaw < 135) || */pf == BlockFace.WEST){
			d = "west";
		} else if (/*(yaw >= 135 && yaw <= 225) || */pf == BlockFace.NORTH){
			d = "north";
		} else if (/*(yaw > 225 && yaw < 315) || */pf == BlockFace.EAST){
			d = "east";
		}
		
		
		JsonArray json = JsonFileRead();
		if (json == null) return false;
		
		JsonObject j = new JsonObject();
		j.addProperty("name", gatename);
		j.addProperty("owner", owneruuid);
		j.addProperty("to", togate);
		j.addProperty("x", x);
		j.addProperty("y", y);
		j.addProperty("z", z);
		j.addProperty("rotate", d);
		j.addProperty("world", world);
		j.addProperty("beforeblock", beforeblock);
		json.getAsJsonArray().add(j);
		
		if (!(JsonFileWrite(json))) return false;
		
		return true;
	}
	
	public void JsonChange(String targetname, String margename, Player ownerplayer, String togate, 
			Location loc, Material beforeblock, Player sendpl) {
		
		JsonArray json = JsonFileRead();
		if (json == null) return;
		
		JsonArray newjson = new JsonArray();
		
		for (JsonElement element : json.getAsJsonArray()) {
			
			JsonObject oldjo = element.getAsJsonObject();
			JsonObject jo = new JsonObject().getAsJsonObject();
			if (oldjo.get("name").getAsString().equalsIgnoreCase(targetname)) {
				
				if (margename != null) {
					jo.addProperty("name", margename);
					
					jo.addProperty("owner", oldjo.get("owner").getAsString());
					jo.addProperty("to", oldjo.get("to").getAsString());
					jo.addProperty("x", oldjo.get("x").getAsString());
					jo.addProperty("y", oldjo.get("y").getAsString());
					jo.addProperty("z", oldjo.get("z").getAsString());
					jo.addProperty("rotate", oldjo.get("rotate").getAsString());
					jo.addProperty("world", oldjo.get("world").getAsString());
					jo.addProperty("beforeblock", oldjo.get("beforeblock").getAsString());
					
				} else if (ownerplayer != null) {
					jo.addProperty("name", oldjo.get("name").getAsString());
					
					jo.addProperty("owner", ownerplayer.getUniqueId().toString());
					
					jo.addProperty("to", oldjo.get("to").getAsString());
					jo.addProperty("x", oldjo.get("x").getAsString());
					jo.addProperty("y", oldjo.get("y").getAsString());
					jo.addProperty("z", oldjo.get("z").getAsString());
					jo.addProperty("rotate", oldjo.get("rotate").getAsString());
					jo.addProperty("world", oldjo.get("world").getAsString());
					jo.addProperty("beforeblock", oldjo.get("beforeblock").getAsString());
					
				} else if (togate != null) {
					jo.addProperty("name", oldjo.get("name").getAsString());
					jo.addProperty("owner", oldjo.get("owner").getAsString());
					
					jo.addProperty("to", togate);
					
					jo.addProperty("x", oldjo.get("x").getAsString());
					jo.addProperty("y", oldjo.get("y").getAsString());
					jo.addProperty("z", oldjo.get("z").getAsString());
					jo.addProperty("rotate", oldjo.get("rotate").getAsString());
					jo.addProperty("world", oldjo.get("world").getAsString());
					jo.addProperty("beforeblock", oldjo.get("beforeblock").getAsString());
					
				} else if (loc != null && beforeblock != null) {
					//float yaw = loc.getYaw();
					String d = "south";
					
					BlockFace pf = sendpl.getFacing();
					
					if (/*(yaw >= 315 || yaw <= 45) ||  */pf == BlockFace.SOUTH){
						d = "south";
					} else if (/*(yaw > 45 && yaw < 135) || */pf == BlockFace.WEST){
						d = "west";
					} else if (/*(yaw >= 135 && yaw <= 225) || */pf == BlockFace.NORTH){
						d = "north";
					} else if (/*(yaw > 225 && yaw < 315) || */pf == BlockFace.EAST){
						d = "east";
					}
					
					jo.addProperty("name", oldjo.get("name").getAsString());
					jo.addProperty("owner", oldjo.get("owner").getAsString());
					jo.addProperty("to", oldjo.get("to").getAsString());
					
					jo.addProperty("x", String.valueOf(loc.getBlockX()));
					jo.addProperty("y", String.valueOf(loc.getBlockY()));
					jo.addProperty("z", String.valueOf(loc.getBlockZ()));
					jo.addProperty("rotate", d);
					jo.addProperty("world", loc.getWorld().getName());
					jo.addProperty("beforeblock", beforeblock.toString());
					
				} else {
					jo.addProperty("name", oldjo.get("name").getAsString());
					jo.addProperty("owner", oldjo.get("owner").getAsString());
					jo.addProperty("to", oldjo.get("to").getAsString());
					jo.addProperty("x", oldjo.get("x").getAsString());
					jo.addProperty("y", oldjo.get("y").getAsString());
					jo.addProperty("z", oldjo.get("z").getAsString());
					jo.addProperty("rotate", oldjo.get("rotate").getAsString());
					jo.addProperty("world", oldjo.get("world").getAsString());
					jo.addProperty("beforeblock", oldjo.get("beforeblock").getAsString());
				}
				
				//json.getAsJsonArray().remove(element.getAsJsonObject());
				newjson.add(jo);
			} else {
				newjson.add(oldjo);
			}
		}
		if (!(JsonFileWrite(newjson))) return;
		return;
	}
	
	public void JsonRemove(String gatename) {
		JsonArray json = JsonFileRead();
		if (json == null) return;
		
		JsonArray newjson = new JsonArray();
		//JsonArray newjson = json;
		
		for (JsonElement element : json.getAsJsonArray()) {
			JsonObject jo = element.getAsJsonObject();
			if (jo.get("name").getAsString().equalsIgnoreCase(gatename)) {
				//newjson.getAsJsonArray().remove(jo.getAsJsonObject());
			} else {
				newjson.add(element);
			}
		}
		if (!(JsonFileWrite(newjson))) return;
		return;
	}
	/**/
	
	
	
	
	/**
	//Use:Jackson
	//Waning: 完成していません
	
	public GateListJson JSONRead(String gatename, Player ownerplayer, String togate, Location loc) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			GateListJson[] glj = mapper.readValue(instance.gatelistjsonfile, GateListJson[].class);
			
			for (GateListJson json : glj) {
				//System.out.println(json.getName());
				if ((!json.getName().isEmpty() && json.getName().equalsIgnoreCase(gatename)) 
						|| (!json.getOwner().isEmpty() && json.getOwner().equalsIgnoreCase(owneruuid)) 
						|| (!json.getTo().isEmpty() && json.getTo().equalsIgnoreCase(togate)) 
						|| ((!json.getX().isEmpty() && json.getX().equalsIgnoreCase(String.valueOf(loc.getBlockX())))
							&& (!json.getY().isEmpty() && json.getY().equalsIgnoreCase(String.valueOf(loc.getBlockY())))
							&& (!json.getZ().isEmpty() && json.getZ().equalsIgnoreCase(String.valueOf(loc.getBlockZ())))
							&& (!json.getWorld().isEmpty() && json.getWorld().equalsIgnoreCase(loc.getWorld().getName())))
						|| (!json.getWorld().isEmpty() && json.getWorld().equalsIgnoreCase(loc.getWorld().getName()))
						|| (!json.getBeforeblock().isEmpty() && json.getBeforeblock().equalsIgnoreCase(beforeblock.toString()))
						) {
					return json;
				}
			}
			return null;
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean JSONWrite(String gatename, Player ownerplayer, String togate,
			Location loc, Material beforeblockm) {
		String owneruuid = ownerplayer.getUniqueId().toString();
		String beforeblock = beforeblockm.toString();
		String x = String.valueOf(loc.getBlockX());
		String y = String.valueOf(loc.getBlockY());
		String z = String.valueOf(loc.getBlockZ());
		String world = loc.getWorld().getName();
		float yaw = loc.getYaw();
		String rotate = null;
		if (yaw >= 315 || yaw <= 45){
			rotate = "0";
		}
		else if (yaw > 45 && yaw < 135){
			rotate = "90";
		}
		else if (yaw >= 135 && yaw <= 225){
			rotate = "180";
		}
		else if (yaw > 225 && yaw < 315){
			rotate = "270";
		} else {
			
		}
		
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			//ArrayNode array = mapper.createArrayNode();
			//GateListJson[] glj = mapper.readValue(instance.gatelistjsonfile, GateListJson[].class);
			//for (GateListJson json : glj) {
			//	ObjectNode node = mapper.createObjectNode();
			//	node.put("name", json.getName());
			//	node.put("owner", json.getOwner());
			//	node.put("to", json.getTo());
			//	node.put("x", json.getX());
			//	node.put("y", json.getY());
			//	node.put("z", json.getZ());
			//	node.put("rotate", json.getRotate());
			//	node.put("world", json.getWorld());
			//	node.put("beforeblock", json.getBeforeblock());
			//	array.add(node);
			//}
			
			ObjectNode node = mapper.createObjectNode();
			node.put("name", gatename);
			node.put("owner", owneruuid);
			node.put("to", togate);
			node.put("x", x);
			node.put("y", y);
			node.put("z", z);
			node.put("rotate", rotate);
			node.put("world", world);
			node.put("beforeblock", beforeblock);
			//array.add(node);
			JsonNode json = mapper.readTree(instance.gatelistjsonfile);
			((ArrayNode) json).add(node);
			
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void JSONMarge(String targetname, String margename, Player ownerplayer, String togate, Location loc) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			
			GateListJson[] glj = mapper.readValue(instance.gatelistjsonfile, GateListJson[].class);
			
			for (GateListJson json : glj) {
				//System.out.println(json.getName());
				if ((!json.getName().isEmpty() && json.getName().equalsIgnoreCase(targetname)) 
						|| (!json.getOwner().isEmpty() && json.getOwner().equalsIgnoreCase(owneruuid)) 
						|| (!json.getTo().isEmpty() && json.getTo().equalsIgnoreCase(togate)) 
						|| ((!json.getX().isEmpty() && json.getX().equalsIgnoreCase(String.valueOf(loc.getBlockX())))
							&& (!json.getY().isEmpty() && json.getY().equalsIgnoreCase(String.valueOf(loc.getBlockY())))
							&& (!json.getZ().isEmpty() && json.getZ().equalsIgnoreCase(String.valueOf(loc.getBlockZ())))
							&& (!json.getWorld().isEmpty() && json.getWorld().equalsIgnoreCase(loc.getWorld().getName())))
						|| (!json.getWorld().isEmpty() && json.getWorld().equalsIgnoreCase(loc.getWorld().getName()))
						|| (!json.getBeforeblock().isEmpty() && json.getBeforeblock().equalsIgnoreCase(beforeblock.toString()))
						) {
					return json;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void JSONRemove(String gatename) {
		
	}
	
	**/
}

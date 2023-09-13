package com.github.hibi_10000.plugins.plategate.util;

import com.github.hibi_10000.plugins.plategate.PlateGate;
import com.google.gson.*;
import org.jetbrains.annotations.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @since 1.0.0
 * @author Hibi_10000
 */
public class ArrayJson {
	File jsonfile;
	List<String> namelist;

	/**
	 * Create {@link ArrayJson} instance
	 * @param instance Plugin instance
	 * @param namelist Set column names for JsonTable (id is set automatically)
	 * @since 1.0.0
	 */
	public ArrayJson(@NotNull PlateGate instance, List<String> namelist) {
		this.jsonfile = instance.gatelistjsonfile;
		this.namelist = new ArrayList<>();
		this.namelist.addAll(namelist);
	}

	/**
	 * Read {@link JsonArray} in {@link #jsonfile}
	 * @return {@link JsonArray} JsonArray in File
	 * @throws IOException {@link #ArrayJson(PlateGate, List) ArrayJson(jsonfile, namelist)} jsonfile isn't found
	 * @throws IllegalStateException The contents of File are not JsonArray
	 * @throws SecurityException {@link Files#lines(Path) Files.lines(jsonfile.toPath())} Is security manager installed to jsonfile?
	 * @throws InvalidPathException {@link File#toPath() jsonfile.toPath()} If jsonfile path string cannot be converted
	 * @since 1.0.0
	 */
	public JsonArray read() throws IOException, IllegalStateException, SecurityException, InvalidPathException {
		Stream<String> lines = Files.lines(jsonfile.toPath());
		String content = lines.collect(Collectors.joining(System.lineSeparator()));
		lines.close();
		JsonArray array = new Gson().fromJson(content, JsonArray.class);
		if (!array.isJsonArray()) {throw new IllegalStateException("The contents of File are not JsonArray");}
		return array;
	}

	/**
	 * Write JsonArray to jsonfile
	 * @param json JsonArray to write to jsonfile
	 * @throws IOException see {@link FileWriter} and {@link  FileWriter#write(String)}
	 * @since 1.0.0
	 */
	public void write(@NotNull JsonArray json) throws IOException {
		Gson write = new GsonBuilder().setPrettyPrinting().create();
		String writetojson = write.toJson(json);
		FileWriter fw = new FileWriter(jsonfile, false);
		fw.write(writetojson);
		fw.close();
	}

	/**
	 * Get the value corresponding to the input value
	 * @param id Id of the entry to get
	 * @param name The name of the column you want to get
	 * @return Value in place corresponding to the input value
	 * @throws RuntimeException see {@link #read()}
	 * @throws IOException see {@link #read()}
	 * @since 1.0.0
	 * @see #read()
	 */
	public String get(@NotNull String id, @NotNull String name) throws RuntimeException, IOException {
		JsonArray j = read();
		for (JsonElement element : j.getAsJsonArray()) {
			JsonObject jo = element.getAsJsonObject();
			if (jo.get("id").getAsString().equalsIgnoreCase(id)) {
				return jo.get(name).getAsString();
			}
		}
		return null;
	}

	/**
	 * Add an entry to the table
	 * @param values Set the default values in the order of {@link #namelist} (id is set automatically)
	 * @throws IOException see {@link #read()}
	 * @throws RuntimeException see {@link #read()}
	 * @since 1.0.0
	 */
	public void add(@NotNull String... values) throws RuntimeException, IOException {
		int lastid = 0;
		JsonArray json = read();
		for (JsonElement element : json.getAsJsonArray()) {
			JsonObject jo = element.getAsJsonObject();
			int id = Integer.parseInt(jo.get("id").getAsString());
			if (lastid < id) lastid = id;
		}
		JsonObject j = new JsonObject();
		j.addProperty("id", String.valueOf(lastid));
		int index = 0;
		for (String value : values) {
			index++;
			j.addProperty(namelist.get(index), value);
		}
		json.getAsJsonArray().add(j);
		write(json);
	}

	/**
	 * Change the value of the specified column of the specified existing entry
	 * @param id Specifies the id of the entry for which you want to change the value of a column
	 * @param key Specify the name of the column whose value you want to change
	 * @param value Specifies a value that replaces the value in the specified column
	 * @throws RuntimeException see {@link #read()}
	 * @throws IOException see {@link #read()} {@literal &} {@link #write(JsonArray)}
	 * @since 1.0.0
	 */
	public void set(@NotNull String id, @NotNull String key, @NotNull String value) throws RuntimeException, IOException {
		JsonArray json = read();
		JsonArray newjson = new JsonArray();
		for (JsonElement element : json.getAsJsonArray()) {
			JsonObject oldjo = element.getAsJsonObject();
			if (oldjo.get("id").getAsString().equalsIgnoreCase(id)) {
				JsonObject jo = new JsonObject().getAsJsonObject();
				for (Map.Entry<String, JsonElement> entry : oldjo.entrySet()) {
					String oldkey = entry.getKey();
					if (oldkey.equalsIgnoreCase(key)) {
						jo.addProperty(key,value);
					} else {
						jo.addProperty(oldkey,oldjo.get(oldkey).getAsString());
					}
				}
				newjson.add(jo);
			} else {
				newjson.add(oldjo);
			}
		}
		write(newjson);
	}

	/**
	 * Remove entry
	 * @param id Id of the entry to remove
	 * @throws IllegalStateException see {@link JsonArray#getAsString()}
	 * @throws IOException see {@link #read()} {@literal &} {@link #write(JsonArray)}
	 * @since 1.0.0
	 */
	public void remove(@NotNull String  id) throws IllegalStateException, IOException {
		JsonArray json = read();
		JsonArray newjson = new JsonArray();
		for (JsonElement element : json) {
			JsonObject jo = element.getAsJsonObject();
			if (!jo.get("id").getAsString().equalsIgnoreCase(id)) {
				newjson.add(jo);
			}
		}
		write(newjson);
	}

	/**
	 * Gets the first entry that matches the value of the specified key
	 * @param key Specify the key to search
	 * @param value Specifies the value corresponding to the specified key
	 * @return Returns the ID of the retrieved entry as a String
	 * @throws IOException see {@link #read()}
	 * @throws IllegalStateException see {@link #read()}
	 * @throws SecurityException see {@link #read()}
	 * @throws InvalidPathException see {@link #read()}
	 * @since 1.0.0
	 */
	@NotNull
	public String firstIndexOf(@NotNull String key, @NotNull String value) throws IOException, IllegalStateException, SecurityException, InvalidPathException {
		JsonArray json = read();
		String back = "0";
		for (JsonElement element : json) {
			JsonObject jo = element.getAsJsonObject();
			if (jo.get(key).getAsString().equalsIgnoreCase(value)
					&& (Integer.parseInt(back) > Integer.parseInt(jo.get("id").getAsString())
					|| back.equalsIgnoreCase("0") )) {
				back = jo.get("id").getAsString();
			}
		}
		return back;
	}

	/**
	 * Gets the last entry that matches the value of the specified key
	 * @param key Specify the key to search
	 * @param value Specifies the value corresponding to the specified key
	 * @return Returns the ID of the retrieved entry as a String
	 * @throws IOException see {@link #read()}
	 * @throws IllegalStateException see {@link #read()}
	 * @throws SecurityException see {@link #read()}
	 * @throws InvalidPathException see {@link #read()}
	 * @since 1.0.0
	 */
	@NotNull
	public String lastIndexOf(String key, String value) throws IOException, IllegalStateException, SecurityException, InvalidPathException {
		JsonArray json = read();
		String back = "0";
		for (JsonElement element : json) {
			JsonObject jo = element.getAsJsonObject();
			if (jo.get(key).getAsString().equalsIgnoreCase(value)
					&& Integer.parseInt(back) < Integer.parseInt(jo.get("id").getAsString())) {
				back = jo.get("id").getAsString();
			}
		}
		return back;
	}

	/**
	 * Gets all entries that match the value of the specified key
	 * @param key Specify the key to search
	 * @param value Specifies the value corresponding to the specified key
	 * @return Returns the ID of the retrieved entry as a StringList
	 * @throws IOException see {@link #read()}
	 * @throws IllegalStateException see {@link #read()}
	 * @throws SecurityException see {@link #read()}
	 * @throws InvalidPathException see {@link #read()}
	 * @since 1.0.0
	 */
	@NotNull
	public List<String> IndexOf(String key, String value) throws IOException, IllegalStateException, SecurityException, InvalidPathException {
		JsonArray json = read();
		List<String> back = new ArrayList<>();
		for (JsonElement element : json) {
			JsonObject jo = element.getAsJsonObject();
			if (jo.get(key).getAsString().equalsIgnoreCase(value)) {
				back.add(jo.get("id").getAsString());
			}
		}
		return back;
	}



	/**
	 * @throws RuntimeException see {@link #write(JsonArray)}
	 * @throws IOException see {@link #write(JsonArray)}
	 * @since 1.0.0
	 * @see #write(JsonArray)
	 */
	public void clear() throws RuntimeException, IOException {write(new JsonArray());}
}

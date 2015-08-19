package org.deguet;

import java.lang.reflect.Type;

import javax.xml.bind.DatatypeConverter;

import org.joda.time.DateTime;

import com.deguet.gutils.vote.RankedVote;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CustomGson {

	public static Gson getIt(){
		GsonBuilder builder = new GsonBuilder();
		builder.enableComplexMapKeySerialization();
		builder.registerTypeAdapter(DateTime.class, new DateTimeSerialiser());
		builder.registerTypeAdapter(RankedVote.class, new RankedVoteSerialiser());
		builder.registerTypeAdapter(byte[].class, new ByteArraySerialiser());
		builder.setPrettyPrinting();
		return builder.create();
	} 
	
	public static class DateTimeSerialiser  implements JsonSerializer<DateTime>,JsonDeserializer<DateTime>  {
		public JsonElement serialize(DateTime src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toString());
		}
		public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return new DateTime(json.getAsJsonPrimitive().getAsString());
		}
	}
	
	public static class RankedVoteSerialiser  implements JsonSerializer<RankedVote<String>>,JsonDeserializer<RankedVote<String>>  {
		public JsonElement serialize(RankedVote<String> src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(src.toCondense());
		}
		public RankedVote<String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return RankedVote.fromCondense(json.getAsJsonPrimitive().getAsString());
		}
	}
	
	public static class ByteArraySerialiser  implements JsonSerializer<byte[]>,JsonDeserializer<byte[]>  {
		public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
			return new JsonPrimitive(DatatypeConverter.printBase64Binary(src));
		}
		public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			return DatatypeConverter.parseBase64Binary(json.getAsJsonPrimitive().getAsString());
		}
	}

	
}

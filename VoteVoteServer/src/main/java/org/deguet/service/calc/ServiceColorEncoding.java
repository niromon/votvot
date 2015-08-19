package org.deguet.service.calc;

import java.util.ArrayList;
import java.util.List;


/**
 * Convert a bit line into a sequence of characters with well known colors
 * 
 * 
 * Idea would be to have an easy way to remember a poll from a combination
 * 
 * 8 gray A pink
 * 
 * Combination : 36 * 8 * 36 * 8 = 82944
 * 
 * @author joris
 *
 */
public class ServiceColorEncoding {
	
	enum Color {Black, Red, Green, Blue, Pink, Yellow, Gray, Orange}

	public List<ColorChar> encode(byte[] bytes){
		// 8 bits per bytes. each color can 3 three bits
		int bitNumber = bytes.length * 8;
		
		int colors = bitNumber / 3  + bitNumber%3!=0?1:0;
		List<ColorChar> result = new ArrayList<ColorChar>();

		return result;
	}
	
	public byte[] decode(List<ColorChar> colors){
		// 8 bits per bytes. each color can 3 three bits
		int bitNumber = colors.size() * 3;
		int byteNumber = bitNumber / 8;
		byte[] result = new byte[byteNumber];

		return result;
	}

	public static class ColorChar {
		public char character;
		public Color color;
	}
	
//	public 
//
//	public byte getBit(int position, byte[] source)
//	{
//		int bytePos = position /8;
//		int inBytePos = position % 8;
//		byte bytee = source[bytePos];
//		return (bytee >> position) & 1;
//	}

}

package com.splicemachine.utils;

public class RightPad {
	
	/**
	 *  @author amitmudgal
	 * RPAD function pads the right-side of a string with a specific set of characters (when string1 is not null).
	 * "Lucubration", 10 
	 * will return "Lucubratio".
	 * "Lucubration", 15,'*'
	 * will return "Lucubration****"
	 * @param inboundString
	 * @param paddedLength
	 * @param padString 
	 * @return  
	 */

	public static String rpad(String inboundString, int paddedLength, String padString) {
		String paddedString = "";
		if(padString != null){
			for (int i=0; i < paddedLength;i++ ){
				padString += padString;
			}
		}
		inboundString = inboundString + padString;
		if(inboundString != null && paddedLength > 0) {
			if( inboundString.length() > paddedLength )
			{ paddedString = inboundString.substring(0, paddedLength);}
			else{
			  paddedString = inboundString;	
			}
		}
		return paddedString;
	}
	
}

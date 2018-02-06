package com.splicemachine.utils;

public class Power {

	/**
	 *  @author amitmudgal
	 * POWER function gives the user ability to get exponent of the base value.
	 * @param base
	 * @param exponent
	 * @param out 
	 * @return  
	 */
	public static int Power(int base, int exponent) {
 		int out;
		if(exponent == 0 && base == 0)
			out =1;
		else
		{
			out =1;
			for(int i=1;i<=exponent;i++){
				out = base * out; 
			}
		}
		return out;
	}
	
}

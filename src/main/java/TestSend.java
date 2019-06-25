/**
 * @author Cornelius Specht
 *
 */

public class TestSend extends Thread{

	private byte[] udpFrame= new byte[13];
	private byte[] header = new byte[5];
	//uid 0x4006 cargo
	//uid 0x4007 dampf
	//dlc has to be 6 if you want to control the speed and uid has to be set
	private int uid = 0;
	private char response = 0;
	private char command = 0; 
	private char prio = 0;
	private char dlc = 5; 
	private char[] data = new char[dlc];
	private char hash = 0;
	int cargoId = 0x4006;
	int steamId = 0x4007;
	

	
	/**
	 * DEFAULT
	 * Constructor
	 */
	public TestSend() {
		udpFrame[0] = (byte) prio; //(cargoId >> 24);
		udpFrame[1] = (byte) command;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;			
	}
	
	/**
	 * @param dataLength
	 * Constructor
	 */
	public TestSend(int dataLength) {
		udpFrame[0] = (byte) (uid >> 24);
		udpFrame[1] = (byte) uid;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dataLength;			
	}
	

	
//	private static final int sizeOfIntInHalfBytes = 8;
//	private static final int numberOfBitsInAHalfByte = 4;
//	private static final int halfByte = 0x0F;
//	private static final char[] hexDigits = { 
//	    '0', '1', '2', '3', '4', '5', '6', '7', 
//	    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
//	};				
	
//	public static String decToHex(int dec) {
//	    StringBuilder hexBuilder = new StringBuilder(sizeOfIntInHalfBytes);
//	    hexBuilder.setLength(sizeOfIntInHalfBytes);
//	    for (int i = sizeOfIntInHalfBytes - 1; i >= 0; --i)
//	    {
//	      int j = dec & halfByte;
//	      hexBuilder.setCharAt(i, hexDigits[j]);
//	      dec >>= numberOfBitsInAHalfByte;
//	    }
//	    return hexBuilder.toString(); 
//	}
	
	/**
	 * @return udpFrame
	 * send stop to all
	 */
	public byte[] stopTrain() {
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
			
			if (i <= 4) {
				udpFrame[5+i] = (byte)data[i];
			} 
			
			if (i >= 4) {
				udpFrame[5+i] = 0; 			
			}
		}
		return udpFrame;
	}
	
	/**
	 * @param id
	 * id of the train
	 * @return udpFrame
	 * send stop command to the train id
	 */
	public byte[] stop(int id)   {
		
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];		
			
			if (i == 0) {
				udpFrame[5+i] = 0;		
			}
			if (i == 1) {
				udpFrame[5+i] = 0;		
			}
			if (i == 2) {
				System.out.println("id: " + getFirstByteOfId(id));
				udpFrame[5+i] = (byte) getFirstByteOfId(id);		
			}
			
			if (i == 3) {
				System.out.println("id2: " + getSecondByteOfId(id));
				udpFrame[5+i] = (byte) getSecondByteOfId(id);		
			}
			
			if (i >= 4) {
				udpFrame[5+i] = 0;
			}
		}
		return udpFrame;
	}
	
	/**
	 * @param id
	 * id of the train
	 * @return udpFrame
	 * send horn on at train id 
	 */
	public byte[] hornOn(int id) {
		dlc = 6;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 12;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];	
			if (i == 2) {
				System.out.println("id: " + getFirstByteOfId(id));
				udpFrame[5+i] = (byte) getFirstByteOfId(id);		
			}
			
			if (i == 3) {
				System.out.println("id2: " + getSecondByteOfId(id));
				udpFrame[5+i] = (byte) getSecondByteOfId(id);		
			}
			
			if (i == 4) {
				udpFrame[5+i] = 3;
			}
			if (i == 5) {
				udpFrame[5+i] = 1;
			}
		}
		return udpFrame;
		
		
	}
	
	/**
	 * @param id
	 * id of the train
	 * @return udpFrame
	 * send horn off to a train ID
	 */
	public byte[] hornOff(int id) {
		dlc = 6;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 12;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];	
			if (i == 2) {
				System.out.println("id: " + getFirstByteOfId(id));
				udpFrame[5+i] = (byte) getFirstByteOfId(id);		
			}
			
			if (i == 3) {
				System.out.println("id2: " + getSecondByteOfId(id));
				udpFrame[5+i] = (byte) getSecondByteOfId(id);		
			}
			
			if (i == 4) {
				udpFrame[5+i] = 3;
			}
			if (i == 5) {
				udpFrame[5+i] = 0;
			}
		}
		return udpFrame;	
		
	}
	
	/**
	 * @param id
	 * id of the train
	 * @return udpFrame
	 * Send Light On to a train Id
	 */
	public byte[] lightOn(int id) {
		dlc = 6;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 12;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];	
			if (i == 2) {
				System.out.println("id: " + getFirstByteOfId(id));
				udpFrame[5+i] = (byte) getFirstByteOfId(id);		
			}
			
			if (i == 3) {
				System.out.println("id2: " + getSecondByteOfId(id));
				udpFrame[5+i] = (byte) getSecondByteOfId(id);		
			}
			
			if (i == 4) {
				udpFrame[5+i] = 0;
			}
			if (i == 5) {
				udpFrame[5+i] = 1;
			}
		}
		return udpFrame;
		
	}
	
	/**
	 * @param id
	 * id of the train
	 * @return udpFrame
	 * Send Light off to a TrainID
	 */
	public byte[] lightOff(int id) {
		dlc = 6;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 12;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];	
			if (i == 2) {
				System.out.println("id: " + getFirstByteOfId(id));
				udpFrame[5+i] = (byte) getFirstByteOfId(id);		
			}
			
			if (i == 3) {
				System.out.println("id2: " + getSecondByteOfId(id));
				udpFrame[5+i] = (byte) getSecondByteOfId(id);		
			}
			
			if (i == 4) {
				udpFrame[5+i] = 0;
			}
			if (i == 5) {
				udpFrame[5+i] = 0;
			}
		}
		return udpFrame;	
		
	}
	
	/**
	 * @return udpFrame
	 * Send go to All
	 */
	public byte[] go() {
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
			
			if (i >= 4) {
				udpFrame[5+i] = 1; 			
			}
		}
		return udpFrame;	
	}
	
	/**
	 * @param id
	 * id of the train
	 * @return udpFrame
	 * Give a go for the provided train ID
	 */
	public byte[] go(int id) {	
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
			if (i == 2) {
				udpFrame[5+i] = 0;//(byte) getFirstByteOfId(id);
			}
			if (i == 3) {
				udpFrame[5+i] = (byte) id;//getSecondByteOfId(id);			
			}
			if (i >= 4) {
				udpFrame[5+i] = 1; 			
			}
		}
		return udpFrame;
	}

	/**
	 * @param speed
	 * The speed that we want to set up
	 * @return udpFrame
	 * The MAX SPEEED 1023!!!
	 * DLC HAS TO BE 6 to SET THE SPEED Otherwise you can't set the SPEED, defined by Maerklin
	 */
	public byte[] setSpeed (int speed) {
		
		//dlc = 6 mandatory to set the speed!
		dlc = 6;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 8;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;
			
		String s = intToHex(speed);
		System.out.println("hexString :" + s);
		byte[] hexData = hexStringToByteArray(s);

		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
		}
		
		for (int i = 0; i < data.length; i++) {
					
			if (i == 2) {
				udpFrame[5+i] = (byte)getFirstByteOfId(steamId);
			}
			if (i == 3) {
				udpFrame[5+i] = (byte)getSecondByteOfId(steamId);
			}
			if (i == 4 && hexData.length == 2) {
				udpFrame[5+i] = hexData[1];		
			} 
			if (i == 5) {
				udpFrame[5+i] = hexData[0];
			}

		}
		return udpFrame;	
	}
	
	/**
	 * @return udpFrame
	 * The MAX SPEEED 1023!!!
	 * DLC HAS TO BE 4 to get THE SPEED Otherwise you can't set the SPEED, defined by Maerklin
	 */
	public byte[] getSpeed () {
		
		//dlc = 4 mandatory to get the speed!
		dlc = 4;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 8;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;
			
		//String s = intToHex(speed);
		//System.out.println("hexString :" + s);
		//byte[] hexData = hexStringToByteArray(s);
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
		}
		
		for (int i = 0; i < data.length; i++) {
					
			if (i == 2) {
				udpFrame[5+i] = (byte)getFirstByteOfId(cargoId);
			}
			if (i == 3) {
				udpFrame[5+i] = (byte)getSecondByteOfId(cargoId);
			}
//			if (i == 4 && hexData.length == 2) {
//				udpFrame[5+i] = hexData[1];		
//			} 
//			if (i == 5) {
//				udpFrame[5+i] = hexData[0];
//			}

		}
		return udpFrame;	
	}


	public byte[] getWater () {
		dlc = 7;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 14;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8); 160
		udpFrame[3] = (byte) 1798;
		udpFrame[4] = (byte) dlc;
	/*
		dlc = 6;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 7;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 114;
		udpFrame[4] = (byte) dlc;
	*/
		//String s = intToHex(speed);
		//System.out.println("hexString :" + s);
		//byte[] hexData = hexStringToByteArray(s);
		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
		}

		for (int i = 0; i < data.length; i++) {

			if (i == 2) {
				udpFrame[5+i] = (byte)getFirstByteOfId(steamId);
			}
			if (i == 3) {
				udpFrame[5+i] = (byte)getSecondByteOfId(steamId);
			}
			if (i == 4) {
				udpFrame[5+i] = (byte)4;
			}
			if (i == 5) {
				udpFrame[5+i] = (byte)237;
			}
			if (i == 6) {
				udpFrame[5+i] = (byte)1;
			}
		}
		return udpFrame;
	}

	public byte[] getSand () {
		dlc = 7;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 14;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 1798;
		udpFrame[4] = (byte) dlc;

		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
		}

		for (int i = 0; i < data.length; i++) {

			if (i == 2) {
				udpFrame[5+i] = (byte)getFirstByteOfId(steamId);
			}
			if (i == 3) {
				udpFrame[5+i] = (byte)getSecondByteOfId(steamId);
			}
			if (i == 4) {
				udpFrame[5+i] = (byte)12; //idx number
			}
			if (i == 5) {
				udpFrame[5+i] = (byte)237;
			}
			if (i == 6) {
				udpFrame[5+i] = (byte)1;
			}
		}
		return udpFrame;
	}

	public byte[] getOil () {
		dlc = 7;
		data = new char[dlc];
		udpFrame[0] = (byte) prio ;
		udpFrame[1] = (byte) 14;
		udpFrame[2] = (byte) 15; // >> 8;//(uid >> 8);
		udpFrame[3] = (byte) 1798;
		udpFrame[4] = (byte) dlc;

		for (int i = 0; i < data.length; i++) {
			udpFrame[5+i] = (byte)data[i];
		}

		for (int i = 0; i < data.length; i++) {

			if (i == 2) {
				udpFrame[5+i] = (byte)getFirstByteOfId(steamId);
			}

			if (i == 3) {
				udpFrame[5+i] = (byte)getSecondByteOfId(steamId);
			}

			if (i == 4) {
				udpFrame[5+i] = (byte)8;
			}

			if (i == 5) {
				udpFrame[5+i] = (byte)237;
			}
			if (i == 6) {
				udpFrame[5+i] = (byte)1;
			}
		}
		return udpFrame;
	}
	
	
	/**
	 * @param direction
	 * sets the direction of the train based on the documentation rules
	 * @return udpFrame
	 * DLC HAS TO BE 5 TO SET THE DIRECTION (Maerklin) 
	 * DLC HAS TO BE 4 TO GET THE DIRECTION (Maerklin) 
	 * set the direction there are 4 options.
	 * 0 = direction remain 
	 * 1 = direction forwards
	 * 2 = direction backwards
	 * 3 = direction switch
 	 */
	public byte [] setDirection (int direction) {
		udpFrame[1] = (byte) 10;
		
		for (int i = 0; i < data.length; i++) {
			if (i == 2) {
				udpFrame[5+i] = (byte)getFirstByteOfId(cargoId);
			}
			if (i == 3) {
				udpFrame[5+i] = (byte)getSecondByteOfId(cargoId);
			}
			if (i == 4) {
				udpFrame[5+i] = (byte)direction;
			} 
		}
		return udpFrame; 
	}
	
	/**
	 * @param value
	 * the value that we will convert
	 * @return hex String
	 * Transform an Integer Value into an HEX Value
	 */
	public String intToHex(int value) {
		Integer i = value;
		String hex = Integer.toHexString(i); //"0" +
		int hexCount = hex.length();
		//ADD LEADING ZERO IF THE HEX is UNEVEN
		if(hexCount % 2 != 0) {
			String zero = "0"; 
			hex = zero + hex.substring(0);
		}
		//String hex = Integer.toHexString(i);
		System.out.println("Test: " + hex);
		return hex; 
	}

	/**
	 * @param s
	 * The string that we will transform into ByteArray
	 * @return data
	 * Get an Hex String and 
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}


	/**
	 * @param id
	 *
	 * @return result
	 */
	public int getFirstByteOfId (int id) {
		int res = (id >>> 8);
		//System.out.println("FirstByte: " + res);
		return res;
	} 
	
	/**
	 * @param id
	 * @return result
	 */
	public int getSecondByteOfId (int id) {
		//Shift the bits to the left and then shift it to the right to get the last 4 bit
		int res = ((id << 28)>> 28);
		//System.out.println("SecondByte: " + res);
		return res; 
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
}


// UID LOK: http://192.168.0.2/config/lokomotive.cs2 ----> uid=0x4007 
// UID is necessary to set the speed of the lok etc. 

	public class SendCan {
		
	/**************************************************************************************
	 * Check if the Message is from a cs2/ms2, returns TRUE or FALSE, the definition by 
	 * maerklin is : the bits 7-9 have the value 110 (binary) 
	 * thats how we can find out if the message is from a cs2 or ms2 because all other message 
	 * which dont have the encoding like 110 will be older and aren't sent by a cs2/ms2
	 **************************************************************************************/
		public boolean isCS2Msg(char Hash) {
			return ((Hash & 0x0380) == 0x0300);
		}		
		
		
	/**************************************************************************************
	 * Calculate the Hash Value
	 **************************************************************************************/
		public int CalcHash(int UID) {
			return ((((UID & 0x0000ffff) ^ (UID >> 16)) & 0xFF7F) | 0x0300);
		}
		
		
	/**************************************************************************************
	 * Decode CAN ID
	 **************************************************************************************/
		public void DecodeId (int canId, char hash, char response, char command, char prio ) {
			hash = (char) ((canId >>0) & 0x0000ffff);
			System.out.println(hash);
			response = (char) ((canId >>16) & 0x0000ffff);
			System.out.println(response);
			command = (char) ((canId >>17) & 0x0000ffff);
			System.out.println(command);
			prio = (char) ((canId >>25) & 0x0000ffff);
			System.out.println(prio);
		}
		

	/**************************************************************************************
	 * Encode CAN ID 
	 **************************************************************************************/
		public int EncodeId (int canId, int hash, char response, char command, char prio) {
			
			return ((int) (hash << 0) 
					| (int) (response << 16) 
					| (int) (command << 17) 
					| (int) (prio <<  25));			
		}
		
	/**************************************************************************************
	 * Decode UDP Package
	 **************************************************************************************/
		public void DecodeUdp (int id, char dlc, char[]data, byte[] udpFrame) {
			
			id = ((int)udpFrame[0] << 24) | 
					((int)udpFrame[1] << 16) | 
					((int) udpFrame[2] << 8) |
					((int) udpFrame[3] << 0);
			
			dlc = (char)udpFrame[4];
			
			for (int i = 0; i < data.length; i++) {
				data[i] = (char)udpFrame[5+i];
				System.out.println("data: " + data[i]);
			}
		}

	/**************************************************************************************
	 * Encode UDP Package
	 **************************************************************************************/		
		public byte[] EncodeUdp (int id, char dlc, char[] data) {
			byte[] udpFrame = new byte[13];
			udpFrame[0] = (byte) ((id >> 24) & 0x000000FF);
			udpFrame[1] = (byte) ((id >> 16) & 0x000000FF);
			udpFrame[2] = (byte) ((id >> 8) & 0x000000FF);
			udpFrame[3] = (byte) ((id >> 0) & 0x000000FF);
			udpFrame[4] = (byte) dlc;
			//30 05 00 00 00 00 00 00 --> stop cmd
			for (int i = 0; i < data.length; i++) {
				udpFrame[5+i] = (byte)data[i];
			}
			
			return udpFrame;
		}
		
	/*	public void send(byte[] data, int port, String ipAdress ) {
			
			InetAddress ip = new InetAddress(ipAdress);
			DatagramPacket dataGram  = null; 
			DatagramSocket send = null;
			try {
				send = new DatagramSocket(port);
				dataGram = new DatagramPacket(data, 8);
				send.send(dataGram);
				send.close();	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		} */
	}



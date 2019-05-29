
public class DecodeCan {
	//private byte[] udpFrame= new byte[13];

	private int uid = 0;
	private char response = 0;
	private char command = 0; 
	private char prio = 0;
	private char dlc = 5; 
	private char[] data; 
	int cargoId = 0x4006;
	int steamId = 0x4007;
	public DecodeCan (byte[] udpFrame) {
		prio = (char) udpFrame[0];
		command = (char) udpFrame[1];
		
		dlc = (char) udpFrame[4];
		
		data = new char[dlc];
		
	}
	
	
}

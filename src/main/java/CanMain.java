import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.concurrent.TimeUnit;

//import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;


public class CanMain {

	private static DatagramSocket dms;
	private static TestSend send;

	public static void main(String[] args) throws IOException, InterruptedException {
		
//		InetSocketAddress endpoint = new InetSocketAddress(ip,port);
//		clientConn.connect(endpoint);
//		InetAddress addresse = InetAddress.getByName(ipAdress);
		String ipAdress = "192.168.0.2";
		
		EncodeCan ec = new EncodeCan();

		ec.start();


//		SendCan udp = new SendCan();				
		
		
		
		
		//Thread sendCan = new Thread(CanMain, "Thread1");
		//myThread.start();
		send = new TestSend();
		sendCanToCS3(ipAdress);
		send.start();


		
		
	//	pingHost(addresse);
		
		//char[] M_CAN_PING_CS2 = { 0x00, 0x30, 0x47, 0x11, 0x08, 0x00, 0x00, 0x00, 0x00, 0x03, 0x08, 0xff, 0xff };
					
//		byte[] udpFrame = new byte[13];
//		char[] data = new char[8];
//		int uid = 6168;
//		char response = 0; 
//		char command = 0; 
//		char prio = 0; 
//		char dlc = 5; 
//		int[] testFrame = new int[13];
//		
//		TestSend send = new TestSend();
//		
//		udpFrame = send.getSpeed();
		//udpFrame = send.go();
		//udpFrame = send.setSpeed(60);
		//udpFrame = send.stopAll();
//		sendTCP(udpFrame, 0, udpFrame.length);
		
		//udpFrame = send.setDirection(3);
		//sendTCP(udpFrame, 0, udpFrame.length);
		
//		udpFrame = send.stopAll();
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
//		
//		udpFrame = send.setDirection(3);
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
//		
//		udpFrame = send.setSpeed(80);
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
//		int cargoId = 0x4006;
//
//		udpFrame = send.setSpeed(50);

		//sendTCP(udpFrame, 0, udpFrame.length);
		//udpFrame = constructCan(udp, udpFrame, data, uid, response, command, prio, dlc);
		
//		System.out.println("udpLength: " + udpFrame.length);
//		for (int i = 0; i < udpFrame.length; i++) {
//			System.out.println("udpFrame["+i+"]: " + udpFrame[i]);
//		}
//		
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
		//sendUDP(udpFrame, addresse);
		
		//udpFrame = send.lightOff(cargoId);
		//sendTCP(udpFrame,0,udpFrame.length); 
		
		//udp.DecodeUdp(id, dlc, data, udpFrame);
		
		//udp.send(data, 15730, "192.168.0.2");
			
		//UdpConnection udp = new UdpConnection();
		//udp.run();
	}
	/**************************************************************************************
	 * SEND CAN MESSAGE
	 * @throws UnknownHostException 
	 ***************************************************************************************/
	public static void sendCanToCS3 (String ipAdress) throws UnknownHostException {
		InetAddress addresse = InetAddress.getByName(ipAdress);
		//SendCan udp = new SendCan();
		//String ipAdress = "192.168.0.2";
		
		byte[] udpFrame = new byte[13];
		char[] data = new char[8];
		int uid = 6168;
		char response = 1; 
		char command = 0; 
		char prio = 0; 
		char dlc = 6; 
		int[] testFrame = new int[13];
		
//		TestSend send = new TestSend();
//		
//		Thread sendCan = new Thread(send, "second");
//		
		
		
		int cargoId = 0x4007;

		udpFrame = send.go();
		
//		udpFrame = send.stopAll();
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
//		
//		udpFrame = send.setDirection(3);
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
//		
//		udpFrame = send.setSpeed(80);
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
		
		System.out.println("udpLength: " + udpFrame.length);
		for (int i = 0; i < udpFrame.length; i++) {
			System.out.println("udpFrame["+i+"]: " + udpFrame[i]);
		}
		
		sendTCP(udpFrame, 0, udpFrame.length);
		//TimeUnit.SECONDS.sleep(1);
	}
	
	
	
	/**************************************************************************************
	 * CONSTRUCT CAN MESSAGE
	 ***************************************************************************************/
	/*public static byte[] constructCan (SendCan udp, byte[] udpFrame, char[] data, int uid, char response, char command, char prio, char dlc) {
		
		//UDP FRAME HAS TO BE the length of 13
		//6168 entspricht hexadezimal 0x1818 --> liegt damit im märklin definierten wertebereich für die UID 	
		int canId = 6168; 
		System.out.println("ID raw: " + uid);
		//DLC MAXIMUL 8 because that is the max of bytes which is usable
			
		//data = new char[dlc];
		System.out.println("Data.length: " + data.length);
		for (int i = 0; i < data.length; i++) {
			//#if (i == 4)
				//data[4] = 0011;
			data[i] = 0;
		}
		int hash = udp.CalcHash(uid);
		System.out.println("hash: " + hash);
		
		//int encodeIdd = udp.EncodeId(canId, hash, response,command, prio);
		
		int encodeId = udp.EncodeId(canId, hash, response, command, prio);
		System.out.println("ID encoded: " + uid);
						
		udpFrame = udp.EncodeUdp(encodeId, dlc, data);
		
		//show the udp frame at cmd
		for (int i = 0; i < udpFrame.length; i++) {
			System.out.println("udpFrame["+i+"]: " + udpFrame[i]);
		}
		byte[] udpFr = new byte[13];
		
		udpFr = udpFrame;
		return udpFr;
	}
	*/
	/*************************************************************************************** 
	 * PING HOST
	 ***************************************************************************************/
	public static void pingHost (InetAddress addresse) {
		try {
			if(addresse.isReachable(5000)) {
				System.out.println("Host is reachable!");
			}
			else {
				System.out.println("sorry not reachable");
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
	
	/*************************************************************************************** 
	 * SEND UDP-FRAME via UDP to HOST 
	 ***************************************************************************************/
	public static void sendUDP (byte[] udpFrame,InetAddress addresse) {
		try { 	
			
			dms = new DatagramSocket();
			DatagramPacket dmp = new DatagramPacket(udpFrame, udpFrame.length, addresse,15730);
			dms.send(dmp);
			System.out.println("SEND!");
	  
		} catch (IOException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
	}
	
	/*************************************************************************************** 
	 * SEND TCP-FRAME via TCP to HOST 
	 ***************************************************************************************/
	public static void sendTCP (byte[] udpFrame, int start, int len) {
		try { 	
			String ip = "192.168.0.2";
			int port = 15731;
			
			Socket socket = new Socket(ip, port);
			OutputStream out = socket.getOutputStream(); 
		    DataOutputStream dos = new DataOutputStream(out);
		    
		    if (len > 0) {
		    	dos.write(udpFrame, start, len);
		    	
		    }
		    
		    socket.close();
			
		} catch (IOException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}
	}

		
	
	
	
	
}


import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.apache.log4j.BasicConfigurator;

import java.sql.*;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
//import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.nio.charset.StandardCharsets;


public class CanMain {

	private static DatagramSocket dms;
	private static TestSend send;
	protected static int coaches;


	public static void main(String[] args) throws IOException, InterruptedException, EventHubException, ExecutionException, InterruptedException, IOException {

		String ipAdress = "192.168.0.2";

		//We will use this variable later to injest data into eventhub
		String payload = "";

		//We'll use this variable to set the datatype for the sql server data
		String dType = "";

		int iterations;			//Number of iterations that we will perform on the resources status.
		//final Gson gson = new GsonBuilder().create();
								//"jdbc:sqlserver://<server>:<port>;databaseName=AdventureWorks;user=<user>;password=<password>"
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		String connectionUrl = "jdbc:sqlserver://edu.hdm-server.eu;databaseName=TRAIN_IOTHUB;user=TRAIN_DBA;password=Password123"; //ports: 1432. 1433. 1434


		//----UNCOMMENT TO CONNECT TO EVENTHUB----
		//This configures the log4j framework/package, necessary to send data to eventhub
		BasicConfigurator.configure();

		//Credentials to connect to eventhub
		final ConnectionStringBuilder connStr = new ConnectionStringBuilder()
				.setNamespaceName("BIAcademyNS")
				.setEventHubName("eventhubmarklinsteamlok")
				.setSasKeyName("RootManageSharedAccessKey")
				.setSasKey("jiuer6fxPoEnrkrxzVwWVdRi1qw2+5A3rAoevEsiEVs=");

		//final Gson gson = new GsonBuilder().create();

		// The Executor handles all asynchronous tasks and this is passed to the EventHubClient instance.
		// This enables the user to segregate their thread pool based on the work load.
		// This pool can then be shared across multiple EventHubClient instances.
		// The following sample uses a single thread executor, as there is only one EventHubClient instance,
		// handling different flavors of ingestion to Event Hubs here.
		final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

		// Each EventHubClient instance spins up a new TCP/SSL connection, which is expensive.
		// It is always a best practice to reuse these instances. The following sample shows this.
		final EventHubClient ehClient = EventHubClient.createSync(connStr.toString(), executorService);

		/*//START USERINTERFACE
		final UserInterfaceChart uic = new UserInterfaceChart();
		uic.go();
		*/




		//Temporary, the program is controlled by iterations. Tip: -1 = Many iterations.
/*		System.out.print("How many iterations do you want to perform?");
		iterations = in.nextInt();
		System.out.println("How many coaches are attached?");
		coaches = in.nextInt();
*/
		//It connects to the CS3, starts to "listen" to the data streamed by the CS3 and filter the resources.
		GetCan DForSQL = new GetCan(ipAdress,15731);
        DForSQL.start();

		GetCan DForAzure = new GetCan(ipAdress, 15731);
        DForAzure.start();

		//uncomment to send Data
		send = new TestSend();
		//uncomment to send Data
		sendCanToCS3(ipAdress, 10);
        DForSQL.stopListener();
		DForAzure.stopListener();

		//----SEND TO MSSQL----
		// create DateFormatter for the right format of date for SQLServer.
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date = new Date();

		try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
			for (int i = 0; i < DForSQL.payload.size(); i++) {
				String SQL = "INSERT INTO [dbo].[T_RESOURCES_USAGE_DATASET] ([DATATYPE], [RECORDING_START_TIME], "
						+ "[TIME_STAMP], [DATASET], [DELIMITER])"
						+ "VALUES ('STEAMDATA', '"
							+ sdf.format(date).toString() + "','"
							+ sdf.format(date).toString() + "','"
							+ DForSQL.payload.get(i)
						+ "', ';')";
				System.out.println("SQL: " + SQL);
				//ResultSet rs =
				try {
					stmt.executeUpdate(SQL);
				} catch (SQLException e) {
					e.printStackTrace();
				}

			}

		}
		// Handle any errors that may have occurred.
		catch (SQLException e) {
			e.printStackTrace();
		}






		//char[] M_CAN_PING_CS2 = { 0x00, 0x30, 0x47, 0x11, 0x08, 0x00, 0x00, 0x00, 0x00, 0x03, 0x08, 0xff, 0xff };

		byte[] udpFrame = new byte[13];
		char[] data = new char[8];
		int uid = 6168;
		char response = 0;
		char command = 0;
		char prio = 0;
		char dlc = 5;
		int[] testFrame = new int[13];

		TestSend send = new TestSend();
		udpFrame = send.setOil();
		sendTCP(udpFrame, 0, udpFrame.length);
		
		System.out.println("udpLength: " + udpFrame.length);
		for (int i = 0; i < udpFrame.length; i++) {
			System.out.println("udpFrame["+i+"]: " + udpFrame[i]);
		}
//		
//		sendTCP(udpFrame, 0, udpFrame.length);
//		TimeUnit.SECONDS.sleep(1);
		//sendUDP(udpFrame, addresse);
		
		//udpFrame = send.lightOff(cargoId);
		//sendTCP(udpFrame,0,udpFrame.length); 
		
		//udp.DecodeUdp(id, dlc, data, udpFrame);
		
		//udp.send(data, 15730, "192.168.0.2");

	}

	public void sendToAzure(GetCan DForAzure, LinkedList payload, EventHubClient ehClient, ScheduledExecutorService executorService) throws EventHubException {
		//----SEND JSON FORMAT TO AZURE EVENTHUB----
		try{
			System.out.println("\t---PayloadJSON output---");


			for(int i = 0; i<DForAzure.jsonPayload.size(); i++){ //ec.jsonPayload.size()
				payload = DForAzure.payload.get(i);

						 //(LinkedList <String>) DForAzure.jsonPayload.get(i);
				System.out.println("@ " + payload);
				byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8.name());
				EventData sendEvent = EventData.create(payloadBytes);

				// Send - not tied to any partition
				// Event Hubs service will round-robin the events across all Event Hubs partitions.
				// This is the recommended & most reliable way to send to Event Hubs.
				ehClient.sendSync(sendEvent);

			}
		}finally {
			ehClient.closeSync();
			executorService.shutdown();
		}

	}

	/**************************************************************************************
	 * SEND CAN MESSAGE
	 * @throws UnknownHostException 
	 ***************************************************************************************/
	public static void sendCanToCS3 (String ipAdress, int iterations) throws UnknownHostException {
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
		int cargoId = 0x4007;

		//If the variable is setted up as -1, Max Limit = 500
		if(iterations == -1) iterations = 500;

		for (int i = 0; i<iterations; i++) {

			//ask status of water
			udpFrame = send.getWater();
			sendTCP(udpFrame, 0, udpFrame.length);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//ask status of oil
			udpFrame = send.getOil();
			sendTCP(udpFrame, 0, udpFrame.length);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//ask status of sand
			udpFrame = send.getSand();
			sendTCP(udpFrame, 0, udpFrame.length);
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/////////////////DEBUG PRINT UDP-Package/////////////////
/*		System.out.println("udpLength: " + udpFrame.length);
		for (int i = 0; i < udpFrame.length; i++) {
			System.out.println("udpFrame["+i+"]: " + udpFrame[i]);
}*/

		}
	}

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

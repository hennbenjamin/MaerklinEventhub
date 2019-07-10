
import com.microsoft.azure.eventhubs.ConnectionStringBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.EventHubException;
import org.apache.log4j.BasicConfigurator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;

//import javax.xml.bind.annotation.adapters.HexBinaryAdapter;


public class CanMain {

	private static DatagramSocket dms;
	private static TestSend send;
	protected static int coaches;



	public static void main(String[] args) throws IOException, InterruptedException, EventHubException, ExecutionException, InterruptedException{
		String ipAdress = "192.168.0.2";

		//We will use this variable later to injest data into eventhub
		String payload = "";

		//We'll use this variable to set the datatype for the sql server data
		String dType = "STEAMDATA";

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
		//final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

		// Each EventHubClient instance spins up a new TCP/SSL connection, which is expensive.
		// It is always a best practice to reuse these instances. The following sample shows this.
		//final EventHubClient ehClient = EventHubClient.createSync(connStr.toString(), executorService);



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
		//GET Data for SQL Server


        //GET Data for Azure
		//GetCan DForAzure = new GetCan(ipAdress, 15731);
        //DForAzure.start();

		//uncomment to send Data
		send = new TestSend();
		//uncomment to GET Data
		//sendCanToCS3(ipAdress, connectionUrl, dType);


		byte[] udpFrame = new byte[13];
		String log = "";

		try {

			byte[] packatData;
			DatagramSocket ds = new DatagramSocket(15731);
			DatagramSocket dsReceive = new DatagramSocket(15730);
			InetAddress ia;
			InetAddress ib;
			ia = InetAddress.getByName("192.168.0.2");
			ib = InetAddress.getByName("192.168.0.104");
			int port = 15731;
			udpFrame = send.getSpeed();
			int i = 0;


			//DatagramPacket sendpacket = new DatagramPacket(testData, testData.length);


				System.out.println("I: " + i);
				DatagramPacket sendPacket = new DatagramPacket( udpFrame, udpFrame.length, ia, 15731 );
				System.out.println("1");
				ds.send( sendPacket );
				System.out.println("2");
				// Auf Anfrage warten
				sendPacket = new DatagramPacket( new byte[13], 13, ib, 15730 );
				dsReceive.receive( sendPacket );
				System.out.println("3");
				//comment

				// Empfänger auslesen
				InetAddress address = sendPacket.getAddress();
				System.out.println("4");
				int         port2    = sendPacket.getPort();
				int         len     = sendPacket.getLength();
				byte[]      data    = sendPacket.getData();

				for (int j = 0; j < data.length; j++) {
					System.out.println("data[" + j + "]: " + data[j]);
					/*System.out.printf( "Anfrage von %s vom Port %d mit der Länge %d:%n%s%n",
							address, port2, len, new String( data, 0, len ) );
					*/
				}


				i++;


		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}



		//Send Command to CS3
		//sendCommandToCS3();


	}

	//----Send Command to CS3----
	public static void sendCommandToCS3 () {

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


		System.out.println("udpLength: " + udpFrame.length);
		for (int i = 0; i < udpFrame.length; i++) {
			System.out.println("udpFrame["+i+"]: " + udpFrame[i]);
		}
		sendTCP(udpFrame, 0, udpFrame.length);


	}

	//----Send received Data from CAN to Azure----
	public static void sendToAzure(GetCan DForAzure, EventHubClient ehClient, ScheduledExecutorService executorService) throws EventHubException, UnsupportedEncodingException {
		//----SEND JSON FORMAT TO AZURE EVENTHUB----
		try{
			System.out.println("\t---PayloadJSON output---");
            String payload = "";

			for(int i = 0; i<DForAzure.jsonPayload.size(); i++){ //ec.jsonPayload.size()
                payload  = DForAzure.payload.get(i);

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
    //----Send received Data from CAN to MS SQL----
	public static void sendToMSSQL(GetCan DForSQL, String connectionUrl, String dType) {

        // create DateFormatter for the right format of date for SQLServer.
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = new Date();

        try (Connection con = DriverManager.getConnection(connectionUrl); Statement stmt = con.createStatement();) {
            for (int i = 0; i < DForSQL.payload.size(); i++) {
                String SQL = "INSERT INTO [dbo].[T_RESOURCES_USAGE_DATASET] ([DATATYPE], [RECORDING_START_TIME], "
                        + "[TIME_STAMP], [DATASET], [DELIMITER])"
                        + "VALUES ('" + dType + "','"
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
    }

	/**************************************************************************************
	 * SEND CAN MESSAGE
	 * @throws UnknownHostException 
	 ***************************************************************************************/
	public static void sendCanToCS3 (String ipAdress, String connectionUrl, String dType) throws UnknownHostException, InterruptedException {
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
		//if(iterations == -1) iterations = 500;
		GetCan DForSQL = new GetCan(ipAdress,15731);
		DForSQL.start();
		byte[] testData = new byte[ 13 ];
		String log = "";



		//DForSQL.stopListener();
		//DForAzure.stopListener();
		//sendToAzure(DForAzure,ehClient,executorService);


		//sendTCP(udpFrame, 0, udpFrame.length);

/*		while (true) {
			long millis = System.currentTimeMillis();
			DForSQL.startListener();

			//ask status of water
			udpFrame = send.getWater();
			sendTCP(udpFrame, 0, udpFrame.length);
			//sendToMSSQL(DForSQL, connectionUrl, dType);
			//DForSQL.stopListener();

			//ask status of oil
			udpFrame = send.getOil();
			sendTCP(udpFrame, 0, udpFrame.length);
			//sendToMSSQL(DForSQL, connectionUrl, dType);
			//DForSQL.stopListener();

			//ask status of sand
			udpFrame = send.getSand();
			sendTCP(udpFrame, 0, udpFrame.length);
			//sendToMSSQL(DForSQL, connectionUrl, dType);
			//DForSQL.stopListener();




			/////////////////DEBUG PRINT UDP-Package/////////////////
			System.out.println("udpLength: " + udpFrame.length);
			for (int i = 0; i < udpFrame.length; i++) {
				System.out.println("udpFrame["+i+"]: " + udpFrame[i]);
			}
			sendToMSSQL(DForSQL, connectionUrl, dType);
			DForSQL.stopListener();
			Thread.sleep(1000 - millis % 1000);
		}*/
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

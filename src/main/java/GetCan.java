import javax.swing.text.MaskFormatter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GetCan extends Thread{

	//private Socket tcp_socket;
	private Socket tcp_socket = null;
	private String ip; 
	private int port; 
	private static final DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	private int RoundCount = 0;
	boolean stop = false;
	private Date startTime = null;
	String dataset = "";
	String resource = "water";
	String resultCSV = "";
	String resultJSON = "";
	LinkedList<String> payload = new LinkedList<String>();
	ArrayList<String> SQLstment = new ArrayList<String>();
	public LinkedList<String> jsonPayload = new LinkedList<String>();

	@Override
	//Starts all the methods,
	public void run() {
		try {
			this.conn();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @param ip ip address of the CS3
	 * @param port port
	 */
	public GetCan(String ip, int port) {
		setIp(ip);
		setPort(port);
	}

	/**
	 *
	 * @throws IOException Input/Output exception
	 * @throws ParseException Parsing exception
	 */
	public void conn() throws IOException, ParseException
	{
		System.out.println("connected to: " +ip+ " port: " + port);
		
		InetSocketAddress endpoint = new InetSocketAddress(ip,port);
		
		byte[] bytes = new byte[13];
		byte[] data = new byte[13];

		InputStream tcp_inputStream;
		
		try {
			tcp_socket = new Socket();
			tcp_socket.connect(endpoint);
			
			tcp_inputStream = tcp_socket.getInputStream();
			tcp_inputStream.read(bytes);
			byte test = (byte)tcp_socket.getInputStream().read();
			
		} catch (UnknownHostException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}	
		
		int rowCount = 0;
        Gson gson = new GsonBuilder().create();
		//While trigger is false, it keeps listening
		while(stop == false) {

			for (int i = 0; i < data.length; i++) {
				data[i] = (byte)tcp_socket.getInputStream().read();
			}

			MaskFormatter mf2 = new MaskFormatter("[HHHHHHHH:HH][HH,HH,HH,HH,HH,HH,HH,HH]");
			mf2.setValueContainsLiteralCharacters(false);
			String hexNr = "00" + hexEncode(data);
			Date date = new Date();

			if(startTime == null)
				startTime = date;
			
			HashMap<String, String> dataMap = new HashMap();
			dataMap = translateToHashMap(hexNr);
			
//			for(int i = 0; i < dataMap.size(); i++) {
//				System.out.println("K/V: \t" + dataMap.keySet() + dataMap.values());
//			}
						
			String hexFormatted = mf2.valueToString(hexNr);
			//System.out.println("HEXFORMATTED: " + hexFormatted);
			 //99m 11µ>[00000f72:5]       0 [00,00,00,00,00]          
			StringBuilder water = new StringBuilder();
			//         [00000F72:0][50,00,00,00,00,00,00,00,000]

			water.append("[000e0f72:7][00,00,40,07,04,ed,04,00]");

			//TIMER STARTEN
			long timeStart = System.currentTimeMillis();
			long timeEnd;



			if (Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,00,40,07,04,ED,[A-F0-9]{2},[A-F0-9]{2}.)", hexFormatted)) {
				if(!Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,00,40,07,04,ED,04,[A-F0-9]{2}.)", hexFormatted) &&
						!Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,00,40,07,04,ED,01,[A-F0-9]{2}.)", hexFormatted)) {
					String lokId = hexFormatted.substring(20, 25).replace(",", "");
					//String Res = hexFormatted.substring(32, 34);
					int Res = data[10];
					if(Res < 0)
						Res += 256;
					/////////////////DEBUG////// ADD hexFormatted
					//System.out.println(hexFormatted);
					resultCSV = rowCount + ";" + sdf.format(date) + ";" + lokId + ";" + "Water" + ";" + Res + ";" + (int) (Res*31.3725) + ";" + RoundCount + ";";
					payload.add(resultCSV);
					resultJSON = "{"
                            + "\"RowCount\":" + rowCount + ","
                            + "\"Date\":" + "\"" + sdf.format(date).toString() + "\"" + ","
                            + "\"LokID\":" + lokId + ","
                            + "\"Resource\":" + "\"Water\"" + ","
                            + "\"ResValue\":" + Res + ","
                            + "\"ResCalc\":" + (int) (Res*31.3725) + ","
                            + "\"Round\":" + RoundCount
                            + "}";
					jsonPayload.add(resultJSON);
					dataset = lokId + ";" + CanMain.coaches + ";Water;" + Res + ";" + (int) (Res*31.3725) + ";" + RoundCount;
					/*SQLstment.add("INSERT INTO [dbo].[T_RESOURCES_USAGE_DATASET] ([DATATYPE], [RECORDING_START_TIME], " +
							"[TIME_STAMP], [DATASET], [DELIMITER])\n" +
							"VALUES (STEAMDATA, " + startTime + ", " + sdf.format(date) + ", " + dataset + ", " + ";");*/
					System.out.println(resultCSV);




					timeEnd = System.currentTimeMillis();
					System.out.println("WaterTimer: " + (timeEnd - timeStart));
					rowCount++;
				}
			}
			
			if (Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,00,40,07,08,ED,[A-F0-9]{2},[A-F0-9]{2}.)", hexFormatted)) {
				if(!Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,00,40,07,08,ED,01,[A-F0-9]{2}.)", hexFormatted)) {
					String lokId = hexFormatted.substring(20, 25).replace(",", "");
					//String Res = hexFormatted.substring(32,34);
					int Res = data[10];
					if (Res < 0)
						Res += 256;

					//System.out.println(hexFormatted);
					resultCSV = rowCount + ";" + sdf.format(date) + ";" + lokId + ";" + "Oil" + ";" + Res + ";" + (int) (Res * 11.7647) + ";" + RoundCount + ";";
					payload.add(resultCSV);
					resultJSON = "{"
							+ "\"RowCount\":" + rowCount + ","
							+ "\"Date\":" + "\"" + sdf.format(date).toString() + "\"" + ","
							+ "\"LokID\":" + lokId + ","
							+ "\"Resource\":" + "\"Oil\"" + ","
							+ "\"ResValue\":" + Res + ","
							+ "\"ResCalc\":" + (int) (Res*11.7647) + ","
							+ "\"Round\":" + RoundCount
							+ "}";
					jsonPayload.add(resultJSON);
					dataset = lokId + ";" + CanMain.coaches + ";Oil;" + Res + ";" + (int) (Res*11.7647) + ";" + RoundCount;
					/*SQLstment.add("INSERT INTO [dbo].[T_RESOURCES_USAGE_DATASET] ([DATATYPE], [RECORDING_START_TIME], " +
							"[TIME_STAMP], [DATASET], [DELIMITER])\n" +
							"VALUES (STEAMDATA, " + startTime + ", " + sdf.format(date) + ", " + dataset + ", " + ";");*/
					System.out.println(resultCSV);
					timeEnd = System.currentTimeMillis();
					System.out.println("OilTimer: " + (timeEnd - timeStart));
					rowCount++;
				}
			}
			
			if (Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,00,40,07,0C,ED,[A-F0-9]{2},[A-F0-9]{2}.)", hexFormatted)) {
				if(!Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,00,40,07,0C,ED,01,[A-F0-9]{2}.)", hexFormatted)) {
					String lokId = hexFormatted.substring(20, 25).replace(",", "");
					//String Res = hexFormatted.substring(32,34);
					int Res = data[10];
					if (Res < 0)
						Res += 256;

					//System.out.println(hexFormatted);
					resultCSV = rowCount + ";" + sdf.format(date) + ";" + lokId + ";" + "Sand" + ";" + Res + ";" + (int) (Res * 0.9803) + ";" + RoundCount + ";";
					payload.add(resultCSV);
					resultJSON = "{"
							+ "\"RowCount\":" + rowCount + ","
							+ "\"Date\":" + "\"" + sdf.format(date).toString() + "\"" + ","
							+ "\"LokID\":" + lokId + ","
							+ "\"Resource\":" + "\"Sand\"" + ","
							+ "\"ResValue\":" + Res + ","
							+ "\"ResCalc\":" + (int) (Res*0.9803) + ","
							+ "\"Round\":" + RoundCount
							+ "}";
					jsonPayload.add(resultJSON);
					dataset = lokId + ";" + CanMain.coaches + ";Sand;" + Res + ";" + (int) (Res*0.9803) + ";" + RoundCount;
					/*SQLstment.add("INSERT INTO [dbo].[T_RESOURCES_USAGE_DATASET] ([DATATYPE], [RECORDING_START_TIME], " +
							"[TIME_STAMP], [DATASET], [DELIMITER])\n" +
							"VALUES (STEAMDATA, " + startTime + ", " + sdf.format(date) + ", " + dataset + ", " + ";");*/
					System.out.println(resultCSV);
					timeEnd = System.currentTimeMillis();
					System.out.println("SandTimer: " + (timeEnd - timeStart));
					rowCount++;
				}
			}

			//if()

			//[0023a706:8]    r 17 [00,01,00,02,00,01,09,7e]

			//GET RoundCount from contact track Every Round --> RoundCount++
			if (Pattern.matches("(.[A-F0-9]{8}.[A-F0-9]{2}..00,01,00,02,00,01,[A-F0-9]{2},[A-F0-9]{2}.)", hexFormatted)) {
				String lokId = hexFormatted.substring(20 , 25).replace(",","");
				rowCount++;
				RoundCount++;
				//System.out.println(rowCount + ";" + "\t\tRound:" + RoundCount);

			}

			//System.out.println("sb: \t" + sb);			
			//else {
			//System.out.println(rowCount + ": \t" + hexFormatted);
			//}
		}
		closeConn(); 
	}

	//it stops conn()
	public void stopListener(){
		stop = true;
	}

	public String getResourceName(){
		return this.resource;
	}

	public void setResourceName(String res){
		this.resource = res;
	}

	//have Data Container where Data is separated into metadata and data
	private HashMap<String,String> translateToHashMap (String hex) {
		HashMap<String, String> map = new HashMap<String, String>();
		String metaData = hex.substring(0,8);
		String data = hex.substring(9,19);
		map.put(metaData, data);
		return map;
	}
	
	//Encoding the byte[] into hexadecimal Number, RETURN STRING
	private static String hexEncode(byte[] buf) {
		return hexEncode(buf, new StringBuilder()).toString();
	}
	
	//Encoding byte[] into hexadecimal Number, RETURN STRINGBUILDER
	public static StringBuilder hexEncode (byte[] buf, StringBuilder sb) {
		for (byte b : buf) {
			sb.append(String.format("%02x",b));
		}	
		return sb;
	}
	
	//CLOSE CONNECTION TCP SOCKET
	public void closeConn () {

		try {
			tcp_socket.shutdownInput();
			tcp_socket.close();
			
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}

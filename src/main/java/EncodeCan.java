import java.io.IOException;
import java.text.ParseException;

public class EncodeCan implements Runnable {

	
	/***************************************************************************************
	 * GET DATA VIA TCP
	 ***************************************************************************************/
//	public static void getData() {
//
//	}

	@Override
	public void run() {
		TcpConnection tcp = new TcpConnection("192.168.0.2",15731); 
		try {
			tcp.conn();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}

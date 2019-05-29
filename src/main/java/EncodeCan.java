import java.io.IOException;
import java.text.ParseException;

public class EncodeCan extends Thread {

	
	/***************************************************************************************
	 * GET DATA VIA TCP
	 ***************************************************************************************/
//	public static void getData() {
//
//	}

	@Override
	public void run() {
		GetCan gc = new GetCan("192.168.0.2",15731);
		try {
			gc.conn();
		} catch (IOException | ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastTest {

	public static void main(String[] args) {
		MulticastSocket socket = null;
		InetAddress group = null;
		
		// More info here: https://docs.oracle.com/javase/tutorial/networking/datagrams/broadcasting.html
		try {
			socket = new MulticastSocket(7234);
			group = InetAddress.getByName("239.1.1.234");
			socket.joinGroup(group);
	
			DatagramPacket packet;
			while(true) {
			    byte[] buf = new byte[256];
			    packet = new DatagramPacket(buf, buf.length);
			    socket.receive(packet);
	
			    // Java byte values are signed. Convert to an int so we don't have to deal with negative values for bytes >= 0x7f (unsigned).
			    int[] valueBuf = new int[2];
			    for(int ii = 0; ii < valueBuf.length; ii++) {
			    	valueBuf[ii] = (buf[ii] >= 0) ? (int)buf[ii] : (int)buf[ii] + 256;
			    }
			    
			    int value = (valueBuf[0] << 8) | valueBuf[1];
			    
			    System.out.println(value);
			}
	
		}
		catch(IOException e) {
			System.out.println(e.toString());
		}
		finally {
			if (socket != null) {
				try {
					if (group != null) {
						socket.leaveGroup(group);
					}
					socket.close();					
				}
				catch(IOException e) {
					
				}
			}
		}
	}

}

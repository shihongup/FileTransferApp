package fta;

import java.io.*;
import java.net.*;


public class FTAClient {
	public void run() {
		try {
			System.out.println("Trying to connect with host...");
			Socket clientFileSoc = new Socket("localhost", 1121);
			System.out.println("File port connection successful!!");
			System.out.println("Server Address：" + clientFileSoc.getRemoteSocketAddress());
			Socket clientContrSoc = new Socket("localhost", 1122);
			System.out.println("Control port connection successful!!!");
			System.out.println("Server Address：" + clientContrSoc.getRemoteSocketAddress());
			
			chooseMode(clientFileSoc, clientContrSoc);
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void upload(Socket s) throws Exception {
		Socket ssock = s;
		//type the file name with path from the console
		System.out.println("type the file path with name:");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String filename = reader.readLine();
		
		//reading file
		System.out.println("Reading File " + filename);
		DataInputStream dis = new DataInputStream(new FileInputStream(filename));
		DataOutputStream dos = new DataOutputStream(ssock.getOutputStream());
		//tell the server the file name
		dos.writeUTF(filename);
		//send the file to the server
		dos.write(readInputStream(dis));
		
		//tell the server that sending has completed
		ssock.shutdownOutput();
		reader.close();
		dis.close();
		dos.close();

		System.out.println("File Sent");
	}

	public void download(Socket s) throws Exception {
		Socket ssock = s;
		
		//get the file name want to receive
		System.out.println("type the file name:");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String filename = reader.readLine();
		
		System.out.println("Requiring File: " + filename);
		DataInputStream dis = new DataInputStream(ssock.getInputStream());
		DataOutputStream dos = new DataOutputStream(ssock.getOutputStream());
		
		//tell the server the file required
		dos.writeUTF(filename);
		System.out.println("Receiving File " + filename);
		
		String path = "ClientFiles/" + filename;
		File f = new File(path);
		FileOutputStream fout = new FileOutputStream(f);
		byte[] data = readInputStream(dis);
		fout.write(data);
		reader.close();
		dis.close();
		dos.close();
		fout.close();

		System.out.println("Received File...");
	}
	
    public byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //make a buffer with 1024 bytes  
        byte[] buffer = new byte[1024];  
        //the length of the content that write to the outputstream  
        int len = 0;  
        while( (len=inStream.read(buffer)) != -1 ){  
            //starting with index 0, write len bytes to outstream
            outStream.write(buffer, 0, len);  
        }  
        //close the inputstream 
        inStream.close();  
        
        return outStream.toByteArray();  
    } 
    
    public void chooseMode(Socket cfs, Socket ccs ) throws Exception {
    	//file transfer socket
    	Socket fileTrans = cfs;
    	//control socket
    	Socket contr = ccs;
		System.out.println("MENU: ");
		System.out.println("1. UPLOAD");
		System.out.println("2. DOWNLOAD ");
		
		boolean loop = true;
		while (loop) {
			//type in the mode number
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			//out stream of control
			DataOutputStream contrOut = new DataOutputStream(ccs.getOutputStream());
			String mode = reader.readLine();
			if (mode.equals("1")) {
				System.out.println("UPLOAD MODE SELECTED...");
				contrOut.writeUTF("RECEIVE");
				upload(fileTrans);
				loop = !loop;
			} else if (mode.equals("2")) {
				System.out.println("DOWNLOAD MODE SELECTED...");
				contrOut.writeUTF("SEND");
				download(fileTrans); 
				loop = !loop;
			} else {
				System.out.println("WRONG INPUT, PLS RESTART THE APP");
			}
		}
    }
    
	public static void main(String args[]) throws Exception {
		FTAClient client = new FTAClient();
		client.run();
	}
}

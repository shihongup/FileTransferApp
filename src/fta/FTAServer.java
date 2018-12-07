package fta;

import java.io.*;
import java.net.*;

public class FTAServer {
	private ServerSocket serverFileSocket;
	private ServerSocket serverContrSocket;
	public FTAServer() throws IOException {
		serverFileSocket = new ServerSocket(1121);
		serverContrSocket = new ServerSocket(1122);
		System.out.println("Server started...");
	}
	
	public void run() {
		while(true) {
			try {
				//server listening to port 1121 for file and 1122 for control message
				Socket file = serverFileSocket.accept();
				Socket contr = serverContrSocket.accept();
				System.out.println("Connection Successfully Established!!");
				System.out.println("Client Address：" + file.getRemoteSocketAddress());
				System.out.println("Client Address：" + contr.getRemoteSocketAddress());
				//server listening to port 1122 for control message
				getMode(contr, file);
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendFile(Socket s) throws Exception {
		Socket ssock = s;
		
		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		String filename = cin.readUTF();
		
		//reading file
		System.out.println("Reading File " + filename);
		DataInputStream dis = new DataInputStream(new FileInputStream("ServerFiles/" + filename));
		DataOutputStream dos = new DataOutputStream(ssock.getOutputStream());

		//send the file to the server
		dos.write(readInputStream(dis));
		
		//tell the server that sending has completed
		ssock.shutdownOutput();
		cin.close();
		dis.close();
		dos.close();

		System.out.println("File Sent");
		
	}
	
	public void receiveFile(Socket s) throws Exception {
		Socket ssock = s;

		DataInputStream cin = new DataInputStream(ssock.getInputStream());
		
		// get the filename
		String fname = cin.readUTF();
		String filename = fname.substring(fname.lastIndexOf("/") + 1);
		System.out.println("Receiving File " + filename);
		
		//store the file received to serverfiles folder
		String storePath = "ServerFiles/" + filename;
		File rfile = new File(storePath);
		System.out.println(rfile.getPath());
		FileOutputStream fos = new FileOutputStream(rfile);
		byte[] data = readInputStream(cin);
		fos.write(data);
		cin.close();
		fos.close();
		
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
    
    public void getMode(Socket scs, Socket sfs) throws Exception {
    	//control socket
    	Socket contr = scs;
    	//file socket
    	Socket fileTrans = sfs;
//    	DataInputStream contrIn = new DataInputStream(contr.getInputStream());

    	boolean loop = true;
    	while (loop) {
        	DataInputStream contrIn = new DataInputStream(contr.getInputStream());
    		String mode = contrIn.readUTF();
    		if (mode.equals("RECEIVE")) {
    			System.out.println("START RECEIVING FILE...");
    			receiveFile(fileTrans);
    			loop = !loop;
    		} else if (mode.equals("SEND")) {
    			System.out.println("START SENDING FILE...");
    			sendFile(fileTrans);
    			loop = !loop;
    		}
    	}
    }
	
	public static void main(String args[]) throws Exception{
		FTAServer fserver = new FTAServer();
		fserver.run();
	}
}

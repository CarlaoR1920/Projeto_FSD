import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.net.*;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import java.nio.charset.StandardCharsets;

public class PrivateMessaging extends UnicastRemoteObject implements PrivateMessagingInterface{
	private HashMap<String, PrivateMessagingInterface> PMIS;
	private PrivateKey privkey;
	public PrivateMessaging(PrivateKey privkey) throws RemoteException {
		super();
		PMIS= new HashMap<>();
		this.privkey=privkey;
	}
	
	public HashMap<String, PrivateMessagingInterface> getMap(){
		return PMIS;
	}
	
	public void sendMessage(String IP, String Name, String Msg, PrivateMessagingInterface PMV) {
		System.out.println(Name + ": " + Msg);
		if(!PMIS.containsKey(IP)){
			PMIS.put(IP,PMV);
		}
	}
	
	public void sendMessageSecure(String IP, String Name, byte[] Msg, PrivateMessagingInterface PMV,byte[] assinatura,PublicKey pk){
		byte[] decipheredText = null;
		try{
			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.DECRYPT_MODE, privkey);  
			decipheredText = cipher.doFinal(Msg);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(decipheredText);
			byte[] digest = md.digest();
			Signature sign = Signature.getInstance("SHA1withRSA");
			sign.initVerify(pk);
			sign.update(digest);
			boolean bool = sign.verify(assinatura);
			if(bool) {
				System.out.println("Signature verified");   
				String s = new String(decipheredText, StandardCharsets.UTF_8);
				System.out.println(Name + ": " + s);
				if(!PMIS.containsKey(IP)){
			PMIS.put(IP,PMV);
		}
			} else {
				System.out.println("Signature failed");
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
		
		
	}
}


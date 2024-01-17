import java.net.*;
import java.io.*;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.*;

public class Presences extends UnicastRemoteObject implements PresencesInterface {

	private static Hashtable<String, IPInfo> presentIPs = new Hashtable<String, IPInfo>();
	private static int cont = 0;
	private static ArrayList<String> Posts= new ArrayList<>();
	public Presences() throws RemoteException {
		super();
	}

	public Vector<String> getPresences(String IPAddress,String Nickname, Socket ligacao)
	{
		long actualTime = new Date().getTime();
		
		synchronized(this) {
			if (presentIPs.containsKey(Nickname)) {
				IPInfo newIp = presentIPs.get(Nickname);
				newIp.setLastSeen(actualTime);
				newIp.setLigacao(ligacao);
			}
			else {
				IPInfo newIP = new IPInfo(IPAddress, actualTime, ligacao);
				presentIPs.put(Nickname,newIP);
			}
		}
		return getNickList();
	}
	
	public void AtualizarPres(String Nick) {
		long actualTime = new Date().getTime();
		synchronized(this) {
				IPInfo newIp = presentIPs.get(Nick);
				newIp.setLastSeen(actualTime);
		}
			
	}
	
	public String getIPRMI(String nick){
		return presentIPs.get(nick).getIP();
	}
	public PublicKey getPublicKeyRMI(String nick){
		return presentIPs.get(nick).getPublicKey();
	}
	public void getPresences2(String IPAddress, String Nickname,PublicKey publickey, PrivateMessagingInterface cl) {
				long actualTime = new Date().getTime();
				IPInfo newIp = presentIPs.get(Nickname);
				newIp.setLastSeen(actualTime);
				newIp.setInterface(cl);
				newIp.setState(true);
				newIp.setPublicKey(publickey);
	}
	
	
	
	public Vector<String> getNickList(){
		
		Vector<String> result = new Vector<String>();
		Enumeration<String> e = presentIPs.keys();
		while (e.hasMoreElements()) {
            String nick = e.nextElement();
			if (!presentIPs.get(nick).timeOutPassed(120*1000)) {
				result.add(nick);
			}
        }
		return result;
	}
	
	public ArrayList<Socket> getSocketList(){
		ArrayList<Socket> result = new ArrayList<Socket>();
		for (Enumeration<IPInfo> e = presentIPs.elements(); e.hasMoreElements(); ) {
			IPInfo element = e.nextElement();
			if (!element.timeOutPassed(120*1000)) {
				result.add(element.getLigacao());
			}
		}
		return result;
	}
	
	public ArrayList<String> getRMIUsers(){
		ArrayList<String> result = new ArrayList<String>();
		for (Enumeration<String> e = presentIPs.keys(); e.hasMoreElements(); ) {
			String nick = e.nextElement();
			IPInfo element = presentIPs.get(nick);
			if (!element.timeOutPassed(120*1000) && element.getState()) {
				result.add(nick + " " + element.getIP() + " " + element.getPublicKey());
			}
		}
		return result;
	}
	
	
	public boolean verTimeOut(String nick){
			IPInfo element = presentIPs.get(nick);
			if (element.timeOutPassed(120*1000)) {
				return true;
			}else{
				return false;
			}
	}
	
	public void SavePost(String post, String nick){
		String msgfinal = nick + ": " + post;
		Posts.add(msgfinal);
	}
	
	private ArrayList<String> GetPosts(){
		return Posts;
	}
	
	public String Get10UltPosts(){
		String pos="";
		int tam=Posts.size(); 
		if(tam>9)
		{
			for(int i=Posts.size();(i-10)<tam;i++)
			{
				pos +=Posts.get(i-10) + "\n";
			}
			return pos;
		}else{
			for(int i=0; i<tam ; i++)
			{
				pos +=Posts.get(i) + "\n";
			}
			return pos;
		}
	}
	
	public void atualizarRMI(String nick){
		presentIPs.get(nick).setState(false);
	}
}

class IPInfo {
	private String ip;
	private long lastSeen;
	private Socket ligacao;
	private PrivateMessagingInterface cl = null;
	private  Boolean state = false;
	private PublicKey publickey;
	
	public IPInfo(String ip, long lastSeen,PrivateMessagingInterface cl) {
		this.ip = ip;
		this.lastSeen = lastSeen;
		this.ligacao=ligacao;
		this.cl=cl;
	}
	
	public IPInfo(String ip, long lastSeen, Socket ligacao) {
		this.ip = ip;
		this.lastSeen = lastSeen;
		this.ligacao=ligacao;
	}
	
	public IPInfo() {}
	public void setPublicKey(PublicKey publickey){
		this.publickey=publickey;
	}
	public PublicKey getPublicKey(){
		return publickey;
	}
	public void setState(Boolean state){
		this.state=state;
	}
	public Boolean getState(){
		return state;
	}
	public PrivateMessagingInterface getInterface(){
		return cl;
	}
	
	public void setInterface(PrivateMessagingInterface cl){
		this.cl=cl;
	}
	
	
	public void setLigacao(Socket ligacao){
		this.ligacao=ligacao;
	}
	
	public Socket getLigacao(){
		return this.ligacao;
	}
	
	public String getIP() {
		return this.ip;
	}
	
	public void setLastSeen(long time){
		this.lastSeen = time;
	}
	
	public boolean timeOutPassed(int timeout){
		boolean result = false;
		long timePassedSinceLastSeen = new Date().getTime() - this.lastSeen;
		if (timePassedSinceLastSeen >= timeout)
			result = true;
		return result;
	}
	
}




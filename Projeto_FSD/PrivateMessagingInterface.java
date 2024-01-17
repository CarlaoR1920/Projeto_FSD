import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.*;

public interface PrivateMessagingInterface extends Remote {

	public void sendMessage(String IP, String Name, String Msg, PrivateMessagingInterface PMV) throws RemoteException;
	
	public void  sendMessageSecure(String IP, String Name, byte[] Msg, PrivateMessagingInterface PMV,byte[] assinatura,PublicKey pk) throws RemoteException;
}
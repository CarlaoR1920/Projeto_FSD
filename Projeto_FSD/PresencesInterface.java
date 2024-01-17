import java.rmi.Remote;
import java.rmi.RemoteException;
import java.net.*;
import java.security.*;


public interface PresencesInterface extends Remote {

	public void getPresences2(String IPAddress,String Nickname,PublicKey publickey, PrivateMessagingInterface cl) throws RemoteException;
	
	public String getIPRMI(String nick) throws RemoteException;
	
	public PublicKey getPublicKeyRMI(String nick) throws RemoteException;
}

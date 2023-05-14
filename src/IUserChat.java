import java.rmi.RemoteException;

public interface IUserChat extends java.rmi.Remote {
    public void deliverMsg(String senderName, String msg) throws RemoteException;
}

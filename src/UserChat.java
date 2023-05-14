import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class UserChat extends UnicastRemoteObject implements IUserChat{
    protected UserChat() throws RemoteException {
        super();
    }
    String userName;
    private ArrayList<String> roomList;

    @Override

    public void deliverMsg(String senderName, String msg) throws RemoteException {

    }

    public static void main(String[] args) {
        try {
            ServerChat stub = (ServerChat) Naming.lookup("rmi://localhost:2020/server");
            roomList = stub.getRooms();
        //System.out.println(stub.Hello());
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }


    }
}

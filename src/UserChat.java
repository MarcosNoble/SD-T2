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
    public ArrayList<String> roomList;

    @Override

    public void deliverMsg(String senderName, String msg) throws RemoteException {

    }

    public static void main(String[] args) {
        try {
            UserChat user = new UserChat();
            ServerChat serverStub = (ServerChat) Naming.lookup("rmi://localhost:2020/server");
            RoomChat RoomStub = (RoomChat) Naming.lookup("rmi://localhost:2020/rooms");

            user.roomList = serverStub.getRooms();
            System.out.println(user.roomList);

        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            e.printStackTrace();
        }


    }
}

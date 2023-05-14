import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class ServerChat extends UnicastRemoteObject implements IServerChat{

    private ArrayList<String> roomList;

    protected ServerChat() throws RemoteException {
        super();
    }

    @Override
    public ArrayList<String> getRooms() throws RemoteException {
        return this.roomList;
    }

    @Override
    public void createRoom(String roomName) throws RemoteException {

    }

    public static void main(String[] args) throws RemoteException {
        try {
            //Registry registro = LocateRegistry.createRegistry(2020);
            LocateRegistry.createRegistry(2020);
            Naming.rebind("rmi://localhost:2020/server", new ServerChat());
            Naming.rebind("rmi://localhost:2020/rooms", new RoomChat());
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

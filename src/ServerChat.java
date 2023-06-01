import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;


public class ServerChat extends UnicastRemoteObject implements IServerChat{

    public static ArrayList<String> roomList;

    protected ServerChat() throws RemoteException {
        this.roomList = new ArrayList<>();
    }

    @Override
    public ArrayList<String> getRooms() throws RemoteException {
        if(this.roomList.size() ==0){
            return null;
        }else{
            return this.roomList;
        }
    }

    @Override
    public void createRoom(String roomName) throws RemoteException {
        try {
            Naming.rebind("rmi://localhost:2020/"+roomName, new RoomChat(roomName));
            roomList.add(roomName);
        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws RemoteException {
        //roomList = new ArrayList<String>();
        try {
            //Registry registro = LocateRegistry.createRegistry(2020);
            LocateRegistry.createRegistry(2020);
            Naming.rebind("rmi://localhost:2020/server", new ServerChat());

        } catch (RemoteException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

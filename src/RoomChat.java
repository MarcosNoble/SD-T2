import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;


public class RoomChat extends UnicastRemoteObject implements IRoomChat{
    private Map<String, IUserChat> userList;
    private String nome;
    protected RoomChat(String nome) throws RemoteException {
        this.nome = nome;
    }

    @Override

    public void sendMsg(String usrName, String msg) throws RemoteException {

    }

    @Override
    public void joinRoom(String usrName, IUserChat user) throws RemoteException{

    }

    @Override
    public void leaveRoom(String usrName) throws RemoteException{

    }

    @Override
    public void closeRoom() throws RemoteException{

    }

    @Override
    public String getRoomName() throws RemoteException{
        return null;
    }

    public static void main(String[] args) {

    }
}

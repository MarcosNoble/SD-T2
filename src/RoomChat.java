import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;


public class RoomChat extends UnicastRemoteObject implements IRoomChat{
    private Map<String, IUserChat> userList;
    private String nome;
    protected RoomChat(String nome) throws RemoteException {
        this.nome = nome;
        this.userList = new HashMap<>();
    }

    @Override

    public void sendMsg(String usrName, String msg) throws RemoteException {
        for (IUserChat user : userList.values()) {
            user.deliverMsg(usrName, msg);
        }
    }

    @Override
    public void joinRoom(String usrName, IUserChat user) throws RemoteException{
        userList.put(usrName, user);
    }

    @Override
    public void leaveRoom(String usrName) throws RemoteException{
        userList.remove(usrName);
    }

    @Override
    public void closeRoom() throws RemoteException{
        //tem que tirar do registo RMI? Tem que chamar o server?
    }

    @Override
    public String getRoomName() throws RemoteException{
        return nome;
    }

//    public static void main(String[] args) {
//
//    }
}

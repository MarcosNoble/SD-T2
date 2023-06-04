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
        if(userList.containsKey(usrName)){
            IUserChat thisUser = userList.get(usrName);
            thisUser.deliverMsg("SYSTEM", "### NOME " + usrName + "INDISPONIVEL ###");
        }
        userList.put(usrName, user);
        sendMsg("SYSTEM", "#### "+ usrName +" ENTROU NA SALA ####");
    }

    @Override
    public void leaveRoom(String usrName) throws RemoteException{
        sendMsg("SYSTEM", "#### "+ usrName +" SAIU DA SALA ####"+ this.nome);
        userList.remove(usrName);
    }

    @Override
    public void closeRoom() throws RemoteException{
        sendMsg("SYSTEMCLOSE","Sala fechada pelo servidor.");
        userList.clear();
    }

    @Override
    public String getRoomName() throws RemoteException{
        return nome;
    }

//    public static void main(String[] args) {
//
//    }
}

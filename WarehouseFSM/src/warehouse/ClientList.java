package warehouse;

import java.util.*;

public class ClientList {
    private final Map<String, Client> clients = new LinkedHashMap<>();
    public void addClient(Client c){ clients.put(c.getId(), c); }
    public Client findClient(String id){ return clients.get(id); }
    public Collection<Client> getAllClients(){ return Collections.unmodifiableCollection(clients.values()); }
}

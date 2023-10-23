## Project Description - SAR:

### Object Caching:
We implemented a cache for shared objects by multiple clients. Each client maintains a cached version of this object on a local server. This approach promotes local access but also minimizes network latency, making it an efficient choice for frequently accessed read-only objects.

### Consistency Management:
To ensure consistency, we adopted an entry consistency approach. When a client intends to perform an action on a shared object, it must first request a lock. Subsequently, the system provides the client with an up-to-date version of the object. After completing its operation, the client must release the acquired lock. This strategy guarantees that the shared objects remain consistent across all clients.

### Communication Protocol:
For communication between local servers and the central coordinator, we used the RMI (Remote Method Invocation) protocol. RMI facilitates the seamless transfer of data and commands, ensuring efficient interaction between clients and the coordinator.

### Object States and Lock Management:
We categorized shared objects into different states and implemented a sophisticated locking mechanism to manage both read and write access. Importantly, we accommodated the scenario where multiple clients may have concurrent read access to the same object.

### Encapsulating Object Information:
In order to streamline object management, we created a wrapper class for shared objects. This encompassing class stores the object's state and keeps track of the servers that are currently utilizing it. This encapsulation enhances the system's overall organization and ease of maintenance.

### Client Termination Handling:
In the event of a client's termination, we implemented a graceful exit strategy. When a client closes its application window, the system checks whether the client was in read or write mode. Subsequently, it releases any write locks held and ensures a clean update of the object before closing the application. This approach minimizes the risk of leaving shared objects in an inconsistent state due to unexpected client terminations.

### Improvement : Javanaise 2
We have implemented a proxy that encapsulates the client method calls by incorporating the locking and unlocking procedures. This allows clients to create a new instance with the object and its name as parameters. The proxy, in turn, returns a shared object and manages the locking and unlocking operations seamlessly. 
Additionally, we used annotations to make the locking mechanisms more generic, enabling the developer to specify whether they require a read or write lock on new created object.
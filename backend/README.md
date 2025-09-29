# BookingApp Backend
This folder contains the **backend implementation** of the BookingApp system.  
The backend is implemented in **Java**, and the communication between components is based on **TCP sockets**.

## Architecture
The backend is designed as a distributed system with multiple processes that communicate asynchronously:

- **Workers**  
  Represent the different users that can connect to the system.  
  Each worker runs on its own port and simulates multiple concurrent clients interacting with the booking service.  
  They handle requests and forward them to the **Master**.

- **Master**  
  Acts as the central coordinator.  
  It receives client requests (via the Console), delegates the work to the available workers, and gathers results.  
  The Master controls the flow of actions and ensures consistency between users.

- **Reducer**  
  Responsible for aggregating results from workers and providing a unified response back to the Master.  
  For example, when filtering hotels or searching across multiple workers, the Reducer combines the data.

- **Console**  
  A simple command-line client that allows interaction from the terminal.  
  Through the Console, a user can log in as a **Client** or **Manager**, and perform the available actions (browse, filter, book, manage listings, etc.).  
  The Console sends requests to the Master over TCP.
  
## How It Works

1. **Reducer** is started first and listens on its configured port.  
2. **Workers** are launched on their own ports. Each worker represents a node capable of handling client requests.  
3. **Master** starts, connects to the workers and reducer, and coordinates their communication.  
4. **Console** connects to the Master, and the user can interact through the terminal menus.

This design models a distributed environment, with the Master as the coordinator, Workers as nodes that handle tasks, and the Reducer as a result aggregator.

### Run (VS Code)
1. Open `backend/BookingApp` in VS Code (or open the repo root and set each launch config `cwd` to `backend/BookingApp`).
2. Use the provided launch configs to start:
   - **Reducer**
   - **Worker-1**, **Worker-2**, **Worker-3** (workers require args: `<id> <port>`)
   - **Master**
   - **Console**


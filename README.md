# BookingApp (Asynchronous)

**BookingApp** is a distributed booking system built on top of **TCP socket communication**.  
It simulates a real-world scenario where multiple users can connect to a central service and perform operations depending on their role.  

The system distinguishes between two types of users:
- **Client:**  
  Clients represent regular users of the system.  
  They can browse available hotels, view details (such as dates, capacity, reviews, and pricing), and make reservations.
  To improve the booking experience, clients can also apply filters (e.g., by location, dates, price range, or rating) to quickly find the most relevant options.  
  Once a booking is placed, clients can also review their active reservations, cancel them if necessary, and check availability for alternative dates.  
  The goal of this role is to provide an intuitive booking flow similar to a travel agency or hotel booking site.

- **Manager:**  
  Managers represent administrators or property owners.  
  They have elevated privileges compared to clients.  
  Managers can register new hotels/rooms, update existing details (e.g., pricing, capacity, available dates), and review the reservations made by clients.  
  They essentially handle the supply side of the booking system.  
  With this role, the system supports a dynamic environment where managers can keep listings up-to-date, ensuring clients always see current information.

## Structure of the repository
- **backend/** contains the full application logic and the TCP services.
- **frontend/** contains the Android Studio project (mobile UI) that talks to the backend.

## How to Run (Backend + Frontend)
1) Start the components in order (each in a separate terminal or using VS Code launch configs):
    - **Reducer**: java -cp target/classes org.aueb.reducer.Reducer
    - **Workers**:
        - java -cp target/classes org.aueb.worker.Worker 1 6001
        - java -cp target/classes org.aueb.worker.Worker 2 6002
        - java -cp target/classes org.aueb.worker.Worker 3 6003
    - **Master**:  java -cp target/classes org.aueb.master.Master

2) Open Android Studio.
3) Configure the backend connection: If running on an emulator → use 10.0.2.2:8000.
4) Run the app

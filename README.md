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

# Structure of the repository
- **backend/** contains the full application logic and the TCP services.
- **frontend/** contains the Android Studio project (mobile UI) that talks to the backend.

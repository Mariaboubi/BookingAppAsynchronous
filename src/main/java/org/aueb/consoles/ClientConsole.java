//package org.aueb.consoles;
//
//import org.aueb.entities.Client;
//import org.aueb.entities.Hotel;
//import org.aueb.util.DateProcessing;
//import org.aueb.util.JSONReaderWriter;
//import org.json.simple.JSONArray;
//import org.json.simple.JSONObject;
//
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//import java.util.stream.Collectors;
//
//import static java.lang.System.exit;
//
//public class ClientConsole {
//    private Client client;
//    private final ArrayList<Hotel> hotels;
//
//    private final Scanner scanner = new Scanner(System.in);
//
//    public ClientConsole(ArrayList<Hotel> hotels) {
//        this.hotels = hotels;
//    }
//
//    public void run(Client clientArgs, ArrayList<Hotel> hotels) throws Exception {
//        while (true) {
//            System.out.println("1.Choose a filter: ");
//            System.out.println("2.Book a hotel: ");
//            System.out.println("3.Rate a hotel (1-5): ");
//            System.out.println("4. Exit");
//            int option = Integer.parseInt(scanner.nextLine());
//            if (option == 1) {
//                filterHotel();
//            } else if (option == 2) {
//                client = clientArgs;
//                makeReservation(false, hotels);
//            } else if (option == 3) {
//                rateHotel();
//            }else if(option==4) {
//                    exit(0);
//            } else {
//                System.out.println("Invalid option.");
//            }
//        }
//    }
//
//    private void filterHotel() {
//
//        System.out.println("Choose one or multiple filters");
//        System.out.println("1.Area");
//        System.out.println("2.Available dates");
//        System.out.println("3.Number of persons");
//        System.out.println("4.Price");
//        System.out.println("5.Stars");
//
//        String option = scanner.nextLine();
//
//        List<Hotel> filteredHotels = new ArrayList<>(hotels);
//        System.out.println("HOtels : " + hotels.size());
//        //filteredHotels=hotels;
//
//        String[] options = option.split(",");
//        String filter;
//
//        for (String optionStr : options) {
//            int filterOption = Integer.parseInt(optionStr.trim());
//            if (filterOption == 1) {
//                System.out.println("Enter the area you're interested in:");
//                String area = scanner.nextLine();
//                filteredHotels = filteredHotels.stream()
//                        .filter(hotel -> hotel.getArea().equalsIgnoreCase(area))
//                        .collect(Collectors.toList());
//            }
//            if (filterOption == 2) {
//                System.out.println("Enter the date you're interested in (YYYY-MM-DD):");
//                String date = scanner.nextLine();
//                filteredHotels = filteredHotels.stream()
//                        .filter(hotel -> hotel.getAvailableDates().equals(date))
//                        .collect(Collectors.toList());
//            }
//
//            if (filterOption == 3) {
//                System.out.println("Choose the number of persons ");
//                Integer numberOfperson = scanner.nextInt();
//                filteredHotels = filteredHotels.stream()
//                        .filter(hotel -> hotel.getNumPeople() == numberOfperson)
//                        .collect(Collectors.toList());
//            }
//            if (filterOption == 4) {
//                System.out.println("Choose the maximum price per night ");
//                double price = Double.parseDouble(scanner.nextLine());
//                filteredHotels = filteredHotels.stream()
//                        .filter(hotel -> hotel.getPrice() <= price)
//                        .collect(Collectors.toList());
//            }
//            if (filterOption == 5) {
//                System.out.println("Choose the stars of the hotel ");
//                double stars = Double.parseDouble(scanner.nextLine());
//                filteredHotels = filteredHotels.stream()
//                        .filter(hotel -> hotel.getStars() >= stars)
//                        .collect(Collectors.toList());
//            }
//        }
//        System.out.println("HOtels filtered: " + filteredHotels.size());
//        for (Hotel hotel : filteredHotels) {
//            System.out.println(hotel.getHotelName());
//        }
//
//        System.out.println("Do you want to proceed in a reservation? Y/N ");
//        String reservation = scanner.nextLine();
//        if (reservation.equalsIgnoreCase("Y")) {
//            makeReservation(true, filteredHotels);
//        }
//    }
//
//
//    public void rateHotel() {
//
//        System.out.println("Give the name of the hotel you want to rate");
//        String hotelName = scanner.nextLine().trim();
//        boolean foundHotel = false;
//        double updatedStars = 0;
//        int updatedReviews = 0;
//        for (Hotel hotel : hotels) {
//
//            if ((hotel.getHotelName()).equalsIgnoreCase(hotelName)) {
//                System.out.println("Give your rating(1-5): ");
//                double newRating = Double.parseDouble(scanner.nextLine());
//                double previousStars = hotel.getStars();
//                int previousReviews = hotel.getNumReviews();
//                updatedStars = ((previousStars * previousReviews) + newRating) / (previousReviews + 1);
//                DecimalFormat df = new DecimalFormat("#.##");
//                updatedStars = Double.parseDouble(df.format(updatedStars));
//                updatedReviews = previousReviews + 1;
//                hotel.setStars(updatedStars);
//                System.out.println("Updated rating: " + hotel.getStars());
//                hotel.setNumReviews(updatedReviews);
//                foundHotel = true;
//                break;
//            }
//
//        }
//
//        if (!foundHotel) {
//            System.out.println("Hotel not found.");
//        }
//
//        try {
//            JSONObject jsonObject = JSONReaderWriter.readJsonFile("bin/hotel.json");
//            JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
//
//            // Iterate through the hotels array
//            for (Object hotelObj : hotelsArray) {
//                JSONObject hotel = (JSONObject) hotelObj;
//                String currentHotelName = (String) hotel.get("hotelName");
//                if (currentHotelName.equals(hotelName)) {
//                    // hotel found, return the hotel's JSONObject or handle as needed
//                    hotel.put("stars", updatedStars);
//                    hotel.put("numReviews", updatedReviews);
//                    JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json"); // This method needs to be implemented
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void makeReservation(Boolean filter, List<Hotel> hotels) {
//        if (!filter) {
//            System.out.println("Do you want to filter the hotels? Y/N ");
//            String answer = scanner.nextLine();
//            if (answer.equals("Y")) {
//                filterHotel();
//            }
//        }
//        for (Hotel hotel : hotels) {
//            System.out.println(hotel.toString());
//        }
//
//        Hotel chooseHotel = null;
//        while (true) {
//            System.out.println("Give the name of the hotel which you want reserve ");
//            String hotelName = scanner.nextLine();
//            for (Hotel hotel : hotels) {
//                if (hotel.getHotelName().equalsIgnoreCase(hotelName)) {
//                    chooseHotel = hotel;
//                    break;
//                }
//            }
//            if (chooseHotel == null) {
//                System.out.println("You give incorrect hotel name.Do you can't to try again? Y/N");
//                String option = scanner.nextLine();
//                if (option.equals("Y")) {
//                    continue;
//                } else {
//                    break;
//                }
//            } else {
//                break;
//            }
//        }
//        if (chooseHotel != null) {
//            int[] firstDate;
//            int[] lastDate;
//            String answer = null;
//            if (chooseHotel.getAvailableDates().equals(" ")) {
//                System.out.println("There are not available dates for this hotel");
//            } else {
//                while (true) {
//                    System.out.println("Give the dates which you want reserve with this format: YYYY/MM/DD - YYYY/MM/DD ");
//                    answer = scanner.nextLine();
//                    String[] dates = answer.split(" - ");
//
//                    String[] StringfirstDate = dates[0].split("/");
//                    firstDate = DateProcessing.formatDate(StringfirstDate);
//
//                    String[] StringlastDate = dates[1].split("/");
//                    lastDate = DateProcessing.formatDate(StringlastDate);
//
//                    if (!DateProcessing.compareDates(firstDate, lastDate)) {
//                        System.out.println("You need to give the dates in the correct order");
//                    } else {
//                        break;
//                    }
//                }
//                String[] all_dates = chooseHotel.getAvailableDates().split(", ");
//                boolean flag = false;
//                String[] date_X = null;
//                for (String date : all_dates) {
//                    date_X = date.split(" - ");
//                    String[] StringfirstDate = date_X[0].split("/");
//                    int[] hotelFirstDate = DateProcessing.formatDate(StringfirstDate);
//
//
//                    String[] StringlastDate = date_X[1].split("/");
//                    int[] hotelLastDate = DateProcessing.formatDate(StringlastDate);
//                    //System.out.println("first" + compareDates(hotelFirstDate, firstDate));
//                    //System.out.println("last" + compareDates(lastDate, hotelLastDate));
//                    if (DateProcessing.compareDates(hotelFirstDate, firstDate) && DateProcessing.compareDates(lastDate, hotelLastDate)) {
//                        flag = true;
//                        break;
//                    }
//
//                }
//                if (!flag) {
//                    System.out.println("The dates you give didn't match with the available dates");
//                } else {
//                    System.out.println("SUCCESS");
//                    chooseHotel.addReservations(client, answer);
//                    String firstdate = firstDate[0] + "/" + firstDate[1] + "/" + firstDate[2];
//
//                    String firstavailabledate = date_X[0] + " - " + firstdate;
//                    String lastdate = lastDate[0] + "/" + lastDate[1] + "/" + lastDate[2];
//                    String lastavailabledate = lastdate + " - " + date_X[1];
//                    String availableDate = firstavailabledate + ", " + lastavailabledate;
//                    for(String date : all_dates){
//                        if(!(date.equals(date_X))){
//                            availableDate+= ", "+ date;
//                        }
//                    }
//                    System.out.println(availableDate);
//                    chooseHotel.setAvailableDates(availableDate);
//                    try {
//                        JSONObject jsonObject = JSONReaderWriter.readJsonFile("bin/hotel.json"); // This method needs to be implemented
//                        JSONArray hotelsArray = (JSONArray) jsonObject.get("hotels");
//
//                        // Iterate through the hotels array
//                        for (Object hotelObj : hotelsArray) {
//                            JSONObject hotel = (JSONObject) hotelObj;
//                            String currentHotelName = (String) hotel.get("hotelName");
//                            if (currentHotelName.equals(chooseHotel.getHotelName())) {
//                                // hotel found, return the hotel's JSONObject or handle as needed
//                                hotel.put("availableDates", availableDate);
//                                JSONReaderWriter.writeJsonFile(jsonObject, "bin/hotel.json"); // This method needs to be implemented
//                                return;
//                            }
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//    }
//
//}

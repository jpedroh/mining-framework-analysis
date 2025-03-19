package org.JavaArt.TicketManager.controllers;
import org.JavaArt.TicketManager.entities.*;
import org.JavaArt.TicketManager.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import java.sql.SQLException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Vladislav Karpenko
 * Date: 19.06.2014
 * Time: 18:06
 */
@Controller @SessionAttributes(value = { "pageName", "events", "event", "sectorsMap", "sector", "row", "rowsMap", "seatsMap", "tickets", "bookingPrice" }) public class BookingController {
  private String errorMessage = "";

  private EventService eventService = new EventService();

  private List<Ticket> tickets = new ArrayList<>();

  private 
<<<<<<< /usr/src/app/output/kvladislav/ticketmanager/2876baf99befd8b453ef6a5c4d51d550ce49f52c/src/main/java/org/JavaArt/TicketManager/controllers/BookingController.java/left.java
  Double
=======
  TicketService
>>>>>>> /usr/src/app/output/kvladislav/ticketmanager/2876baf99befd8b453ef6a5c4d51d550ce49f52c/src/main/java/org/JavaArt/TicketManager/controllers/BookingController.java/right.java
   
<<<<<<< /usr/src/app/output/kvladislav/ticketmanager/2876baf99befd8b453ef6a5c4d51d550ce49f52c/src/main/java/org/JavaArt/TicketManager/controllers/BookingController.java/left.java
  bookingPrice = Double.valueOf(0)
=======
  ticketService = new TicketService()
>>>>>>> /usr/src/app/output/kvladislav/ticketmanager/2876baf99befd8b453ef6a5c4d51d550ce49f52c/src/main/java/org/JavaArt/TicketManager/controllers/BookingController.java/right.java
  ;

  private SectorService sectorService = new SectorService();

  @RequestMapping(value = "Booking/Booking.do", method = RequestMethod.GET) public String bookingGet(Model model) throws SQLException {
    model.addAttribute("pageName", 2);
    List<Event> events = eventService.getAllEvents();
    if (errorMessage != null) {
      model.addAttribute("errorMessage", errorMessage);
    }
    model.addAttribute("bookingPrice", bookingPrice);
    if (events != null && events.size() > 0) {
      Event lastEvent = null;
      Sector lastSector = null;
      if (tickets.size() > 0) {
        lastEvent = tickets.get(tickets.size() - 1).getSector().getEvent();
        lastSector = tickets.get(tickets.size() - 1).getSector();
      }
      if (lastEvent == null) {
        lastEvent = events.get(0);
      }
      model.addAttribute("event", lastEvent);
      model.addAttribute("events", events);
      model.addAttribute("tickets", tickets);
      model.addAttribute("row", 0);
      List<Sector> sectors = sectorService.getSectorsByEvent(lastEvent);
      if (lastSector == null) {
        lastSector = sectors.get(0);
      }
      if (sectors != null && sectors.size() > 0) {
        model.addAttribute("sector", lastSector);
        Map<Sector, Integer> sectorsMap = new TreeMap<>();
        for (Sector sector : sectors) {
          sectorsMap.put(sector, ticketService.getFreeTicketsAmountBySector(sector));
        }
        model.addAttribute("sectorsMap", sectorsMap);

<<<<<<< Unknown file: This is a bug in JDime.
=======
        for (int i = 0; i < lastSector.getMaxRows(); i++) {
          rowsMap.put(i, ticketService.getFreeTicketsAmountBySectorRow(lastSector, i));
        }
>>>>>>> /usr/src/app/output/kvladislav/ticketmanager/2876baf99befd8b453ef6a5c4d51d550ce49f52c/src/main/java/org/JavaArt/TicketManager/controllers/BookingController.java/right.java
      }
    }
    return "Booking";
  }

  @RequestMapping(value = "Booking/setSectors.do", method = RequestMethod.POST) public String bookingSetSectors(@RequestParam(value = "eventId", required = true) int eventId, Model model) throws SQLException {
    Event event = eventService.getEventById(eventId);
    model.addAttribute("event", event);
    List<Sector> sectors = sectorService.getSectorsByEvent(event);
    Map<Sector, Integer> sectorsMap = new TreeMap<>();
    for (Sector sector : sectors) {
      sectorsMap.put(sector, ticketService.getFreeTicketsAmountBySector(sector));
    }
    model.addAttribute("sectorsMap", sectorsMap);
    return "Booking";
  }

  @RequestMapping(value = "Booking/setRow.do", method = RequestMethod.POST) public String bookingSetRow(@RequestParam(value = "sectorId", required = true) int sectorId, Model model) throws SQLException {
    Sector sector = sectorService.getSectorById(sectorId);
    model.addAttribute("sector", sector);
    Map<Integer, Integer> rowsMap = new TreeMap<>();
    for (int i = 1; i <= sector.getMaxRows(); i++) {
      rowsMap.put(i, ticketService.getFreeTicketsAmountBySectorRow(sector, i));
    }
    model.addAttribute("rowsMap", rowsMap);
    return "Booking";
  }

  @RequestMapping(value = "Booking/setSeat.do", method = RequestMethod.POST) public String bookingSetSeat(@RequestParam(value = "row", required = true) int row, @ModelAttribute Sector sector, Model model) throws SQLException {
    Set<Integer> seatsMap = new TreeSet<>();
    for (int i = 1; i <= sector.getMaxSeats(); i++) {

<<<<<<< /usr/src/app/output/kvladislav/ticketmanager/2876baf99befd8b453ef6a5c4d51d550ce49f52c/src/main/java/org/JavaArt/TicketManager/controllers/BookingController.java/left.java
      if (service.isPlaceFree(sector, row, i)) {
        seatsMap.add(i);
      }
=======
      seatsMap.put(i, ticketService.isPlaceFree(sector, row, i));
>>>>>>> /usr/src/app/output/kvladislav/ticketmanager/2876baf99befd8b453ef6a5c4d51d550ce49f52c/src/main/java/org/JavaArt/TicketManager/controllers/BookingController.java/right.java
    }
    model.addAttribute("row", row);
    model.addAttribute("seatsMap", seatsMap);
    return "Booking";
  }

  @RequestMapping(value = "Booking/addTicket.do", method = RequestMethod.POST) public String bookingAddTicket(@ModelAttribute(value = "row") int row, @RequestParam(value = "seats", required = false) int[] seats, @ModelAttribute Sector sector, SessionStatus status, Model model) throws SQLException {
    if (seats == null) {
      return "redirect:/Booking/Booking.do";
    }
    errorMessage = " ";
    for (int seat : seats) {
      if (ticketService.isPlaceFree(sector, row, seat)) {
        Ticket ticket = new Ticket();
        ticket.setSector(sector);
        ticket.setRow(row);
        ticket.setSeat(seat);
        bookingPrice = bookingPrice + sector.getPrice();
        ticketService.addTicket(ticket);
        tickets.add(ticket);
      } else {
        errorMessage += sector.getEvent().getDescription() + " \u0421\u0435\u043a\u0442\u043e\u0440:" + sector.getName() + " \u0420\u044f\u0434: " + row + " \u041c\u0435\u0441\u0442\u043e:" + seat + " \u0443\u0436\u0435 \u043f\u0440\u043e\u0434\u0430\u043d" + "<br>";
      }
    }
    status.setComplete();
    return "redirect:/Booking/Booking.do";
  }

  @RequestMapping(value = "Booking/Finish.do", method = RequestMethod.POST) public String bookingFinish(SessionStatus status) throws SQLException {
    tickets.clear();
    bookingPrice = Double.valueOf(0);
    status.setComplete();
    return "redirect:/Booking/Booking.do";
  }

  @RequestMapping(value = "Booking/Cancel.do", method = RequestMethod.POST) public String bookingCancel(SessionStatus status) throws SQLException {
    ticketService.deleteTickets(tickets);
    tickets.clear();
    bookingPrice = Double.valueOf(0);
    status.setComplete();
    errorMessage = null;
    return "redirect:/Booking/Booking.do";
  }
}
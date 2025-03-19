package org.JavaArt.TicketManager.controllers;
import org.JavaArt.TicketManager.entities.Event;
import org.JavaArt.TicketManager.entities.Sector;
import org.JavaArt.TicketManager.entities.Ticket;
import org.JavaArt.TicketManager.service.EventService;
import org.JavaArt.TicketManager.service.SectorService;
import org.JavaArt.TicketManager.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@Controller @SessionAttributes(value = { "pageName", "eventsOrder", "eventOrder", "sectorsMapOrder", "sectorOrder", "legendaOrder", "rowOrder", "rowsMapOrder", "seatOrder", "seatsMapOrder", "orderList", "orderPrice", "messageOrder", "errorOrder", "orderConfirmation", "priceConfirmation" }) public class OrderController {
  private EventService eventService = new EventService();

  private TicketService ticketService = TicketService.getInstance();

  private SectorService sectorService = new SectorService();

  @RequestMapping(value = "Order/Order.do", method = RequestMethod.GET) public String orderGet(Model model) {
    ArrayList<Ticket> orderTickets = (ArrayList) model.asMap().get("orderList");
    Double orderPrice = (Double) model.asMap().get("orderPrice");
    Event currentEvent = (Event) model.asMap().get("eventOrder");
    Sector currentSector = (Sector) model.asMap().get("sectorOrder");
    Integer currentRow = (Integer) model.asMap().get("rowOrder");
    Integer currentSeat = (Integer) model.asMap().get("seatOrder");
    if (orderTickets == null) {
      orderTickets = new ArrayList<>();
    }
    if (orderPrice == null) {
      orderPrice = 0.0;
    }
    model.addAttribute("pageName", 1);
    List<Event> events = eventService.getFutureEvents();
    if (events != null && events.size() > 0) {
      if (currentEvent == null || eventService.getEventById(currentEvent.getId()) == null || currentEvent.getDate().before(new Date())) {
        currentEvent = events.get(0);
      }
      model.addAttribute("eventOrder", currentEvent);
      model.addAttribute("eventsOrder", events);
      List<Sector> sectors = sectorService.getSectorsByEvent(currentEvent);
      if (sectors != null && sectors.size() > 0) {
        if (currentSector == null || sectorService.getSectorById(currentSector.getId()) == null || currentSector.getEvent().getDate().before(new Date())) {
          currentSector = sectors.get(0);
          currentRow = 1;
          currentSeat = 1;
        }
        model.addAttribute("sectorOrder", currentSector);
        Map<Sector, Short> sectorsMap = new TreeMap<>();
        for (Sector sector : sectors) {
          sectorsMap.put(sector, (short) ticketService.getFreeTicketsAmountBySector(sector));
        }
        model.addAttribute("sectorsMapOrder", sectorsMap);
        List<Sector> sectorsOrderPrice = sectorService.getSectorsByEventOrderPrice(currentEvent);
        model.addAttribute("legendaOrder", sectorService.getLegenda(sectorsOrderPrice));
        Map<Byte, Byte> rowsMap1 = new TreeMap<>();
        for (byte i = 1; i <= currentSector.getMaxRows(); i++) {
          rowsMap1.put(i, (byte) ticketService.getFreeTicketsAmountBySectorRow(currentSector, i));
        }
        model.addAttribute("rowsMapOrder", rowsMap1);
        Map<Integer, Integer> seatsMap1 = new TreeMap<>();
        for (int i = 1; i <= currentSector.getMaxSeats(); i++) {
          seatsMap1.put(i, ticketService.isPlaceFree(currentSector, currentRow, i));
        }
        model.addAttribute("rowOrder", currentRow);
        model.addAttribute("seatOrder", currentSeat);
        model.addAttribute("seatsMapOrder", seatsMap1);
        model.addAttribute("orderPrice", orderPrice);
        model.addAttribute("orderList", orderTickets);
        model.addAttribute("messageOrder", "");
        model.addAttribute("errorOrder", "");
      }
    }
    return "Order";
  }

  @RequestMapping(value = "Order/setSectors.do", method = RequestMethod.POST) public String orderSetSectors(@RequestParam(value = "eventId", required = true) int eventId, Model model) {
    Event currentEvent = eventService.getEventById(eventId);
    model.addAttribute("eventOrder", currentEvent);
    List<Sector> sectors = sectorService.getSectorsByEvent(currentEvent);
    model.addAttribute("sectorOrder", sectors.get(0));
    model.addAttribute("rowOrder", 1);
    model.addAttribute("seatOrder", 1);
    return "redirect:/Order/Order.do";
  }

  @RequestMapping(value = "Order/setRow.do", method = RequestMethod.POST) public String orderSetRow(@RequestParam(value = "sectorId", required = true) int sectorId, Model model) {
    Sector currentSector = sectorService.getSectorById(sectorId);
    model.addAttribute("sectorOrder", currentSector);
    model.addAttribute("rowOrder", 1);
    model.addAttribute("seatOrder", 1);
    return "redirect:/Order/Order.do";
  }

  @RequestMapping(value = "Order/setSeat.do", method = RequestMethod.POST) public String orderSetSeat(@RequestParam(value = "row", required = true) int row, Model model) {
    Integer currentRow = row;
    model.addAttribute("rowOrder", currentRow);
    model.addAttribute("seatOrder", 1);
    return "redirect:/Order/Order.do";
  }

  @SuppressWarnings(value = { "unchecked" }) @RequestMapping(value = "Order/addTicket.do", method = RequestMethod.POST) public String orderAddTicket(@RequestParam(value = "seat", required = false) int[] seat, Model model, @ModelAttribute(value = "eventOrder") Event currentEvent, @ModelAttribute(value = "sectorOrder") Sector currentSector, @ModelAttribute(value = "rowOrder") Integer currentRow) {
    ArrayList<Ticket> orderTickets = (ArrayList) model.asMap().get("orderList");
    if (orderTickets == null) {
      orderTickets = new ArrayList<>();
    }
    Double orderPrice = (Double) model.asMap().get("orderPrice");
    if (orderPrice == null) {
      orderPrice = 0.0;
    }
    model.addAttribute("errorOrder", "");
    model.addAttribute("messageOrder", "");
    StringBuilder message = new StringBuilder(500);
    boolean doubleTicket;
    message.append("");
    if (orderTickets != null && orderTickets.size() > 0) {
      ArrayList<Ticket> deletingTicket = new ArrayList<>();
      for (Ticket ord : orderTickets) {
        if (ticketService.getTicketById(ord.getId()) == null) {
          deletingTicket.add(ord);
        }
      }
      if (deletingTicket.size() == 1) {
        model.addAttribute("errorOrder", "\u0411\u0438\u043b\u0435\u0442 ID = " + deletingTicket.get(0).getId() + " \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0443\u0434\u0430\u043b\u0435\u043d \u0438\u0437 \u0437\u0430\u043a\u0430\u0437\u0430, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0435 \u0431\u044b\u043b \u043a\u0443\u043f\u043b\u0435\u043d \u0432 \u0442\u0435\u0447\u0435\u043d\u0438\u0438 10 \u043c\u0438\u043d\u0443\u0442");
        orderTickets.remove(deletingTicket.get(0));
        orderPrice -= deletingTicket.get(0).getSector().getPrice();
      }
      if (deletingTicket.size() > 1) {
        StringBuilder builder = new StringBuilder(200);
        for (Ticket tic : deletingTicket) {
          builder.append(tic.getId()).append("  ");
          orderTickets.remove(tic);
          orderPrice -= tic.getSector().getPrice();
        }
        model.addAttribute("errorOrder", "\u0411\u0438\u043b\u0435\u0442\u044b ID = " + builder.toString() + " \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0443\u0434\u0430\u043b\u0435\u043d\u044b \u0438\u0437 \u0437\u0430\u043a\u0430\u0437\u0430, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0435 \u0431\u044b\u043b\u0438 \u043a\u0443\u043f\u043b\u0435\u043d\u044b \u0432 \u0442\u0435\u0447\u0435\u043d\u0438\u0438 10 \u043c\u0438\u043d\u0443\u0442");
      }
    }
    for (int seat1 : seat) {
      Ticket ticket = new Ticket();
      ticket.setSector(currentSector);
      ticket.setRow(currentRow);
      ticket.setSeat(seat1);
      doubleTicket = false;
      if (orderTickets.size() > 0) {
        for (Ticket ord : orderTickets) {
          if (ord.getSector().equals(currentSector) && ord.getSeat() == seat1 && ord.getRow() == currentRow) {
            doubleTicket = true;
            break;
          }
        }
      }
      if (!doubleTicket) {
        if (ticketService.isPlaceFree(currentSector, currentRow, seat1) == 0 && !sectorService.getSectorById(currentSector.getId()).isDeleted()) {
          ticketService.addTicket(ticket);
          orderPrice += currentSector.getPrice();
          orderTickets.add(ticket);
        } else {
          message.append("\u0411\u0438\u043b\u0435\u0442 \u043d\u0430 ").append(currentSector.getEvent().getDescription()).append(" \u0421\u0435\u043a\u0442\u043e\u0440: ").append(currentSector.getName()).append(" \u0420\u044f\u0434: ").append(currentRow).append(" \u041c\u0435\u0441\u0442\u043e: ").append(seat1).append(" \u0443\u0436\u0435 \u043f\u0440\u043e\u0434\u0430\u043d<br>");
        }
      }
    }
    List<Event> events = eventService.getFutureEvents();
    if (eventService.getEventById(currentEvent.getId()) == null || !events.contains(currentEvent) || currentEvent.getDate().before(new Date())) {
      currentEvent = events.get(0);
    }
    List<Sector> sectors = sectorService.getSectorsByEvent(currentEvent);
    Map<Sector, Short> sectorsMap = new TreeMap<>();
    for (Sector sector : sectors) {
      sectorsMap.put(sector, (short) ticketService.getFreeTicketsAmountBySector(sector));
    }
    if (sectorService.getSectorById(currentSector.getId()) == null || currentSector.getEvent().getDate().before(new Date())) {
      currentSector = sectors.get(0);
      currentRow = 1;
    }
    model.addAttribute("sectorsMapOrder", sectorsMap);
    Map<Byte, Byte> rowsMap1 = new TreeMap<>();
    for (byte i = 1; i <= currentSector.getMaxRows(); i++) {
      rowsMap1.put(i, (byte) ticketService.getFreeTicketsAmountBySectorRow(currentSector, i));
    }
    model.addAttribute("rowsMapOrder", rowsMap1);
    Map<Integer, Integer> seatsMap1 = new TreeMap<>();
    for (int i = 1; i <= currentSector.getMaxSeats(); i++) {
      seatsMap1.put(i, ticketService.isPlaceFree(currentSector, currentRow, i));
    }
    model.addAttribute("eventOrder", currentEvent);
    model.addAttribute("sectorOrder", currentSector);
    model.addAttribute("rowOrder", currentRow);
    model.addAttribute("seatsMapOrder", seatsMap1);
    model.addAttribute("messageOrder", message);
    model.addAttribute("seatOrder", seat[(seat.length - 1)]);
    model.addAttribute("orderPrice", orderPrice);
    model.addAttribute("orderList", orderTickets);
    return "Order";
  }

  @RequestMapping(value = "Order/delTicket.do", method = RequestMethod.POST) public String orderDelTicket(@RequestParam(value = "orderId") int orderId, Model model, @ModelAttribute(value = "eventOrder") Event currentEvent, @ModelAttribute(value = "sectorOrder") Sector currentSector, @ModelAttribute(value = "rowOrder") Integer currentRow) {
    ArrayList<Ticket> orderTickets = (ArrayList) model.asMap().get("orderList");
    Double orderPrice = (Double) model.asMap().get("orderPrice");
    model.addAttribute("errorOrder", "");
    model.addAttribute("messageOrder", "");
    if (orderTickets != null && orderTickets.size() > 0) {
      ArrayList<Ticket> deletingTicket = new ArrayList<>();
      for (Ticket ord : orderTickets) {
        if (ticketService.getTicketById(ord.getId()) == null) {
          deletingTicket.add(ord);
        }
      }
      if (deletingTicket.size() == 1) {
        model.addAttribute("errorOrder", "\u0411\u0438\u043b\u0435\u0442 ID = " + deletingTicket.get(0).getId() + " \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0443\u0434\u0430\u043b\u0435\u043d \u0438\u0437 \u0437\u0430\u043a\u0430\u0437\u0430, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0435 \u0431\u044b\u043b \u043a\u0443\u043f\u043b\u0435\u043d \u0432 \u0442\u0435\u0447\u0435\u043d\u0438\u0438 10 \u043c\u0438\u043d\u0443\u0442");
        orderTickets.remove(deletingTicket.get(0));
        orderPrice -= deletingTicket.get(0).getSector().getPrice();
      }
      if (deletingTicket.size() > 1) {
        StringBuilder builder = new StringBuilder(200);
        for (Ticket tic : deletingTicket) {
          builder.append(tic.getId()).append("  ");
          orderTickets.remove(tic);
          orderPrice -= tic.getSector().getPrice();
        }
        model.addAttribute("errorOrder", "\u0411\u0438\u043b\u0435\u0442\u044b ID = " + builder.toString() + " \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0443\u0434\u0430\u043b\u0435\u043d\u044b \u0438\u0437 \u0437\u0430\u043a\u0430\u0437\u0430, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0435 \u0431\u044b\u043b\u0438 \u043a\u0443\u043f\u043b\u0435\u043d\u044b \u0432 \u0442\u0435\u0447\u0435\u043d\u0438\u0438 10 \u043c\u0438\u043d\u0443\u0442");
      }
    }
    for (Ticket ord : orderTickets) {
      if (ord.getId() == orderId) {
        ticketService.deleteTicket(ord);
        orderTickets.remove(ord);
        orderPrice -= ord.getSector().getPrice();
        model.addAttribute("messageOrder", "\u0411\u0438\u043b\u0435\u0442 ID = " + ord.getId() + " \u0443\u0434\u0430\u043b\u0451\u043d \u0438\u0437 \u0437\u0430\u043a\u0430\u0437\u0430");
        break;
      }
    }
    List<Event> events = eventService.getFutureEvents();
    if (eventService.getEventById(currentEvent.getId()) == null || !events.contains(currentEvent) || currentEvent.getDate().before(new Date())) {
      currentEvent = events.get(0);
    }
    List<Sector> sectors = sectorService.getSectorsByEvent(currentEvent);
    Map<Sector, Short> sectorsMap = new TreeMap<>();
    for (Sector sector : sectors) {
      sectorsMap.put(sector, (short) ticketService.getFreeTicketsAmountBySector(sector));
    }
    if (sectorService.getSectorById(currentSector.getId()) == null || currentSector.getEvent().getDate().before(new Date())) {
      currentSector = sectors.get(0);
      currentRow = 1;
    }
    model.addAttribute("sectorsMapOrder", sectorsMap);
    Map<Byte, Byte> rowsMap1 = new TreeMap<>();
    for (byte i = 1; i <= currentSector.getMaxRows(); i++) {
      rowsMap1.put(i, (byte) ticketService.getFreeTicketsAmountBySectorRow(currentSector, i));
    }
    model.addAttribute("rowsMapOrder", rowsMap1);
    Map<Integer, Integer> seatsMap1 = new TreeMap<>();
    for (int i = 1; i <= currentSector.getMaxSeats(); i++) {
      seatsMap1.put(i, ticketService.isPlaceFree(currentSector, currentRow, i));
    }
    model.addAttribute("eventOrder", currentEvent);
    model.addAttribute("sectorOrder", currentSector);
    model.addAttribute("rowOrder", currentRow);
    model.addAttribute("seatsMapOrder", seatsMap1);
    model.addAttribute("orderPrice", orderPrice);
    model.addAttribute("orderList", orderTickets);
    return "Order";
  }

  @RequestMapping(value = "Order/Buy.do", method = RequestMethod.POST) public String orderBuy(Model model, @ModelAttribute(value = "sectorOrder") Sector currentSector, @ModelAttribute(value = "rowOrder") Integer currentRow, @ModelAttribute(value = "eventOrder") Event currentEvent) {
    Double orderPrice = (Double) model.asMap().get("orderPrice");
    ArrayList<Ticket> orderTickets = (ArrayList) model.asMap().get("orderList");
    StringBuilder idBuy = new StringBuilder(200);
    model.addAttribute("errorOrder", "");
    model.addAttribute("messageOrder", "");
    if (orderTickets != null && orderTickets.size() > 0) {
      ArrayList<Ticket> deletingTicket = new ArrayList<>();
      for (Ticket ord : orderTickets) {
        if (ticketService.getTicketById(ord.getId()) == null) {
          deletingTicket.add(ord);
        }
      }
      if (deletingTicket.size() == 1) {
        model.addAttribute("errorOrder", "\u0411\u0438\u043b\u0435\u0442 ID = " + deletingTicket.get(0).getId() + " \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0443\u0434\u0430\u043b\u0435\u043d \u0438\u0437 \u0437\u0430\u043a\u0430\u0437\u0430, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0435 \u0431\u044b\u043b \u043a\u0443\u043f\u043b\u0435\u043d \u0432 \u0442\u0435\u0447\u0435\u043d\u0438\u0438 10 \u043c\u0438\u043d\u0443\u0442");
        orderTickets.remove(deletingTicket.get(0));
        orderPrice -= deletingTicket.get(0).getSector().getPrice();
      }
      if (deletingTicket.size() > 1) {
        StringBuilder builder = new StringBuilder(200);
        for (Ticket tic : deletingTicket) {
          builder.append(tic.getId()).append("  ");
          orderTickets.remove(tic);
          orderPrice -= tic.getSector().getPrice();
        }
        model.addAttribute("errorOrder", "\u0411\u0438\u043b\u0435\u0442\u044b ID = " + builder.toString() + " \u0430\u0432\u0442\u043e\u043c\u0430\u0442\u0438\u0447\u0435\u0441\u043a\u0438 \u0443\u0434\u0430\u043b\u0435\u043d\u044b \u0438\u0437 \u0437\u0430\u043a\u0430\u0437\u0430, \u0442\u0430\u043a \u043a\u0430\u043a \u043d\u0435 \u0431\u044b\u043b\u0438 \u043a\u0443\u043f\u043b\u0435\u043d\u044b \u0432 \u0442\u0435\u0447\u0435\u043d\u0438\u0438 10 \u043c\u0438\u043d\u0443\u0442");
      }
    }
    for (Ticket ticket : orderTickets) {
      if (ticketService.getTicketById(ticket.getId()).isConfirmed()) {
        if (ticketService.getTicketById(ticket.getId()).isReserved()) {
          model.addAttribute("errorOrder", "\u041e\u0428\u0418\u0411\u041a\u0410! \u0411\u0438\u043b\u0435\u0442 ID = " + ticket.getId() + " \u0443\u0436\u0435 \u0437\u0430\u0431\u0440\u043e\u043d\u0438\u0440\u043e\u0432\u0430\u043d");
        } else {
          model.addAttribute("errorOrder", "\u041e\u0428\u0418\u0411\u041a\u0410! \u0411\u0438\u043b\u0435\u0442 ID = " + ticket.getId() + " \u0443\u0436\u0435 \u043f\u0440\u043e\u0434\u0430\u043d");
        }
        orderTickets.remove(ticket);
        orderPrice -= ticket.getSector().getPrice();
        model.addAttribute("orderPrice", orderPrice);
        return "Order";
      }
    }
    for (Ticket ticket : orderTickets) {
      ticket.setConfirmed(true);
      idBuy.append(ticket.getId()).append("  ");
      ticketService.addTicket(ticket);
    }
    List<Event> events = eventService.getFutureEvents();
    if (eventService.getEventById(currentEvent.getId()) == null || !events.contains(currentEvent) || currentEvent.getDate().before(new Date())) {
      currentEvent = events.get(0);
    }
    List<Sector> sectors = sectorService.getSectorsByEvent(currentEvent);
    Map<Sector, Short> sectorsMap = new TreeMap<>();
    for (Sector sector : sectors) {
      sectorsMap.put(sector, (short) ticketService.getFreeTicketsAmountBySector(sector));
    }
    if (sectorService.getSectorById(currentSector.getId()) == null || currentSector.getEvent().getDate().before(new Date())) {
      currentSector = sectors.get(0);
      currentRow = 1;
    }
    model.addAttribute("sectorsMapOrder", sectorsMap);
    Map<Byte, Byte> rowsMap1 = new TreeMap<>();
    for (byte i = 1; i <= currentSector.getMaxRows(); i++) {
      rowsMap1.put(i, (byte) ticketService.getFreeTicketsAmountBySectorRow(currentSector, i));
    }
    model.addAttribute("rowsMapOrder", rowsMap1);
    Map<Integer, Integer> seatsMap1 = new TreeMap<>();
    for (int i = 1; i <= currentSector.getMaxSeats(); i++) {
      seatsMap1.put(i, ticketService.isPlaceFree(currentSector, currentRow, i));
    }
    model.addAttribute("eventOrder", currentEvent);
    model.addAttribute("sectorOrder", currentSector);
    model.addAttribute("rowOrder", currentRow);
    model.addAttribute("seatsMapOrder", seatsMap1);
    model.addAttribute("orderConfirmation", orderTickets);
    model.addAttribute("priceConfirmation", orderPrice);
    model.addAttribute("orderPrice", 0.0);
    model.addAttribute("orderList", new ArrayList<Ticket>());
    return "OrderInfo";
  }

  @SuppressWarnings(value = { "unchecked" }) @RequestMapping(value = "OrderInfo/Clear.do") public String orderInfoClear(Model model) {
    ArrayList<Ticket> orderTickets = (ArrayList) model.asMap().get("orderConfirmation");
    orderTickets.clear();
    model.addAttribute("orderConfirmation", orderTickets);
    model.addAttribute("priceConfirmation", 0.0);
    return "Order";
  }

  @SuppressWarnings(value = { "unchecked" }) @RequestMapping(value = "Order/Cancel.do") public String orderCancel(Model model) {
    ArrayList<Ticket> orderTickets = (ArrayList) model.asMap().get("orderList");
    if (orderTickets != null) {
      for (Ticket ord : orderTickets) {
        ticketService.deleteTicket(ord);
      }
      orderTickets.clear();
      model.addAttribute("orderList", orderTickets);
    }
    model.addAttribute("orderPrice", 0.0);
    return "redirect:/Order/Order.do";
  }
}
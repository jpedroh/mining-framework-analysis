package org.JavaArt.TicketManager.service;
import org.JavaArt.TicketManager.DAO.TicketRepository;
import org.JavaArt.TicketManager.DAO.impl.TicketRepositoryImpl;
import org.JavaArt.TicketManager.entities.Client;
import org.JavaArt.TicketManager.entities.Sector;
import org.JavaArt.TicketManager.entities.Ticket;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service public class TicketService {
  private TicketRepository ticketRepository = new TicketRepositoryImpl();

  private static TicketService ticketService;

  private TicketService() {
  }

  public static TicketService getInstance() {
    if (ticketService == null) {
      ticketService = new TicketService();
    }
    return ticketService;
  }

  public int getTicketsAmountByClient(Client client) {
    return ticketRepository.getTicketsAmountByClient(client);
  }

  public List<Ticket> getTicketsByClient(Client client) {
    return ticketRepository.getTicketsByClient(client);
  }

  public int getFreeTicketsAmountBySector(Sector sector) {
    return ticketRepository.getFreeTicketsAmountBySector(sector);
  }

  public int getFreeTicketsAmountBySectorRow(Sector sector, int row) {
    return ticketRepository.getFreeTicketsAmountBySectorRow(sector, row);
  }

  public void deleteTickets(List<Ticket> tickets) {
    ticketRepository.deleteTickets(tickets);
  }

  public int isPlaceFree(Sector sector, int row, int seat) {
    return ticketRepository.isPlaceFree(sector, row, seat);
  }

  public void addTicket(Ticket ticket) {
    ticketRepository.saveOrUpdateTicket(ticket);
  }

  public Ticket getTicketById(int ticketId) {
    return ticketRepository.getTicketById(ticketId);
  }

  public void deleteTicket(Ticket ticket) {
    ticketRepository.deleteTicket(ticket);
  }

  public void saveOrUpdateTickets(List<Ticket> tickets) {
    ticketRepository.saveOrUpdateTickets(tickets);
  }

  public void deleteNonConfirmedTickets(int minutes) {
    ticketRepository.deleteNonConfirmedTickets(minutes);
  }

  public Map<Integer, String> seatStatus(Sector sector, int row, ArrayList<Ticket> order) {
    Map<Integer, String> seatsMap = new TreeMap<>();
    List<Ticket> ticket = ticketRepository.getAllTicketsBySectorAndRow(sector, row);
    for (int i = 1; i <= sector.getMaxSeats(); i++) {

<<<<<<< /usr/src/app/output/kvladislav/ticketmanager/7e604bf4845cfc20af24cc20cbacff5ea768dbc4/src/main/java/org/JavaArt/TicketManager/service/TicketService.java/left.java
      if (ticketRepository.isPlaceFree(sector, row, i) == 0) {
        seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441:  \u0432 \u043f\u0440\u043e\u0434\u0430\u0436\u0435");
      } else {
        for (Ticket tic : ticket) {
          if (tic.getSeat() == i) {
            if ((tic.getReserved() && tic.isConfirmed())) {
              seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441: \u0437\u0430\u0431\u0440\u043e\u043d\u0438\u0440\u043e\u0432\u0430\u043d");
            }
            if (!tic.getReserved() && tic.isConfirmed()) {
              seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441: \u043f\u0440\u043e\u0434\u0430\u043d");
            }
            if (!tic.isConfirmed()) {
              seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441: \u043d\u0435 \u0443\u0442\u0432\u0435\u0440\u0436\u0434\u0451\u043d");
            }
          }
        }
      }
=======
      if (ticketRepository.isPlaceFree(sector, row, i)) {
        if (order.size() > 0 && order != null) {
          for (Ticket ord : order) {
            if (ord.getSector().equals(sector) && ord.getRow() == row && ord.getSeat() == i) {
              seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441: \u043d\u0435 \u0443\u0442\u0432\u0435\u0440\u0436\u0434\u0451\u043d");
              break;
            } else {
              seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441:  \u0432 \u043f\u0440\u043e\u0434\u0430\u0436\u0435");
            }
          }
        } else {
          seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441:  \u0432 \u043f\u0440\u043e\u0434\u0430\u0436\u0435");
        }
      } else {
        for (Ticket tic : ticket) {
          if (tic.getSeat() == i) {
            if ((tic.getReserved() && tic.getConfirmed())) {
              seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441: \u0437\u0430\u0431\u0440\u043e\u043d\u0438\u0440\u043e\u0432\u0430\u043d");
            }
            if (!tic.getReserved() && tic.getConfirmed()) {
              seatsMap.put(i, "\u0421\u0442\u0430\u0442\u0443\u0441: \u043f\u0440\u043e\u0434\u0430\u043d");
            }
          }
        }
      }
>>>>>>> /usr/src/app/output/kvladislav/ticketmanager/7e604bf4845cfc20af24cc20cbacff5ea768dbc4/src/main/java/org/JavaArt/TicketManager/service/TicketService.java/right.java
    }
    return seatsMap;
  }
}
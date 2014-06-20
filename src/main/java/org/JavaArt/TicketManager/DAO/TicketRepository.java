package org.JavaArt.TicketManager.DAO;

import org.JavaArt.TicketManager.entities.Sector;
import org.JavaArt.TicketManager.entities.Ticket;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Vladislav Karpenko
 * Date: 06.06.2014
 * Time: 10:28
 */

@Repository

public interface TicketRepository {
    public void addTicket(Ticket ticket) throws SQLException;
    public void updateTicket(Ticket ticket) throws SQLException;
    public Ticket getTicketById(int id) throws SQLException;
    public List<Ticket> getAllTickets() throws SQLException;
    public void deleteTicket(Ticket ticket) throws SQLException;
    public int getFreeTicketsAmountBySector(Sector sector) throws SQLException;
    public int getFreeTicketsAmountBySectorRow(Sector sector, int row) throws SQLException;
}

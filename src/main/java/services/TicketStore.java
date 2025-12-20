package services;

import models.Ticket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TicketStore {

    private static final List<Ticket> TICKETS = new ArrayList<>();

    public static void addTicket(Ticket ticket) {
        TICKETS.add(ticket);
    }

    public static List<Ticket> getAllTickets() {
        return Collections.unmodifiableList(TICKETS);
    }

    // NEW: find by id
    public static Ticket getTicketById(String id) {
        for (Ticket t : TICKETS) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    // NEW: assign to agent
    public static void assignTicketToAgent(String ticketId, String agentName) {
        Ticket t = getTicketById(ticketId);
        if (t != null) {
            t.setAssignedTo(agentName);
            t.setStatus("IN_PROGRESS");
        }
    }
}

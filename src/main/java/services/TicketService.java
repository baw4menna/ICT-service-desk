package services;

import models.Ticket;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TicketService {

    private static final String TICKETS_FILE = "tickets.txt";
    private static final TicketService INSTANCE = new TicketService();
    private List<Ticket> allTickets = new ArrayList<>();

    private TicketService() {
        ensureFileExists();
        loadTicketsFromFile();
    }

    public static TicketService getInstance() {
        return INSTANCE;
    }

    private void ensureFileExists() {
        try {
            Path path = Paths.get(TICKETS_FILE);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // SAVE ticket to file (APPEND new ticket)
    public void saveTicket(Ticket ticket) {
        try {
            String line = String.join("|",
                    ticket.getId(),
                    ticket.getTitle(),
                    ticket.getDescription(),
                    ticket.getStatus(),
                    ticket.getStudentEmail(),
                    ticket.getCategory() != null ? ticket.getCategory() : "",
                    ticket.getPriority() != null ? ticket.getPriority() : "",
                    ticket.getAssignedTo() != null ? ticket.getAssignedTo() : "",
                    ticket.getCreatedDate() != null ? ticket.getCreatedDate() : "",
                    ticket.getUpdatedDate() != null ? ticket.getUpdatedDate() : "",
                    ticket.getResolutionNotes() != null ? ticket.getResolutionNotes() : ""
            );

            Files.write(
                    Paths.get(TICKETS_FILE),
                    (line + "\n").getBytes(),
                    StandardOpenOption.APPEND
            );

            // Add to in-memory list
            allTickets.add(ticket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // LOAD all tickets from file
    private void loadTicketsFromFile() {
        allTickets.clear();
        try {
            if (!Files.exists(Paths.get(TICKETS_FILE))) {
                return;
            }

            List<String> lines = Files.readAllLines(Paths.get(TICKETS_FILE));
            for (String line : lines) {
                if (line.trim().isEmpty()) continue;
                Ticket ticket = parseTicketFromLine(line);
                if (ticket != null) {
                    allTickets.add(ticket);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Ticket parseTicketFromLine(String line) {
        try {
            String[] parts = line.split("\\|", -1);
            if (parts.length < 5) return null;

            Ticket t = new Ticket(
                    parts[1],  // title
                    parts[2],  // description
                    parts[3],  // status
                    parts[4]   // studentEmail
            );
            t.setId(parts[0]);
            if (parts.length > 5 && !parts[5].isEmpty()) t.setCategory(parts[5]);
            if (parts.length > 6 && !parts[6].isEmpty()) t.setPriority(parts[6]);
            if (parts.length > 7 && !parts[7].isEmpty()) t.setAssignedTo(parts[7]);
            if (parts.length > 8 && !parts[8].isEmpty()) t.setCreatedDate(parts[8]);
            if (parts.length > 9 && !parts[9].isEmpty()) t.setUpdatedDate(parts[9]);
            if (parts.length > 10 && !parts[10].isEmpty()) t.setResolutionNotes(parts[10]);

            return t;
        } catch (Exception e) {
            return null;
        }
    }

    // GET all tickets
    public List<Ticket> getAllTickets() {
        return new ArrayList<>(allTickets);
    }

    // RELOAD all tickets from file
    public void reloadAllTickets() {
        loadTicketsFromFile();
    }

    // UPDATE ticket in file (find by ID and replace line)
    public void updateTicket(Ticket ticket) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(TICKETS_FILE));
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).startsWith(ticket.getId() + "|")) {
                    String newLine = String.join("|",
                            ticket.getId(),
                            ticket.getTitle(),
                            ticket.getDescription(),
                            ticket.getStatus(),
                            ticket.getStudentEmail(),
                            ticket.getCategory() != null ? ticket.getCategory() : "",
                            ticket.getPriority() != null ? ticket.getPriority() : "",
                            ticket.getAssignedTo() != null ? ticket.getAssignedTo() : "",
                            ticket.getCreatedDate() != null ? ticket.getCreatedDate() : "",
                            ticket.getUpdatedDate() != null ? ticket.getUpdatedDate() : "",
                            ticket.getResolutionNotes() != null ? ticket.getResolutionNotes() : ""
                    );
                    lines.set(i, newLine);
                    break;
                }
            }
            Files.write(Paths.get(TICKETS_FILE), lines);
            reloadAllTickets(); // Reload in-memory copy
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GET ticket by ID
    public Ticket getTicketById(String id) {
        for (Ticket t : allTickets) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    // ASSIGN ticket to agent
    public void assignTicketToAgent(String ticketId, String agentName) {
        Ticket t = getTicketById(ticketId);
        if (t != null) {
            t.setAssignedTo(agentName);
            t.setStatus("IN_PROGRESS");
            updateTicket(t);
        }
    }
}

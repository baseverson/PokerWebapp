package com.sevdev;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ActionLog {

    // This class is a singleton.
    private static ActionLog instance = null;
    public static ActionLog getInstance() {
        if (instance == null) {
            instance = new ActionLog();
        }
        return instance;
    }

    int maxSize = 20;
    Queue<String> log = new LinkedList<String>();

    public Queue<String> getLog() { return this.log; }

    /**
     * Constructor.
     */
    public ActionLog() {
        // Initialize the log with 20 blank strings.
        for (int i=0; i<maxSize; i++) {
            log.add("");
        }
    }

    /**
     * Add a new message to the log.
     *
     * @param message - new message to add.
     */
    public void add(String message) {
        log.add(message);
        log.remove();
    }

    /**
     * Returns the log as a JSON string
     *
     * @return - log as a JSON string
     *
     * @throws Exception - if error converting to JSON string
     */
    public String getLogAsJSON() throws Exception {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(log);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error converting ActionLog to JSON.");
        }
    }
}

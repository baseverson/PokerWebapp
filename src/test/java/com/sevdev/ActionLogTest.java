package com.sevdev;

import junit.framework.TestCase;

import javax.swing.*;

public class ActionLogTest extends TestCase {

    public ActionLogTest(String name) {
        super(name);
    }

    public void testGeneral() {

        for (int i=0; i<22; i++) {
            ActionLog.getInstance().add("Log" + i);
        }

        try {
            String compareJSON = "[ \"Log2\", \"Log3\", \"Log4\", \"Log5\", \"Log6\", \"Log7\", \"Log8\", \"Log9\", \"Log10\", \"Log11\", \"Log12\", \"Log13\", \"Log14\", \"Log15\", \"Log16\", \"Log17\", \"Log18\", \"Log19\", \"Log20\", \"Log21\" ]";
            String logJSON = ActionLog.getInstance().getLogAsJSON();
            System.out.println(logJSON);
            assertEquals(compareJSON, logJSON);
        }
        catch (Exception e) {
            assertTrue(false);
        }
    }
}

package com.newrelic.telemetry.examples;

import com.newrelic.telemetry.Attributes;
import com.newrelic.telemetry.EventBatchSenderFactory;
import com.newrelic.telemetry.OkHttpPoster;
import com.newrelic.telemetry.events.Event;
import com.newrelic.telemetry.events.EventBatch;
import com.newrelic.telemetry.events.EventBatchSender;
import com.newrelic.telemetry.events.EventBuffer;

import javax.lang.model.type.ArrayType;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class StudentDataGeneration {
    public static void main(String[] args) throws Exception {

        // This snippet configures objects that will send our data to New Relic One
        String licenseKey = args[0];
        EventBatchSenderFactory factory =
                EventBatchSenderFactory.fromHttpImplementation(OkHttpPoster::new);

        EventBatchSender sender =
                EventBatchSender.create(factory.configureWith(licenseKey).useLicenseKey(true).build());

        EventBuffer eventBuffer = new EventBuffer(getCommonAttributes());

        // Ranges of names, ages, etc. that are used to randomly generate student interactions

        String[] names = {"bob", "john", "oliver", "emily", "sarah", "wendy"};
        Integer[] ages = {17, 18, 19, 20, 21};
        String[] departments = {"Engineering", "Business", "Other"};
        String[] univYear = {"Freshman", "Sophomore", "Junior", "Senior"};
        String[] websiteSections = {"Explorer", "Browse Data", "Dashboards", "Alerts / AI", "Kubernetes"};

        // Random object used to generate student data

        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            long timestamp = System.currentTimeMillis();
            String randName = names[rand.nextInt(names.length)];
            int randAge = ages[rand.nextInt(ages.length)];
            String randDept = departments[rand.nextInt(departments.length)];
            String randYear = univYear[rand.nextInt(univYear.length)];
            String randSection = websiteSections[rand.nextInt(websiteSections.length)];

            int numClicks = rand.nextInt(100); // Assume that they click on the NR website between 0 and 100 times
            double duration = 180 * rand.nextDouble(); // Assume that they stay on the NR website for 3 mins max

            // Attributes for Each Student

            Attributes curAttr = new Attributes();
            curAttr.put("Name", randName);
            curAttr.put("Age", randAge);
            curAttr.put("Department", randDept);
            curAttr.put("Year", randYear);
            curAttr.put("ClickType", randSection);
            curAttr.put("Clicks", numClicks);
            curAttr.put("Duration", duration);

            // Each event represents a record for one student

            Event testEvent = new Event("StudentDataV2", curAttr, timestamp);
            eventBuffer.addEvent(testEvent);
        }

        sender.sendBatch(eventBuffer.createBatch());
    }

    private static Attributes getCommonAttributes () {
        return new Attributes().put("exampleName", "StudentDataExample");
    }
}

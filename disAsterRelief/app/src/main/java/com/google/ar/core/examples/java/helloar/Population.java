package com.google.ar.core.examples.java.helloar;

import java.util.ArrayList;
import java.util.List;

/**
 * This class will contain an array of people
 * Created by moss on 1/13/18.
 */

public class Population {
    private List<Person> pop = new ArrayList<Person>();
    //private variable for location of the observer, assumes first element is latitude and second is longitude
    private double[] location;
    public Population(int scale, double[] location) {
        this.location = location;
        this.populateEvent(scale);
    }

    public void populateEvent(int scale) {
        pop.add(new Person(21,75,createRandomLocation(), "Matt"));
        pop.add(new Person(21,29,createRandomLocation(), "Massimo"));
        pop.add(new Person(20,198,createRandomLocation(), "Elliot"));
        pop.add(new Person(20,160,createRandomLocation(), "Alex"));
        pop.add(new Person(35,0,createRandomLocation(), "Jake"));
    }
    /** //creates a single person with random attributes
    public Person createPerson(double seed) {
        double[] loc = createRandomLocation();
        double hr =  Math.random()*200;
        if(hr == 0) {
            System.out.println("HR is Zero!!!");
        }
        Person p = new Person((int)seed*100,hr, loc);
        return p;
    }
     **/
    //creates a random location for the person, we will assume for the sake of the demo that the person is within 30 meters.
    //this method creates the location in longitude and latitude for authenticity, as that would be the results from a gps.
    public double[] createRandomLocation() {
        int radius = 6378137 + 728;
        double pi = 3.1415926;
        double[] d = new double[2];
        double theta = 360*Math.random();
        double r_deg = 180*(5 + 10*Math.random())/radius/pi;
        d[0] = location[0] + r_deg*Math.cos(theta);
        d[1] = location[1] + r_deg*Math.sin(theta);
        return d;
    }

    public List<Person> getSpecificStatus(int status) {
        List<Person> people = new ArrayList<Person>();
        for(Person p : pop) {
            if(p.getStatus() == status) people.add(p);
        }
        return people;
    }

    public List<Person> getPopulation() {
        return pop;
    }
}

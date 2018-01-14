package com.google.ar.core.examples.java.helloar;

/**
 * This class represents a person, a person contains a heartrate, age, and location. They also have a status dependent on heartrate, as well
 * well as a location vector created specifically for the ARcore coordinates
 * Created by moss on 1/13/18.
 */

public class Person {
    private int age;
    private double heartrate;
    private String name;
    //private variable for location of the person, assumes first element is latitude and second is longitude
    private double[] location;
    private int radius = 6378137 + 728; //radius of earth


    public Person(int age, double heartrate, double[] location, String name) {
        this.age = age;
        this.heartrate = Math.round(heartrate);
        this.location = location;
        this.name = name;
    }
    public String getName() {
        return name;
    }
    public int getAge(){
        return age;
    }

    public double getHeartrate() {
        return heartrate;
    }

    public double getLat() {
        return location[0];
    }

    public double getLong() {
        return location[1];
    }
    //method returns the health status of a person depending on heartrate, integers rate the severity:
    // 0=dead, 1=urgent, 2=unhealthy, 3=healthy
    public int getStatus() {
        if(heartrate < 60 || 150 < heartrate) {
            if(heartrate == 0) {
                return 0;
            }
            if(heartrate < 30 || 180 < heartrate) {
                return 1;
            }
            return 2;
        }
        return 3;
    }
    //method returns the x-coordinate of the person in ARcore real world coordinates
    public double getX(double[] n,  double[] observer) {
        //create vector and matrix variables for coordinate transformation
        double pi = 3.1415926;
       double obs_n = pi*(observer[0] - location[0])*radius/180;
       double obs_e = pi*(observer[1] - location[1])*radius/180;
       double x = n[0]*obs_n - n[2]*obs_e;
       return x;
    }

    //method returns the y-coordinate of the person in ARcode real world coordinates
    public double getZ(double[] n, double[] observer) {
        //create vector and matrix variables for coordinate transformation
        double pi = 3.1415926;
        double obs_n = pi*(observer[0] - location[0])*radius/180;
        double obs_e = pi*(observer[1] - location[1])*radius/180;
        double z = n[2]*obs_n + n[0]*obs_e;
        return z;
    }
}

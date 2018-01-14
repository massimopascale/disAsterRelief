package com.google.ar.core.examples.java.helloar;


import com.google.ar.core.Anchor;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the same as an Anchor, but also holds a corresponding person object.
 * Created by moss on 1/13/18.
 */

public class Pin {
    private Person person;
    private Anchor anchor;
    private List<Anchor> billboardList = new ArrayList<>();

    public Pin(Person p, Anchor a, Anchor bill1, Anchor bill2, Anchor bill3) {
        this.anchor = a;
        this.person = p;
        billboardList.add(bill1);
        billboardList.add(bill2);
        billboardList.add(bill3);

    }
    public Anchor getAnchor() {
        return anchor;
    }

    public List<Anchor> getBillboardList() {
        return billboardList;
    }
    public Person getPerson() {
        return person;
    }
}

package com.myvisontoday.ourmap;

import android.support.test.filters.SmallTest;

import com.google.android.gms.maps.model.LatLng;

import junit.framework.TestCase;

/**
 * Created by Master on 6/17/2017.
 */

public class UserTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @SmallTest
    public void testSetGetName(){
        User u = new User();
        String random = "Something random";
        u.setUserName(random);
        String s = u.getUserName();
        assertEquals(random, s);
    }

//    @SmallTest
//    public void testSetGetAge(){
//        User u = new User();
//        String random = "10";
//        u.setUserName(random);
//        String s = u.getAge();
//        assertEquals(random,s);
//    }

//    @SmallTest
//    public void testSetGetGender(){
//        User u = new User();
//        String random = "random gender";
//        u.setUserName(random);
//        String s = u.getGender();
//        assertEquals(random, s);
//    }

    @SmallTest
    public void testSetGetLatLng(){
        User u = new User();
        LatLng latlng = new LatLng(14,115);
        u.setLatLng(latlng);
        LatLng result = u.getLatLng();
        assertEquals(result, latlng);
    }



    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

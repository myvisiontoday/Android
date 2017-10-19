package com.myvisontoday.ourmap;

import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Master on 6/17/2017.
 */

public class RegistrationTest extends ActivityInstrumentationTestCase2<RegisterActivity> {

    public RegistrationTest(){
        super(RegisterActivity.class);
    }


    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    //testing if the interface is in place
    @SmallTest
    public void testEmail(){
        EditText e = (EditText) getActivity().findViewById(R.id.editTextEmail);
        assertNotNull(e);
    }

    @SmallTest
    public void testPassword(){
        EditText e = (EditText) getActivity().findViewById(R.id.editTextPassword);
        assertNotNull(e);
    }

    @SmallTest
    public void testAge(){
        EditText e = (EditText) getActivity().findViewById(R.id.editTextAge);
        assertNotNull(e);
    }

    @SmallTest
    public void testName(){
        EditText e = (EditText) getActivity().findViewById(R.id.editTextName);
        assertNotNull(e);
    }


    @SmallTest
    public void testGender(){
        EditText e = (EditText) getActivity().findViewById(R.id.editTextGender);
        assertNotNull(e);
    }


    @SmallTest
    public void testButtonRegister(){
        Button b = (Button) getActivity().findViewById(R.id.buttonRegister);
        assertNotNull(b);
    }

    //test for input

    @SmallTest
    public void testEmailInput(){
        RegisterActivity ra = new RegisterActivity();
        if(ra.buttonClicked())
        {
        boolean check;
        EditText e = (EditText) getActivity().findViewById(R.id.editTextEmail);
        String s = e.getText().toString();
        String[] aux = s.split("@");
        if(aux.length > 1) check = true;
        else check = false;

        assertEquals(true, check);}

    }

    @SmallTest
    public void testPasswordInput(){
        RegisterActivity ra = new RegisterActivity();
        if(ra.buttonClicked()) {
            boolean check;
            EditText e = (EditText) getActivity().findViewById(R.id.editTextPassword);
            String s = e.getText().toString();
            if (s.length() >= 6) check = true;
            else check = false;

            assertEquals(true, check);
        }
    }

    @SmallTest
    public void testGenderInput(){
        RegisterActivity ra = new RegisterActivity();
        if(ra.buttonClicked()) {
            boolean check;
            EditText e = (EditText) getActivity().findViewById(R.id.editTextGender);
            String s = e.getText().toString();
            if (s == "male" || s == "MALE" || s == "m" || s == "M" ||
                    s == "female" || s == "FEMALE" || s == "f" || s == "F") check = true;
            else check = false;
            assertEquals(true, check);
        }
    }

    @SmallTest
    public void testAgeInput(){
        RegisterActivity ra = new RegisterActivity();
        if(ra.buttonClicked())
        { boolean check;
        EditText e = (EditText) getActivity().findViewById(R.id.editTextAge);
        String s = e.getText().toString();
        int age = Integer.parseInt("s");
        if(age > 4 && age < 150) check = true;
        else check = false;
        assertEquals(true, check);}
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


}

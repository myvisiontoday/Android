package com.myvisontoday.ourmap;

import android.support.test.filters.SmallTest;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Master on 6/17/2017.
 */

public class LoginTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    public LoginTest(){
        super(LoginActivity.class);
    }


    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }

    //test the initialization of the interface
    @SmallTest
    public void testPassword(){
        EditText e = (EditText) getActivity().findViewById(R.id.password);
        assertNotNull(e);
    }

    @SmallTest
    public void testEmail(){
        EditText e = (EditText) getActivity().findViewById(R.id.email);
        assertNotNull(e);
    }

    @SmallTest
    public void testLoginView(){
        View login = getActivity().findViewById(R.id.login_form);
        assertNotNull(login);
    }

    @SmallTest
    public void testButton(){
        Button b = (Button) getActivity().findViewById(R.id.email_sign_in_button);
        assertNotNull(b);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}

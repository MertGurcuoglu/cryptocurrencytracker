package com.mertgurcuoglu.currencytracking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

  
@SpringBootTest  //the @springboottest annotation loads the full application context to ensure all components are working correctly
class CurrencyTrackingApplicationTests { //this class is used for testing the application's context

     //this test verifies that the application context loads successfully
    // if the method runs without throwing an exception, it means the application's basic setup is correct
    @Test
    void contextLoads() {
    }

}
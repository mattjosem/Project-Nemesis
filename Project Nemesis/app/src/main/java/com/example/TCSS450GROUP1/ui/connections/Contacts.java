package com.example.TCSS450GROUP1.ui.connections;

import java.io.Serializable;

/**
 * @author Joseph Rushford
 * Serializable Contacts to pass between fragments
 */
public class Contacts implements Serializable {


        private final String mEmail;

        /**



        /**
         * Constructor that instantiates fields from a JSONObject.
         */
        public Contacts(String username, int rowCount) throws Exception {

            mEmail = username;

        }


        /**
         * Returns username
         * @return
         */
        public String getUserName() {
            return mEmail;
        }






}



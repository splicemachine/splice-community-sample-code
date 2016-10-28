/*
 * Copyright 2012 - 2016 Splice Machine, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.splicemachine.tutorials.jpa.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet(name="Login", urlPatterns={"/login.do"})
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        
        String user = request.getParameter("user");
        String pwd = request.getParameter("pwd");
        Map<String, Object> responseMap = new HashMap<String, Object>();

        try {
            request.login(user, pwd);
            responseMap.put("authorized", true);
            
            //Normally you would connect to another system to get the details about
            //the user.  This is hard coded for demo purposes.
            responseMap.put("userId", user);
            responseMap.put("firstName", "Splice");
            responseMap.put("lastName", "User");
        } catch (Exception e) {
            responseMap.put("authorized", false);
        }

        response.setContentType("application/json");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(responseMap);
        response.getWriter().write(json);
    }

}

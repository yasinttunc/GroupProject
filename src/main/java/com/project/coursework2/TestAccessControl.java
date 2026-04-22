package com.project.coursework2.model;

import com.project.coursework2.data.AccessControlDAO;

public class TestAccessControl {
    public static void main(String[] args) {
        AccessControlDAO dao = new AccessControlDAO();

        boolean result = dao.canUserBookResource("USR010", "RES009", "BTEST001");

        System.out.println(result ? "Access granted" : "Access denied");
    }
}
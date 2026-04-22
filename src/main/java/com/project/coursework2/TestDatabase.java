package com.project.coursework2;

import com.project.coursework2.data.ResourceDAO;
import com.project.coursework2.model.Resource;
import java.util.List;

public class TestDatabase {

    public static void main(String[] args) {

        ResourceDAO dao = new ResourceDAO();
        List<Resource> list = dao.getAllResources();

        for (Resource r : list) {
            System.out.println(r.getName());
        }
    }
}
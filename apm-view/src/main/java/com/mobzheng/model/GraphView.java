package com.mobzheng.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GraphView {
    private String title;
    private Nodes showDefaultNode;
    private List<Nodes> nodes = new ArrayList<>();
    private List<Edges> edges = new ArrayList<>();


    @lombok.Data
    public static class Nodes {
        private String id;
        private String title;
        private String subTitle;
        private String icon;
        private String state;
        private String tips;
        private String type;
        private Object data;

    }

    @lombok.Data
    public static class Edges {

        private String from;
        private String to;
        private String label;
        private String title;
        private String description;
        private String type;
        private int count;

    }

    @lombok.Data
    public static class Data {
        String id;
        String name;
        String value;
    }

}
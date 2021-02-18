package com.diastock.app;

public class WarehouseLocation
{
    private String building = new String();
    private String department = new String();
    private String shelf = new String();
    private String position = new String();
    private String floor = new String();
    private int positionCode= 0;
    private String entirePosition = new String();
    private int maxsku= 0;
    public static final short KEYBOARD_INPUT_LENGHT = 23;
    public static final short DB_INPUT_LENGHT = 19;
    public static final byte WAREHOUSE_LEN = 5;
    public static final byte DEPARTMENT_LEN = 5;
    public static final byte SHELF_LEN = 3;
    public static final byte POSITION_LEN = 3;
    public static final byte FLOOR_LEN = 3;
    public static final String SEPARATOR = "-";

    public WarehouseLocation() throws Exception {
        building = "";
        department = "";
        shelf = "";
        position = "";
        floor = "";
        positionCode = 0;
        entirePosition = "";
        maxsku = 0;
    }

    public String getBuilding() throws Exception {
        return building;
    }

    public void setBuilding(String value) throws Exception {
        building = value;
    }

    public String getDepartment() throws Exception {
        return department;
    }

    public void setDepartment(String value) throws Exception {
        department = value;
    }

    public String getShelf() throws Exception {
        return shelf;
    }

    public void setShelf(String value) throws Exception {
        shelf = value;
    }

    public String getPosition() throws Exception {
        return position;
    }

    public void setPosition(String value) throws Exception {
        position = value;
    }

    public String getFloor() throws Exception {
        return floor;
    }

    public void setFloor(String value) throws Exception {
        floor = value;
    }

    public int getPositionCode() throws Exception {
        return positionCode;
    }

    public void setPositionCode(int value) throws Exception {
        positionCode = value;
    }

    public int getMaxsku() throws Exception {
        return maxsku;
    }

    public void setMaxsku(int value) throws Exception {
        maxsku = value;
    }

    public String getEntirePosition() throws Exception {
        return entirePosition;
    }

    public void setEntirePosition(String value) throws Exception {
        if (value.length() == KEYBOARD_INPUT_LENGHT)
        {
            // Input from keyboard/Laser
            entirePosition = value.replace(SEPARATOR, "");
            building = value.substring(0, WAREHOUSE_LEN);
            department = value.substring(WAREHOUSE_LEN + 1, WAREHOUSE_LEN + 1 + DEPARTMENT_LEN);
            shelf = value.substring(WAREHOUSE_LEN + 1 + DEPARTMENT_LEN + 1, WAREHOUSE_LEN + 1 + DEPARTMENT_LEN + 1+ SHELF_LEN);
            position = value.substring(WAREHOUSE_LEN + 1 + DEPARTMENT_LEN + 1 + SHELF_LEN + 1, WAREHOUSE_LEN + 1 + DEPARTMENT_LEN + 1 + SHELF_LEN + 1 + POSITION_LEN);
            floor = value.substring(WAREHOUSE_LEN + 1 + DEPARTMENT_LEN + 1 + SHELF_LEN + 1 + POSITION_LEN + 1, WAREHOUSE_LEN + 1 + DEPARTMENT_LEN + 1 + SHELF_LEN + 1 + POSITION_LEN + 1 + FLOOR_LEN);
            positionCode = 0;
        }
        else if (value.length() == DB_INPUT_LENGHT)
        {
            // From database
            entirePosition = value;
            building = value.substring(0, WAREHOUSE_LEN);
            department = value.substring(WAREHOUSE_LEN, WAREHOUSE_LEN + DEPARTMENT_LEN);
            shelf = value.substring(WAREHOUSE_LEN + DEPARTMENT_LEN, WAREHOUSE_LEN + DEPARTMENT_LEN + SHELF_LEN);
            position = value.substring(WAREHOUSE_LEN + DEPARTMENT_LEN + SHELF_LEN, WAREHOUSE_LEN + DEPARTMENT_LEN + SHELF_LEN + POSITION_LEN);
            floor = value.substring(WAREHOUSE_LEN + DEPARTMENT_LEN + SHELF_LEN + POSITION_LEN, WAREHOUSE_LEN + DEPARTMENT_LEN + SHELF_LEN + POSITION_LEN + FLOOR_LEN);
            positionCode = 0;
        }

    }

    public String getInputPosition() throws Exception {
        return building + "-" + department + "-" + shelf + "-" + position + "-" + floor;
    }

    public void setInputPosition(String value) throws Exception {
    }

}



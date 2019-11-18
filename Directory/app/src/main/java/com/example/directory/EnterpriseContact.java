package com.example.directory;

public class EnterpriseContact {
    private int id;
    private String name;
    private String contactPhone;
    private String contactEmail;

    public EnterpriseContact(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.contactEmail = email;
        this.contactPhone = phone;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public String getContactEmail() {
        return contactEmail;
    }

}

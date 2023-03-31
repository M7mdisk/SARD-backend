package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document
public class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String department;

    public enum Type {
    ADMIN,
    DEV,
    TESTER}


    private Type type;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public Enum<Type> getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}

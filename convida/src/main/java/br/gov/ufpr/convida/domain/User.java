package br.gov.ufpr.convida.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

public class User implements Serializable{

    private static final long serialVersionUID = 1L;
    
    @Id
    private String grr;
    private String name;
    private String lastName;
    private String password;
    private String email;
    private Date birth;

    @DBRef(lazy = true)
    private List<Event> fav = new ArrayList<>();
    


    public User() {
    }    


    public String getGrr() {
        return this.grr;
    }

    public void setGrr(String grr) {
        this.grr = grr;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Date getBirth() {
        return this.birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }


    public List<Event> getFav() {
        return this.fav;
    }

    public void setFav(List<Event> fav) {
        this.fav = fav;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(grr, user.grr);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(grr);
    }







}
package com.myorg.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User implements Serializable {
  
	private static final long serialVersionUID = 1L;
	 @Id
	    @GeneratedValue(strategy = GenerationType.AUTO)
	    @Column(name = "user_id")
	    private int id;

	    @Column(name = "email")
	    private String email;

	    @Column(name = "name")
	    private String name;
	    @Column(name = "password")
	    private String password;
	    @Column(name = "last_name")
	    private String lastName;
	    @Column(name = "active")
	    private int active;

	    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	    @JoinTable(name = "user_role", joinColumns =
	    @JoinColumn(name = "user_id"), inverseJoinColumns =
	    @JoinColumn(name = "role_id"))
	    private Set<Role> roles;


	    public User() {
	    }

	    public User(User users) {

	        this.active = users.active;
	        this.email = users.email;
	        this.id = users.id;
	        this.lastName = users.lastName;
	        this.name = users.name;
	        this.password = users.password;
	        this.roles = users.roles;

	    }




}
package com.example.braingain.ModelClass

class AuthenticationUserModel {
    var name = ""
    var age = 0
    var email = ""
    var password = ""
//    var image = 0

    constructor()
    constructor(name: String, age: Int, email: String, password: String) {
        this.name = name
        this.age = age
        this.email = email
        this.password = password
//        this.image = image
    }

}
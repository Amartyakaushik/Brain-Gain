package com.example.braingain.ModelClass

//class historyModelClass(var timeAndDate : String, var coin : String) {
//}
// for storing the data in the database as history
class historyModelClass{
    var timeAndDate : String = ""
    var coin : String = ""
    var isWithdrawal : Boolean = false
    constructor()
    constructor(timeAndDate: String, coin: String, isWithdrawal: Boolean) {
        this.timeAndDate = timeAndDate
        this.coin = coin
        this.isWithdrawal = isWithdrawal
    }


}
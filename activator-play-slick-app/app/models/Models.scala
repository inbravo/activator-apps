package models

case class Employee(name: String, email: String, companyName: String, position: String, id: Option[Int] = None)
case class Car(name: String, modal: String, companyName: String, id: Option[Int] = None)


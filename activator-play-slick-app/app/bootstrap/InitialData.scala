package bootstrap

import com.google.inject.Inject
import javax.inject.Singleton
import repo.{ EmployeeRepository, CarRepository }
import models.{ Employee, Car }
import java.util.Date
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.Logger
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class InitialData @Inject() (employeeRepo: EmployeeRepository, carRepo: CarRepository) {

  def insert = for {

    /* Insert employees */
    emps <- employeeRepo.getAll() if (emps.length == 0)
    _ <- employeeRepo.insertAll(Data.employees)

    /* Insert cars */
    cars <- carRepo.getAll() if (cars.length == 0)
    _ <- carRepo.insertAll(Data.cars)

  } yield {}

  try {
    Logger.info("DB initialization.................")
    Await.result(insert, Duration.Inf)
  } catch {
    case ex: Exception =>
      Logger.error("Error in database initialization ", ex)
  }
}

object Data {

  /* List of employees */
  val employees = List(
    Employee("Satendra", "satendra@knoldus.com", "Knoldus", "Senior Consultant"),
    Employee("Mayank", "mayank@knoldus.com", "knoldus", "Senior Consultant"),
    Employee("Sushil", "sushil@knoldus.com", "knoldus", "Consultant"),
    Employee("Narayan", "narayan@knoldus.com", "knoldus", "Consultant"),
    Employee("Himanshu", "himanshu@knoldus.com", "knoldus", "Senior Consultant"))

  /* List of cars */
  val cars = List(
    Car("Maruti", "Zen", "Maruti Suzuki"),
    Car("Ford", "Figo", "Ford India"))
}

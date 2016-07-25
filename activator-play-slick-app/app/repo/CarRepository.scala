package repo

import javax.inject.{ Inject, Singleton }
import models.Car
import play.api.Logger
import play.api.db.slick.{ DatabaseConfigProvider, HasDatabaseConfigProvider }
import slick.driver.JdbcProfile
import scala.concurrent.Future

/**
 *
 */
class CarRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends CarTable with HasDatabaseConfigProvider[JdbcProfile] {

  val logger = Logger(this.getClass())

  import driver.api._

  def insert(car: Car): Future[Int] = db.run {

    logger.debug("Inserting car: " + car)
    carTableQueryInc += car
  }

  def insertAll(cars: List[Car]): Future[Seq[Int]] = db.run {
    carTableQueryInc ++= cars
  }

  def update(car: Car): Future[Int] = db.run {

    logger.debug("Updating car: " + car)
    carTableQuery.filter(_.id === car.id).update(car)
  }

  def delete(id: Int): Future[Int] = db.run {

    logger.debug("Deleting car with id: " + id)
    carTableQuery.filter(_.id === id).delete
  }

  def getAll(): Future[List[Car]] = db.run {
    carTableQuery.to[List].result
  }

  def getById(carId: Int): Future[Option[Car]] = db.run {
    
    logger.debug("Getting car with id: " + carId)
    carTableQuery.filter(_.id === carId).result.headOption
  }

  def ddl = carTableQuery.schema
}

private[repo] trait CarTable {

  self: HasDatabaseConfigProvider[JdbcProfile] =>

  import driver.api._

  lazy protected val carTableQuery = TableQuery[CarTable]
  lazy protected val carTableQueryInc = carTableQuery returning carTableQuery.map(_.id)

  /* Map with data base car */
  private[CarTable] class CarTable(tag: Tag) extends Table[Car](tag, "car") {

    val id: Rep[Int] = column[Int]("id", O.AutoInc, O.PrimaryKey)
    val name: Rep[String] = column[String]("name", O.SqlType("VARCHAR(200)"))
    val modal: Rep[String] = column[String]("modal", O.SqlType("VARCHAR(200)"))
    val companyName: Rep[String] = column[String]("company_name")

    def * = (name, modal, companyName, id.?) <> (Car.tupled, Car.unapply)
  }
}
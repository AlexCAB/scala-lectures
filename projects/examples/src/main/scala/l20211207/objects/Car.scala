package l20211207.objects


case class Car(
                manufacturer: String = "Honda",
                capacity: Int = 4,
                isRun:Boolean = false
              ){
  def run(): Car = copy(isRun = true)

  def printState(): Unit = println(s"Car$manufacturer with $capacity passengers is${if(isRun) "run" else "stop" }")
}


object MyApp extends App{

  println("Hi")

  val car = new Car

  car.printState()

  val ranCar = car.run()

  ranCar.printState()
}




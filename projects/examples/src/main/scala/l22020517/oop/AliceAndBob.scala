package l22020517.oop

import scala.concurrent.ExecutionContext

sealed trait Msg


object Msg {
  case class Hi(sender: Actor) extends Msg

  case class Hey(sender: Actor) extends Msg

  case object Go extends Msg
}


abstract class Actor(name: String) {
  val receive: PartialFunction[Msg, Any]

  override def toString: String = name
}


class Alice(bob: Actor) extends Actor("Alice") { self =>

  val receive: PartialFunction[Msg, Any] = {

    case Msg.Go =>
      println("[Alice] Got msg Go")
      bob.receive(Msg.Hi(self))

    case Msg.Hey(sender) =>
      println(s"[Alice] Got msg Hey from $sender")
  }
}

class Bob extends Actor("Bob") { self =>

  val receive: PartialFunction[Msg, Any] = {

    case Msg.Hi(sender) =>

      // Thread.sleep(10000)                             // <-- To uncomment

      println(s"[Bob] Got msg Hi from $sender")
      sender.receive(Msg.Hey(self))
  }
}


object AliceAndBod extends App {

  println("########## START ##########")

  val bob = new Bob
  val alice = new Alice(bob)

  alice.receive(Msg.Go)

  println("########### END ###########")
}

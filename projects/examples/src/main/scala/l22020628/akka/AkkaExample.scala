package l22020628.akka

import akka.actor.{ Actor, ActorRef, ActorSystem, PoisonPill, Props}


sealed trait Msg


object Msg {
  case object Hi extends Msg

  case object Go extends Msg
}


class Alice(bob: ActorRef) extends Actor {

  val receive: Receive = {

    case Msg.Go =>
      println("[Alice] Got msg Go")
      while (true) bob ! Msg.Hi

  }
}

class Bob extends Actor {

  val receive: Receive = {

    case Msg.Hi =>
      println(s"[Bob] Got msg Hi from ${sender()} and go to sleep")
      Thread.sleep(100000)                                                                          // <-- To uncomment
  }
}


object AkkaExample extends App {

  println("########## START ##########")


  val system = ActorSystem("akka-example")

  val bob = system.actorOf(Props[Bob](), "Bob")
  val alice = system.actorOf(Props(classOf[Alice], bob), "Alice")

  alice ! "Go"

  alice ! Msg.Go

  Thread.sleep(2000)

  system.terminate()

  println("########### END ###########")
}

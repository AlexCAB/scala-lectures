package l22020517.akka

import akka.actor.{ Actor, ActorRef, ActorSystem, PoisonPill, Props}


sealed trait Msg


object Msg {
  case object Hi extends Msg

  case object Hey extends Msg

  case object Go extends Msg

  case object Stop extends Msg

  case object DoSomethingElse extends Msg
}


class Alice(bob: ActorRef) extends Actor {

  val receive: Receive = {

    case Msg.Go =>
      println("[Alice] Got msg Go")
      bob ! Msg.Hi
      // println("[Alice] Sent Hi to Bob")                                                           // <-- To uncomment

    case Msg.Hey =>
      println(s"[Alice] Got msg Hey from ${sender()}")

    case Msg.DoSomethingElse =>
      println("[Alice] Will do something else")

    case Msg.Stop =>
      bob ! PoisonPill
      self ! PoisonPill
  }
}

class Bob extends Actor {

  val receive: Receive = {

    case Msg.Hi =>

      //Thread.sleep(10000)                                                                          // <-- To uncomment

      println(s"[Bob] Got msg Hi from ${sender()}")
      sender() ! Msg.Hey
  }
}


object AkkaExample extends App {

  println("########## START ##########")


  val system = ActorSystem("akka-example")

  val bob = system.actorOf(Props[Bob](), "Bob")
  val alice = system.actorOf(Props(classOf[Alice], bob), "Alice")

  alice ! Msg.Go

  //Thread.sleep(5000)                                                                               // <-- To uncomment

  //alice ! Msg.DoSomethingElse                                                                      // <-- To uncomment

  Thread.sleep(2000)

  alice ! Msg.Stop

  alice ! Msg.Go

  Thread.sleep(2000)

  system.terminate()

  println("########### END ###########")
}

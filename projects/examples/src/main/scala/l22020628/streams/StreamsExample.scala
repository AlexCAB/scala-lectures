package l22020628.streams

import akka.Done
import akka.actor.ActorSystem
import akka.stream.{Inlet, Outlet, Shape, SourceShape}
import akka.stream.scaladsl.{BidiFlow, Flow, GraphDSL, Sink, Source}

import scala.concurrent.{ExecutionContext, Future}


sealed trait Msg


object Msg {
  case object Go extends Msg

  case object Hi extends Msg

  case object Hey extends Msg

  case object Done extends Msg
}


case class AliceShape(goIn: Inlet[Msg], hiOut: Outlet[Msg], heyIn: Inlet[Msg], doneOut: Outlet[Msg]) extends Shape {
  override val inlets: Seq[Inlet[_]] = List(goIn, heyIn)
  override val outlets: Seq[Outlet[_]] = List(hiOut, doneOut)
  override def deepCopy(): AliceShape = AliceShape(
    goIn.carbonCopy(), hiOut.carbonCopy(), heyIn.carbonCopy(), doneOut.carbonCopy())
}


case class BobShape(hiIn: Inlet[Msg], heyOut: Outlet[Msg]) extends Shape {
  override val inlets: Seq[Inlet[_]] = List(hiIn)
  override val outlets: Seq[Outlet[_]] = List(heyOut)
  override def deepCopy(): BobShape = BobShape(hiIn.carbonCopy(), heyOut.carbonCopy())
}


object StreamsExample extends App {

  // Init

  implicit val system: ActorSystem = ActorSystem("streams-example")
  implicit val ec: ExecutionContext = system.dispatcher

  // Components

  val goSource = Source.single(Msg.Go)
//  val goSource = Source.repeat(Msg.Go)

  val aliceGraph = GraphDSL.create(){ implicit builder ⇒ import GraphDSL.Implicits.*

    val goFlow = builder.add(Flow[Msg].collect[Msg] {
      case Msg.Go =>
        println("Alice received Msg.Go")
        Msg.Hi
    })

    val heyFlow = builder.add(Flow[Msg].collect[Msg] {
      case Msg.Hey =>
        println("Alice received Msg.Hey")
        Msg.Done
    })

    AliceShape(goFlow.in, goFlow.out, heyFlow.in, heyFlow.out)
  }

  val bobGraph = GraphDSL.create(){ implicit builder ⇒ import GraphDSL.Implicits.*

    val hiFlow = builder.add(Flow[Msg].collect[Msg] {
      case Msg.Hi =>
        println("Bob received Msg.Hi")
        Msg.Hey
    })

    BobShape(hiFlow.in, hiFlow.out)
  }

  // Program

  println("########## START ##########")

  val graph = Source.fromGraph(GraphDSL.create(){ implicit builder ⇒ import GraphDSL.Implicits.*

    val source = builder.add(goSource)
    val alice = builder.add(aliceGraph)
    val bob = builder.add(bobGraph)

    source.out ~> alice.goIn; alice.hiOut ~> bob.hiIn; bob.heyOut ~> alice.heyIn

    SourceShape(alice.doneOut)
  })

  val run = graph.runWith(Sink.last)

  // Exit

  run.onComplete { _ =>
    println("########### END ###########")
    system.terminate()
  }
}

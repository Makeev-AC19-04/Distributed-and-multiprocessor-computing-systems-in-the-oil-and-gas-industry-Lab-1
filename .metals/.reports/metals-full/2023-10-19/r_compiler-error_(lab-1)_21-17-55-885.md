file://<HOME>/%D0%94%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D1%8B/scala_projects/lab%201/lab-1/src/main/scala/Client.scala
### java.lang.NullPointerException

occurred in the presentation compiler.

action parameters:
offset: 1440
uri: file://<HOME>/%D0%94%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D1%8B/scala_projects/lab%201/lab-1/src/main/scala/Client.scala
text:
```scala
package ru.asu.DS.lab1

import java.net._
import java.io.PrintWriter
import scala.io.StdIn.readLine
import java.util.Calendar
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import scala.collection.mutable.ArrayBuffer

class Client {
  private val bufferSize = 1024
  private val serverAddress = InetAddress.getByName("localhost")
  private val serverPort = 1502
  private var socket: Socket = _
  private var running = false
  //private var queue: ArrayBuffer[DatagramPacket] =  new ArrayBuffer[DatagramPacket]


  def sendMessage(): Unit = {

    println("Enter client's name: ")
    val name = readLine()   

    println("Sending messages every 4 seconds...")
    println("Type anything to close client.")
    running = true
    
    try {
      while (running){
        Thread.sleep(4000)

        val now = Calendar.getInstance().getTime()
        val message = s"Message from $name: $now"   

        socket = new Socket(serverAddress, serverPort)
        var out = new PrintWriter(socket.getOutputStream, true)
        out.println(message)

        out.close()
        socket.close()
      }
  } catch {
      case se: java.net.SocketException =>
        println("Server is down")
        running = false
    }
  }

  def waitForStop(): Unit = {
    Future {
      readLine()
      println("Shutting down client")
      running = false
      socket.close()
    }
  }

  def recieveMessages()@@

}

object Client extends App {
  val client = new Client()
  //client.sendMessage()

  val f1 = new Thread{
    override def run = {
      client.sendMessage()
    }
  }

  val f2 = new Thread{
    override def run = {
      client.waitForStop()
    }
  }
  f1.start()
  f2.start()

}

```



#### Error stacktrace:

```
scala.reflect.internal.Definitions$DefinitionsClass.isByNameParamType(Definitions.scala:420)
	scala.reflect.internal.TreeInfo.isStableIdent(TreeInfo.scala:140)
	scala.reflect.internal.TreeInfo.isStableIdentifier(TreeInfo.scala:113)
	scala.reflect.internal.TreeInfo.isPath(TreeInfo.scala:102)
	scala.tools.nsc.interactive.Global.stabilizedType(Global.scala:974)
	scala.tools.nsc.interactive.Global.typedTreeAt(Global.scala:822)
	scala.meta.internal.pc.SignatureHelpProvider.signatureHelp(SignatureHelpProvider.scala:23)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$signatureHelp$1(ScalaPresentationCompiler.scala:282)
```
#### Short summary: 

java.lang.NullPointerException
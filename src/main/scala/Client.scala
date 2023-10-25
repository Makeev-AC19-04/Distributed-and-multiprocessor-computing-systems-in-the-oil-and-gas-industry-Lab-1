package ru.asu.DS.lab1

import java.net._
import java.io.PrintWriter
import scala.io.StdIn.readLine
import java.util.Calendar
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.nio.charset.StandardCharsets

import scala.collection.mutable.ArrayBuffer

class Client {
  private val bufferSize = 1024
  private val serverAddress = InetAddress.getByName("localhost")
  private val port = 1502
  private val group = InetAddress.getByName("233.0.0.1")
  private val udpSocket = new MulticastSocket(port)
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

        socket = new Socket(serverAddress, port)
        var out = new PrintWriter(socket.getOutputStream, true)
        out.println(message)

        out.close()
        socket.close()
      }
  } catch {
      case se: java.net.SocketException =>
        println("Server is down")
        running = false
        System.exit(0)
    }
  }

  def waitForStop(): Unit = {
    Future {
      readLine()
      println("Shutting down client")
      running = false
      socket.close()
      System.exit(0)
    }
  }

  def recieveMessages(): Unit = {
    udpSocket.joinGroup(group)
    while(true) {
      val buf: Array[Byte] = new Array[Byte](256)
      var packet = new DatagramPacket(buf, buf.length)
      udpSocket.receive(packet)
      print (new String(packet.getData,0, packet.getLength, StandardCharsets.UTF_8).trim)
      println("")
  }
    udpSocket.leaveGroup(group)
    udpSocket.close()
  }

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

  val f3 = new Thread{
    override def run = {
      client.recieveMessages()
    }
  }

  f1.start()
  f2.start()
  f3.start()

}

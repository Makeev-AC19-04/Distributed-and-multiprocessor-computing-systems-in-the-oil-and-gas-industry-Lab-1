package ru.asu.DS.lab1

import scala.collection.mutable.ArrayBuffer
import scala.io.StdIn.readLine
import java.io.{BufferedReader, InputStreamReader, ObjectInputStream}
import scala.util.{Success, Failure}


import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import java.net._
import java.net.SocketException   
import scala.concurrent.Await

class Server {
  private val bufferSize = 1024
  private val port = 1502
  private val group = InetAddress.getByName("233.0.0.1")

  private var tcpSocket: ServerSocket = new ServerSocket(port)
  private var udpSocket: MulticastSocket = new MulticastSocket(port)
  private var clientList: ArrayBuffer[ClientHandler] = new ArrayBuffer[ClientHandler]
  private var queue: ArrayBuffer[String] = new ArrayBuffer[String]

  def startServer(): Unit = {
    udpSocket.joinGroup(group)
    println(s"Server started. Listening on port $port.")
    println("Type anything to close client.")
  }

  def recieveMessages(): Unit = {
    println("Recieving messages")
    while (true) { //true
      val clientSocket = tcpSocket.accept()

      // Создание нового потока для каждого клиента
      var client = new ClientHandler(clientSocket)
      clientList += client

      val clientThread = new Thread(client)
      clientThread.start()
    }
  }

  def waitForStop(): Unit = {

    Future {
      readLine()
      println("Shutting down server")

      for (i <- clientList) {
        i.close()
      }
      clientList.clear()
      //print("Or There's gonna be exception")
      //tcpSocket.close()
      System.exit(0)
    }
  }

  def sendMessages(): Unit = {
    var sendSocket = new DatagramSocket()
    while (true){
      Thread.sleep(10000)
      for (i <- queue){   
        val buf: Array[Byte] = i.getBytes
        val packet = new DatagramPacket(buf, buf.length, group, port)
        sendSocket.send(packet)     
        println(i)
      }
      queue.clear()
    }
  }


class ClientHandler(clientSocket: Socket) extends Runnable {
  override def run(): Unit = {
    val reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream))    

    var line: String = null
    do {
      line = reader.readLine()
      if (line != null) {
        queue += line
        //println(line)
      }
    } while (line != null)

    reader.close()
    clientSocket.close()
  }

  
  def close(): Unit = {
    clientSocket.close()
  }

}

}


object Server extends App {

  val server = new Server()
  server.startServer()


  val f1 = new Thread{
    override def run {
      server.waitForStop()
    }
  }

  val f2 = new Thread{
    override def run {
      server.recieveMessages()
    }
  }

  val f3 = new Thread{
    override def run {
      server.sendMessages()
    }
  }

  f1.start()
  f2.start()
  f3.start()
}

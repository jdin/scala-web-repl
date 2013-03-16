package models;

import play.api.libs.iteratee.Concurrent._
import java.io._

class ChannelWriter(channel:Channel[String]) extends Writer() {
  
  def close() = {}
  
  def flush() = {}

  def write(chars:Array[Char], off:Int, len:Int) =
      channel.push(new String(chars,off,len))
}

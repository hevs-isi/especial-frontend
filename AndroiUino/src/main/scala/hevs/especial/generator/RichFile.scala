/**
 * Source from "helgoboss-commons".
 * https://code.google.com/p/helgoboss-commons
 */
package hevs.androiduino.dsl.generator

import java.io._

import scala.language.implicitConversions


object RichFile {
  implicit def file2RichFile(file: File) = new RichFile(file)
}

/**
 * Some helper methods to work with files.
 *
 * @param file the file to work with
 */
class RichFile(file: File) {

  def write(text: String) = {
    val fw = new FileWriter(file)
    try {
      fw.write(text)
    } finally {
      fw.close()
    }
  }

  def makeSureDirExistsAndIsEmpty() = {
    if (file.exists) {
      deleteRecursively()
    }
    file.mkdirs
  }

  private def deleteRecursively() = {
    def deleteFile(subFile: File): Unit = {
      if (subFile.isDirectory) {
        val del = deleteFile _
        subFile.listFiles.foreach(del)
      }
      subFile.delete
    }
    deleteFile(file)
  }
}
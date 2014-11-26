package hevs.especial.utils

import java.io._

import scala.language.implicitConversions


object RichFile {
  implicit def file2RichFile(file: File) = new RichFile(file)
}

/**
 * Some helper methods to work with files and folders.
 *
 * Code adapted from:
 * @see https://code.google.com/p/helgoboss-commons
 *
 * @param file the file to work with
 */
class RichFile(file: File) {

  /**
   * Write test to a file.
   * @param text the text to write to the file
   * @return true if write succeed, false otherwise
   */
  def write(text: String): Boolean = {
    try {
      val fw = new FileWriter(file)
      fw.write(text)
      fw.close()
      true
    }
    catch {
      case e: IOException => false
    }
  }

  /**
   * Check if the folder exist and if not create it. If it already exist, delete all files.
   * @return `false` if the folder cannot be created, `true `otherwise
   */
  def createEmptyFolder(): Boolean = {
    if (createFolder()) {
      return false
    }
    else {
      // Delete all files in this existing directory
      deleteFilesRecursively()
    }
    true
  }

  /**
   * Create a folder if it does not exit. Do nothing if already exist.
   * @return `false` if the folder cannot be created, `true `otherwise
   */
  def createFolder(): Boolean = {
    if (!file.exists()) {
      if (!file.mkdirs())
        return false
    }
    true
  }

  /**
   * Delete all files in a directory, but not the directory itself.
   */
  private def deleteFilesRecursively(): Unit = {
    def deleteFile(subFile: File): Unit = {
      if (subFile.isDirectory) {
        val del = deleteFile _
        subFile.listFiles.foreach(del)
      }
      subFile.delete
    }

    val del = deleteFile _
    file.listFiles().foreach(del)
  }
}
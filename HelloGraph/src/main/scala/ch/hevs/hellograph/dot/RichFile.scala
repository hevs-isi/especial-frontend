package ch.hevs.hellograph.dot

import java.io._

import scala.language.implicitConversions

// Source from "helgoboss-commons"
// https://code.google.com/p/helgoboss-commons/source/browse/trunk/scala-rich-file/src/main/scala/org/helgoboss/scala_rich_file/ScalaRichFile.scala?r=26

object RichFile {
    implicit def file2RichFile(file: File) = new RichFile(file)
}


class RichFile(file: File) {

    def write(text: String) {
        val fw = new FileWriter(file)
        try { 
            fw.write(text) 
        } finally { 
            fw.close 
        }
    }
        
    def makeSureDirExistsAndIsEmpty {
        if (file.exists) {
            deleteRecursively
        }
        file.mkdirs
    }
    
    def deleteRecursively {
        def deleteFile(subFile: File): Unit = {
            if(subFile.isDirectory) {
                subFile.listFiles.foreach(deleteFile(_))
            }
            subFile.delete
        }
        deleteFile(file)
    }
}
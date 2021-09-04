package com.sergey.pisarev.model

import com.sergey.pisarev.model.CharacterDetector.Companion.instance
import java.nio.file.Files
import java.nio.charset.Charset
import java.util.stream.Collectors
import kotlin.Throws
import javafx.scene.input.DragEvent
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.function.Consumer

object MyFile {
    var filePath: File? = null
    private var encoding = StandardCharsets.UTF_8.toString()
    fun getFileTextContent(event: DragEvent): String {
        val file = event.dragboard.files
        filePath = file[0]
        val sb = java.lang.StringBuilder()
        checkEncoding(file[0])
        try {
            Files.lines(file[0].toPath(), Charset.forName(encoding))
                    .peek { line: String? -> sb.append(line).append('\n') }
                    .collect(Collectors.toList())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return sb.toString()
    }

    private fun getFileListContent(file: File): List<StringBuilder?> {
        var listFrame: List<java.lang.StringBuilder?> = ArrayList()
        checkEncoding(file)
        try {
            listFrame = Files.lines(file.toPath(), Charset.forName(encoding))
                    .map { str: String? -> StringBuilder(str) }
                    .collect(Collectors.toList())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return listFrame
    }

    fun getFileTextContent(file: File): String {
        var listFrame: List<java.lang.StringBuilder?> = ArrayList()
        checkEncoding(file)
        try {
            listFrame = Files.lines(file.toPath(), Charset.forName(encoding))
                    .map { str: String? -> StringBuilder(str) }
                    .collect(Collectors.toList())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val text : StringBuilder = java.lang.StringBuilder()
        listFrame.forEach(Consumer { p: StringBuilder? -> text.append(p).append('\n') })
        return text.toString()
    }

    fun setFileContent(file: File, text: String) {
        try {
            writer(file, text, Charset.forName(encoding))
        } catch (e: IOException) {
            try {
                writer(file, text, StandardCharsets.UTF_8)
            } catch (ioException: IOException) {
                ioException.printStackTrace()
            }
        }
    }

    @Throws(IOException::class)
    private fun writer(file: File, text: String, cs: Charset) {
        val writer: Writer = Files.newBufferedWriter(file.toPath(), cs)
        writer.write(text)
        writer.close()
    }

    fun getParameter(path: File): List<StringBuilder?> {
        val folder = File(path.parent)
        val listOfFiles = folder.listFiles()!!
        for (listOfFile in listOfFiles) {
            if (listOfFile.isFile) {
                if (listOfFile.name.contains("PAR")) {
                    return getFileListContent(listOfFile)
                }
            }
        }
        return ArrayList()
    }

    private fun guessEncoding(`is`: InputStream): String {
        val characterDetector = instance
        var encoding = ""
        try {
            encoding = characterDetector!!.detect(`is`)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return encoding
    }

    private fun checkEncoding(file: File?) {
        if (file != null) {
            try {
                val text = FileInputStream(file)
                encoding = guessEncoding(text)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }
}
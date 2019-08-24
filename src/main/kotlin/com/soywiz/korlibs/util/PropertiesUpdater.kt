package com.soywiz.korlibs.util

import java.io.*

object PropertiesUpdater {
	fun update(file: File, changes: Map<String, String>) {
		val lines = file.readLines().toMutableList()
		for (nline in lines.indices) {
			lines[nline] = lines[nline].let { line ->
				val key = (line.split('=', limit = 2).firstOrNull() ?: "")
				val change = changes[key]
				if (change != null) {
					"$key=$change"
				} else {
					line
				}
			}
		}
		//println("LINES: $lines")
		file.writeText(lines.joinToString("\n"))
	}
}
package com.soywiz.korlibs.util

import java.io.*
import java.lang.StringBuilder

fun shellExec(
	vararg cmds: String,
	workingDir: File = File("."),
	envs: Map<String, String> = mapOf(),
	passthru: Boolean = true,
	captureOutput: Boolean = true,
	onOut: (data: ByteArray) -> Unit = {},
	onErr: (data: ByteArray) -> Unit = {}
): ShellExecResult {
	val p = ProcessBuilder(*cmds)
		.directory(workingDir)
		.also { it.environment().also { it.putAll(envs) } }
		.start()
	var closing = false
	var output = StringBuilder()
	var error = StringBuilder()
	while (true) {
		val o = p.inputStream.readAvailableChunk(readRest = closing)
		val e = p.errorStream.readAvailableChunk(readRest = closing)
		if (passthru) {
			System.out.print(o.toString(Charsets.UTF_8))
			System.err.print(e.toString(Charsets.UTF_8))
		}
		if (captureOutput) {
			output.append(o.toString(Charsets.UTF_8))
			error.append(e.toString(Charsets.UTF_8))
		}
		if (o.isNotEmpty()) onOut(o)
		if (e.isNotEmpty()) onErr(e)
		if (closing) break
		if (o.isEmpty() && e.isEmpty() && !p.isAliveJre7) {
			closing = true
			continue
		}
		Thread.sleep(1L)
	}
	p.waitFor()
	//handler.onCompleted(p.exitValue())
	val exitCode = p.exitValue()
	return ShellExecResult(exitCode, output.toString(), error.toString())
}

data class ShellExecResult(
	val exitCode: Int,
	val output: String,
	val error: String
) {
	val outputAndError: String get() = "$output$error"
	val outputIfNotError get() = if (exitCode == 0) output else error("Error executing command: $outputAndError")
}

private fun InputStream.readAvailableChunk(readRest: Boolean): ByteArray {
	val out = ByteArrayOutputStream()
	while (if (readRest) true else available() > 0) {
		val c = this.read()
		if (c < 0) break
		out.write(c)
	}
	return out.toByteArray()
}

private val Process.isAliveJre7: Boolean
	get() = try {
		exitValue()
		false
	} catch (e: IllegalThreadStateException) {
		true
	}

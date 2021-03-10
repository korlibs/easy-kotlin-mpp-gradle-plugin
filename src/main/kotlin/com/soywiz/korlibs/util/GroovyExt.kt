package com.soywiz.korlibs.util

import groovy.lang.*

fun <T> baseClosureOf(action: T.() -> Any?): Closure<Any?> =
	object : Closure<Any?>(Unit, Unit) {
		@Suppress("unused") // to be called dynamically by Groovy
		fun doCall(it: T): Any? = it.action()
	}

fun <T, R> closureOf(action: T.() -> R): Closure<R> =
	object : Closure<R>(Unit, Unit) {
		@Suppress("unused") // to be called dynamically by Groovy
		fun doCall(it: T): R = it.action()
	}

fun <T> simpleClosureOf(action: () -> T): Closure<T> =
	object : Closure<T>(Unit, Unit) {
		@Suppress("unused") // to be called dynamically by Groovy
		fun doCall(): T = action()
	}

package com.soywiz.korlibs

import com.soywiz.korlibs.util.*
import org.junit.*
import kotlin.test.*

class SemVerTest {
	@Test
	fun test() {
		assertEquals("1.2.3", SemVer("1.2.3").version)
		assertEquals("1.2.3-SNAPSHOT", SemVer("1.2.3-SNAPSHOT").version)
		assertEquals("1.2-SNAPSHOT", SemVer("1.2-SNAPSHOT").version)
		assertEquals("1-SNAPSHOT", SemVer("1-SNAPSHOT").version)
	}
}
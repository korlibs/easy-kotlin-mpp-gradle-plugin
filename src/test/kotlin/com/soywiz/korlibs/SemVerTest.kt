package com.soywiz.korlibs

import com.soywiz.korlibs.util.*
import kotlin.test.*
import kotlin.test.Test

class SemVerTest {
	@Test
	fun test() {
		assertEquals("1.2.3", SemVer("1.2.3").version)
		assertEquals("1.2.3-SNAPSHOT", SemVer("1.2.3-SNAPSHOT").version)
		assertEquals("1.2-SNAPSHOT", SemVer("1.2-SNAPSHOT").version)
		assertEquals("1-SNAPSHOT", SemVer("1-SNAPSHOT").version)
		assertEquals("1.2.3-SNAPSHOT", SemVer("1.2.3").withSnapshot().withSnapshot().version)
		assertEquals("1.2.3-SNAPSHOT", SemVer("1.2.3").withSnapshot().withSnapshot().withoutSnapshot().withSnapshot().version)
		assertEquals("1.2.3-SNAPSHOT", SemVer("1.2.3-SNAPSHOT").withSnapshot().withSnapshot().withoutSnapshot().withSnapshot().version)
		assertEquals("1.2.3-SNAPSHOT", SemVer("1.2.3-SNAPSHOT-SNAPSHOT").withSnapshot().withSnapshot().withoutSnapshot().withSnapshot().version)
		assertEquals("1.2.3", SemVer("1.2.3-SNAPSHOT-SNAPSHOT").withoutSnapshot().version)

		assertEquals("1.2.4", SemVer("1.2.3").withIncrementedVersion().version)
		assertEquals("1.3", SemVer("1.2").withIncrementedVersion().version)
		assertEquals("3", SemVer("2").withIncrementedVersion().version)

		assertEquals("1.2.4-SNAPSHOT", SemVer("1.2.3-SNAPSHOT").withIncrementedVersion().version)
		assertEquals("1.3-SNAPSHOT", SemVer("1.2-SNAPSHOT").withIncrementedVersion().version)
		assertEquals("3-SNAPSHOT", SemVer("2-SNAPSHOT").withIncrementedVersion().version)

		assertEquals("1.7.0-rc-1-SNAPSHOT", SemVer("1.7.0-rc-1-SNAPSHOT").version)
		assertEquals("1.7.1-rc-1-SNAPSHOT", SemVer("1.7.0-rc-1-SNAPSHOT").withIncrementedVersion().version)
	}
}
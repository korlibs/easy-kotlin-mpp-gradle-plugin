package com.soywiz.korlibs.modules

import org.junit.*

class CITest {
	val ciMasterGithub = CI("1.0", mapOf("GITHUB_ACTIONS" to "true", "GITHUB_REF" to "refs/heads/master"))
	val ciTagGithub = CI("1.0", mapOf("GITHUB_ACTIONS" to "true", "GITHUB_REF" to "refs/tags/0.1"))
	val ciMasterGithubSnapshot = CI("1.0-SNAPSHOT", mapOf("GITHUB_ACTIONS" to "true", "GITHUB_REF" to "refs/heads/master"))

	val ciMasterTravis = CI("1.0", mapOf("TRAVIS_PULL_REQUEST" to "false", "TRAVIS_BRANCH" to "master"))
	val ciTagTravis = CI("1.0", mapOf("TRAVIS_PULL_REQUEST" to "false", "TRAVIS_BRANCH" to ""))
	val ciMasterTravisSnapshot = CI("1.0-SNAPSHOT", mapOf("TRAVIS_PULL_REQUEST" to "false", "TRAVIS_BRANCH" to "master"))

	@Test
	fun testGithub() {
		ciMasterGithub.apply {
			Assert.assertEquals("master", CI_BRANCH)
			Assert.assertEquals(false, CI_PULL_REQUEST)
			Assert.assertEquals(false, isSnapshotVersion)
			Assert.assertEquals(true, ciMustPublish)
		}
		ciMasterGithubSnapshot.apply {
			Assert.assertEquals("master", CI_BRANCH)
			Assert.assertEquals(false, CI_PULL_REQUEST)
			Assert.assertEquals(true, isSnapshotVersion)
			Assert.assertEquals(false, ciMustPublish)
		}

		ciTagGithub.apply {
			Assert.assertEquals("", CI_BRANCH)
			Assert.assertEquals(false, CI_PULL_REQUEST)
			Assert.assertEquals(false, isSnapshotVersion)
			Assert.assertEquals(false, ciMustPublish)
		}
	}

	@Test
	fun testTravis() {
		ciMasterTravis.apply {
			Assert.assertEquals("master", CI_BRANCH)
			Assert.assertEquals(false, CI_PULL_REQUEST)
			Assert.assertEquals(false, isSnapshotVersion)
			Assert.assertEquals(true, ciMustPublish)
		}
		ciMasterTravisSnapshot.apply {
			Assert.assertEquals("master", CI_BRANCH)
			Assert.assertEquals(false, CI_PULL_REQUEST)
			Assert.assertEquals(true, isSnapshotVersion)
			Assert.assertEquals(false, ciMustPublish)
		}

		ciTagTravis.apply {
			Assert.assertEquals("", CI_BRANCH)
			Assert.assertEquals(false, CI_PULL_REQUEST)
			Assert.assertEquals(false, isSnapshotVersion)
			Assert.assertEquals(false, ciMustPublish)
		}
	}
}
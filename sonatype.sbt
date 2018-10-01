credentials += Credentials(Path.userHome / ".sbt" / "credentials")

sonatypeProfileName := "eventbox"

// To sync with Maven central, you need to supply the following information:
publishMavenStyle := true

// License of your choice
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

// Where is the source code hosted
import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("io.github.eventbox", "eventbox", "b.ponsero@gmail.com"))

// or if you want to set these fields manually
homepage := Some(url("https://eventbox.github.io/eventbox/"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/eventbox/eventbox.git"),
    "scm:git@github.com:eventbox/eventbox.git"
  )
)
developers := List(
  Developer(id="bpo", name="Benoit ponsero", email="b.ponsero@gmail.com", url=url("https://github.com/eventbox/eventbox.git"))
)

import sbt._
import Keys._
import com.typesafe.sbt.SbtPgp.PgpKeys.publishSigned
import xerial.sbt.Sonatype.sonatypeSettings

organization := "org.jquantlib"
name         := "jquantlib"


autoScalaLibrary := false
crossPaths := false


// compilation --------------------------------------------------------------------------------------------------

lazy val librarySettings : Seq[Setting[_]] =
  Seq(
    autoScalaLibrary :=  false,
    crossPaths       :=  false,
    javacOptions     ++= Seq(
      "-encoding", "UTF-8"
    )
  )


lazy val javadocSettings : Seq[Setting[_]] = Seq(
  javacOptions  in (Compile,compile) ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
  javacOptions  in (Compile,doc)     ++= Seq("-Xdoclint", "-notimestamp", "-linksource")
)


/*
TODO: Translate to SBT

    //TODO: doclet  'gr.spinellis:UmlGraph:4.9.0'
    //TODO: //-- doclet  'org.umlgraph:doclet:5.1'
    //TODO: //-- taglets 'net.sf.latextaglet:latextaglet:0.1.2'
    //TODO: taglets 'org.jquantlib:latextaglet:1.0-SNAPSHOT'

    task umlgraph(type: Javadoc) {
        description = "JQuantLib $version API Javadoc"
        classpath = files( new File("$projectDir/src/main/java") )
        source = sourceSets.main.allJava

        options.doclet     = "gr.spinellis.umlgraph.doclet.UmlGraphDoc"
        // options.doclet     = "org.umlgraph.doclet.UmlGraphDoc"

        options.docletpath = configurations.doclet as List
        options.taglets    = [ 'net.sf.latextaglet.LaTeXBlockEquationTaglet','net.sf.latextaglet.LaTeXEquationTaglet','net.sf.latextaglet.LaTeXInlineTaglet' ]
        options.tagletPath = configurations.taglets as List
        options.addStringOption('inferrel')
        options.addStringOption('inferdep')
        options.addStringOption('quiet')
        options.addStringOption('qualify')
        options.addStringOption('postfixpackage')
        options.addStringOption('hide', 'java.*')
        options.addStringOption('collpackages', 'java.util.*')
        options.addStringOption('nodefontsize', '9')
        options.addStringOption('nodefontpackagesize', '7')
    }


    task demo(type: JavaExec) {
        description = 'Run EquityOptions'
        classpath configurations.demo
        main = 'org.jquantlib.samples.EquityOptions'
    }

*/




// test frameworks ----------------------------------------------------------------------------------------------

lazy val junitSettings : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "junit"        % "junit"           % "4.12"    % "test",
      "com.novocode" % "junit-interface" % "0.11"    % "test" ),
    testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-v")
  )


// dependencies -------------------------------------------------------------------------------------------------

lazy val deps_common : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "org.slf4j"              %  "slf4j-api"                             % "1.4.0",
      "net.jcip"               %  "jcip-annotations"                      % "1.0",
      "org.slf4j"              %  "slf4j-simple"                          % "1.4.0"       % "test"
    )
  )

lazy val deps_samples : Seq[Setting[_]] =
  Seq(
    libraryDependencies ++= Seq(
      "jfree"                  %  "jfreechart"                            % "1.0.0"
    )
  )







// projects -----------------------------------------------------------------------------------------------------




lazy val root =
  project.in(file("."))
    .aggregate(jquantlib, `jquantlib-helpers`, `jquantlib-contrib`, `jquantlib-samples`)
    .settings(disablePublishing:_*)

lazy val jquantlib =
  project.in(file("jquantlib"))
    .settings(librarySettings:_*)
    .settings(javadocSettings:_*)
    .settings(junitSettings:_*)
    .settings(deps_common:_*)

lazy val `jquantlib-helpers` =
  project.in(file("jquantlib-helpers"))
    .dependsOn(jquantlib)
    .settings(librarySettings:_*)
    .settings(javadocSettings:_*)
    .settings(junitSettings:_*)
    .settings(deps_common:_*)

lazy val `jquantlib-contrib` =
  project.in(file("jquantlib-contrib"))
    .dependsOn(jquantlib)
    .settings(librarySettings:_*)
    .settings(javadocSettings:_*)
    .settings(junitSettings:_*)
    .settings(deps_common:_*)

lazy val `jquantlib-samples` =
  project.in(file("jquantlib-samples"))
    .dependsOn(jquantlib, `jquantlib-helpers`)
    .settings(librarySettings:_*)
    .settings(javadocSettings:_*)
    .settings(junitSettings:_*)
    .settings(deps_common:_*)
    .settings(deps_samples:_*)



// publish settings ---------------------------------------------------------------------------------------------

lazy val disablePublishing =
  sonatypeSettings ++
    Seq(
      publishArtifact := false,
      publishSigned := (),
      publish := (),
      publishLocal := ()
    )

lazy val publishSettings =
  sonatypeSettings ++
    Seq(
      publishTo := {
        val nexus = "https://oss.sonatype.org/"
        if (isSnapshot.value)
          Some("snapshots" at nexus + "content/repositories/snapshots")
        else
          Some("releases"  at nexus + "service/local/staging/deploy/maven2")
      },
      pomIncludeRepository := { _ => false },
      pomExtra := {
        <url>http://github.com/frgomes/jquantlib</url>
          <licenses>
            <license>
              <name>BSD</name>
            </license>
          </licenses>
          <scm>
            <developerConnection>scm:git:git@github.com:frgomes/jquantlib.git</developerConnection>
                         <connection>scm:git:github.com/frgomes/jquantlib.git</connection>
                                 <url>http://github.com/frgomes/jquantlib</url>
          </scm>
          <developers>
            <developer>
              <id>frgomes</id>
              <name>Richard Gomes</name>
              <url>http://rgomes-info.blogspot.com</url>
            </developer>
          </developers>
      }
    )

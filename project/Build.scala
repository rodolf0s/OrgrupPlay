import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "OrgrupPlay"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
    	"postgresql" % "postgresql" % "9.1-902.jdbc4"
      "mysql" % "mysql-connector-java" % "5.1.20"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here      
    )

}

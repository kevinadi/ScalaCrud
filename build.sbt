name := "ScalaCrud"
version := "1.0"
scalaVersion := "2.11.7"
libraryDependencies ++= Seq(
     "javax.servlet" % "javax.servlet-api" % "3.1.0"
    ,"org.eclipse.jetty" % "jetty-server" % "9.3.4.RC0"
    ,"org.eclipse.jetty" % "jetty-servlet" % "9.3.4.RC0"
    ,"org.eclipse.jetty" % "jetty-servlets" % "9.3.4.RC0"
    ,"org.eclipse.jetty" % "jetty-webapp" % "9.3.4.RC0"
    ,"com.h2database" % "h2" % "1.4.189"
)

name := "DBManProject2"

// The := method used in Name and Version is one of two fundamental methods.
// The other method is <<=
// All other initialization methods are implemented in terms of these.
version := "1.0"

// Add a single dependency
libraryDependencies += "junit" % "junit" % "4.8" % "test"

mainClass in (Compile, run) := Some(“PerformanceTesting”)

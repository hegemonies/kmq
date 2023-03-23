rootProject.name = "kmq"

val projects = listOf(
    "server",
    "proto-compiler",
)

projects.forEach { project ->
    include(project)
}

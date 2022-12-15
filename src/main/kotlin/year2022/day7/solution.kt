package year2022.day7

import java.io.File as JavaFile

const val LS = "$ ls"
const val CD = "$ cd "
const val DIR = "dir "

sealed class Entity {
    abstract fun calculateSize(): Int
    abstract fun print()

    private var _size: Int? = null
    open val size: Int get() = _size ?: calculateSize().also { _size = it }
}

class Directory private constructor(
    val name: String
) : Entity() {
    constructor(name: String, parentDir: Directory) : this(name) {
        _parentDir = parentDir
    }

    private var _parentDir: Directory? = root
    val parentDir: Directory get() = _parentDir ?: root

    val content = mutableMapOf<String, Entity>()
    override fun calculateSize() = content.entries.sumOf { it.value.calculateSize() }

    companion object {
        val root = Directory("/")
    }

    override fun print() {
        print("$name (dir, size=$size)")
    }
}

data class File(
    val name: String,
    override val size: Int
) : Entity() {
    override fun calculateSize() = size
    override fun print() {
        print("$name (file, size=$size)")
    }
}

private val data = JavaFile("src/main/kotlin/year2022/day7/input").readLines()

const val totalSpace = 70000000
const val requiredSpace = 30000000
fun main() {
    buildFilesystem()
    val smallerThan100k = findAll(Directory.root) {
        it is Directory && it.size <= 100_000
    }
    printTree()
    println()
    println("sum of sizes of directories smalle than 100_000: ${smallerThan100k.sumOf { (it as Directory).size }}")

    val allocatedSpace = Directory.root.size
    val freeSpace = totalSpace - allocatedSpace
    val toFreeUp = requiredSpace - freeSpace
    val candidates = findAll(Directory.root) {
        it is Directory && it.size >= toFreeUp
    }
    val directoryToDelete = candidates.minBy { it.size }

    println("size of smallest sufficient directory: ${directoryToDelete.size}")
}

fun buildFilesystem() {
    var currentDir: Directory = Directory.root

    data.forEach { line ->
        if (line.startsWith(CD)) {
            val name = line.drop(CD.length)
            currentDir = if (name == "..") currentDir.parentDir
            else currentDir.content.getOrPut(name) { Directory(name, currentDir) } as Directory
        } else if (line.startsWith(LS)) {
            //do nothing
        } else if (line.startsWith(DIR)) {
            val name = line.drop(DIR.length)
            currentDir.content.getOrPut(name) { Directory(name, currentDir) }
        } else {
            val (size, name) = line.split(" ")
            currentDir.content.getOrPut(name) { File(name, size.toInt()) }
        }
    }
}

fun findAll(root: Directory, predicate: (Entity) -> Boolean): List<Entity> {
    return (if (predicate(root)) listOf(root) else emptyList()) + root.content.entries.filter { it.value is Directory }
        .flatMap {
            findAll(it.value as Directory, predicate)
        }
}

fun printTree() {
    printNode("- ", Directory.root)

}

fun printNode(prefix: String, node: Entity) {
    print(prefix)
    node.print()
    println()
    (node as? Directory)?.content?.entries?.forEach { printNode("  $prefix", it.value) }
}

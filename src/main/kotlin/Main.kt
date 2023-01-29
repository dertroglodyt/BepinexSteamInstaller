import java.io.*
import java.net.URL
import java.nio.file.Files
import java.util.zip.ZipFile
import kotlin.io.path.createSymbolicLinkPointingTo
import kotlin.io.path.createTempDirectory
import kotlin.io.path.fileSize
import kotlin.system.exitProcess

private val programsDir = System.getenv("ProgramFiles(X86)")
private val steamDir = "$programsDir/Steam/steamapps/common/"
private val workshopDir = "$programsDir/Steam/steamapps/workshop/content/"

private object Main

/**
 * Program entry point.
 */
fun main(args: Array<String>) {
    // read know games list
    val gameMap = Main.javaClass.getResource("/games.csv").openStream().bufferedReader()
        .readLines().associate { line ->
            val items = line.split(",")
            items[0] to (items[1] to items[2])
        }

    if (args.size != 1) {
        println("Usage: install <Game>\nExample: install \"c:/Program Files/Steam/steamapps/common/<game dir>\"")
        println("Known games: ${gameMap.keys}")
        exitProcess(-1)
    }
    val game = args[0].replace('\\', '/').substringAfterLast("/")
    val gameID = gameMap[game]?.first ?: run {
        println("Unknown game $game!")
        println("Known games: ${gameMap.keys}")
        exitProcess(-1)
    }

    // find game directory
    val gameDir = if (args[0].contains("/") || args[0].contains("\\")) {
        File(args[0])
    } else {
        File(steamDir + game)
    }
    if (!gameDir.exists()) {
        println("Path to game directory not found: ${gameDir.absolutePath}")
        exitProcess(-1)
    }

    println("Downloading latest BepInEx from Github...")
    val zip = File(createTempDirectory().toFile(), "bepinex.zip")
    BufferedInputStream(URL((gameMap[game])!!.second).openStream()).use { Files.copy(it, zip.toPath()) }
    println("Downloaded ${zip.toPath().fileSize()} bytes to ${zip.absolutePath}.")

    println("Unzipping downloaded file to \"${gameDir.absolutePath}${File.separator}BepInEx\"...")
    unzip(zip, gameDir)

    println("Creating symbolic links to workshop directory...")
    val workshop = File(workshopDir + gameID)
    val bep1 = File(gameDir.absolutePath + "/BepInEx/plugins/workshop")
    bep1.parentFile.mkdirs()
    bep1.toPath().createSymbolicLinkPointingTo(workshop.toPath())

    val bep2 = File(gameDir.absolutePath + "/BepInEx/patchers/workshop")
    bep2.parentFile.mkdirs()
    bep2.toPath().createSymbolicLinkPointingTo(workshop.toPath())

    println("Done.")
}

/**
 * Unzip a complete ZIP file.
 */
private fun unzip(src: File, dest: File) {
    ZipFile(src).use { zip ->
        zip.entries().asSequence().forEach { entry ->
            zip.getInputStream(entry).use { input ->
                val filePath = dest.path + File.separator + entry.name

                if (!entry.isDirectory) {
                    File(filePath).parentFile.mkdirs()
                    extractFile(input, filePath)
                } else {
                    File(filePath).mkdirs()
                }
            }
        }
    }
}

/**
 * Extracts a zip entry (file entry).
 */
private fun extractFile(inputStream: InputStream, destFilePath: String) {
    val bos = BufferedOutputStream(FileOutputStream(destFilePath))
    val bytesIn = ByteArray(4096)
    var read: Int
    while (inputStream.read(bytesIn).also { read = it } != -1) {
        bos.write(bytesIn, 0, read)
    }
    bos.close()
}
package net.mangolise.gradle;

import com.google.gson.Gson;
import net.minestom.server.MinecraftServer;
import net.worldseed.resourcepack.PackBuilder;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;
import org.zeroturnaround.zip.ZipUtil;

public class PackageResourcePack extends DefaultTask {
    private static final Gson GSON = new Gson();

    public PackageResourcePack() {
    }

    private final Path ROOT_PATH = getProject().getRootDir().toPath();
    private final Path BASE_PATH = ROOT_PATH.resolve("resourcepack");
    private final Path MODEL_PATH = BASE_PATH.resolve("models");

    @TaskAction
    public void packageResourcePack() throws Exception {

        System.out.println("Packaging resource pack");

        // delete previous output
        remove(BASE_PATH.resolve("out.zip"));
        remove(BASE_PATH.resolve("out"));
        remove(BASE_PATH.resolve("models"));
        remove(BASE_PATH.resolve("model_mappings.json"));

        copy(BASE_PATH.resolve("pack"), BASE_PATH.resolve("out"));

        MinecraftServer.init();

        PackBuilder.ConfigJson config = PackBuilder.Generate(BASE_PATH.resolve("bbmodel"), BASE_PATH.resolve("out"), MODEL_PATH);
        Files.writeString(BASE_PATH.resolve("model_mappings.json"), config.modelMappings(), Charset.defaultCharset(), StandardOpenOption.CREATE);

        // zip the output resourcepack
        ZipUtil.pack(BASE_PATH.resolve("out").toFile(), BASE_PATH.resolve("out.zip").toFile());

        System.out.println("Done packaging resource pack");
    }

    private void remove(Path path) throws IOException {
        if (!Files.isDirectory(path)) {
            Files.deleteIfExists(path);
            return;
        }
        try (Stream<Path> stream = Files.walk(path)) {
            stream.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    private void copy(Path source, Path dest) throws IOException {
        if (!Files.isDirectory(source)) {
            Files.copy(source, dest, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        Files.createDirectories(dest);
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(path -> {
                try {
                    Files.copy(path, dest.resolve(source.relativize(path)), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}

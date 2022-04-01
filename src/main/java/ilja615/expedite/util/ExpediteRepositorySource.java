package ilja615.expedite.util;

import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ExpediteRepositorySource implements RepositorySource
{
    private final File file;

    public ExpediteRepositorySource(Path dir) {

        file = dir.toFile();

            if (!file.exists()) {
                file.mkdirs();
            }

            if (!file.isDirectory()) {
                throw new IllegalStateException("Is not a dir");
            }

    }

    @Override
    public void loadPacks(Consumer<Pack> consumer, Pack.PackConstructor packConstructor)
    {
        for (File packCandidate : Objects.requireNonNull(file.listFiles())) {

            final String packName = packCandidate.getName();
            final Pack pack = Pack.create(packName, true, createPackSupplier(packCandidate), packConstructor, Pack.Position.TOP, PackSource.BUILT_IN);

            if (pack != null) {
                consumer.accept(pack);
            }
        }
    }

    private Supplier<PackResources> createPackSupplier (File packFile) {
        return () -> packFile.isDirectory() ? new FolderPackResources(packFile) : new FilePackResources(packFile);
    }
}

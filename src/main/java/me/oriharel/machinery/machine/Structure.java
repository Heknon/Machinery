package me.oriharel.machinery.machine;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.session.PasteBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Structure {
    private final PasteBuilder builder;
    private final EditSession editSession;
    private final String name;

    public Structure(File schematic, String name, org.bukkit.World world) {
        PasteBuilder builder;
        this.editSession =
                WorldEdit.getInstance()
                        .getEditSessionFactory()
                        .getEditSession(BukkitAdapter.adapt(world), -1);
        this.name = name;
        try {
            builder =
                    new ClipboardHolder(
                            ClipboardFormats.findByFile(schematic)
                                    .getReader(new FileInputStream(schematic))
                                    .read())
                            .createPaste(editSession);
        } catch (IOException e) {
            e.printStackTrace();
            builder = null;
        }
        this.builder = builder;
    }

    public PasteBuilder getBuilder() {
        return builder;
    }

    public String getName() {
        return name;
    }

    public void build(int x, int y, int z, Runnable callback) {
        if (this.builder == null)
            throw new NullPointerException("Structure (" + name + ") builder is null!");
        Operation operation = this.builder.to(BlockVector3.at(x, y, z)).ignoreAirBlocks(true).build();
        try {
            Operations.complete(operation);
            editSession.flushSession();

        } catch (WorldEditException e) {
            callback.run();
            e.printStackTrace();
        }
    }
}

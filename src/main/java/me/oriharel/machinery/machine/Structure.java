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
import com.sun.istack.internal.NotNull;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Structure {
    private final File schematic;
    private PasteBuilder builder;
    private EditSession editSession;
    private String name;

    public Structure(File schematic, String name) {
        this.name = name;
        this.schematic = schematic;
    }

    public File getSchematic() {
        return schematic;
    }

    public PasteBuilder getBuilder() {
        return builder;
    }

    public EditSession getEditSession() {
        return editSession;
    }

    public String getName() {
        return name;
    }

    public void build(@NotNull Location loc, Runnable callback) {
        PasteBuilder builder;
        this.editSession =
                WorldEdit.getInstance()
                        .getEditSessionFactory()
                        .getEditSession(BukkitAdapter.adapt(loc.getWorld()), -1);
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
        if (builder == null)
            throw new NullPointerException("Structure (" + name + ") builder is null!");
        Operation operation = builder.to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ())).ignoreAirBlocks(true).build();
        try {
            Operations.complete(operation);
            editSession.flushSession();
            callback.run();

        } catch (WorldEditException e) {
            callback.run();
            e.printStackTrace();
        }
    }
}

package me.oriharel.machinery.config;

import com.google.common.io.ByteStreams;
import me.oriharel.machinery.Machinery;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class FileManager {

    private final Machinery machinery;
    private Map<String, Config> loadedConfigs = new HashMap<>();

    public FileManager(Machinery machinery) {
        this.machinery = machinery;

        loadConfigs();
    }

    public void loadConfigs() {
        if (!machinery.getDataFolder().exists()) {
            machinery.getDataFolder().mkdir();
        }

        Map<String, File> configFiles = new LinkedHashMap<>();
        configFiles.put("machines.yml", new File(machinery.getDataFolder(), "machines.yml"));
        configFiles.put("fuels.yml", new File(machinery.getDataFolder(), "fuels.yml"));
        configFiles.put("machine_registry.yml", new File(machinery.getDataFolder(), "machine_registry.yml"));
        configFiles.put("config.yml", new File(machinery.getDataFolder(), "config.yml"));

        for (Entry<String, File> configEntry : configFiles.entrySet()) {

            String fileName = configEntry.getKey();
            File configFile = configEntry.getValue();

            if (configFile.exists()) {
                FileChecker fileChecker;

                fileChecker = new FileChecker(machinery, this, fileName, true);

                fileChecker.loadSections();
                fileChecker.compareFiles();
                fileChecker.saveChanges();
            } else {
                try {
                    configFile.createNewFile();
                    try (InputStream is = machinery.getResource(fileName); OutputStream os = new FileOutputStream(configFile)) {
                        ByteStreams.copy(is, os);
                    }
                } catch (IOException ex) {
                    Bukkit.getServer().getLogger().log(Level.WARNING, "Machinery | Error: Unable to create configuration file.");
                }
            }
        }


    }


    public boolean isFileExist(File configPath) {
        return configPath.exists();
    }

    public void unloadConfig(File configPath) {
        loadedConfigs.remove(configPath.getPath());
    }

    public void deleteConfig(File configPath) {
        Config config = getConfig(configPath);
        config.getFile().delete();
        loadedConfigs.remove(configPath.getPath());
    }

    public Config getConfig(File configPath) {

        Config cached = loadedConfigs.get(configPath.getPath());

        if (cached != null) return cached;

        Config config = new Config(this, configPath);
        loadedConfigs.put(configPath.getPath(), config);

        return config;
    }

    public Map<String, Config> getConfigs() {
        return loadedConfigs;
    }

    public boolean isConfigLoaded(File configPath) {
        return loadedConfigs.containsKey(configPath.getPath());
    }

    public InputStream getConfigContent(Reader reader) {
        try {
            String addLine, currentLine, pluginName = machinery.getDescription().getName();
            int commentNum = 0;

            StringBuilder whole = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(reader);

            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.contains("#")) {
                    addLine = currentLine.replace("[!]", "IMPORTANT").replace(":", "-").replaceFirst("#", pluginName + "_COMMENT_" + commentNum + ":");
                    whole.append(addLine + "\n");
                    commentNum++;
                } else {
                    whole.append(currentLine + "\n");
                }
            }

            String config = whole.toString();
            InputStream configStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.UTF_8));
            bufferedReader.close();

            return configStream;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        }
    }

    public InputStream getConfigContent(File configFile) {
        if (!configFile.exists()) {
            return null;
        }

        try {
            return getConfigContent(new FileReader(configFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String prepareConfigString(String configString) {
        String[] lines = configString.split("\n");
        StringBuilder config = new StringBuilder();

        for (String line : lines) {
            if (line.contains(machinery.getDescription().getName() + "_COMMENT")) {
                config.append(line.replace("IMPORTANT", "[!]").replace("\n", "").replace(machinery.getDescription().getName() + "_COMMENT_", "#").replaceAll("[0-9]+:", "") + "\n");
            } else if (line.contains(":")) {
                config.append(line + "\n");
            }
        }

        return config.toString();
    }

    public void saveConfig(String configString, File configFile) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
            writer.write(prepareConfigString(configString));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Config {

        private File configFile;
        private FileConfiguration configLoad;

        public Config(FileManager fileManager, File configPath) {
            configFile = configPath;

            if (configPath.getName().equals("config.yml")) {
                configLoad = YamlConfiguration.loadConfiguration(new InputStreamReader(fileManager.getConfigContent(configFile)));
            } else {
                configLoad = YamlConfiguration.loadConfiguration(configPath);
            }
        }

        public File getFile() {
            return configFile;
        }

        public FileConfiguration getFileConfiguration() {
            return configLoad;
        }

        public FileConfiguration loadFile() {
            configLoad = YamlConfiguration.loadConfiguration(configFile);
            return configLoad;
        }
    }
}

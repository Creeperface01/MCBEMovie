package itsu.mcbe.mcbemovie.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jcodec.common.io.IOUtils;

import com.google.gson.Gson;

public class ResourcePackMaker {

    private UUID headerUUID;
    private UUID moduleUUID;

    private Map<String, Object> manifest;
    private Map<String, Object> manifestHeader;
    private Map<String, Object> manifestModule;

    private Map<String, Object> definition;
    private Map<String, Object> data;
    private Map<String, Object> sound;

    private File musicFolder;
    private File music = new File("mcbemovie.ogg");

    public static void main(String args[]) {
        ResourcePackMaker maker = new ResourcePackMaker();
        maker.process();
    }

    private void process() {
        if(!new File("mcbemovie.ogg").exists()) {
            System.err.println("mcbemovie.oggが見つかりません。");
            return;
        }

        System.out.println("フォルダ階層を作成しています...");
        init();
        makeManifest();
        makeSoundDefinition();

        try {
            makeRawFolders();
            createZip();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("完了しました。MCBEMovie.mcpackを適用させてください。");

    }

    private void init() {
        headerUUID = UUID.randomUUID();
        moduleUUID = UUID.randomUUID();

        manifestModule = new HashMap<>();
        manifestHeader = new HashMap<>();
        manifest = new HashMap<>();
        definition = new HashMap<>();
        data = new HashMap<>();
        sound = new HashMap<>();

        musicFolder = new File("sounds/music/");
    }

    private void makeManifest() {
        manifestModule.put("description", "MCBEMoviePack");
        manifestModule.put("type", "resources");
        manifestModule.put("uuid", headerUUID.toString());
        manifestModule.put("version", new int[]{1, 0, 0});

        manifestHeader.put("description", "MCBEMoviePack");
        manifestHeader.put("name", "MCBEMoviePack");
        manifestHeader.put("uuid", moduleUUID.toString());
        manifestHeader.put("version", new int[]{1, 0, 0});

        manifest.put("format_version", 1);
        manifest.put("header", manifestHeader);
        manifest.put("modules", new Map[]{manifestModule});
    }

    private void makeSoundDefinition() {
        sound.put("name", "sounds/music/mcbemovie");
        sound.put("volume", 1);

        data.put("category", "music");
        data.put("sounds", new Map[]{sound});

        definition.put("music.mcbemovie", data);
    }

    private void makeRawFolders() throws IOException {
        musicFolder.mkdirs();

        Utils.writeFile(new File("manifest.json"), new Gson().toJson(manifest));
        Utils.writeFile(new File("sounds/sound_definitions.json"), new Gson().toJson(definition));

        Utils.copyFile(music, new File("sounds/music/mcbemovie.ogg"));
    }

    /**
     * Copied from https://qiita.com/areph/items/8d1ab96c93aa2463ff4a
     * @author areph
     */
    private void createZip() {
        System.out.println("圧縮中...");
        File[] files = {new File("sounds/music/mcbemovie.ogg"), new File("sounds/sound_definitions.json"),new File("manifest.json")};
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File("MCBEMovie.mcpack"))));
            createZip(zos, files);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(zos);
        }
    }

    /**
     * Copied from https://qiita.com/areph/items/8d1ab96c93aa2463ff4a
     * @author areph
     */
    private void createZip(ZipOutputStream zos, File[] files) throws IOException {
        byte[] buf = new byte[1024];
        InputStream is = null;
        try {
            for (File file : files) {
                ZipEntry entry = new ZipEntry(file.getPath());
                zos.putNextEntry(entry);
                is = new BufferedInputStream(new FileInputStream(file));
                int len = 0;
                while ((len = is.read(buf)) != -1) {
                    zos.write(buf, 0, len);
                }
            }
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

}

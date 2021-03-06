package de.marhali.easyi18n.io.implementation;

import com.google.gson.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;

import de.marhali.easyi18n.io.TranslatorIO;
import de.marhali.easyi18n.model.LocalizedNode;
import de.marhali.easyi18n.model.Translations;
import de.marhali.easyi18n.util.IOUtil;
import de.marhali.easyi18n.util.JsonUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Consumer;

/**
 * Implementation for JSON translation files.
 * @author marhali
 */
public class JsonTranslatorIO implements TranslatorIO {

    private static final String FILE_EXTENSION = "json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    @Override
    public void read(@NotNull Project project, @NotNull String directoryPath, @NotNull Consumer<Translations> callback) {
        ApplicationManager.getApplication().saveAll(); // Save opened files (required if new locales were added)

        ApplicationManager.getApplication().runReadAction(() -> {
            VirtualFile directory = LocalFileSystem.getInstance().findFileByIoFile(new File(directoryPath));

            if(directory == null || directory.getChildren() == null) {
                throw new IllegalArgumentException("Specified folder is invalid (" + directoryPath + ")");
            }

            VirtualFile[] files = directory.getChildren();

            List<String> locales = new ArrayList<>();
            LocalizedNode nodes = new LocalizedNode(LocalizedNode.ROOT_KEY, new ArrayList<>());

            try {
                for(VirtualFile file : files) {

                    if(!IOUtil.isFileRelevant(project, file)) { // File does not matches pattern
                        continue;
                    }

                    locales.add(file.getNameWithoutExtension());

                    JsonObject tree = GSON.fromJson(new InputStreamReader(file.getInputStream(),
                            file.getCharset()), JsonObject.class);

                    JsonUtil.readTree(file.getNameWithoutExtension(), tree, nodes);
                }

                callback.accept(new Translations(locales, nodes));

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(null);
            }
        });
    }

    @Override
    public void save(@NotNull Project project, @NotNull Translations translations,
                     @NotNull String directoryPath, @NotNull Consumer<Boolean> callback) {
        ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                for(String locale : translations.getLocales()) {
                    JsonObject content = new JsonObject();
                    JsonUtil.writeTree(locale, content, translations.getNodes());

                    String fullPath = directoryPath + "/" + locale + "." + FILE_EXTENSION;
                    File file = new File(fullPath);
                    boolean created = file.createNewFile();

                    VirtualFile vf = created ? LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file)
                            : LocalFileSystem.getInstance().findFileByIoFile(file);

                    vf.setBinaryContent(GSON.toJson(content).getBytes(vf.getCharset()));
                }

                // Successfully saved
                callback.accept(true);

            } catch(IOException e) {
                e.printStackTrace();
                callback.accept(false);
            }
        });
    }
}
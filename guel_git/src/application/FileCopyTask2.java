package application;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javafx.concurrent.Task;

public class FileCopyTask2 extends Task<Void> {
    private Path source;
    private Path target;

    public FileCopyTask2(Path source, Path target) {
        this.source = source;
        this.target = target;
    }
    
    @Override
    protected Void call() throws Exception {
        Files.copy(this.source, this.target, StandardCopyOption.COPY_ATTRIBUTES);
        return null;
    }
}

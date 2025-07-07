package hello.rebobatch.domain.store.partitioner;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VirtualFile {
    private final String filePath;
    private final long startLine;
    private final long endLine;
    private final long lineCount;
    
    public VirtualFile(String filePath, long startLine, long endLine) {
        this.filePath = filePath;
        this.startLine = startLine;
        this.endLine = endLine;
        this.lineCount = endLine - startLine + 1;
    }
    
    @Override
    public String toString() {
        return String.format("%s[%d-%d](%d lines)", 
            filePath.substring(filePath.lastIndexOf('/') + 1), 
            startLine, endLine, lineCount);
    }
} 
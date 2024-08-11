package com.liuqi.common.utils.rest;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * 内存文件流
 *
 * @author  LiuQi 2019/12/19-17:49
 * @version V1.0
 **/
public class MemoryInputStreamResource extends InputStreamResource {
    private String fileName;
    private long contentLength;

    /**
     * Create a new InputStreamResource.
     *
     * @param inputStream the InputStream to use
     */
    public MemoryInputStreamResource(String fileName, long contentLength,  InputStream inputStream) {
        super(inputStream);
        this.fileName = fileName;
        this.contentLength = contentLength;
    }

    /**
     * This implementation reads the entire InputStream to calculate the
     * content length. Subclasses will almost always be able to provide
     * a more optimal version of this, e.g. checking a File length.
     *
     * @see #getInputStream()
     */
    @Override
    public long contentLength() throws IOException {
        return contentLength;
    }

    /**
     * This implementation always returns {@code null},
     * assuming that this resource type does not have a filename.
     */
    @Override
    public String getFilename() {
        return fileName;
    }
}

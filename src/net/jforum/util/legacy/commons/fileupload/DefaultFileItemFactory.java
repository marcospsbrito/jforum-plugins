/*
 * Copyright 2001-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.jforum.util.legacy.commons.fileupload;

import java.io.File;

import net.jforum.util.legacy.commons.fileupload.disk.DiskFileItemFactory;

/**
 * <p>The default {@link org.apache.commons.fileupload.FileItemFactory}
 * implementation. This implementation creates
 * {@link org.apache.commons.fileupload.FileItem} instances which keep their
 * content either in memory, for smaller items, or in a temporary file on disk,
 * for larger items. The size threshold, above which content will be stored on
 * disk, is configurable, as is the directory in which temporary files will be
 * created.</p>
 *
 * <p>If not otherwise configured, the default configuration values are as
 * follows:
 * <ul>
 *   <li>Size threshold is 10KB.</li>
 *   <li>Repository is the system default temp directory, as returned by
 *       <code>System.getProperty("java.io.tmpdir")</code>.</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:martinc@apache.org">Martin Cooper</a>
 *
 * @version $Id: DefaultFileItemFactory.java,v 1.1 2005/05/29 16:31:54 rafaelsteil Exp $
 *
 * @deprecated Use <code>DiskFileItemFactory</code> instead.
 */
public class DefaultFileItemFactory extends DiskFileItemFactory {

    // ----------------------------------------------------------- Constructors


    /**
     * Constructs an unconfigured instance of this class. The resulting factory
     * may be configured by calling the appropriate setter methods.
     *
     * @deprecated Use <code>DiskFileItemFactory</code> instead.
     */
    public DefaultFileItemFactory() {
        super();
    }


    /**
     * Constructs a preconfigured instance of this class.
     *
     * @param sizeThreshold The threshold, in bytes, below which items will be
     *                      retained in memory and above which they will be
     *                      stored as a file.
     * @param repository    The data repository, which is the directory in
     *                      which files will be created, should the item size
     *                      exceed the threshold.
     *
     * @deprecated Use <code>DiskFileItemFactory</code> instead.
     */
    public DefaultFileItemFactory(int sizeThreshold, File repository) {
        super(sizeThreshold, repository);
    }


    // --------------------------------------------------------- Public Methods

    /**
     * Create a new {@link org.apache.commons.fileupload.DefaultFileItem}
     * instance from the supplied parameters and the local factory
     * configuration.
     *
     * @param fieldName   The name of the form field.
     * @param contentType The content type of the form field.
     * @param isFormField <code>true</code> if this is a plain form field;
     *                    <code>false</code> otherwise.
     * @param fileName    The name of the uploaded file, if any, as supplied
     *                    by the browser or other client.
     *
     * @return The newly created file item.
     *
     * @deprecated Use <code>DiskFileItemFactory</code> instead.
     */
    public FileItem createItem(
            String fieldName,
            String contentType,
            boolean isFormField,
            String fileName
            ) {
        return new DefaultFileItem(fieldName, contentType,
                isFormField, fileName, getSizeThreshold(), getRepository());
    }

}

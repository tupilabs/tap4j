/*
 * The MIT License
 *
 * Copyright (c) 2010 tap4j team (see AUTHORS)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.tap4j.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A TAP element.
 *
 * @since 1.0
 */
public class TapElement implements Serializable {
    
    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 9108597596777603763L;

    /**
     * input line indentation.
     */
    int indentation;
    
    /**
     * Iterable object returned by snakeyaml.
     */
    private Map<String, Object> diagnostic = new LinkedHashMap<String, Object>();
    
    /**
     * Comment.
     */
    Comment comment;

    /**
     * Get me indentation of the corresponding input line.
     * 
     * @return Indentation in the original stream
     */
    public int getIndentation() {
        return indentation;
    }
    
    /**
     * Yaml diagnostic information.
     */
    public Map<String, Object> getDiagnostic() {
        return this.diagnostic;
    }

    /**
     * Yaml diagnostic information setter.
     */
    public void setDiagnostic(Map<String, Object> diagnostic) {
        this.diagnostic = diagnostic;
    }
    
    /**
     * @param comment Comment.
     */
    public void setComment(Comment comment) {
        this.comment = comment;
    }

    /**
     * @return Comment.
     */
    public Comment getComment() {
        return this.comment;
    }
}

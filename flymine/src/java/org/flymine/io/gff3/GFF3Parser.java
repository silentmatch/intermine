package org.flymine.io.gff3;

/*
 * Copyright (C) 2002-2005 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

import java.io.IOException;
import java.io.BufferedReader;

/**
 * A parser for GFF3 files.  This code was taken from Matthew Pocock's GFF3 parser in BioJava.
 *
 * @author Kim Rutherford
 */

public class GFF3Parser
{
    /**
     * Read GFF3 lines from a BufferedReader and return an Iterator over the GFF3Records.
     * @param reader the Reader to reader from
     * @return an Iterator over the GFF3Record objects from the reader
     * @throws IOException if there is an error during reading or parsing
     */
    public static Iterator parse(final BufferedReader reader) throws IOException {
        int lineNum = 0;
        List list = new ArrayList();

        String line = null;

        while ((line = reader.readLine()) != null) {
            String trimmedLine = line.trim();

            if (trimmedLine.length() == 0 || trimmedLine.startsWith("#")) {
                continue;
            }

            // throws IOException if the first GFF line isn't valid
            new GFF3Record(trimmedLine);

            break;
        }

        final String firstGFFLine = line;

        return new Iterator() {
            String currentLine = firstGFFLine;

            public boolean hasNext() {
                return (currentLine != null);
            }
            
            public Object next() {
                if (currentLine == null) {
                    throw new NoSuchElementException();
                } else {
                    Object objectToReturn = null;
                    try {
                        objectToReturn = new GFF3Record(currentLine);
                        while ((currentLine = reader.readLine()) != null) {
                            String trimmedLine = currentLine.trim();
                            if (trimmedLine.length() == 0 || trimmedLine.startsWith("#")) {
                                continue;
                            }
                            break;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException("IOException while getting next GFF record", e);
                    }
                    return objectToReturn;
                }
            }

            public void remove() throws UnsupportedOperationException {
                throw new UnsupportedOperationException("remove not supported");
            }
        };
    }
}

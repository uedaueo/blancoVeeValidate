/*
 * blanco Framework
 * Copyright (C) 2004-2008 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.veevalidate.task;

import blanco.cg.BlancoCgSupportedLang;
import blanco.veevalidate.*;
import blanco.veevalidate.message.BlancoVeeValidateMessage;
import blanco.veevalidate.task.valueobject.BlancoVeeValidateProcessInput;
import blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure;

import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BlancoVeeValidateProcessImpl implements BlancoVeeValidateProcess {

    /**
     * A message.
     */
    private final BlancoVeeValidateMessage fMsg = new BlancoVeeValidateMessage();

    /**
     * {@inheritDoc}
     */
    public int execute(final BlancoVeeValidateProcessInput input)
            throws IOException, IllegalArgumentException {
        System.out.println("- " + BlancoVeeValidateConstants.PRODUCT_NAME
                + " (" + BlancoVeeValidateConstants.VERSION + ")");
        try {
            final File fileMetadir = new File(input.getMetadir());
            if (fileMetadir.exists() == false) {
                throw new IllegalArgumentException(fMsg.getMbvoja01(input
                        .getMetadir()));
            }

            /*
             * Determines the newline code.
             */
            String LF = "\n";
            String CR = "\r";
            String CRLF = CR + LF;
            String lineSeparatorMark = input.getLineSeparator();
            String lineSeparator = "";
            if ("LF".equals(lineSeparatorMark)) {
                lineSeparator = LF;
            } else if ("CR".equals(lineSeparatorMark)) {
                lineSeparator = CR;
            } else if ("CRLF".equals(lineSeparatorMark)) {
                lineSeparator = CRLF;
            }
            if (lineSeparator.length() != 0) {
                System.setProperty("line.separator", lineSeparator);
                if (input.getVerbose()) {
                    System.out.println("lineSeparator try to change to " + lineSeparatorMark);
                    String newProp = System.getProperty("line.separator");
                    String newMark = "other";
                    if (LF.equals(newProp)) {
                        newMark = "LF";
                    } else if (CR.equals(newProp)) {
                        newMark = "CR";
                    } else if (CRLF.equals(newProp)) {
                        newMark = "CRLF";
                    }
                    System.out.println("New System Props = " + newMark);
                }
            }

            /*
             * Processes targetdir and targetStyle.
             * Sets the storage location for the generated code.
             * targetstyle = blanco:
             *  Creates a main directory under targetdir.
             * targetstyle = maven:
             *  Creates a main/java directory under targetdir.
             * targetstyle = free:
             *  Creates a directory using targetdir as is.
             *  However, the default string (blanco) is used if targetdir is empty.
             * by tueda, 2019/08/30
             */
            String strTarget = input.getTargetdir();
            String style = input.getTargetStyle();
            // Always true when passing through here.
            boolean isTargetStyleAdvanced = true;
            if (style != null && BlancoVeeValidateConstants.TARGET_STYLE_MAVEN.equals(style)) {
                strTarget = strTarget + "/" + BlancoVeeValidateConstants.TARGET_DIR_SUFFIX_MAVEN;
            } else if (style == null ||
                    !BlancoVeeValidateConstants.TARGET_STYLE_FREE.equals(style)) {
                strTarget = strTarget + "/" + BlancoVeeValidateConstants.TARGET_DIR_SUFFIX_BLANCO;
            }
            /* Uses targetdir as is if style is free. */
            if (input.getVerbose()) {
                System.out.println("/* tueda */ TARGETDIR = " + strTarget);
            }

            // Creates a temporary directory.
            new File(input.getTmpdir()
                    + BlancoVeeValidateConstants.TARGET_SUBDIRECTORY).mkdirs();

            new BlancoVeeValidateMeta2Xml().processDirectory(fileMetadir, input
                    .getTmpdir()
                    + BlancoVeeValidateConstants.TARGET_SUBDIRECTORY);

            // Generates ValueObject from XML-ized metafiles.
            // Scans the temporary folder first.
            final File[] fileMeta2 = new File(input.getTmpdir()
                    + BlancoVeeValidateConstants.TARGET_SUBDIRECTORY)
                    .listFiles();

        /*
         * First, searches all the sheets and makes a list of class and package names.
         * This is because the package name is not specified when specifying a class in the PHP format definition.
         */
            BlancoVeeValidateUtil.isVerbose = input.getVerbose();
            BlancoVeeValidateUtil.processValueObjects(input);

            List<BlancoVeeValidateClassStructure> validatorStructures = new ArrayList<>();
            List<BlancoVeeValidateClassStructure> sortedValidatorStructures = new ArrayList<>();

            // Next, scans the directory specified as the meta directory.
            for (int index = 0; index < fileMeta2.length; index++) {
                if (fileMeta2[index].getName().endsWith(".xml") == false) {
                    continue;
                }

                BlancoVeeValidateXmlParser parser = new BlancoVeeValidateXmlParser();
                parser.setVerbose(input.getVerbose());
                final BlancoVeeValidateClassStructure[] structures = parser.parse(fileMeta2[index]);

                /*
                 * Collects them all.
                 */
                for (int index2 = 0; index2 < structures.length; index2++) {
                    BlancoVeeValidateClassStructure classStructure = structures[index2];
                    validatorStructures.add(classStructure);
                }
            }

            /* sort validatorStructures */
            if (validatorStructures.size() == 1) {
                sortedValidatorStructures.add(validatorStructures.get(0));
            } else if (validatorStructures.size() > 1) {
                sortedValidatorStructures = validatorStructures.stream().sorted(
                        new Comparator<BlancoVeeValidateClassStructure>() {
                            @Override
                            public int compare(BlancoVeeValidateClassStructure o1, BlancoVeeValidateClassStructure o2) {
                                return o1.getName().compareTo(o2.getName());
                            }
                        }
                ).collect(Collectors.toList());
            }

            /*
             * Then, generates the ValidateInitializer class.
             */
            final BlancoVeeValidateXml2TypeScriptClass xml2Class = new BlancoVeeValidateXml2TypeScriptClass();
            xml2Class.setEncoding(input.getEncoding());
            xml2Class.setVerbose(input.getVerbose());
            xml2Class.setTargetStyleAdvanced(isTargetStyleAdvanced);
            xml2Class.setXmlRootElement(input.getXmlrootelement());
            xml2Class.setSheetLang(new BlancoCgSupportedLang().convertToInt(input.getSheetType()));
            xml2Class.setTabs(input.getTabs());
            xml2Class.process(sortedValidatorStructures, new File(strTarget));

            return BlancoVeeValidateBatchProcess.END_SUCCESS;
        } catch (TransformerException e) {
            throw new IOException("An exception occurred during the XML conversion process: " + e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean progress(final String argProgressMessage) {
        System.out.println(argProgressMessage);
        return false;
    }
}

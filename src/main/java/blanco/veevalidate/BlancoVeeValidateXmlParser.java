/*
 * blanco Framework
 * Copyright (C) 2004-2008 IGA Tosiki
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.veevalidate;

import blanco.cg.BlancoCgSupportedLang;
import blanco.commons.util.BlancoNameUtil;
import blanco.commons.util.BlancoStringUtil;
import blanco.valueobjectts.valueobject.BlancoValueObjectTsClassStructure;
import blanco.veevalidate.message.BlancoVeeValidateMessage;
import blanco.veevalidate.resourcebundle.BlancoVeeValidateResourceBundle;
import blanco.veevalidate.valueobject.BlancoVeeValidateClassStructure;
import blanco.veevalidate.valueobject.BlancoVeeValidateFieldStructure;
import blanco.veevalidate.valueobject.BlancoVeeValidateMessageStructure;
import blanco.xml.bind.BlancoXmlBindingUtil;
import blanco.xml.bind.BlancoXmlUnmarshaller;
import blanco.xml.bind.valueobject.BlancoXmlAttribute;
import blanco.xml.bind.valueobject.BlancoXmlDocument;
import blanco.xml.bind.valueobject.BlancoXmlElement;

import java.io.File;
import java.util.*;

/**
 * A class that parses (reads and writes) the intermediate XML file format of blancoValueObject.
 *
 * @author IGA Tosiki
 */
public class BlancoVeeValidateXmlParser {
    /**
     * A message.
     */
    private final BlancoVeeValidateMessage fMsg = new BlancoVeeValidateMessage();

    private boolean fVerbose = false;

    public void setVerbose(boolean argVerbose) {
        this.fVerbose = argVerbose;
    }

    public boolean isVerbose() {
        return fVerbose;
    }

    /**
     * Resource bundle object for blancoValueObject.
     */
    private final static BlancoVeeValidateResourceBundle fBundle = new BlancoVeeValidateResourceBundle();

    public static Map<String, Integer> mapCommons = new HashMap<String, Integer>() {
        {
            put(fBundle.getMeta2xmlElementCommon(), BlancoCgSupportedLang.PHP);
        } // Supports for PHP-type sheets.

        {
            put(fBundle.getMeta2xmlElementCommonCs(), BlancoCgSupportedLang.CS);
        }

        {
            put(fBundle.getMeta2xmlElementCommonJs(), BlancoCgSupportedLang.JS);
        }

        {
            put(fBundle.getMeta2xmlElementCommonVb(), BlancoCgSupportedLang.VB);
        }

        {
            put(fBundle.getMeta2xmlElementCommonPhp(), BlancoCgSupportedLang.PHP);
        }

        {
            put(fBundle.getMeta2xmlElementCommonRuby(), BlancoCgSupportedLang.RUBY);
        }

        {
            put(fBundle.getMeta2xmlElementCommonPython(), BlancoCgSupportedLang.PYTHON);
        }

        {
            put(fBundle.getMeta2xmlElementCommonKt(), BlancoCgSupportedLang.KOTLIN);
        }

        {
            put(fBundle.getMeta2xmlElementCommonTs(), BlancoCgSupportedLang.TS);
        }
    };

    /**
     * Parses an XML document in an intermediate XML file to get an array of information.
     *
     * @param argMetaXmlSourceFile An intermediate XML file.
     * @return An array of information obtained as a result of parsing.
     */
    public BlancoVeeValidateClassStructure[] parse(
            final File argMetaXmlSourceFile) {
        final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller()
                .unmarshal(argMetaXmlSourceFile);
        if (documentMeta == null) {
            return null;
        }

        return parse(documentMeta);

    }

    /**
     * Parses an XML document in an intermediate XML file to get an array of value object information.
     *
     * @param argXmlDocument XML document of an intermediate XML file.
     * @return An array of value object information obtained as a result of parsing.
     */
    public BlancoVeeValidateClassStructure[] parse(
            final BlancoXmlDocument argXmlDocument) {
        final List<BlancoVeeValidateClassStructure> listStructure = new ArrayList<BlancoVeeValidateClassStructure>();

        // Gets the root element.
        final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                .getDocumentElement(argXmlDocument);
        if (elementRoot == null) {
            // The process is aborted if there is no root element.
            return null;
        }

        // Gets a list of sheets (Excel sheets).
        final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                .getElementsByTagName(elementRoot, "sheet");

        final int sizeListSheet = listSheet.size();
        for (int index = 0; index < sizeListSheet; index++) {
            final BlancoXmlElement elementSheet = listSheet.get(index);

            /*
             * Supports sheets written for languages other than Java.
             */
            List<BlancoXmlElement> listCommon = null;
            int sheetLang = BlancoCgSupportedLang.JAVA;
            for (String common : mapCommons.keySet()) {
                listCommon = BlancoXmlBindingUtil
                        .getElementsByTagName(elementSheet,
                                common);
                if (listCommon.size() != 0) {
                    BlancoXmlAttribute attr = new BlancoXmlAttribute();
                    attr.setType("CDATA");
                    attr.setQName("style");
                    attr.setLocalName("style");

                    sheetLang = mapCommons.get(common);
                    attr.setValue(new BlancoCgSupportedLang().convertToString(sheetLang));

                    elementSheet.getAtts().add(attr);

                    /* tueda DEBUG */
                    if (this.isVerbose()) {
                        System.out.println("/* tueda */ style = " + BlancoXmlBindingUtil.getAttribute(elementSheet, "style"));
                    }

                    break;
                }
            }

            if (listCommon == null || listCommon.size() == 0) {
                // Skips if there is no common.
                continue;
            }

            // Processes only the first item.
            final BlancoXmlElement elementCommon = listCommon.get(0);
            final String name = BlancoXmlBindingUtil.getTextContent(
                    elementCommon, "name");
            if (BlancoStringUtil.null2Blank(name).trim().length() == 0) {
                continue;
            }

            if (sheetLang != BlancoCgSupportedLang.PHP) {
                System.out.println("### ERROR Just PHP SheetLang is permitted.");
                throw new IllegalArgumentException("### ERROR: Invalid SheetLang : " + sheetLang);
            }

            BlancoVeeValidateClassStructure objClassStructure = parseElementSheetPhp(elementSheet);
            ;

            if (objClassStructure != null) {
                // Stores the obtained information.
                listStructure.add(objClassStructure);
                /* Saves the lang information of the message. */
                for (BlancoVeeValidateMessageStructure message : objClassStructure.getMessage()) {
                    String lang = message.getLang();
                    System.out.println("!!! message lang = " + lang);
                    if (lang != null && lang.length() > 0) {
                        BlancoVeeValidateUtil.messageLangs.add(lang.toLowerCase());
                    }
                }
            }
        }

        final BlancoVeeValidateClassStructure[] result = new BlancoVeeValidateClassStructure[listStructure
                .size()];
        listStructure.toArray(result);
        return result;
    }

    /**
     * Parses the "sheet" XML element (PHP-format) in the intermediate XML file to get the value object information.
     *
     * @param argElementSheet "sheet" XML element in the intermediate XML file.
     * @return Value object information obtained as a result of parsing. Null is returned if "name" is not found.
     */
    public BlancoVeeValidateClassStructure parseElementSheetPhp(
            final BlancoXmlElement argElementSheet
    ) {

        final BlancoVeeValidateClassStructure objClassStructure = new BlancoVeeValidateClassStructure();
        /*
         * Stores the headerList generated when VO is used as the parameter type in objClass Structure#importList.
         */
        final Map<String, List<String>> importHeaderList = new HashMap<>();

        // Gets the common information.
        final BlancoXmlElement elementCommon = BlancoXmlBindingUtil
                .getElement(argElementSheet, fBundle
                        .getMeta2xmlElementCommon());
        if (elementCommon == null) {
            // Skips the processing of this sheet if there is no common.
            return null;
        }

        final String name = BlancoXmlBindingUtil.getTextContent(
                elementCommon, "name");
        if (BlancoStringUtil.null2Blank(name).trim().length() == 0) {
            // Skips if name is empty.
            return null;
        }

        if (this.isVerbose()) {
            System.out.println("BlancoVeeValidate#parseElementPhp name = " + name);
        }

        // validate definition common
        this.parseVeeValidateCommon(elementCommon, importHeaderList, objClassStructure);

        // validate definition inheritance
        final List<BlancoXmlElement> extendsList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, fBundle.getMeta2xmlElementExtends());
        if (extendsList != null && extendsList.size() != 0) {
            final BlancoXmlElement elementExtendsRoot = extendsList.get(0);
            parseVeeValidateExtends(elementExtendsRoot, importHeaderList, objClassStructure);
        }

        // validate definition message
        final List<BlancoXmlElement> messageList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, fBundle.getMeta2xmlElementMessage());
        if (messageList != null && messageList.size() != 0) {
            final BlancoXmlElement elementListRoot = messageList.get(0);
            this.parseVeeValidateMessage(elementListRoot, objClassStructure);
        }


        // validate definition parameter
        final List<BlancoXmlElement> listList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet, fBundle.getMeta2xmlElementList());
        if (listList != null && listList.size() != 0) {
            final BlancoXmlElement elementListRoot = listList.get(0);
            this.parseVeeValidateProperties(elementListRoot, importHeaderList, objClassStructure);
        }

        // Gets the header information.
        List<BlancoXmlElement> headerElementList = BlancoXmlBindingUtil
                .getElementsByTagName(argElementSheet,
                        fBundle.getMeta2xmlElementHeader());
        List<String> headerList = this.parseHeaderList(headerElementList, importHeaderList, null);
        if (headerList != null && headerList.size() > 0) {
            objClassStructure.getHeaderList().addAll(headerList);
        }

        return objClassStructure;
    }

    public static Map<String, String> createClassListFromSheets(final File[] argFileMeta) {
        Map<String, String> classList = new HashMap<String, String>();

        for (int index = 0; index < argFileMeta.length; index++) {
            File metaXmlSourceFile = argFileMeta[index];

            if (metaXmlSourceFile.getName().endsWith(".xml") == false) {
                continue;
            }

            final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller()
                    .unmarshal(metaXmlSourceFile);
            if (documentMeta == null) {
                continue;
            }

            // Gets the root element.
            final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                    .getDocumentElement(documentMeta);
            if (elementRoot == null) {
                // The process is aborted if there is no root element.
                continue;
            }

            // Gets a list of sheets (Excel sheets).
            final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                    .getElementsByTagName(elementRoot, "sheet");


            for (BlancoXmlElement elementSheet : listSheet) {
                /*
                 * Supports sheets written for languages other than Java.
                 */
                List<BlancoXmlElement> listCommon = null;
                for (String common : mapCommons.keySet()) {
                    listCommon = BlancoXmlBindingUtil
                            .getElementsByTagName(elementSheet,
                                    common);
                    if (listCommon.size() != 0) {
                        BlancoXmlElement elementCommon = listCommon.get(0);
                        classList.put(
                                BlancoXmlBindingUtil.getTextContent(elementCommon, "name"),
                                BlancoXmlBindingUtil.getTextContent(elementCommon, "package")
                        );

//                        System.out.println("/* tueda */ createClassList = " +
//                                BlancoXmlBindingUtil.getTextContent(elementCommon, "name") + " : " +
//                                BlancoXmlBindingUtil.getTextContent(elementCommon, "package"));
                        break;
                    }
                }
            }
        }

        return classList;
    }

    /**
     * Parse the intermediate XML file to get the "validate definition common".
     *
     * @param argElementCommon
     * @param argObjClassStructure
     */
    private void parseVeeValidateCommon(
            final BlancoXmlElement argElementCommon,
            final Map<String, List<String>> argImportHeaderList,
            final BlancoVeeValidateClassStructure argObjClassStructure
    ) {
        argObjClassStructure.setValidator(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "validator"));
        argObjClassStructure.setName(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "name"));
        argObjClassStructure.setPackage(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "package"));
        argObjClassStructure.setClassAlias(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "classAlias"));
        argObjClassStructure.setBasedir(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "basedir"));
        argObjClassStructure.setImpledir(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "impledir"));

        argObjClassStructure.setDescription(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "description"));
        if (BlancoStringUtil.null2Blank(argObjClassStructure.getDescription())
                .length() > 0) {
            final String[] lines = BlancoNameUtil.splitString(argObjClassStructure
                    .getDescription(), '\n');
            for (int index = 0; index < lines.length; index++) {
                if (index == 0) {
                    argObjClassStructure.setDescription(lines[index]);
                } else {
                    // For a multi-line description, it will be split and stored.
                    // From the second line, assumes that character reference encoding has been properly implemented. 
                    argObjClassStructure.getDescriptionList().add(lines[index]);
                }
            }
        }
        argObjClassStructure.setFileDescription(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "fileDescription"));

        /*
         * validator types:  builtin, custom, config, message
         */
        argObjClassStructure.setValidatorKind(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "validatorKind"));

        argObjClassStructure.setAlterMessage("true".equals(BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "alterMessage")));

        argObjClassStructure.setComputesRequired("true".equals(BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "computesRequired")));

        argObjClassStructure.setAdjustFieldName("true".equals(BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "adjustFieldName")));
        argObjClassStructure.setCreateImportList("true"
                .equals(BlancoXmlBindingUtil.getTextContent(argElementCommon,
                        "createImportList")));

        argObjClassStructure
                .setFieldList(new ArrayList<>());


        /*
         * Generate an import statement for RuleSchema.
         * Auto-generation of import is not required for message.
         * In config, it needs to import all the validators it is using separately.
         */
        String kind = argObjClassStructure.getValidatorKind();
        if (!"message".equalsIgnoreCase(kind) && argObjClassStructure.getCreateImportList()) {
            String validator = argObjClassStructure.getValidator();
            String packageName = "dist.rules";
            String impleDir = "vee-validate";
            boolean isCustom = false;
            if ("custom".equalsIgnoreCase(kind)) {
                validator += "Validator";
                packageName = BlancoStringUtil.null2Blank(argObjClassStructure.getPackage());
                impleDir = BlancoStringUtil.null2Blank(argObjClassStructure.getImpledir());
                isCustom = true;
            }
            BlancoVeeValidateUtil.makeImportHeaderList(packageName, validator, argImportHeaderList, impleDir, "", !isCustom);
        }
    }

    /**
     * Parses an XML document in an intermediate XML file to get "validate definition inheritance".
     * @param argElementExtendsRoot
     * @param argImportHeaderList
     * @param argClassStructure
     */
    private void parseVeeValidateExtends(
            final BlancoXmlElement argElementExtendsRoot,
            final Map<String, List<String>> argImportHeaderList,
            final BlancoVeeValidateClassStructure argClassStructure) {

        String className = BlancoXmlBindingUtil.getTextContent(argElementExtendsRoot, "name");
        if (className != null) {
            String classNameCanon = className;
            String packageName = BlancoXmlBindingUtil.getTextContent(argElementExtendsRoot, "package");
            if (packageName == null) {
                /*
                 * Searches for the package name for this class.
                 */
                BlancoValueObjectTsClassStructure voStructure = BlancoVeeValidateUtil.objects.get(className);
                if (voStructure != null) {
                    packageName = voStructure.getPackage();
                }
            }
            if (packageName != null) {
                classNameCanon = packageName + "." + className;
            }
            if (isVerbose()) {
                System.out.println("Extends : " + classNameCanon);
            }
            argClassStructure.setExtends(classNameCanon);

            /*
             * Creates import information for TypeScript.
             */
            if (argClassStructure.getCreateImportList()) {
                BlancoVeeValidateUtil.makeImportHeaderList(packageName, className, argImportHeaderList, argClassStructure.getBasedir(), argClassStructure.getPackage(), false);
            }
        } else {
            System.out.println("/* Extends Skip */ className is not specified!!!");
        }
    }

    private void parseVeeValidateMessage(
            final BlancoXmlElement argElementMessage,
            final BlancoVeeValidateClassStructure argObjClassStructure
    ) {
        final List<BlancoXmlElement> listChildNodes = BlancoXmlBindingUtil
                .getElementsByTagName(argElementMessage, "message");
        for (int index = 0; index < listChildNodes.size(); index++) {
            final BlancoXmlElement elementList = listChildNodes.get(index);
            final BlancoVeeValidateMessageStructure messageStructure = new BlancoVeeValidateMessageStructure();

            messageStructure.setNo(BlancoXmlBindingUtil.getTextContent(
                    elementList, "no"));
            messageStructure.setMessage(BlancoXmlBindingUtil.getTextContent(
                    elementList, "template"));
            if (messageStructure.getMessage() == null
                    || messageStructure.getMessage().trim().length() == 0) {
                continue;
            }
            messageStructure.setLang(BlancoXmlBindingUtil.getTextContent(
                    elementList, "lang"));
            if (messageStructure.getLang() == null
                    || messageStructure.getLang().trim().length() == 0) {
                continue;
            }
            argObjClassStructure.getMessage().add(messageStructure);
        }
    }

    private void parseVeeValidateProperties(
            final BlancoXmlElement argElementProperties,
            final Map<String, List<String>> argImportHeaderList,
            final BlancoVeeValidateClassStructure argObjClassStructure
    ) {
        final List<BlancoXmlElement> listChildNodes = BlancoXmlBindingUtil
                .getElementsByTagName(argElementProperties, "field");
        for (int index = 0; index < listChildNodes.size(); index++) {
            final BlancoXmlElement elementList = listChildNodes.get(index);
            final BlancoVeeValidateFieldStructure fieldStructure = new BlancoVeeValidateFieldStructure();

            fieldStructure.setNo(BlancoXmlBindingUtil.getTextContent(
                    elementList, "no"));
            fieldStructure.setName(BlancoXmlBindingUtil.getTextContent(
                    elementList, "name"));
            if (fieldStructure.getName() == null
                    || fieldStructure.getName().trim().length() == 0) {
                continue;
            }

            /*
             * Gets the type. Changes the type name to TypeScript style here.
             */
            String phpType = BlancoXmlBindingUtil.getTextContent(elementList, "type");
            String targetType = BlancoVeeValidateUtil.parsePhpTypes(phpType, argObjClassStructure, argImportHeaderList, false);
            fieldStructure.setType(targetType);

            /* Supports Generic. */
            String phpGeneric = BlancoXmlBindingUtil.getTextContent(elementList, "generic");
            if (BlancoStringUtil.null2Blank(phpGeneric).length() != 0) {
                String targetGeneric = BlancoVeeValidateUtil.parsePhpTypes(phpGeneric, argObjClassStructure, argImportHeaderList, true);
                fieldStructure.setGeneric(targetGeneric);
            }

            // Supports Nullable.
            fieldStructure.setNullable("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "nullable")));

            // Description
            fieldStructure.setDescription(BlancoXmlBindingUtil
                    .getTextContent(elementList, "description"));
            final String[] lines = BlancoNameUtil.splitString(
                    fieldStructure.getDescription(), '\n');
            for (int indexLine = 0; indexLine < lines.length; indexLine++) {
                if (indexLine == 0) {
                    fieldStructure.setDescription(lines[indexLine]);
                } else {
                    // For a multi-line description, it will be split and stored.
                    // From the second line, assumes that character reference encoding has been properly implemented.   
                    fieldStructure.getDescriptionList().add(
                            lines[indexLine]);
                }
            }

            // Default
            fieldStructure.setDefault(BlancoXmlBindingUtil.getTextContent(
                    elementList, "default"));

            // Required
            fieldStructure.setNullable("true".equals(BlancoXmlBindingUtil
                    .getTextContent(elementList, "required")));

            argObjClassStructure.getFieldList().add(fieldStructure);
        }
    }

    /**
     *
     *
     * @param argHeaderElementList
     * @param argImportHeaderList
     * @return
     */
    private List<String> parseHeaderList(
            final List<BlancoXmlElement> argHeaderElementList,
            final Map<String, List<String>> argImportHeaderList,
            final Map<String, List<String>> argDefaultExportedHeaderList
    ) {
        if (this.isVerbose()) {
            System.out.println("BlancoVeeValidate#parseHeaderList: Start parseHeaderList.");
        }

        List<String> headerList = new ArrayList<>();

        /*
         * Creates a list of header.
         * First, outputs what is written in the definition as it is.
         */
        if (argHeaderElementList != null && argHeaderElementList.size() > 0) {
            final BlancoXmlElement elementHeaderRoot = argHeaderElementList.get(0);
            final List<BlancoXmlElement> listHeaderChildNodes = BlancoXmlBindingUtil
                    .getElementsByTagName(elementHeaderRoot, "header");
            for (int index = 0; index < listHeaderChildNodes.size(); index++) {
                final BlancoXmlElement elementList = listHeaderChildNodes
                        .get(index);

                final String headerName = BlancoXmlBindingUtil
                        .getTextContent(elementList, "name");
                if (this.isVerbose()) {
                    System.out.println("BlancoVeeValidate#parseHeaderList header = " + headerName);
                }
                if (headerName == null || headerName.trim().length() == 0) {
                    continue;
                }
                headerList.add(
                        BlancoXmlBindingUtil
                                .getTextContent(elementList, "name"));
            }
        }

        /*
         * Next, outputs the auto-generated one.
         * The current method requires the following assumptions.
         *  * One class definition per file
         *  * Represents directories with Java/Kotlin style package notation in the definition sheet
         * TODO: Should it be possible to define the directory where the files are located on the definition sheet?
         */
        this.parseGeneratedHeaderList(headerList, argImportHeaderList, false);

        /*
         * Then, outputs the default exported.
         * The current method requires the following assumptions.
         *  * One class definition per file
         *  * Represents directories with Java/Kotlin style package notation in the definition sheet
         * TODO: Should it be possible to define the directory where the files are located on the definition sheet?
         */
        if (argDefaultExportedHeaderList != null && argDefaultExportedHeaderList.size() > 0) {
            this.parseGeneratedHeaderList(headerList, argDefaultExportedHeaderList, true);
        }

        return headerList;
    }

    private void parseGeneratedHeaderList(
            List<String> argHeaderList,
            final Map<String, List<String>> argImportHeaderList,
            boolean isDefaultExported
    ) {
        if (argImportHeaderList != null && argImportHeaderList.size() > 0) {
            Set<String> fromList = argImportHeaderList.keySet();
            for (String strFrom : fromList) {
                StringBuffer sb = new StringBuffer();
                if (isDefaultExported) {
                    sb.append("import ");
                } else {
                    sb.append("import { ");
                }
                List<String> classNameList = argImportHeaderList.get(strFrom);
                int count = 0;
                for (String className : classNameList) {
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(className);
                    count++;
                }
                if (count > 0) {
                    if (isDefaultExported) {
                        sb.append(" from \"" + strFrom + "\"");
                    } else {
                        sb.append(" } from \"" + strFrom + "\"");
                    }
                    if (this.isVerbose()) {
                        System.out.println("BlancoRestGeneratorTsXmlParser#parseGeneratedHeaderList import = " + sb.toString());
                    }
                    argHeaderList.add(sb.toString());
                }
            }
        }
    }
}
